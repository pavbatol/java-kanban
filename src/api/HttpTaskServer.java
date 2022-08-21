package api;

import api.GsonAdapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.FileBackedTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static tasks.TaskType.*;
import static util.Functions.*;

public class HttpTaskServer {
    private static final String NAME = "localhost";
    private static final int PORT = 8080;
    FileBackedTaskManager fbtm;
    HttpServer server;

    public HttpTaskServer(FileBackedTaskManager fbtm) throws IOException {
        this.fbtm = fbtm;
        this.server = HttpServer.create();
        this.server.bind(new InetSocketAddress(NAME, PORT), 0);
        server.createContext("/tasks/history", new AllTasksHandler());
        server.createContext("/tasks", new AllTasksHandler());
        server.createContext("/tasks/task", new TaskHandler());
        server.createContext("/tasks/subtask", new TaskHandler());
        server.createContext("/tasks/epic", new TaskHandler());
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        server.start();
        System.out.println("Сервер " + getClass().getSimpleName() + " запущен на " + PORT + " порту");
        System.out.println("Открой в браузере http://" + NAME + ":" + PORT + "/");
    }

    public void stop() {
        server.stop(1);
        System.out.println("Сервер " + getClass().getSimpleName() + " остановлен.");
    }

    private class AllTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange h) throws IOException {
            System.out.println("Началась обработка. Строка запроса: " + h.getRequestURI());

            try {
                String response;
                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .create();

                if ("GET".equals(h.getRequestMethod())) {
                    String path = h.getRequestURI().getPath();
                    String rawQuery = h.getRequestURI().getRawQuery();
                    if ((path.endsWith("/tasks") || path.endsWith("/tasks/"))  && rawQuery == null) {
                        List<Task> allTasks = fbtm.getTasks();
                        allTasks.addAll(fbtm.getEpics());
                        allTasks.addAll(fbtm.getSubtasks());
                        response = gson.toJson(allTasks);
                        sendText(h, 200, response);
                    } else if ((path.endsWith("/tasks/history") || path.endsWith("/tasks/history/"))
                            && rawQuery == null) {
                        response = gson.toJson(fbtm.getHistory());
                        sendText(h, 200, response);
                    } else {
                        response = gson.toJson("В запросе содержится ошибка. Проверьте и отправьте запрос снова.");
                        sendText(h, 400, response);
                    }
                } else {
                    response = "Поддерживается только метод GET.";
                    response = gson.toJson(response);
                    sendText(h, 405, response);
                }
            } finally {
                h.close();
            }
        }
    }

    private class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange h) throws IOException {
            System.out.println("Началась обработка. Строка запроса: " + h.getRequestURI());
            try {
                TaskType pathType = getPathType(h.getRequestURI().getPath());
                if (pathType == null) {
                    h.sendResponseHeaders(400, 0);
                    return;
                }

                String finding = "id=";
                String rawQuery = h.getRequestURI().getRawQuery();
                int id = parseToId(finding, rawQuery);

                Headers headers = h.getResponseHeaders();
                headers.set("Content-Type", "text/JSON");
                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                        .create();

                switch (h.getRequestMethod()) {
                    case "GET":
                        sendResponseForGet(rawQuery, pathType, id, h, gson);
                        break;
                    case "POST":
                        sendResponseForPOST(rawQuery, pathType, id, h, gson);
                        break;
                    case "DELETE":
                        sendResponseForDELETE(rawQuery, pathType, id, h);
                        break;
                    default:
                        h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        }

        private TaskType getPathType(String path) {
            TaskType pathType;
            if ("/tasks/task".equals(path) || "/tasks/task/".equals(path)) {
                pathType = TASK;
            } else if ("/tasks/subtask".equals(path) || "/tasks/subtask/".equals(path)) {
                pathType = SUBTASK;
            } else if ("/tasks/epic".equals(path) || "/tasks/epic/".equals(path)) {
                pathType = EPIC;
            } else {
                return null;
            }
            return pathType;
        }

        private int parseToId(String prefix, String rawQuery) {
            int id = -1;
            if (prefix == null || rawQuery == null) {
                return id;
            }
            String[] parts = rawQuery.split("&");
            for (String part : parts) {
                if (part.contains(prefix) && prefix.length() < part.length()) {
                    String targetStr = part.substring(prefix.length());
                    try {
                        id = Integer.parseInt(targetStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Некорректное значение для " + prefix);
                        break;
                    }
                    break;
                }
            }
            return id;
        }

        private void sendResponseForGet(String rawQuery,
                                          TaskType pathType,
                                          int requestId,
                                          HttpExchange h,
                                          Gson gson) throws IOException {
            String response;
            if (rawQuery != null) {
                if (requestId < 0) {
                    h.sendResponseHeaders(400, 0);
                } else {
                    response = gson.toJson(getAnyTypeTaskByIdForType(requestId, fbtm, pathType));
                    sendText(h, 200, response);
                }
            } else {
                response = gson.toJson(getAnyTypeTasksForType(fbtm, pathType));
                sendText(h, 200, response);
            }
        }

        private void sendResponseForDELETE(String rawQuery,
                                             TaskType pathType,
                                             int requestId,
                                             HttpExchange h) throws IOException {
            String response = "";
            if (rawQuery != null) {
                if (requestId < 0) {
                    h.sendResponseHeaders(400, 0);
                } else {
                    removedAnyTypeTaskByIdForType(requestId, fbtm, pathType);
                    sendText(h, 200, response);
                }
            } else {
                removedAnyTypeTasksForType(fbtm, pathType);
                sendText(h, 200, response);
            }
        }

        private void sendResponseForPOST(String rawQuery,
                                           TaskType pathType,
                                           int requestId,
                                           HttpExchange h,
                                           Gson gson) throws IOException {
            String response = "";
            Task task;
            String body = readText(h);

            Class<? extends Task> classOfT;
            if (pathType == TASK) {
                classOfT = Task.class;
            } else if (pathType == SUBTASK) {
                classOfT = Subtask.class;
            } else if (pathType == EPIC) {
                classOfT = Epic.class;
            } else {
                System.out.println("!Не определен тип по пути запроса");
                h.sendResponseHeaders(400, 0);
                return ;
            }
            task = gson.fromJson(body, classOfT);

            if (rawQuery != null) {
                if (requestId < 0) {
                    h.sendResponseHeaders(400, 0);
                } else if (requestId != task.getId()) {
                    response = gson.toJson("!Не совпадают id в запросе и в задаче из тела запроса. "
                            + "Обновление не выполнено");
                    sendText(h, 400, response);
                } else {
                    updateAnyTypeTaskForType(task, fbtm, pathType);
                    sendText(h, 200, response);
                }
            } else {
                int addedTaskId = addAnyTypeTaskForType(task, fbtm, pathType);
                if (addedTaskId < 0) {
                    response = gson.toJson("!Задача не добавлена, id не найден для " + pathType);
                    sendText(h, 400, response);
                } else {
                    response = gson.toJson("Задача добавлена");
                    sendText(h, 201, response);
                }
            }
        }
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, int code, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
    }
}

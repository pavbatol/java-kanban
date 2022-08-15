package api;

import com.google.gson.Gson;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static tasks.TaskType.*;
import static util.Functions.*;

public class HttpTaskServer {
    private static final int PORT = 8080;
    FileBackedTaskManager fbtm;
    HttpServer server;

    public HttpTaskServer(FileBackedTaskManager fbtm) throws IOException {
        this.server = HttpServer.create();
        this.server.bind(new InetSocketAddress(PORT), 0);
        this.fbtm = fbtm;
    }

    public void start() {
        // TODO: 14.08.2022 Make all with try/catch
        server.createContext("/tasks/history", new AllTasksHandler());
        server.createContext("/tasks", new AllTasksHandler());
        server.createContext("/tasks/task", new TaskHandler());
        server.createContext("/tasks/subtask", new TaskHandler());
        server.createContext("/tasks/epic", new TaskHandler());
        server.start();
        System.out.println("Сервер " + getClass().getSimpleName() + " запущен на " + PORT + " порту.");
    }

    public void stop() {
        server.stop(1);
        System.out.println("Сервер " + getClass().getSimpleName() + " остановлен.");
    }

    private class AllTasksHandler implements HttpHandler {
        // Будем возвращать в ответе все задач-подзадачи-эпики или историю просмотра
        @Override
        public void handle(HttpExchange he) throws IOException {
            System.out.println("Началась обработка. Строка запроса: " + he.getRequestURI());

            String response;
            Headers headers = he.getResponseHeaders();
            headers.set("Content-Type", "text/JSON");
            Gson gson = new Gson();

            if (he.getRequestMethod().equals("GET")) {
                String path = he.getRequestURI().getPath();
                String rawQuery = he.getRequestURI().getRawQuery();

                if ((path.endsWith("/tasks") || path.endsWith("/tasks/"))  && rawQuery == null) {
                    List<Task> allTasks = fbtm.getTasks();
                    allTasks.addAll(fbtm.getEpics());
                    allTasks.addAll(fbtm.getSubtasks());
                    response = gson.toJson(allTasks);
                    he.sendResponseHeaders(200, 0);
                } else if ((path.endsWith("/tasks/history") || path.endsWith("/tasks/history/")) && rawQuery == null) {
                    response = gson.toJson(fbtm.getHistory());
                    he.sendResponseHeaders(200, 0);
                } else {
                    response = gson.toJson("В запросе содержится ошибка. Проверьте и отправьте запрос снова.");
                    he.sendResponseHeaders(400, 0);
                }
            } else {
                response = "Поддерживается только метод GET.";
                response = gson.toJson(response);
                he.sendResponseHeaders(405, 0);
            }

            try (OutputStream os = he.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    private class TaskHandler implements HttpHandler {
        // Возвращаем в ответе только Tasks/Subtasks/Epics если GET и не указан id,
        // или работаем с конкретной задачей по Методу запроса
        @Override
        public void handle(HttpExchange he) throws IOException {
            System.out.println("Началась обработка. Строка запроса: " + he.getRequestURI());

            TaskType pathType = getPathType(he.getRequestURI().getPath());
            if (pathType == null) {
                he.sendResponseHeaders(400, 0);
                return;
            }

            String finding = "id=";
            String rawQuery = he.getRequestURI().getRawQuery();
            int id = parsFindingStrToIntFromQuery(finding, rawQuery, -1); // после знака "?"

            Headers headers = he.getResponseHeaders();
            headers.set("Content-Type", "text/JSON");
            Gson gson = new Gson();
            String response = "";

            switch (he.getRequestMethod()) {
                case "GET":
                    response = buildResponseForGet(rawQuery, pathType, id, he, gson);
                    break;
                case "POST":
                    response = buildResponseForPOST(rawQuery, pathType, id, he, gson);
                    break;
                case "DELETE":
                    response = buildResponseForDELETE(rawQuery, pathType, id, he);
                    break;
                default:
                    he.sendResponseHeaders(405, 0);
            }

            try (OutputStream os = he.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private TaskType getPathType(String path) {
            TaskType pathType;
            if (path.contains("/tasks/task")) {
                pathType = TASK;
            } else if (path.contains("/tasks/subtask")) {
                pathType = SUBTASK;
            } else if (path.contains("/tasks/epic")) {
                pathType = EPIC;
            } else {
                return null;
            }
            return pathType;
        }

        private int parsFindingStrToIntFromQuery(String finding, String rawQuery, int defaultReturn) {
            int id = defaultReturn;
            if (finding == null || rawQuery == null) {
                return id;
            }
            String[] parts = rawQuery.split("&");
            for (String part : parts) {
                if (part.contains(finding) && finding.length() < part.length()) {
                    String targetStr = part.substring(finding.length());
                    try {
                        id = Integer.parseInt(targetStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Некорректное значение для " + finding);
                        break;
                    }
                    break;
                }
            }
            return id;
        }

        private String buildResponseForGet(String rawQuery,
                                           TaskType pathType,
                                           int requestId,
                                           HttpExchange he,
                                           Gson gson) throws IOException {
            String response = "";
            if (rawQuery != null) {
                if (requestId < 0) {
                    he.sendResponseHeaders(400, 0); // нет id но rawQuery есть
                } else {
                    response = gson.toJson(getAnyTypeTaskByIdForType(requestId, fbtm, pathType));
                    he.sendResponseHeaders(200, 0);
                }
            } else {
                response = gson.toJson(getAnyTypeTasksForType(fbtm, pathType));
                he.sendResponseHeaders(200, 0);
            }
            return response;
        }

        private String buildResponseForDELETE(String rawQuery,
                                              TaskType pathType,
                                              int requestId,
                                              HttpExchange he) throws IOException {
            String response = "";
            if (rawQuery != null) {
                if (requestId < 0) {
                    he.sendResponseHeaders(400, 0); // нет id но rawQuery есть
                } else {
                    removedAnyTypeTaskByIdForType(requestId, fbtm, pathType);
                    he.sendResponseHeaders(200, 0);
                }
            } else {
                removedAnyTypeTasksForType(fbtm, pathType);
                he.sendResponseHeaders(200, 0);
            }
            return response;
        }

        private String buildResponseForPOST(String rawQuery,
                                            TaskType pathType,
                                            int requestId,
                                            HttpExchange he,
                                            Gson gson) throws IOException {
            String response = "";
            Task task;
            InputStream inputStream = he.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            Class<? extends Task> classOfT;
            if (pathType == TASK) {
                classOfT = Task.class;
            } else if (pathType == SUBTASK) {
                classOfT = Subtask.class;
            } else if (pathType == EPIC) {
                classOfT = Epic.class;
            } else {
                he.sendResponseHeaders(400, 0);
                return "Не определен тип по пути запроса";
            }
            task = gson.fromJson(body, classOfT);

            if (rawQuery != null) {
                if (requestId < 0) {
                    he.sendResponseHeaders(400, 0); // нет id, но rawQuery есть
                } else if (requestId != task.getId()) {
                    response = gson.toJson("Не совпадают id в запросе и в задаче из тела запроса");
                    he.sendResponseHeaders(203, 0); // не совпадают id
                } else {
                    // Обновляем
                    updateAnyTypeTaskForType(task, fbtm, pathType);
                    he.sendResponseHeaders(200, 0);
                }
            } else {
                //Добавляем
                int addedTaskId = addAnyTypeTaskForType(task, fbtm, pathType);
                if (addedTaskId < 0) {
                    response = gson.toJson("Задача не добавлена");
                    he.sendResponseHeaders(202, 0);
                } else {
                    response = gson.toJson("Задача добавлена");
                    he.sendResponseHeaders(201, 0);
                }
            }
            return response;
        }

    }
}

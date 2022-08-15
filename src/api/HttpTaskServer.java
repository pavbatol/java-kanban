package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.FileBackedTaskManager;
import tasks.Task;
import tasks.TaskType;
import util.Functions;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

import static tasks.TaskType.*;


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
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка. Строка запроса: " + httpExchange.getRequestURI());

            String response;
            Headers headers = httpExchange.getResponseHeaders();
            headers.set("Content-Type", "text/JSON");
            Gson gson = new Gson();
            if (httpExchange.getRequestMethod().equals("GET")) {
                String path = httpExchange.getRequestURI().getPath();
                String query = httpExchange.getRequestURI().getQuery();

//                System.out.println(path);
//                System.out.println(query==null);

                if ((path.endsWith("/tasks") || path.endsWith("/tasks/"))  && query == null) {
                    List<Task> allTasks = fbtm.getTasks();
                    allTasks.addAll(fbtm.getEpics());
                    allTasks.addAll(fbtm.getSubtasks());
                    response = gson.toJson(allTasks);
                    httpExchange.sendResponseHeaders(200, 0);
                } else if ((path.endsWith("/tasks/history") || path.endsWith("/tasks/history/")) && query == null) {
                    response = gson.toJson(fbtm.getHistory());
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    response = gson.toJson("В запросе содержится ошибка. Проверьте и отправьте запрос снова.");
                    httpExchange.sendResponseHeaders(400, 0);
                }
            } else {
                response = "Поддерживается только метод GET.";
                response = gson.toJson(response);
                httpExchange.sendResponseHeaders(405, 0);
            }

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    private class TaskHandler implements HttpHandler {
        // Возвращаем в ответе только Tasks/Subtasks/Epics если GET и не указан id,
        // или работаем с конкретной задачей по Методу запроса
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка. Строка запроса: " + httpExchange.getRequestURI());

            TaskType pathType = getPathType(httpExchange.getRequestURI().getPath());
            if (pathType == null) {
                httpExchange.sendResponseHeaders(400, 0);
                return;
            }

            String finding = "id=";
            String query = httpExchange.getRequestURI().getQuery();
            int id = parsFindingStrToIntFromQuery(finding, query, -1); // после знака "?"

//            System.out.println(httpExchange.getRequestURI().getQuery());
//            System.out.println(id);

            Headers headers = httpExchange.getResponseHeaders();
            headers.set("Content-Type", "text/JSON");
            Gson gson = new Gson();
            String response = "";

            switch (httpExchange.getRequestMethod()) {
                case "GET":
//                    if (query != null) {
//                        if (id < 0) {
//                            httpExchange.sendResponseHeaders(400, 0); // нет id
//                        } else {
//                            response = gson.toJson(Functions.getAnyTypeTaskByIdForType(id, fbtm, pathType));
//                            httpExchange.sendResponseHeaders(200, 0);
//                        }
//                    } else {
//                        response = gson.toJson(Functions.getAnyTypeTasksForType(fbtm, pathType) );
//                        httpExchange.sendResponseHeaders(200, 0);
//                    }

                    response = responsForGet(query, pathType, id, httpExchange, gson);

                    break;
                case "POST":
                    if (id < 0) {
                        httpExchange.sendResponseHeaders(400, 0);
                    } else {
                        // Добавляем или меняем задачу
                        httpExchange.sendResponseHeaders(200, 0);
//                        Element element =
//                        Task task =
//                        if (fbtm.addTask(task) < 0) {
//                            fbtm.updateTask(task);
//                        }
                    }
                    break;
                case "DELETE":
                    if (query != null) {
                        if (id < 0) {
//                            httpExchange.sendResponseHeaders(400, 0); // нет id
                        } else {
//                            response = gson.toJson(Functions.getAnyTypeTaskByIdForType(id, fbtm, pathType));
//                            httpExchange.sendResponseHeaders(200, 0);
                        }
                    } else {
//                        response = gson.toJson(Functions.getAnyTypeTasksForType(fbtm, pathType) );
//                        httpExchange.sendResponseHeaders(200, 0);
                    }
                    break;
                default:
                    httpExchange.sendResponseHeaders(405, 0);
            }

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

//        public Task getAnyTypeTaskByIdForType(int taskId, TaskManager tm, TaskType taskType) {
//            if (tm == null || taskType == null) {
//                return null;
//            }
//            Task task = null;
//            switch (taskType) {
//                case TASK: task = tm.getTaskById(taskId);
//                    break;
//                case SUBTASK: task = tm.getSubtaskById(taskId);
//                    break;
//                case EPIC: task = tm.getEpicById(taskId);
//                    break;
//            }
//            return task;
//        }

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

        private int parsFindingStrToIntFromQuery(String finding, String query, int defaultReturn) {
            int id = defaultReturn;
            if (finding == null || query == null) {
                return id;
            }
            String[] parts = query.split("&");
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


        private String responsForGet(String query,
                                     TaskType pathType,
                                     int requestId,
                                     HttpExchange httpExchange,
                                     Gson gson) throws IOException {
            String response = "";
            if (query != null) {
                if (requestId < 0) {
                    httpExchange.sendResponseHeaders(400, 0); // нет id
                } else {
                    response = gson.toJson(Functions.getAnyTypeTaskByIdForType(requestId, fbtm, pathType));
                    httpExchange.sendResponseHeaders(200, 0);
                }
            } else {
                response = gson.toJson(Functions.getAnyTypeTasksForType(fbtm, pathType) );
                httpExchange.sendResponseHeaders(200, 0);
            }
            return response;
        }

    }


}

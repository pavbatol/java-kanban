package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.FileBackedTaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;


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
        server.createContext("/tasks", new prioritizedTasksHandler());
        server.createContext("/tasks/task", new taskHandler());
//        server.createContext("/tasks/subtask", new subtaskHandler());
//        server.createContext("/tasks/epic", new epicHandler());
//        server.createContext("/tasks/history", new historyHandler());
        server.start();
        System.out.println("Сервер " + getClass().getSimpleName() + " запущен на " + PORT + " порту.");
    }

    public void stop() {
        server.stop(1);
        System.out.println("Сервер " + getClass().getSimpleName() + " остановлен.");

    }

    private class prioritizedTasksHandler implements HttpHandler {
        // Будем возвращать в ответе все задачи и подзадачи
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка /tasks запроса от клиента.");

            String response;
            String method = httpExchange.getRequestMethod();
            Headers headers = httpExchange.getResponseHeaders();
            Gson gson = new Gson();
            if (method.equals("GET")) {
                headers.set("Content-Type", "text/JSON");

                List<Task> prioritizedTasks  = fbtm.getPrioritizedTasks(); //отсортированный по времени

                response = gson.toJson(prioritizedTasks);
                httpExchange.sendResponseHeaders(200, 0);
            } else {
                headers.set("Content-Type", "text/JSON");
                response = "В запросе содержится ошибка. Проверьте параметры и повторите запрос.";
                response = gson.toJson(response);
                httpExchange.sendResponseHeaders(400, 0);
            }

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    private class taskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка /task запроса от клиента.");

            String response;
            String method = httpExchange.getRequestMethod();
            Headers headers = httpExchange.getResponseHeaders();
            Gson gson = new Gson();
            if (method.equals("GET")) {
                headers.set("Content-Type", "text/JSON");

                List<Task> prioritizedTasks  = fbtm.getPrioritizedTasks(); //отсортированный по времени

                response = gson.toJson(prioritizedTasks);
                httpExchange.sendResponseHeaders(200, 0);
            } else {
                headers.set("Content-Type", "text/JSON");
                response = "В запросе содержится ошибка. Проверьте параметры и повторите запрос.";
                response = gson.toJson(response);
                httpExchange.sendResponseHeaders(400, 0);
            }

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }


}

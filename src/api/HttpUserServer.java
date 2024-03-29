package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import managers.TaskManager;
import managers.UserManager;
import tasks.User;
import util.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpUserServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final UserManager um;
    private final TaskManager tm;
    private final Gson gson;

    public HttpUserServer() throws IOException {
        this(Managers.getDefaultUserManager());
    }

    public HttpUserServer(UserManager um) throws IOException {
        this.um = um;
        this.tm = um.getTaskManager();
        this.gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/api/v1/users", this::handleUsers);
    }

    public static void main(String[] args) {
        try {
            HttpUserServer userServer = new HttpUserServer();
            userServer.start();
            //userServer.stop();

        } catch (IOException e) {
            System.out.println("Не удалось запустить сервер");
        }
    }

    private void handleUsers(HttpExchange h) {
        try {
            String method = h.getRequestMethod();
            System.out.println("Обработка " + method + " /api/v1/users: " + h.getRequestURI());
            String path = h.getRequestURI().getPath();
            switch (method) {
                case "GET": {
                    if (Pattern.matches("^/api/v1/users$", path)) {
                        final String response = gson.toJson(um.getUsers());
                        sendText(h, 200, response);
                        return;
                    }
                    if (Pattern.matches("^/api/v1/users/\\d+$", path)) {
                        int id = parsePathId(path.replaceFirst("/api/v1/users/", ""));
                        if (id == -1) {
                            System.out.println("Неверный запрос, невозможно определить id");
                            h.sendResponseHeaders(400, 0);
                        } else {
                            final String response = gson.toJson(um.getUser(id));
                            sendText(h, 200, response);
                        }
                        return;
                    }
                    if (Pattern.matches("^/api/v1/users/\\d+/tasks$", path)) {
                        String idStr = path.replaceFirst("/api/v1/users/", "")
                                .replaceFirst("/tasks", "");
                        int id = parsePathId(idStr);
                        if (id == -1) {
                            System.out.println("Неверный запрос, невозможно определить id");
                            h.sendResponseHeaders(400, 0);
                        } else {
                            final String response = gson.toJson(um.getUserAllTasks(id));
                            sendText(h, 200, response);
                        }
                        return;
                    }
                    System.out.println("Неверный запрос: " + h.getRequestURI());
                    h.sendResponseHeaders(400, 0);
                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("^/api/v1/users$", path)) {
                        um.removeUsers();
                        System.out.println("Все пользователи удалены");
                        h.sendResponseHeaders(200, 0);
                        return;
                    }
                    if (Pattern.matches("^/api/v1/users/\\d+$", path)) {
                        int id = parsePathId(path.replaceFirst("/api/v1/users/", ""));
                        if (id == -1) {
                            System.out.println("Неверный запрос, невозможно определить id");
                            h.sendResponseHeaders(400, 0);
                        } else {
                            um.removeUser(id);
                            System.out.println("Удален пользователь с id = " + id);
                            h.sendResponseHeaders(200, 0);
                        }
                        return;
                    }
                    System.out.println("Неверный запрос: " + h.getRequestURI());
                    h.sendResponseHeaders(400, 0);
                    break;
                }
                case "POST": {
                    if (Pattern.matches("^/api/v1/users/\\d+$", path)) {
                        int id = parsePathId(path.replaceFirst("/api/v1/users/", ""));
                        if (id == -1) {
                            System.out.println("Неверный запрос, невозможно определить id");
                            h.sendResponseHeaders(400, 0);
                        } else {
                            User user = gson.fromJson(readText(h), User.class);
                            if (user == null || user.getId() != id) {
                                System.out.println("Не равны id в полученных строке запроса и задаче");
                                h.sendResponseHeaders(400, 0);
                            } else {
                                if (um.getUser(id) == null) {
                                    System.out.println("Нет пользователя с id = " + id);
                                    h.sendResponseHeaders(400, 0);
                                } else {
                                    um.updateUser(user);
                                    System.out.println("Обновлен пользователь с id = " + id);
                                    h.sendResponseHeaders(200, 0);
                                }
                            }
                        }
                        return;
                    }
                    if (Pattern.matches("^/api/v1/users$", path)) {
                        User user = gson.fromJson(readText(h), User.class);
                        int userId = um.addUser(user);
                        if (userId == -1) {
                            System.out.println("Не удалось добавить пользователя, " +
                                    "проверьте корректность объекта User");
                            h.sendResponseHeaders(400, 0);
                        } else {
                            System.out.println("Добавлен пользователь с id = " + userId);
                            h.sendResponseHeaders(201, 0);
                        }
                        return;
                    }
                }
                default:
                    System.out.println("Ждем GET или DELETE, а получен " + method);
                    h.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при обработке запроса");
        } finally {
            h.close();
        }
    }

    private int parsePathId(String idStr) {
        try {
            return  Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    void start() {
        server.start();
        System.out.printf("Сервер %s запущен на порту %s\n", getClass().getSimpleName(), PORT);
        System.out.printf("Используйте адрес: http://localhost:%s/api/v1/users\n", PORT);
    }

    void stop() {
        server.stop(1);
        System.out.printf("Сервер %s остановлен", getClass().getSimpleName());
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

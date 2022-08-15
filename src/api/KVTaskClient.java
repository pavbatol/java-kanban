package api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow;

public class KVTaskClient {
    private final String url;
    private final HttpClient client;
    private final String apiToken;

    public KVTaskClient(String url) throws RuntimeException{
        this.url = url;
        this.client = HttpClient.newHttpClient();
        apiToken = getApiToken();
        if (apiToken.isEmpty()) {
            System.out.println("Не получен токен");
            throw new RuntimeException();
        }
        System.out.println("Ваш API_TOKEN: " + apiToken);
    }

    void put(String key, String json) {
        // POST /save/<ключ>?API_TOKEN=
        URI uri = URI.create(url + "/save" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(new HttpRequest.BodyPublisher() {
                    @Override
                    public long contentLength() {
                        return 0;
                    }

                    @Override
                    public void subscribe(Flow.Subscriber<? super ByteBuffer> subscriber) {

                    }
                })
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Данные сохранены");
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время отправки запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    String load(String key) {
        //GET /load/<ключ>?API_TOKEN=
        URI uri = URI.create(url + "/save" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Данные сохранены");
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время отправки запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }


        return ...
    }

    private String getApiToken() {
        String result = "";
        URI uri = URI.create(url + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                result = response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время запроса регистрации возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return result;
    }

//    protected String readText(HttpExchange h) throws IOException {
//        return new String(h.getRequestBody().readAllBytes(), UTF_8);
//    }
//
//    protected void sendText(HttpExchange h, String text) throws IOException {
//        byte[] resp = text.getBytes(UTF_8);
//        h.getResponseHeaders().add("Content-Type", "application/json");
//        h.sendResponseHeaders(200, resp.length);
//        h.getResponseBody().write(resp);
//    }

}

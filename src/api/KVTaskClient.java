package api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

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
            throw new RuntimeException("Не удалось получить API_TOKEN");
        }
        System.out.println("Ваш API_TOKEN: " + apiToken);
    }

    public void put(String key, String json) {
        URI uri = URI.create(String.format("%s/save/%s?API_TOKEN=%s", url, key, apiToken));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
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

    public String load(String key) {
        String result = "";
        URI uri = URI.create(String.format("%s/load/%s?API_TOKEN=%s", url, key, apiToken));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                result = response.body();
                System.out.println("Данные получены");
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время отправки запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return result;
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
                System.out.println("Регистрация успешна");
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время запроса регистрации возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KVTaskClient client1 = (KVTaskClient) o;
        return url.equals(client1.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}

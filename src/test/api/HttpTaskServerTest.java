package api;

import managers.FileBackedTaskManager;
import managers.HTTPTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    public final int port = 8080;
    public final String host = "http://localhost";
    public final String url = host +":" + port;
    public int id = -1;
    HttpTaskServer server;

    @BeforeEach
    public void beforeEach() {
        id = -1;
        FileBackedTaskManager fbtm = new FileBackedTaskManager(Path.of(""));
        try {
            server = new HttpTaskServer(fbtm);
            server.start();
        } catch (IOException e) {
            System.out.println("Не удалось запустить HTTP-Server\n" + e.getMessage());
            return;
        }

    }

    @AfterEach
    void tearDown() {
        server.stop();
    }



}
package util;

import api.GsonAdapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import managers.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Locale;

public final class Managers {
    public static final Path path = Paths.get("resources", "back.csv");
    public static final String url = "http://localhost:8078";

    private Managers() {
    }

    public static HTTPTaskManager getNewHTTPTaskManager() {
        return new HTTPTaskManager(url);
    }

    public static FileBackedTaskManager getNewFileBackedTaskManager() {
        return new FileBackedTaskManager(path);
    }

    public static InMemoryTaskManager getNewInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getDefault() {
        return getNewHTTPTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager(10, false);
    }

    public static UserManager getDefaultUserManager() {
        return new InMemoryUserManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }

}

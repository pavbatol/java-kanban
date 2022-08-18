package api.GsonAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import managers.HistoryManager;
import tasks.Task;

import java.io.IOException;


public class HistoryManagerAdapter extends TypeAdapter<HistoryManager> {
    @Override
    public void write(JsonWriter jsonWriter, HistoryManager mn) throws IOException {

        jsonWriter.beginObject();
        jsonWriter.name("history");
        jsonWriter.beginArray();
        for (Task task : mn.getHistory()) {
            jsonWriter.value(task.getId());
        }
        jsonWriter.endArray();
        jsonWriter.endObject();

    }

    @Override
    public HistoryManager read(JsonReader jsonReader) {
        return null;
    }
}
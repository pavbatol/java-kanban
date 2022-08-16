package api.GsonAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import managers.InMemoryHistoryManager;
import tasks.Task;

import java.io.IOException;


public class InMemoryHistoryManagerAdapter extends TypeAdapter<InMemoryHistoryManager> {
    @Override
    public void write(JsonWriter jsonWriter, InMemoryHistoryManager mn) throws IOException {

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
    public InMemoryHistoryManager read(JsonReader jsonReader) throws IOException {


        return null;
    }
}
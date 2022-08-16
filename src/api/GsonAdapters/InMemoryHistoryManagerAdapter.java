package api.GsonAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import managers.InMemoryHistoryManager;

import java.io.IOException;

public class InMemoryHistoryManagerAdapter extends TypeAdapter<InMemoryHistoryManager> {
    @Override
    public void write(JsonWriter jsonWriter, InMemoryHistoryManager mn) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("sizeMax").value(mn.getSizeMax());
        jsonWriter.name("isNormalOrder").value(mn.isNormalOrder());
        jsonWriter.name("lastViewedTasks").value(mn.getLastViewedTasks().toString());
        jsonWriter.endObject();
    }

    @Override
    public InMemoryHistoryManager read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
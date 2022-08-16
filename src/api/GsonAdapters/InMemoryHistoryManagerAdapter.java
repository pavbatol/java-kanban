package api.GsonAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import managers.InMemoryHistoryManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.join;


public class InMemoryHistoryManagerAdapter extends TypeAdapter<InMemoryHistoryManager> {
    @Override
    public void write(JsonWriter jsonWriter, InMemoryHistoryManager mn) throws IOException {
        /*jsonWriter.value(mn.lastViewedTasksToJson());*/
        jsonWriter.beginObject();
        jsonWriter.name("sizeMax").value(mn.getSizeMax());
        jsonWriter.name("isNormalOrder").value(mn.isNormalOrder());

        List<String> ids = mn.getHistory().stream()
                        .map(Task::getId)
                        .map(i -> Integer.toString(i))
                                .collect(Collectors.toList());

        jsonWriter.name("lastViewedTasks").beginArray().value( join(",", ids)  ).endArray();

        jsonWriter.endObject();
    }

    @Override
    public InMemoryHistoryManager read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
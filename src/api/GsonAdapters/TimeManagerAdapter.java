package api.GsonAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import managers.TimeManager;

import java.io.IOException;
import java.util.Map;

public class TimeManagerAdapter extends TypeAdapter<TimeManager> {
    @Override
    public void write(JsonWriter jsonWriter, TimeManager tm) throws IOException {

        jsonWriter.beginObject();
        jsonWriter.name("timeMarks");
        jsonWriter.beginObject();
        for (Map.Entry<String, Integer> entry : tm.getTimeMarksInt().entrySet()) {
            jsonWriter.name(entry.getKey());
            jsonWriter.value(entry.getValue());
        }
        jsonWriter.endObject();
        jsonWriter.endObject();
    }

    @Override
    public TimeManager read(JsonReader jsonReader) {
        return null;
    }
}

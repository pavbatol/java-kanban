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
        //Вложенный массив в корень
//        jsonWriter.beginObject();
//        jsonWriter.name("timeMarks");
//        jsonWriter.beginArray();
//        for (Map.Entry<String, Integer> entry : tm.getTimeMarksInt().entrySet()) {
//            jsonWriter.beginObject();
//            jsonWriter.name(entry.getKey());
//            jsonWriter.value(entry.getValue());
//            jsonWriter.endObject();
//        }
//        jsonWriter.endArray();
//        jsonWriter.endObject();


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
    public TimeManager read(JsonReader jsonReader) throws IOException {
//        int timeStep = 15;
//        TimeManager manager = new TimeManager(timeStep);
//        return manager;
        return null;
    }
}

package api.GsonAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import managers.HistoryManager;
import managers.InMemoryHistoryManager;
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
    public HistoryManager read(JsonReader jsonReader) throws IOException {
//        System.out.println("Адапрен начал");
//
//        jsonReader.beginObject();
//        if (jsonReader.hasNext()) {
//            String token = jsonReader.nextName();
//            System.out.println(token);
//            if (token.equals("history")) {
//                System.out.println("history!!!");
//            } else {
//                jsonReader.skipValue();
//            }
//        }
//        jsonReader.endObject();
//        jsonReader.close();
//
//
//        System.out.println("Создаем InMemoryHistoryManager");
//        int sizeMax = 10;
//        boolean isNormalOrder = false;
//        HistoryManager hm = new InMemoryHistoryManager(sizeMax, isNormalOrder);
//        System.out.println("Создан InMemoryHistoryManager!");
//        return hm;
//        return new InMemoryHistoryManager(15, false);
        return null;
    }
}
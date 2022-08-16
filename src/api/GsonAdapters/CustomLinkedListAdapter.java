package api.GsonAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import managers.InMemoryHistoryManager;
import tasks.Task;

import java.io.IOException;

public class CustomLinkedListAdapter extends TypeAdapter<InMemoryHistoryManager.CustomLinkedList<Task>> {

    @Override
    public void write(JsonWriter jsonWriter, InMemoryHistoryManager.CustomLinkedList<Task> customLinkedList)
            throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("aaa").value("bbb");
        jsonWriter.endObject();
//        JsonWriter root = jsonWriter.beginObject();
//        root.name("size").value(customLinkedList.size());
//        root.name("sizeMax").value(customLinkedList.getSizeMax());
//        root.name("isNormalOrder").value(customLinkedList.isNormalOrder());


        //jsonWriter.beginObject();
        //jsonWriter.name("size").value(customLinkedList.size());
        //jsonWriter.name("sizeMax").value(customLinkedList.getSizeMax());
        //jsonWriter.name("isNormalOrder").value(customLinkedList.isNormalOrder());


//        String nodesMap = new Gson().toJson(customLinkedList.getNodes());
//        root.value(nodesMap);
        //root.name("nodes").value(customLinkedList.getNodes());

        //jsonWriter.endObject();
    }

    @Override
    public InMemoryHistoryManager.CustomLinkedList<Task> read(JsonReader jsonReader) throws IOException {
        return null;

//        PersonJson personJson = new PersonJson();
//        jsonReader.beginObject();
//        while (jsonReader.hasNext()) {
//            switch (jsonReader.nextName()) {
//                case "name":
//                    personJson.setName(jsonReader.nextString());
//                    break;
//                case "age":
//                    try {
//                        personJson.setAge(Integer.valueOf(jsonReader.nextString()));
//                    } catch (Exception e) {
//                    }
//                    break;
//                case "hobby":
//                    personJson.setHobby(jsonReader.nextString());
//            }
//        }
//        jsonReader.endObject();
//        return personJson;
    }
}

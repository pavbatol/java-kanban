package api.GsonAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.nio.file.Path;

public class PathAdapter extends TypeAdapter<Path> {
    @Override
    public void write(JsonWriter jsonWriter, Path path) throws IOException {
        jsonWriter.value(path == null ? null : path.toString());
    }

    @Override
    public Path read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
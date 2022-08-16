package api.GsonAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private final DateTimeFormatter formatterWriter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final DateTimeFormatter formatterReader = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        jsonWriter.value(localDateTime == null ? null : localDateTime.format(formatterWriter));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        return  jsonReader == null ? null
                : LocalDateTime.parse(jsonReader.nextString(), formatterReader);
    }
}
package com.datdeveloper.datfactions.database.jsonAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.world.level.ChunkPos;

import java.io.IOException;

public class ChunkPosAdapter extends TypeAdapter<ChunkPos> {
    @Override
    public void write(final JsonWriter jsonWriter, final ChunkPos chunkPos) throws IOException {
        if (chunkPos == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.beginObject();
            jsonWriter.name("x").value(chunkPos.x);
            jsonWriter.name("z").value(chunkPos.z);
            jsonWriter.endObject();
        }
    }

    @Override
    public ChunkPos read(final JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }

        Integer x = null;
        Integer z = null;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            switch (jsonReader.nextName()) {
                case "x" -> x = jsonReader.nextInt();
                case "z" -> z = jsonReader.nextInt();
            }
        }
        jsonReader.endObject();

        if (x == null || z == null) return null;

        return new ChunkPos(x, z);
    }
}

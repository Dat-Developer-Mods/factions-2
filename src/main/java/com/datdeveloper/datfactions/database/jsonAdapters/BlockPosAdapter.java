package com.datdeveloper.datfactions.database.jsonAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.core.BlockPos;

import java.io.IOException;

public class BlockPosAdapter extends TypeAdapter<BlockPos> {
    @Override
    public void write(final JsonWriter jsonWriter, final BlockPos blockPos) throws IOException {
        if (blockPos == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.beginObject();
            jsonWriter.name("x").value(blockPos.getX());
            jsonWriter.name("y").value(blockPos.getY());
            jsonWriter.name("z").value(blockPos.getZ());
            jsonWriter.endObject();
        }
    }

    @Override
    public BlockPos read(final JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }

        Integer x = null;
        Integer y = null;
        Integer z = null;

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            switch (jsonReader.nextName()) {
                case "x" -> x = jsonReader.nextInt();
                case "y" -> y = jsonReader.nextInt();
                case "z" -> z = jsonReader.nextInt();
            }
        }
        jsonReader.endObject();

        if (x == null || y == null || z == null) return null;

        return new BlockPos(x, y, z);
    }
}

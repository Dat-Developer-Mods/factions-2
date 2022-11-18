package com.datdeveloper.datfactions.database.jsonAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.core.BlockPos;

import java.io.IOException;

public class BlockPosAdapter extends TypeAdapter<BlockPos> {
    @Override
    public void write(JsonWriter jsonWriter, BlockPos blockPos) throws IOException {
        if (blockPos == null) {
            jsonWriter.name("blockPos");
            jsonWriter.nullValue();
        } else {
            jsonWriter
                    .beginObject()
                    .name("x")
                    .value(blockPos.getX())
                    .name("y")
                    .value(blockPos.getY())
                    .name("z")
                    .value(blockPos.getZ())
                    .endObject();
        }
    }

    @Override
    public BlockPos read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            return null;
        }
        return new BlockPos(jsonReader.nextInt(), jsonReader.nextInt(), jsonReader.nextInt());
    }
}

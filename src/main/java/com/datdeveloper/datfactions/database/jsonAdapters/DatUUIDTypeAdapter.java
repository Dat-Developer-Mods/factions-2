package com.datdeveloper.datfactions.database.jsonAdapters;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mojang.util.UUIDTypeAdapter;

import java.io.IOException;
import java.util.UUID;

public class DatUUIDTypeAdapter extends UUIDTypeAdapter {
    @Override
    public void write(final JsonWriter out, final UUID value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        super.write(out, value);
    }

    @Override
    public UUID read(final JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return super.read(in);
    }
}

package com.datdeveloper.datfactions.database.jsonAdapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class ResourceKeyAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
        if (!ResourceKey.class.isAssignableFrom(typeToken.getRawType())) return null;

        //noinspection unchecked
        return (TypeAdapter<T>) new ResourceKeyAdapter();
    }

    public static class ResourceKeyAdapter extends TypeAdapter<ResourceKey<?>> {
        @Override
        public void write(final JsonWriter jsonWriter, final ResourceKey resourceKey) throws IOException {
            if (resourceKey == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.beginObject();
                jsonWriter.name("registry").value(resourceKey.registry().toString());
                jsonWriter.name("location").value(resourceKey.location().toString());
                jsonWriter.endObject();
            }
        }

        @Override
        public ResourceKey<?> read(final JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }

            String registry = null;
            String location = null;

            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case "registry" -> registry = jsonReader.nextString();
                    case "location" -> location = jsonReader.nextString();
                }
            }
            jsonReader.endObject();

            if (registry == null || location == null) return null;

            return ResourceKey.create(ResourceKey.createRegistryKey(new ResourceLocation(registry)), new ResourceLocation(location));
        }
    }

}

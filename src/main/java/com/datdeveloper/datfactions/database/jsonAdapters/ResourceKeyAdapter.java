package com.datdeveloper.datfactions.database.jsonAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class ResourceKeyAdapter extends TypeAdapter<ResourceKey<?>> {
    @Override
    public void write(JsonWriter jsonWriter, ResourceKey resourceKey) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("registry")
                .value(resourceKey.registry().toString());
        jsonWriter.name("location")
                .value(resourceKey.location().toString());
    }

    @Override
    public ResourceKey<?> read(JsonReader jsonReader) throws IOException {
        return ResourceKey.create(ResourceKey.createRegistryKey(new ResourceLocation(jsonReader.nextString())), new ResourceLocation(jsonReader.nextString()));
    }
}

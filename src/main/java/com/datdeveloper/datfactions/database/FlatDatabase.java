package com.datdeveloper.datfactions.database;

import com.datdeveloper.datfactions.database.jsonAdapters.BlockPosAdapter;
import com.datdeveloper.datfactions.database.jsonAdapters.ResourceKeyAdapter;
import com.datdeveloper.datfactions.factionData.Faction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;

import java.lang.reflect.Modifier;
import java.util.UUID;

public class FlatDatabase {
    Gson gson = buildGson();

    private Gson buildGson() {
        return new GsonBuilder().setPrettyPrinting()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .serializeNulls()
                .enableComplexMapKeySerialization()

                .registerTypeAdapter(BlockPos.class, new BlockPosAdapter())
                .registerTypeAdapter(ResourceKey.class, new ResourceKeyAdapter())
                .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
                .create();
    }

    public void saveFaction(Faction faction) {
        System.out.println(gson.toJson(faction));
    }
}

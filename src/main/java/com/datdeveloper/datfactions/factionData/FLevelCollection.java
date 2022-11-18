package com.datdeveloper.datfactions.factionData;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class FLevelCollection {
    Map<ResourceKey<Level>, FactionLevel> levels;
    private static final FLevelCollection instance = new FLevelCollection();

    FLevelCollection() {
        levels = new HashMap<>();
    }

    public static FLevelCollection getInstance() {
        return instance;
    }

    public FactionLevel getFactionLevel(ResourceKey<Level> levelKey) {
        return levels.get(levelKey);
    }

    public Map<ResourceKey<Level>, FactionLevel> getLevels() {
        return this.levels;
    }
}

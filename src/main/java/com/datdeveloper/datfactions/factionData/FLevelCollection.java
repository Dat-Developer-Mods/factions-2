package com.datdeveloper.datfactions.factionData;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class FLevelCollection extends BaseCollection<ResourceKey<Level>, FactionLevel>{
    private static final FLevelCollection instance = new FLevelCollection();

    public static FLevelCollection getInstance() {
        return instance;
    }

    @Override
    void initialise() {

    }
}

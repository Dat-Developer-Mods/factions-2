package com.datdeveloper.datfactions.factionData;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class FLevelCollection extends BaseCollection<ResourceKey<Level>, FactionLevel>{
    private static final FLevelCollection instance = new FLevelCollection();

    /**
     * The default settings levels use when they haven't modified their own settings
     */
    public FactionLevelSettings defaultSettings;

    public static FLevelCollection getInstance() {
        return instance;
    }

    @Override
    void initialise() {

    }
}

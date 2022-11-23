package com.datdeveloper.datfactions.factionData;

import com.datdeveloper.datfactions.database.Database;
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

    public FactionLevel loadOrCreate(final ResourceKey<Level> levelId) {
        FactionLevel level = Database.instance.loadLevel(levelId);

        if (level == null) {
            level = new FactionLevel(levelId, defaultSettings);
        }

        return map.put(levelId, level);
    }

    /* ========================================= */
    /* Setup and teardown
    /* ========================================= */

    @Override
    public void initialise() {
        defaultSettings = Database.instance.loadLevelDefaultSettings();
        if (defaultSettings == null) {
            defaultSettings = new FactionLevelSettings();
            Database.instance.storeDefaultSettings(defaultSettings);
        }
    }
}

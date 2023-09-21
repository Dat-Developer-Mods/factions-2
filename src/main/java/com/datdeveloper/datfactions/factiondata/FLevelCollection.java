package com.datdeveloper.datfactions.factiondata;

import com.datdeveloper.datfactions.database.Database;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.datdeveloper.datfactions.Datfactions.logger;


public class FLevelCollection extends BaseCollection<ResourceKey<Level>, FactionLevel>{
    private static final FLevelCollection instance = new FLevelCollection();

    /**
     * The default settings levels use when they haven't modified their own settings
     */
    public FactionLevelSettings defaultSettings;

    public static FLevelCollection getInstance() {
        return instance;
    }

    /**
     * Load a level from the database or create a new one
     * @param levelId the Level Key of the level to load
     * @return the level
     */
    public FactionLevel loadOrCreate(final ResourceKey<Level> levelId) {
        FactionLevel level = Database.instance.loadLevel(levelId);

        if (level == null) {
            level = new FactionLevel(levelId);
            Database.instance.storeLevel(level);
            logger.info("Created new FactionLevel for " + level.getName());
        }
        map.put(levelId, level);
        return level;
    }

    public FactionLevel removeLevel(final ResourceKey<Level> levelId) {
        final FactionLevel level = map.remove(levelId);
        Database.instance.deleteLevel(level);
        return level;
    }

    /**
     * Get a list of the given faction's chunks per level
     * @param faction The faction to get the chunks of
     * @return A map of levels to chunks the faction owns
     */
    public Map<FactionLevel, List<ChunkPos>> getAllFactionChunks(final Faction faction) {
        final HashMap<FactionLevel, List<ChunkPos>> levelChunks = new HashMap<>();
        for (final FactionLevel level : getAll().values()) {
            final List<ChunkPos> chunks = level.getFactionChunks(faction);
            if (!chunks.isEmpty()) {
                levelChunks.put(level, chunks);
            }
        }
        return levelChunks;
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

package com.datdeveloper.datfactions.events;

import com.datdeveloper.datfactions.Datfactions;
import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.database.Database;
import com.datdeveloper.datfactions.database.FlatFileDatabase;
import com.datdeveloper.datfactions.factiondata.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.datdeveloper.datfactions.Datfactions.logger;

/**
 * Events for dealing with faction, player, and level data
 */
@Mod.EventBusSubscriber(modid = Datfactions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DataEvents {
    private DataEvents() {
        throw new IllegalStateException("Utility class");
    }

    /* ========================================= */
    /* Startup and shutdown
    /* ========================================= */

    /**
     * Load faction data for world
     */
    @SubscribeEvent
    public static void serverStart(final ServerAboutToStartEvent event) {
        logger.info("Factions loading factions and players");
        final Path worldPath = event.getServer().getWorldPath(new LevelResource("datfactions"));
        Database.instance = new FlatFileDatabase(worldPath);
        FactionCollection.getInstance().initialise();
        FPlayerCollection.getInstance().initialise();
        FLevelCollection.getInstance().initialise();
        FactionIndex.getInstance().initialise();

        // Validate empty factions
        if (FactionsConfig.getValidateEmptyFactions() != FactionsConfig.EValidationType.IGNORE) {
            final List<Faction> emptyFactions = FactionCollection.getInstance().getAll().values().stream()
                    .filter(faction -> faction.getPlayers().isEmpty() && !faction.hasFlag(EFactionFlags.PERMANENT))
                    .toList();

            if (!emptyFactions.isEmpty()) {
                if (FactionsConfig.getValidateEmptyFactions() == FactionsConfig.EValidationType.REMOVE) {
                    emptyFactions.forEach(faction -> FactionCollection.getInstance().disbandFaction(faction.getId()));

                    logger.warn("Found and removed " + emptyFactions.size() + " empty non-permanent factions");
                } else {
                    logger.warn("Found " + emptyFactions.size() + " empty non-permanent factions, if these were not manually created then they probably need removing");
                }
            }
        }
    }

    /**
     * Unload faction stuff
     */
    @SubscribeEvent
    public static void onServerStopping(final ServerStoppedEvent event) {
        logger.info("Factions system unloading");
        FactionIndex.getInstance().uninitialise();
        FLevelCollection.getInstance().uninitialise();
        FPlayerCollection.getInstance().uninitialise();
        FactionCollection.getInstance().uninitialise();
        Database.instance.close();
        Database.instance = null;
    }


    /* ========================================= */
    /* Saving
    /* ========================================= */

    /**
     * Save factions at the same time the world is saved
     */
    @SubscribeEvent
    public static void levelSave(final LevelEvent.Save event) {
        // This fires for all levels, we just want to save once, so only run on the overworld
        if (!Level.OVERWORLD.equals(((ServerLevel) event.getLevel()).dimension())) return;

        logger.info("Saving faction data");
        FactionCollection.getInstance().saveDirty();
        FPlayerCollection.getInstance().saveDirty();
        FLevelCollection.getInstance().saveDirty();
    }

    /* ========================================= */
    /* Level
    /* ========================================= */

    /**
     * Create new level
     * Fired when a level is loaded during startup<br>
     * Or when a new level is created like by infiniverse or rftools
     */
    @SubscribeEvent
    public static void levelLoad(final LevelEvent.Load event) {
        if (event.getLevel().isClientSide()) return;

        final ResourceKey<Level> levelId = ((ServerLevel) event.getLevel()).dimension();
        logger.info("Factions loading level " + levelId);
        final FactionLevel level = FLevelCollection.getInstance().loadOrCreate(levelId);

        // Validate Land ownership
        if (FactionsConfig.getValidateLandOwnership() != FactionsConfig.EValidationType.IGNORE) {
            final List<ChunkPos> orphans = level.getClaims().entrySet().stream()
                    .filter(entry -> FactionCollection.getInstance().getByKey(entry.getValue().getFactionId()) == null)
                    .map(Map.Entry::getKey)
                    .toList();

            if (!orphans.isEmpty()) {
                if (FactionsConfig.getValidateLandOwnership() == FactionsConfig.EValidationType.REMOVE) {
                    final Set<ChunkPos> claims = level.getClaims().keySet();
                    orphans.forEach(claims::remove);
                    level.markDirty();

                    logger.warn("Found and removed " + orphans.size() + " orphaned claims in " + level.getName());
                } else {
                    logger.warn("Found " + orphans.size() + " orphaned claims in " + level.getName() + ". These will cause crashes unless removed");
                }
            }
        }
    }

    /* ========================================= */
    /* Player
    /* ========================================= */

    /**
     * Register a player if they're new
     * High priority to catch it before we handle the player again later in {@link PlayerEvents}
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void playerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
        final ServerPlayer player = (ServerPlayer) event.getEntity();
        final FPlayerCollection playerCollection = FPlayerCollection.getInstance();

        if (playerCollection.isPlayerRegistered(player)) return;

        playerCollection.registerNewPlayer(player);
    }

    /**
     * Remove a player from the database system if they're banned and the config says to
     */
    @SubscribeEvent
    public static void playerLeave(final PlayerEvent.PlayerLoggedOutEvent event) {
        if (!FactionsConfig.getRemovePlayerOnBan()) return;

        final MinecraftServer server = event.getEntity().getServer();
        final ServerPlayer player = (ServerPlayer) event.getEntity();

        if (!server.getPlayerList().getBans().isBanned(player.getGameProfile())) return;

        FPlayerCollection.getInstance().deregisterPlayer(player.getUUID());
    }
}

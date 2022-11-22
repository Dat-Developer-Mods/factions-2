package com.datdeveloper.datfactions.events;

import com.datdeveloper.datfactions.Datfactions;
import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.database.Database;
import com.datdeveloper.datfactions.database.FlatFileDatabase;
import com.datdeveloper.datfactions.factionData.FLevelCollection;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.FactionCollection;
import com.datdeveloper.datfactions.factionData.FactionIndex;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.nio.file.Path;

/**
 * Events for dealing with faction, player, and level data
 */
@Mod.EventBusSubscriber(modid = Datfactions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DataEvents {
    private static final Logger logger = LogUtils.getLogger();

    /* ========================================= */
    /* Startup and shutdown
    /* ========================================= */

    /**
     * Load faction data for world
     */
    @SubscribeEvent
    public static void serverStart(ServerAboutToStartEvent event) {
        logger.info("Factions loading factions and players");
        Path worldPath = event.getServer().getWorldPath(new LevelResource("datfactions"));
        Database.instance = new FlatFileDatabase(worldPath);
        FactionCollection.getInstance().initialise();
        FPlayerCollection.getInstance().initialise();
        FLevelCollection.getInstance().initialise();
        FactionIndex.getInstance().initialise();
    }

    /**
     * Unload faction stuff
     */
    @SubscribeEvent
    public static void onServerStopping(ServerStoppedEvent event) {
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
    public static void LevelSave(LevelEvent.Save event) {
        // This fires for all levels, we just want to save once, so only run on the overworld
        if (!ServerLevel.OVERWORLD.equals(((ServerLevel) event.getLevel()).dimension())) return;

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
    public static void worldLoad(LevelEvent.Load event) {
        if (event.getLevel().isClientSide()) return;

        ResourceKey<Level> levelId = ((ServerLevel) event.getLevel()).dimension();
        logger.info("Factions loading level " + levelId);
        FLevelCollection.getInstance().loadOrCreate(levelId);
    }

    /* ========================================= */
    /* Player
    /* ========================================= */

    /**
     * Register a player if they're new
     * High priority to catch it before we handle the player again later in {@link PlayerEvents}
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        FPlayerCollection playerCollection = FPlayerCollection.getInstance();

        if (playerCollection.isPlayerRegistered(player)) return;

        playerCollection.registerNewPlayer(player);
    }

    /**
     * Remove a player from the database system if they're banned and the config says to
     */
    @SubscribeEvent
    public static void playerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!FactionsConfig.getRemovePlayerOnBan()) return;

        MinecraftServer server = event.getEntity().getServer();
        ServerPlayer player = (ServerPlayer) event.getEntity();

        if (!server.getPlayerList().getBans().isBanned(player.getGameProfile())) return;

        FPlayerCollection.getInstance().deregisterPlayer(player.getUUID());
        // Handle Banned
    }
}

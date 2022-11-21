package com.datdeveloper.datfactions.events;

import com.datdeveloper.datfactions.Datfactions;
import com.datdeveloper.datfactions.database.Database;
import com.datdeveloper.datfactions.database.FlatDatabase;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.FactionCollection;
import com.datdeveloper.datfactions.factionData.FactionIndex;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = Datfactions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LoadEvents {
    /**
     * Load faction data for world
     */
    @SubscribeEvent
    public static void serverStart(ServerAboutToStartEvent event) {
        Path worldPath = event.getServer().getWorldPath(new LevelResource("datfactions"));
        Database.instance = new FlatDatabase(worldPath);
        FactionCollection.getInstance().initialise();
        FPlayerCollection.getInstance().initialise();
        FactionIndex.getInstance().iniitialise();
    }

    /**
     * Unload faction stuff
     */
    @SubscribeEvent
    public static void onServerStopping(ServerStoppedEvent event) {
        Database.instance = null;
    }

    /**
     * Create new level
     * Fired when a new level is created
     * Like by infiniverse or rftools
     */
    @SubscribeEvent
    public static void worldLoad(LevelEvent.Load event) {
        if (event.getLevel().isClientSide()) return;
        System.out.println("Test");
        // TODO: Create a new level
    }

    /**
     * Save factions at the same time the world is saved
     */
    @SubscribeEvent
    public static void LevelSave(LevelEvent.Save event) {
        // This fires for all levels, we just want to save once, so only run on the overworld
        if (!ServerLevel.OVERWORLD.equals(((ServerLevel) event.getLevel()).dimension())) return;
        // TODO:
        // Save levels

        // Save Factions

        // Save Players
    }
}

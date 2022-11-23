package com.datdeveloper.datfactions.events;

import com.datdeveloper.datfactions.Datfactions;
import commoble.infiniverse.api.UnregisterDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Datfactions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InfiniverseEvents {

    /**
     * Delete level data
     * Fired when a level is deleted
     */
    @SubscribeEvent
    public static void levelDeleted(final UnregisterDimensionEvent event) {
        System.out.println("Deleted " + event.getLevel().getLevel().dimension().location().getPath());

        // TODO: Delete level
    }
}

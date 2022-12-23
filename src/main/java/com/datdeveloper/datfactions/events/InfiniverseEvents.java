package com.datdeveloper.datfactions.events;

import com.datdeveloper.datfactions.Datfactions;
import com.datdeveloper.datfactions.factionData.FLevelCollection;
import commoble.infiniverse.api.UnregisterDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Events fired by the Infiniverse mod
 */
@Mod.EventBusSubscriber(modid = Datfactions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InfiniverseEvents {

    /**
     * Delete level data
     * Fired when a level is deleted
     */
    @SubscribeEvent
    public static void levelDeleted(final UnregisterDimensionEvent event) {
        System.out.println("Deleted " + event.getLevel().getLevel().dimension().location().getPath());

        FLevelCollection.getInstance().removeLevel(event.getLevel().dimension());
    }
}

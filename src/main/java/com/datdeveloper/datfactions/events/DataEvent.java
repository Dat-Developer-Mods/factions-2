package com.datdeveloper.datfactions.events;

import com.datdeveloper.datfactions.Datfactions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = Datfactions.MODID)
public class DataEvent {
    @SubscribeEvent
    public void playerJoin(final PlayerEvent.PlayerLoggedInEvent event) {

    }
}

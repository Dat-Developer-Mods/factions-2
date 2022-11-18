package com.datdeveloper.datfactions.events;

import com.datdeveloper.datfactions.Datfactions;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = Datfactions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents {
    private static final Logger logger = LogUtils.getLogger();
    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) {
            return;
        }

        ServerPlayer player = (ServerPlayer) event.getEntity();
        FPlayerCollection playerCollection = FPlayerCollection.getInstance();

        if (playerCollection.isPlayerRegistered(player)) return;

        playerCollection.registerNewPlayer(player);
    }

    @SubscribeEvent
    public static void playerLeave(PlayerEvent.PlayerLoggedOutEvent event) {

    }

    @SubscribeEvent
    public static void playerDamaged(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof ServerPlayer)) return;

        ServerPlayer target = (ServerPlayer) event.getEntity();
        ServerPlayer source = (ServerPlayer) event.getSource().getEntity();
    }

    @SubscribeEvent
    public static void playerKilled(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof ServerPlayer)) return;

        ServerPlayer target = (ServerPlayer) event.getEntity();
        ServerPlayer source = (ServerPlayer) event.getSource().getEntity();
    }

    @SubscribeEvent
    public static void enterChunk(EntityEvent.EnteringSection event) {
        if (!(event.getEntity() instanceof ServerPlayer)) return;
        ServerPlayer player = (ServerPlayer) event.getEntity();
        if (!event.didChunkChange()) return;


    }

    @SubscribeEvent
    public static void playerChat(ServerChatEvent.Submitted event) {
        logger.info(event.getMessage().toString());
    }
}

package com.datdeveloper.datfactions.events;

import com.datdeveloper.datfactions.Datfactions;
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

    /**
     * Register power gain event
     */
    @SubscribeEvent
    public static void playerJoin(final PlayerEvent.PlayerLoggedInEvent event) {

    }

    /**
     * Force save player data, deregister power gain event
     */
    @SubscribeEvent
    public static void playerLeave(final PlayerEvent.PlayerLoggedOutEvent event) {

    }

    /**
     * Block damage if the player shares a faction or alliance
     */
    @SubscribeEvent
    public static void playerDamaged(final LivingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer target)) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof ServerPlayer source)) return;

    }

    /**
     * Remove Power
     */
    @SubscribeEvent
    public static void playerKilled(final LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer target)) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof ServerPlayer source)) return;

    }

    /**
     * Send enter border message
     */
    @SubscribeEvent
    public static void enterChunk(final EntityEvent.EnteringSection event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!event.didChunkChange()) {
        }


    }

    /**
     * Handle faction chat
     */
    @SubscribeEvent
    public static void playerChat(final ServerChatEvent.Submitted event) {
        logger.info(event.getMessage().toString());
    }
}

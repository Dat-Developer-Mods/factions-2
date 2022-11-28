package com.datdeveloper.datfactions.events;

import com.datdeveloper.datfactions.Datfactions;
import com.datdeveloper.datfactions.factionData.*;
import com.datdeveloper.datfactions.util.RelationUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.Objects;

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
        if (!event.didChunkChange()) return;
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);

        if (fPlayer.getChunkAlertMode() == EFPlayerChunkAlertMode.DISABLED) return;

        final FactionLevel level = FLevelCollection.getInstance().getByKey(player.getLevel().dimension());
        final Faction lastChunkOwner = level.getChunkOwningFaction(event.getOldPos().chunk());
        final Faction nextChunkOwner = level.getChunkOwningFaction(event.getNewPos().chunk());

        if (Objects.equals(lastChunkOwner, nextChunkOwner)) return;

        final MutableComponent title = nextChunkOwner.getNameWithDescription(fPlayer.getFaction())
                                .withStyle(RelationUtil.getRelation(fPlayer.getFaction(), nextChunkOwner).formatting);

        final MutableComponent description = Component.literal(nextChunkOwner.getDescription()).withStyle(ChatFormatting.WHITE);
        switch (fPlayer.getChunkAlertMode()) {
            case TITLE, ACTIONBAR -> {
                player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 40, 10));
                if (fPlayer.getChunkAlertMode() == EFPlayerChunkAlertMode.TITLE) {
                    player.connection.send(new ClientboundSetSubtitleTextPacket(description));
                    player.connection.send(new ClientboundSetTitleTextPacket(title));
                } else {
                    player.connection.send(new ClientboundSetActionBarTextPacket(title));
                }
            }
            case CHAT -> player.sendSystemMessage(
                    title.append("\n").append(description)
            );
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

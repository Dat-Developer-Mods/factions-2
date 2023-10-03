package com.datdeveloper.datfactions.delayedEvents;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionPlayerHomeEvent;
import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datmoddingapi.delayedEvents.TimeDelayedEvent;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

/**
 * A delayed event to teleport the player to their faction home after a short delay
 */
public class FactionHomeTeleportDelayedEvent extends TimeDelayedEvent {
    /**
     * The position the player will teleport to
     */
    public BlockPos destinationPos;

    /**
     * The level the player will teleport to
     */
    public ResourceKey<Level> destinationWorld;

    /**
     * The Player being teleported
     */
    public ServerPlayer player;

    /**
     * The starting position of the player
     * Used to calculate if the event should cancel for the player moving
     */
    public BlockPos startingPos;

    public FactionHomeTeleportDelayedEvent(final ServerPlayer player, final Faction faction) {
        super(FactionsConfig.getTeleportDelay());
        this.destinationPos = faction.getHomeLocation();
        this.destinationWorld = faction.getHomeLevel();
        this.player = player;

        this.startingPos = player.getOnPos();
    }

    @Override
    public void execute() {
        @SuppressWarnings("ConstantConditions") final ServerLevel level = player.getServer().getLevel(destinationWorld);
        if (level == null) {
            player.sendSystemMessage(Component.literal(ChatFormatting.RED + "Failed to find faction home level"));
            return;
        }

        final ResourceKey<Level> oldPlayerLevel = player.level().dimension();

        player.teleportTo(level, (double) destinationPos.getX() + 0.5f, (double) destinationPos.getY() + 0.5f, (double) destinationPos.getZ() + 0.5f, player.getXRot(), player.getYRot());
        MinecraftForge.EVENT_BUS.post(new FactionPlayerHomeEvent.Post(FPlayerCollection.getInstance().getPlayer(player), startingPos, oldPlayerLevel));
    }

    @Override
    public boolean shouldRequeue(final boolean hasFinished) {
        if (!hasFinished && startingPos.distToCenterSqr(player.position()) > 1) {
            player.sendSystemMessage(Component.literal(DatChatFormatting.TextColour.ERROR + "Teleport cancelled"));
            return false;
        }

        return super.shouldRequeue(hasFinished);
    }
}

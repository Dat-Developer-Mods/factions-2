package com.datdeveloper.datfactions.delayedEvents;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionPlayerPowerChangeEvent;
import com.datdeveloper.datfactions.factionData.*;
import com.datdeveloper.datmoddingapi.delayedEvents.TimeDelayedEvent;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A delayed event to passively increase a player's power as they play on the server
 */
public class PowerDelayedEvent extends TimeDelayedEvent {
    final FactionPlayer fPlayer;

    public PowerDelayedEvent(final FactionPlayer fPlayer) {
        super(FactionsConfig.getPlayerPassivePowerGainInterval());
        this.fPlayer = fPlayer;
    }

    @Override
    public void execute() {
        try {
            final ServerPlayer player = fPlayer.getServerPlayer();
            final Faction faction = fPlayer.getFaction();

            final int basePower = FactionsConfig.getPlayerPassivePowerGainAmount();
            final int baseMaxPower = FactionsConfig.getPlayerPassiveMaxPowerGainAmount();
            final Map<String, Float> multipliers = new HashMap<>();

            // Level multipliers
            {
                final FactionLevel level = FLevelCollection.getInstance().getByKey(player.getLevel().dimension());
                multipliers.put("Level", level.getSettings().getPassivePowerGainMultiplier());


                final Faction chunkOwner = level.getChunkOwningFaction(new ChunkPos(player.getOnPos()));
                if (chunkOwner.hasFlag(EFactionFlags.BONUSPOWER)) {
                    multipliers.put("Bonus", FactionsConfig.getBonusPowerFlagDeathMultiplier());
                }
            }

            // Faction multipliers
            if (fPlayer.hasFaction()) {
                final float roleAlpha = (faction.getRoleIndex(fPlayer.getRoleId()) / (float) (faction.getRoles().size() - 1));
                multipliers.put("Role (" + fPlayer.getRole().getName() + ")", ((1 - roleAlpha) * FactionsConfig.getPassiveMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.OWNER) + roleAlpha * FactionsConfig.getPassiveMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.RECRUIT)));
            } else {
                multipliers.put("No Faction", FactionsConfig.getPassiveMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.NOFACTION));
            }

            final FactionPlayerPowerChangeEvent.PreFactionPlayerPowerChangeEvent preEvent = new FactionPlayerPowerChangeEvent.PreFactionPlayerPowerChangeEvent(null, fPlayer, null, basePower, baseMaxPower, multipliers, FactionPlayerPowerChangeEvent.EPowerChangeReason.PASSIVE);
            MinecraftForge.EVENT_BUS.post(preEvent);
            if (preEvent.isCanceled()) return;

            final int maxPowerDelta = fPlayer.addMaxPower(preEvent.getFinalPowerChange());
            final int powerDelta = fPlayer.addPower(preEvent.getFinalMaxPowerChange());
            if (powerDelta == 0 && maxPowerDelta == 0) return;

            MinecraftForge.EVENT_BUS.post(
                    new FactionPlayerPowerChangeEvent.PostFactionPlayerPowerChangeEvent(null, fPlayer, null, preEvent.getBasePowerChange(), preEvent.getBaseMaxPowerChange(), multipliers, FactionPlayerPowerChangeEvent.EPowerChangeReason.PASSIVE)
            );

            final MutableComponent message = Component.literal(DatChatFormatting.TextColour.INFO + "You have gained ");

            final MutableComponent multiplierComponent = Component.literal("Modifiers:").append("\n")
                    .append(ComponentUtils.formatList(
                            multipliers.entrySet().stream()
                                    .map((entry ->
                                            Component.literal(DatChatFormatting.TextColour.INFO + "  %s: ".formatted(entry.getKey()))
                                                    .append(ChatFormatting.WHITE + "x%.2f".formatted(entry.getValue()))
                                    ))
                                    .collect(Collectors.toList()),
                            Component.literal("\n")
                    )
            );

            if (powerDelta != 0) {
                final MutableComponent summary = Component.empty()
                        .append(DatChatFormatting.TextColour.INFO + "Base: ").append(String.valueOf(basePower)).append("\n")
                        .append(multiplierComponent).append("\n")
                        .append(DatChatFormatting.TextColour.INFO + "Limit: ").append(String.valueOf(fPlayer.getMaxPower()));

                message.append(
                                Component.literal(ChatFormatting.DARK_PURPLE + String.valueOf(powerDelta))
                                        .withStyle(
                                                Style.EMPTY
                                                        .withHoverEvent(new HoverEvent(
                                                                HoverEvent.Action.SHOW_TEXT,
                                                                summary
                                                        ))
                                        )
                        )
                        .append(DatChatFormatting.TextColour.INFO + " power");
            }

            if (maxPowerDelta != 0) {
                if (powerDelta != 0) message.append(DatChatFormatting.TextColour.INFO + " and ");

                final MutableComponent summary = Component.empty()
                        .append(DatChatFormatting.TextColour.INFO + "Base: ").append(String.valueOf(baseMaxPower)).append("\n")
                        .append(multiplierComponent).append("\n")
                        .append(DatChatFormatting.TextColour.INFO + "Limit: ").append(String.valueOf(FactionsConfig.getPlayerMaxPower()));

                message.append(
                                Component.literal(ChatFormatting.DARK_PURPLE + String.valueOf(maxPowerDelta))
                                        .withStyle(
                                                Style.EMPTY
                                                        .withHoverEvent(new HoverEvent(
                                                                HoverEvent.Action.SHOW_TEXT,
                                                                summary
                                                        ))
                                        )
                        )
                        .append(DatChatFormatting.TextColour.INFO + " max power");
            }

            player.sendSystemMessage(message);
        } finally {
            this.exeTime = System.currentTimeMillis() + (((long) FactionsConfig.getPlayerPassivePowerGainInterval()) * 1000L);
        }
    }

    @Override
    public boolean canExecute() {
        return super.canExecute() && fPlayer.isPlayerOnline();
    }

    @Override
    public boolean shouldRequeue(final boolean hasFinished) {
        return fPlayer.isPlayerOnline();
    }
}

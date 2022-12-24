package com.datdeveloper.datfactions.delayedEvents;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.factionData.FLevelCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionLevel;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datmoddingapi.delayedEvents.TimeDelayedEvent;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

public class PowerDelayedEvent extends TimeDelayedEvent {
    final FactionPlayer fPlayer;

    public PowerDelayedEvent(final FactionPlayer fPlayer) {
        super(FactionsConfig.getPlayerPassivePowerGainInterval());
        this.fPlayer = fPlayer;
    }

    @Override
    public void execute() {
        try {
            if (!fPlayer.hasFaction()) return;

            final ServerPlayer player = fPlayer.getServerPlayer();
            final Faction faction = fPlayer.getFaction();
            final FactionLevel level = FLevelCollection.getInstance().getByKey(player.getLevel().dimension());

            final int basePower = FactionsConfig.getPlayerPassivePowerGainAmount();
            final int baseMaxPower = FactionsConfig.getPlayerPassiveMaxPowerGainAmount();
            final float levelMultiplier = level.getSettings().getPassivePowerGainMultiplier();
            final float roleMultiplier;
            {
                final float roleAlpha = (faction.getRoleIndex(fPlayer.getRoleId()) / (float) (faction.getRoles().size() - 1));
                roleMultiplier = ((1 - roleAlpha) * FactionsConfig.getOwnerRolePassivePowerGainMultiplier() + roleAlpha * FactionsConfig.getRecruitRolePassivePowerGainMultiplier());
            }

            final int maxPowerGain = (int) Math.floor(
                    baseMaxPower
                            * levelMultiplier
                            * roleMultiplier
            );
            final int powerGain = (int) Math.floor(
                    basePower
                            * levelMultiplier
                            * roleMultiplier
            );

            final int maxPowerDelta = fPlayer.addMaxPower(maxPowerGain);
            final int powerDelta = fPlayer.addPower(powerGain);
            if (!(powerDelta != 0 || maxPowerDelta != 0)) return;

            final MutableComponent message = Component.literal(DatChatFormatting.TextColour.INFO + "You have gained ");

            if (powerDelta != 0) {
                final MutableComponent summary = Component.empty()
                        .append(DatChatFormatting.TextColour.INFO + "Base: ").append(String.valueOf(basePower)).append("\n")
                        .append(DatChatFormatting.TextColour.INFO + "Level Modifier: ").append("x%.2f".formatted(levelMultiplier)).append("\n")
                        .append(DatChatFormatting.TextColour.INFO + "Role Modifier: ").append("x%.2f".formatted(roleMultiplier)).append("\n")
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
                        .append(DatChatFormatting.TextColour.INFO + "Level Modifier: ").append("x%.2f".formatted(levelMultiplier)).append("\n")
                        .append(DatChatFormatting.TextColour.INFO + "Role Modifier: ").append("x%.2f".formatted(roleMultiplier)).append("\n")
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

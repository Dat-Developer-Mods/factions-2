package com.datdeveloper.datfactions.util;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionPlayerPowerChangeEvent;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.Map;
import java.util.stream.Collectors;

public class PowerUtil {
    public static void handlePowerChange(final FactionPlayer fPlayer, final int basePower, final int baseMaxPower, final Map<String, Float> multipliers) {

        final FactionPlayerPowerChangeEvent.PreFactionPlayerPowerChangeEvent preEvent = new FactionPlayerPowerChangeEvent.PreFactionPlayerPowerChangeEvent(null, fPlayer, null, basePower, baseMaxPower, multipliers, FactionPlayerPowerChangeEvent.EPowerChangeReason.PASSIVE);
        MinecraftForge.EVENT_BUS.post(preEvent);
        if (preEvent.isCanceled()) return;

        final int maxPowerDelta = fPlayer.addMaxPower(preEvent.getFinalMaxPowerChange());
        final int powerDelta = fPlayer.addPower(preEvent.getFinalPowerChange());
        if (powerDelta == 0 && maxPowerDelta == 0) return;

        MinecraftForge.EVENT_BUS.post(
                new FactionPlayerPowerChangeEvent.PostFactionPlayerPowerChangeEvent(null, fPlayer, null, preEvent.getBasePowerChange(), preEvent.getBaseMaxPowerChange(), multipliers, FactionPlayerPowerChangeEvent.EPowerChangeReason.PASSIVE)
        );

        final ServerPlayer player = fPlayer.getServerPlayer();
        if (player != null) {
            final MutableComponent message = Component.literal(DatChatFormatting.TextColour.INFO + "You have " + (powerDelta > 0 ? "gained " : "lost "));

            final MutableComponent multiplierComponent = multipliers.isEmpty()
                    ? Component.empty()
                    : Component.literal(DatChatFormatting.TextColour.INFO + "Modifiers:").append("\n")
                            .append(ComponentUtils.formatList(
                                    multipliers.entrySet().stream()
                                            .filter(entry -> entry.getValue() != 1.f)
                                            .map((entry ->
                                                    Component.literal(DatChatFormatting.TextColour.INFO + "  %s: ".formatted(entry.getKey()))
                                                            .append(ChatFormatting.WHITE + "x%.2f".formatted(entry.getValue()))
                                            ))
                                            .collect(Collectors.toList()),
                                    Component.literal("\n")
                            )).append("\n");

            if (powerDelta != 0) {
                final MutableComponent summary = Component.empty()
                        .append(DatChatFormatting.TextColour.INFO + "Base: ").append(String.valueOf(basePower)).append("\n")
                        .append(multiplierComponent)
                        .append(DatChatFormatting.TextColour.INFO + "Limit: ").append(String.valueOf(fPlayer.getMaxPower()));

                message.append(
                                Component.literal(ChatFormatting.DARK_PURPLE + String.valueOf(Math.abs(powerDelta)))
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
                if (powerDelta > 0 != maxPowerDelta > 0) message.append(maxPowerDelta > 0 ? "gained " : "lost ");

                final MutableComponent summary = Component.empty()
                        .append(DatChatFormatting.TextColour.INFO + "Base: ").append(String.valueOf(baseMaxPower)).append("\n")
                        .append(multiplierComponent).append("\n")
                        .append(DatChatFormatting.TextColour.INFO + "Limit: ").append(String.valueOf(FactionsConfig.getPlayerMaxPower()));

                message.append(
                                Component.literal(ChatFormatting.DARK_PURPLE + String.valueOf(Math.abs(maxPowerDelta)))
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
        }
    }
}

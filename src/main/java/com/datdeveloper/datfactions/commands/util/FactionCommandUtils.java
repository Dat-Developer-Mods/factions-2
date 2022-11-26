package com.datdeveloper.datfactions.commands.util;

import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class FactionCommandUtils {
    public static Component wrapCommand(final String display, final String actualCommand) {
        return MutableComponent.create(Component.literal(display).getContents())
                .withStyle(DatChatFormatting.TextColour.COMMAND)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, actualCommand)));
    }
}

package com.datdeveloper.datfactions.commands.util;

import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class FactionCommandUtils {
    /**
     * Wrap a command with the command colour and a click event that suggests said command to the user
     * @see DatChatFormatting.TextColour
     * @param command The command
     * @return A component wrapping the command
     */
    public static MutableComponent wrapCommand(final String command) {
        return wrapCommand(command, command);
    }

    /**
     * Wrap a command with the command colour and a click event that suggests said command to the user
     * @see DatChatFormatting.TextColour
     * @param display The text to display
     * @param actualCommand The command to suggest to the user
     * @return A component wrapping the command
     */
    public static MutableComponent wrapCommand(final String display, final String actualCommand) {
        return MutableComponent.create(Component.literal(display).getContents())
                .withStyle(DatChatFormatting.TextColour.COMMAND)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, actualCommand)));
    }
}

package com.datdeveloper.datfactions.commands.util;

import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;

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
                .withStyle(
                        Style.EMPTY
                                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, actualCommand))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(actualCommand).withStyle(DatChatFormatting.TextColour.COMMAND)))
                );
    }

    /**
     *
     * Workaround for <a href="https://github.com/Mojang/brigadier/issues/46">https://github.com/Mojang/brigadier/issues/46</a>
     * <br>
     * Adapted from <a href="https://github.com/PaperMC/Velocity/blob/8abc9c80a69158ebae0121fda78b55c865c0abad/proxy/src/main/java/com/velocitypowered/proxy/util/BrigadierUtils.java#L38">Velocity</a>
     * <a href="https://github.com/PaperMC/Velocity/blob/8abc9c80a69158ebae0121fda78b55c865c0abad/LICENSE">MIT License</a>
     */
    public static LiteralCommandNode<CommandSourceStack> buildRedirect(final String alias, final LiteralCommandNode<CommandSourceStack> destination) {
        // Redirects only work for nodes with children, but break the top argument-less command.
        // Manually adding the root command after setting the redirect doesn't fix it.
        // See https://github.com/Mojang/brigadier/issues/46). Manually clone the node instead.
        final LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(alias)
                .requires(destination.getRequirement())
                .forward(
                        destination.getRedirect(),
                        destination.getRedirectModifier(),
                        destination.isFork()
                )
                .executes(destination.getCommand());
        for (final CommandNode<CommandSourceStack> child : destination.getChildren()) {
            builder.then(child);
        }
        return builder.build();
    }

    /**
     * Get the player or the player template if the player hasn't been registered yet
     * @param player the serverplayer to get
     * @return the player or the player template
     */
    public static FactionPlayer getPlayerOrTemplate(final ServerPlayer player) {
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
        return fPlayer != null ? fPlayer : FPlayerCollection.getInstance().getTemplate();
    }
}

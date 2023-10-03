package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionPlayerSetChatModeEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factiondata.EFPlayerChatMode;
import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.factiondata.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import java.util.Arrays;
import java.util.List;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_CHAT;

/**
 * A command that allows the player to choose which chat mode they use.
 */
public class FactionChatCommand {
    /** The argument string for the chat type */
    static final String CHAT_TYPE_ARGUMENT = "Chat Type";

    /**
     * Visitor to register the command
     */
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        command.then(Commands.literal("chat")
                .requires(commandSourceStack -> {
                    if (!(FactionsConfig.getUseFactionChat() && commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_CHAT)))
                        return false;

                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    final Faction faction = fPlayer.getFaction();

                    return faction != null && fPlayer.getRole().hasAnyPermissions(ERolePermissions.ALLYCHAT, ERolePermissions.FACTIONCHAT);
                })
                .then(Commands.argument(CHAT_TYPE_ARGUMENT, StringArgumentType.word())
                        .suggests((context, builder) -> {
                            final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(context.getSource().getPlayer());
                            Arrays.stream(EFPlayerChatMode.values())
                                    .filter(chatMode -> chatMode.requiredPermission == null || fPlayer.getRole().hasPermission(chatMode.requiredPermission))
                                            .forEach(chatMode -> builder.suggest(chatMode.name().toLowerCase()));

                            return builder.buildFuture();
                        })
                        .executes(c -> setChatTypeByName(c, c.getArgument(CHAT_TYPE_ARGUMENT, String.class))))
                .executes(FactionChatCommand::cycleChatType)
                .build());
    }

    /**
     * Handle setting the chat type directly
     * @param c The command Context
     * @return 1 for success
     * throws CommandSyntaxException when an unknown chat mode is passed
     */
    private static int setChatTypeByName(final CommandContext<CommandSourceStack> c, final String chatName) throws CommandSyntaxException {
        final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(c.getSource().getPlayer());

        final EFPlayerChatMode chatMode;
        try {
            chatMode = EFPlayerChatMode.valueOf(chatName.toUpperCase());
        } catch (final IllegalArgumentException ignored) {
            throw new SimpleCommandExceptionType(Component.literal("Unknown chat mode")).create();
        }

        if (chatMode == fPlayer.getChatMode()) {
            throw new CommandRuntimeException(Component.literal("You're chat mode is already set to ")
                    .append(
                            Component.literal(chatMode.name().toLowerCase())
                                    .withStyle(ChatFormatting.DARK_PURPLE)
                    ));
        }

        return setChatMode(c, fPlayer, chatMode);
    }

    /**
     * Cycle to the next chat type
     * @param c The command context
     * @return 1 for success
     */
    private static int cycleChatType(final CommandContext<CommandSourceStack> c) {
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());

        final List<EFPlayerChatMode> chatModes = Arrays.stream(EFPlayerChatMode.values())
                .filter(chatMode -> chatMode.requiredPermission == null || fPlayer.getRole().hasPermission(chatMode.requiredPermission))
                .toList();

        // If size is one then they only have permissions to public chat
        if (chatModes.size() == 1) {
            throw new CommandRuntimeException(Component.literal("You do not have access to any other chat modes"));
        }

        final int currentChatMode = chatModes.indexOf(fPlayer.getChatMode());
        final EFPlayerChatMode chatMode = chatModes.get((currentChatMode + 1) % chatModes.size());

        return setChatMode(c, fPlayer, chatMode);
    }

    /**
     * Fire event, handle result, and set the chat mode
     * @param c The command context
     * @param fPlayer The player
     * @param passedChatMode The chat mode to use
     * @return 1 if successful
     */
    private static int setChatMode(final CommandContext<CommandSourceStack> c, final FactionPlayer fPlayer, final EFPlayerChatMode passedChatMode) {
        final FactionPlayerSetChatModeEvent.Pre event = new FactionPlayerSetChatModeEvent.Pre(c.getSource().getPlayer(), fPlayer, passedChatMode);
        MinecraftForge.EVENT_BUS.post(event);

        final Event.Result result = event.getResult();
        final EFPlayerChatMode chatMode = event.getNewChatMode();

        if (result == Event.Result.DENY) {
            final Component reason = event.getDenyReason();
            if (reason != null) {
                throw new CommandRuntimeException(reason);
            } else {
                return 0;
            }
        } else if (result == Event.Result.DEFAULT && !fPlayer.getRole().hasPermission(chatMode.requiredPermission)) {
            throw new CommandRuntimeException(Component.literal("You do not have permission to use ")
                    .append(Component.literal(chatMode.name().toLowerCase() + " chat")
                            .withStyle(ChatFormatting.DARK_PURPLE))
            );
        }

        fPlayer.setChatMode(chatMode);
        c.getSource().sendSuccess(() ->
                        Component.literal(DatChatFormatting.TextColour.INFO + "Successfully set your chat mode to ")
                                .append(
                                        Component.literal(chatMode.name())
                                                .withStyle(ChatFormatting.DARK_PURPLE)
                                ),
                false
        );

        return 1;
    }

}

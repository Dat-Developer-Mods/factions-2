package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.factionData.EFPlayerChatMode;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_CHAT;

public class FactionChatCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("chat")
                .requires((commandSourceStack) -> {
                    if (!(FactionsConfig.getUseFactionChat() && commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_CHAT)))
                        return false;
                    final FactionPlayer fPlayer = getPlayerOrTemplate(commandSourceStack.getPlayer());
                    final Faction faction = fPlayer.getFaction();
                    return faction != null && fPlayer.getRole().hasAnyPermissions(ERolePermissions.ALLYCHAT, ERolePermissions.FACTIONCHAT);
                })
                .then(Commands.argument("Chat Type", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            final FactionPlayer fPlayer = getPlayerOrTemplate(context.getSource().getPlayer());
                            builder.suggest("public");
                            if (fPlayer.getRole().hasPermission(ERolePermissions.FACTIONCHAT)) builder.suggest("faction");
                            if (fPlayer.getRole().hasPermission(ERolePermissions.ALLYCHAT)) builder.suggest("ally");
                            return builder.buildFuture();
                        })
                        .executes(c -> {
                            final FactionPlayer fPlayer = getPlayerOrTemplate(c.getSource().getPlayer());
                            final String chatName = c.getArgument("Chat Type", String.class);
                            final EFPlayerChatMode chatMode;
                            try {
                                chatMode = EFPlayerChatMode.valueOf(chatName.toUpperCase());
                            } catch (final IllegalArgumentException ignored) {
                                c.getSource().sendFailure(Component.literal("Unknown chat mode"));
                                return 2;
                            }

                            if (chatMode == fPlayer.getChatMode()) {
                                c.getSource().sendFailure(
                                        Component.literal("You're chatmode is already ")
                                                .append(
                                                        Component.literal(chatMode.name())
                                                                .withStyle(ChatFormatting.DARK_PURPLE)
                                                )
                                );
                                return 3;
                            } else if (
                                    (chatMode == EFPlayerChatMode.FACTION && !fPlayer.getRole().hasPermission(ERolePermissions.FACTIONCHAT))
                                    || chatMode == EFPlayerChatMode.ALLY && !fPlayer.getRole().hasPermission(ERolePermissions.ALLYCHAT)
                            ) {
                                c.getSource().sendFailure(
                                        Component.literal("You do not have permission to use ")
                                                .append(
                                                        Component.literal(chatMode.name() + " chat")
                                                                .withStyle(ChatFormatting.DARK_PURPLE)
                                                )
                                );
                                return 4;
                            }

                            return execute(c, fPlayer, chatMode);
                        }))
                .executes(c -> {
                    final FactionPlayer factionPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
                    final EFPlayerChatMode newChatMode;
                    switch (factionPlayer.getChatMode()) {
                        case PUBLIC -> {
                            if (factionPlayer.getRole().hasPermission(ERolePermissions.FACTIONCHAT)) {
                                newChatMode = EFPlayerChatMode.FACTION;
                            } else {
                                newChatMode = EFPlayerChatMode.ALLY;
                            }
                        }
                        case FACTION -> {
                            if (factionPlayer.getRole().hasPermission(ERolePermissions.ALLYCHAT)) {
                                newChatMode = EFPlayerChatMode.ALLY;
                            } else {
                                newChatMode = EFPlayerChatMode.PUBLIC;
                            }
                        }
                        case ALLY -> {
                            newChatMode = EFPlayerChatMode.PUBLIC;
                        }
                        default -> throw new IllegalStateException("Unexpected value: " + factionPlayer.getChatMode());
                    }

                    return execute(c, factionPlayer, newChatMode);
                })
                .build();

        command.then(subCommand);
    }

    static int execute(final CommandContext<CommandSourceStack> context, final FactionPlayer fPlayer, final EFPlayerChatMode chatMode) {
        fPlayer.setChatMode(chatMode);
        context.getSource().sendSuccess(
                Component.literal(DatChatFormatting.TextColour.INFO + "Successfully set chatmode to ")
                        .append(
                                Component.literal(chatMode.name())
                                        .withStyle(ChatFormatting.DARK_PURPLE)
                        ),
                false
        );

        return 1;
    }
}

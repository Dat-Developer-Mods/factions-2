package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionChangeFlagsEvent;
import com.datdeveloper.datfactions.commands.suggestions.DatSuggestionProviders;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factionData.EFactionFlags;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.asyncTask.AsyncHandler;
import com.datdeveloper.datmoddingapi.command.util.Pager;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashSet;
import java.util.Set;

import static com.datdeveloper.datfactions.commands.FactionPermissions.*;

public class FactionFlagsCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {

        final LiteralArgumentBuilder<CommandSourceStack> subCommand = Commands.literal("flags")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasAnyPermissions(commandSourceStack.getPlayer(), FACTION_FLAG_LIST, FACTION_FLAG_ADD, FACTION_FLAG_REMOVE)))
                        return false;
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    final Faction faction = fPlayer.getFaction();
                    return faction != null && fPlayer.getRole().hasAnyPermissions(ERolePermissions.FLAGLIST, ERolePermissions.FLAGADD, ERolePermissions.FLAGREMOVE);
                })
                .then(buildFlagListCommand())
                .then(buildFlagAddCommand())
                .then(buildFlagRemoveCommand());

        command.then(subCommand.build());
    }

    /* ========================================= */
    /* Flags List
    /* ========================================= */

    static LiteralArgumentBuilder<CommandSourceStack> buildFlagListCommand() {
        return Commands.literal("list")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_FLAG_LIST) && fPlayer.getRole().hasPermission(ERolePermissions.FLAGLIST);
                })
                .then(
                        Commands.argument("Page", IntegerArgumentType.integer(1))
                                .executes(c -> executeList(c.getSource(), c.getArgument("Page", Integer.class)))
                )
                .executes(c -> executeList(c.getSource(), 1));
    }

    private static int executeList(final CommandSourceStack sourceStack, final int page) {
        final ServerPlayer player = sourceStack.getPlayer();
        final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
        final Faction faction = fPlayer.getFaction();

        AsyncHandler.runAsyncTask(() -> {
            if (faction.getFlags().isEmpty()) {
                sourceStack.sendFailure(
                        Component.literal("Your faction doesn't have any flags")
                );
                return;
            }

            final Pager<EFactionFlags> pager = new Pager<>(
                    "/f flags list",
                    "Flags",
                    faction.getFlags(),
                    (EFactionFlags::getChatComponent)
            );
            pager.sendPage(page, sourceStack.source);
        });

        return 1;
    }

    /* ========================================= */
    /* Flags Add
    /* ========================================= */

    static LiteralArgumentBuilder<CommandSourceStack> buildFlagAddCommand() {
        return Commands.literal("add")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_FLAG_ADD) && fPlayer.getRole().hasPermission(ERolePermissions.FLAGADD);
                })
                .then(
                        Commands.argument("Flag", StringArgumentType.word())
                                .suggests(DatSuggestionProviders.flagSuggestionProvider)
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                                    final Faction faction = fPlayer.getFaction();

                                    final Set<EFactionFlags> flags = new HashSet<>();
                                    try {
                                        flags.add(EFactionFlags.valueOf(c.getArgument("Flag", String.class).toUpperCase()));
                                    } catch (final IllegalArgumentException ignored) {
                                        c.getSource().sendFailure(Component.literal("Unknown Flag"));
                                        return 2;
                                    }



                                    final FactionChangeFlagsEvent.PreAdd pre = new FactionChangeFlagsEvent.PreAdd(c.getSource().source, faction, flags);
                                    MinecraftForge.EVENT_BUS.post(pre);

                                    if (flag.admin || !FactionsConfig.getFlagWhitelisted(flag)) {
                                        c.getSource().sendFailure(Component.literal("You're not allowed to use that flag"));
                                        return 3;
                                    } else if (faction.hasFlag(flag)) {
                                        c.getSource().sendFailure(Component.literal("Your faction already has that flag"));
                                        return 4;
                                    }

                                    faction.addFlag(flag);

                                    c.getSource().sendSuccess(
                                            Component.literal(DatChatFormatting.TextColour.INFO + "Successfully added the ")
                                                    .append(
                                                            flag.getChatComponent()
                                                                    .withStyle(ChatFormatting.DARK_PURPLE)
                                                    )
                                                    .append(DatChatFormatting.TextColour.INFO + " flag to your faction"),
                                            false);

                                    return 1;
                                })
                );
    }

    /* ========================================= */
    /* Flags Remove
    /* ========================================= */

    static LiteralArgumentBuilder<CommandSourceStack> buildFlagRemoveCommand() {
        return Commands.literal("remove")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_FLAG_REMOVE) && fPlayer.getRole().hasPermission(ERolePermissions.FLAGREMOVE);
                })
                .then(
                        Commands.argument("Flag", StringArgumentType.word())
                                .suggests(DatSuggestionProviders.flagSuggestionProvider)
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                                    final Faction faction = fPlayer.getFaction();

                                    final EFactionFlags flag;
                                    try {
                                        flag = EFactionFlags.valueOf(c.getArgument("Flag", String.class).toUpperCase());
                                    } catch (final IllegalArgumentException ignored) {
                                        c.getSource().sendFailure(Component.literal("Unknown Flag"));
                                        return 2;
                                    }

                                    if (flag.admin) {
                                        c.getSource().sendFailure(Component.literal("You're not allowed to use that flag"));
                                        return 3;
                                    } else if (!faction.hasFlag(flag)) {
                                        c.getSource().sendFailure(Component.literal("Your faction does not have that flag to remove"));
                                        return 4;
                                    }

                                    faction.removeFlag(flag);

                                    c.getSource().sendSuccess(
                                            Component.literal(DatChatFormatting.TextColour.INFO + "Successfully removed the ")
                                                    .append(
                                                            flag.getChatComponent()
                                                                    .withStyle(ChatFormatting.DARK_PURPLE)
                                                    )
                                                    .append(DatChatFormatting.TextColour.INFO + " flag from your faction"),
                                            false);

                                    return 1;
                                })
                );
    }
}
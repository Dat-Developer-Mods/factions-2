package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionChangeFlagsEvent;
import com.datdeveloper.datfactions.commands.suggestions.DatSuggestionProviders;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factiondata.EFactionFlags;
import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.factiondata.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.command.util.Pager;
import com.datdeveloper.datmoddingapi.concurrentTask.ConcurrentHandler;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import java.util.HashSet;
import java.util.Set;

import static com.datdeveloper.datfactions.commands.FactionPermissions.*;

/**
 * A command that allows a player to interact with their faction's flags
 */
public class FactionFlagsCommand {
    /** Exception thrown when a flag is not recognised */
    static final CommandRuntimeException UNKNOWN_FACTION_FLAG_EXCEPTION = new CommandRuntimeException(Component.literal("Unknown faction flag"));

    /**
     * The argument for the flag
     * <br>
     * Used by add and remove
     */
    static final String FLAG_ARG = "Flag";

    /**
     * Visitor to register the command
     */
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        command.then(Commands.literal("flags")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasAnyPermissions(commandSourceStack.getPlayer(), FACTION_FLAG_LIST, FACTION_FLAG_ADD, FACTION_FLAG_REMOVE)))
                        return false;

                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    final Faction faction = fPlayer.getFaction();
                    return faction != null && fPlayer.getRole().hasAnyPermissions(ERolePermissions.FLAGLIST, ERolePermissions.FLAGADD, ERolePermissions.FLAGREMOVE);
                })
                .then(buildFlagListCommand())
                .then(buildFlagAddCommand())
                .then(buildFlagRemoveCommand())
                .build());
    }

    /* ========================================= */
    /* Flags List
    /* ========================================= */
    /**
     * The argument for the page number
     * <br>
     * Only used by list
     */
    static final String PAGE_ARG = "Page";

    /**
     * Build the Flag List command
     */
    private static LiteralArgumentBuilder<CommandSourceStack> buildFlagListCommand() {
        return Commands.literal("list")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_FLAG_LIST) && fPlayer.getRole().hasPermission(ERolePermissions.FLAGLIST);
                })
                .then(
                        Commands.argument(PAGE_ARG, IntegerArgumentType.integer(1))
                                .executes(c -> executeList(c.getSource(), c.getArgument(PAGE_ARG, Integer.class)))
                )
                .executes(c -> executeList(c.getSource(), 1));
    }

    /**
     * Handle the f list command
     * @param sourceStack The command Source
     * @param page The page of the list to view
     * @return 1 for success
     */
    private static int executeList(final CommandSourceStack sourceStack, final int page) {
        final ServerPlayer player = sourceStack.getPlayer();
        final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
        final Faction faction = fPlayer.getFaction();

        ConcurrentHandler.runConcurrentTask(() -> {
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
    /**
     * Build the flag add command
     */
    static LiteralArgumentBuilder<CommandSourceStack> buildFlagAddCommand() {
        return Commands.literal("add")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_FLAG_ADD) && fPlayer.getRole().hasPermission(ERolePermissions.FLAGADD);
                })
                .then(
                        Commands.argument(FLAG_ARG, StringArgumentType.word())
                                .suggests(DatSuggestionProviders.flagSuggestionProvider)
                                .executes(c -> executeAdd(c, c.getArgument(FLAG_ARG, String.class)))
                );
    }

    /**
     * Handle flag add command
     * @param c The command context
     * @param flagName The name of the flag being added
     * @return 1 for success
     */
    private static int executeAdd(final CommandContext<CommandSourceStack> c, final String flagName) {
        final ServerPlayer player = c.getSource().getPlayer();
        final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
        final Faction faction = fPlayer.getFaction();

        final Set<EFactionFlags> passedFlags = new HashSet<>();
        try {
            passedFlags.add(EFactionFlags.valueOf(flagName.toUpperCase()));
        } catch (final IllegalArgumentException ignored) {
            throw UNKNOWN_FACTION_FLAG_EXCEPTION;
        }

        final FactionChangeFlagsEvent.PreAdd event = new FactionChangeFlagsEvent.PreAdd(c.getSource().getPlayer(), faction, passedFlags);
        MinecraftForge.EVENT_BUS.post(event);

        final Event.Result result = event.getResult();

        // Need to get flags again as it can be set to a new Set in the event
        final Set<EFactionFlags> flags = event.getFlags();

        if (result == Event.Result.DENY) {
            final Component reason = event.getDenyReason();
            if (reason != null) {
                throw new CommandRuntimeException(reason);
            } else {
                return 0;
            }
        } else if (result == Event.Result.DEFAULT) {
            if (flags.stream().anyMatch(flag -> flag.admin || !FactionsConfig.getFlagWhitelisted(flag))) {
                throw new CommandRuntimeException(Component.literal("You're not allowed to use that flag"));
            } else if (flags.stream().allMatch(faction::hasFlag)) {
                throw new CommandRuntimeException(Component.literal("You already have that flag"));
            }
        }

        faction.addFlags(flags);

        c.getSource().sendSuccess(() ->
                Component.literal(DatChatFormatting.TextColour.INFO + "Successfully added the flag(s) ")
                        .append(
                                ComponentUtils.formatList(flags,
                                        flag -> flag.getChatComponent().withStyle(ChatFormatting.DARK_PURPLE))
                        )
                        .append(DatChatFormatting.TextColour.INFO + ", to your faction"),
                false);

        return 1;
    }

    /* ========================================= */
    /* Flags Remove
    /* ========================================= */

    /**
     * Build flag remove command
     */
    static LiteralArgumentBuilder<CommandSourceStack> buildFlagRemoveCommand() {
        return Commands.literal("remove")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_FLAG_REMOVE) && fPlayer.getRole().hasPermission(ERolePermissions.FLAGREMOVE);
                })
                .then(
                        Commands.argument(FLAG_ARG, StringArgumentType.word())
                                .suggests(DatSuggestionProviders.flagSuggestionProvider)
                                .executes(c -> executeRemove(c, c.getArgument(FLAG_ARG, String.class)))
                );
    }

    /**
     * Handle flag remove command
     * @param c The command context
     * @param flagName The name of the flag to remove
     * @return 1 on success
     */
    private static int executeRemove(final CommandContext<CommandSourceStack> c, final String flagName) {
        final ServerPlayer player = c.getSource().getPlayer();
        final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
        final Faction faction = fPlayer.getFaction();

        final Set<EFactionFlags> passedFlags = new HashSet<>();
        try {
            passedFlags.add(EFactionFlags.valueOf(flagName.toUpperCase()));
        } catch (final IllegalArgumentException ignored) {
            throw UNKNOWN_FACTION_FLAG_EXCEPTION;
        }

        final FactionChangeFlagsEvent.PreRemove event = new FactionChangeFlagsEvent.PreRemove(c.getSource().getPlayer(), faction, passedFlags);
        MinecraftForge.EVENT_BUS.post(event);

        final Event.Result result = event.getResult();

        // Need to get flags again as it can be set to a new Set in the event
        final Set<EFactionFlags> flags = event.getFlags();

        if (result == Event.Result.DENY) {
            final Component reason = event.getDenyReason();
            if (reason != null) {
                throw new CommandRuntimeException(reason);
            } else {
                return 0;
            }
        } else if (result == Event.Result.DEFAULT) {
            if (flags.stream().anyMatch(flag -> flag.admin || !FactionsConfig.getFlagWhitelisted(flag))) {
                throw new CommandRuntimeException(Component.literal("You're not allowed to use that flag"));
            } else if (flags.stream().allMatch(faction::hasFlag)) {
                throw new CommandRuntimeException(Component.literal("You already have that flag"));
            }
        }

        faction.removeFlags(flags);

        c.getSource().sendSuccess(() ->
                        Component.literal(DatChatFormatting.TextColour.INFO + "Successfully removed the flag(s) ")
                                .append(
                                        ComponentUtils.formatList(flags,
                                                flag -> flag.getChatComponent().withStyle(ChatFormatting.DARK_PURPLE))
                                )
                                .append(DatChatFormatting.TextColour.INFO + ", from your faction"),
                false);

        return 1;
    }
}
package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionInviteEvent;
import com.datdeveloper.datfactions.commands.suggestions.DatSuggestionProviders;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factiondata.EFactionFlags;
import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.factiondata.permissions.ERolePermissions;
import com.datdeveloper.datfactions.factiondata.relations.EFactionRelation;
import com.datdeveloper.datfactions.util.RelationUtil;
import com.datdeveloper.datmoddingapi.concurrentTask.ConcurrentHandler;
import com.datdeveloper.datmoddingapi.command.util.Pager;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.Comparator;
import java.util.List;

import static com.datdeveloper.datfactions.commands.FactionPermissions.*;

/**
 * A command that allows factions to manage their invites to players
 */
public class FactionInvitesCommand {

    /**
     * The argument for the player being targeted by the command
     */
    static final String TARGET_PLAYER_ARG = "Target Player";

    /**
     * Visitor to register the command
     */
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralArgumentBuilder<CommandSourceStack> subCommand = Commands.literal("invites")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasAnyPermissions(commandSourceStack.getPlayer(), FACTION_INVITE, FACTION_UNINVITE, FACTION_INVITE_LIST_FACTION)))
                        return false;
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    final Faction faction = fPlayer.getFaction();
                    return faction != null && !faction.hasFlag(EFactionFlags.UNRELATEABLE) && fPlayer.getRole().hasAnyPermissions(ERolePermissions.INVITE, ERolePermissions.UNINVITE, ERolePermissions.INVITELIST);
                })
                .then(buildInviteListCommand())
                .then(buildInviteAddCommand())
                .then(buildUninviteCommand());

        command.then(subCommand.build());
    }

    /* ========================================= */
    /* Invite list
    /* ========================================= */
    /**
     * The argument for the page number
     * <br>
     * Only used by list
     */
    static final String PAGE_ARG = "Page";

    /**
     * Build the invite list command
     */
    static LiteralArgumentBuilder<CommandSourceStack> buildInviteListCommand() {
        return Commands.literal("list")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_INVITE_LIST_FACTION) && fPlayer.getRole().hasPermission(ERolePermissions.INVITELIST);
                })
                .then(Commands.argument(PAGE_ARG, IntegerArgumentType.integer(1))
                                .executes(c -> executeList(c.getSource(), c.getArgument(PAGE_ARG, Integer.class)))
                )
                .executes(c -> executeList(c.getSource(), 1));
    }

    /**
     * Handle the list command
     * @param sourceStack The caller of the command
     * @param page The page of the list to view
     * @return 1 for success
     */
    private static int executeList(final CommandSourceStack sourceStack, final int page) {
        final ServerPlayer player = sourceStack.getPlayer();
        final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
        final Faction faction = fPlayer.getFaction();

        // There's no threading protections around this, but since we're only reading it'll probably be fine
        ConcurrentHandler.runConcurrentTask(() -> {
            final List<FactionPlayer> values = faction.getPlayerInvites().stream()
                    .map(playerId -> FPlayerCollection.getInstance().getByKey(playerId))
                    .sorted(Comparator.comparing(FactionPlayer::getName))
                    .toList();

            if (values.isEmpty()) {
                sourceStack.sendFailure(Component.empty()
                        .append(
                                faction.getNameWithDescription(faction)
                                        .withStyle(EFactionRelation.SELF.formatting)
                        )
                        .append(" has not invited any players"));
                return;
            }

            final Pager<FactionPlayer> pager = new Pager<>(
                    "/f invites list",
                    "Player invites",
                    values,
                    (playerEl -> Component.empty()
                            .append(
                                    playerEl.getNameWithDescription(faction)
                                            .withStyle(RelationUtil.getRelation(faction, playerEl).formatting)
                            ))
            );

            pager.sendPage(page, sourceStack.source);
        });

        return 1;
    }

    /* ========================================= */
    /* Invite
    /* ========================================= */

    /**
     * Build the add command
     */
    static LiteralArgumentBuilder<CommandSourceStack> buildInviteAddCommand() {
        return Commands.literal("add")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_INVITE) && fPlayer.getRole().hasPermission(ERolePermissions.INVITE);
                })
                .then(Commands.argument(TARGET_PLAYER_ARG, StringArgumentType.word())
                                .suggests(DatSuggestionProviders.fPlayerProvider)
                                .executes(c -> executeAdd(c, c.getArgument(TARGET_PLAYER_ARG, String.class)))
                );
    }

    /**
     * Execute the invite add command
     * @param c The command context
     * @param targetName The player being invited
     * @return 1 if successful
     */
    private static int executeAdd(final CommandContext<CommandSourceStack> c, final String targetName) throws CommandSyntaxException {
        final ServerPlayer player = c.getSource().getPlayer();
        final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
        final Faction faction = fPlayer.getFaction();

        final FactionPlayer passedTarget = FPlayerCollection.getInstance().getByName(targetName);
        if (passedTarget == null) {
            throw new SimpleCommandExceptionType(Component.literal("Cannot find a player with that name")).create();
        }

        final FactionInviteEvent.Pre event = new FactionInviteEvent.Pre(
                c.getSource().getPlayer(),
                faction,
                passedTarget,
                FactionInviteEvent.EInviteType.INVITE
        );
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            final Component reason = event.getDenyReason();
            if (reason != null) {
                throw new CommandRuntimeException(reason);
            } else {
                return 0;
            }
        }

        final FactionPlayer target = event.getPlayer();

        if (faction.getPlayers().contains(target)) {
            throw new CommandRuntimeException(Component.literal("You cannot invite players that are already in your faction"));
        } else if (faction.getPlayerInvites().contains(target.getId())) {
            throw new CommandRuntimeException(Component.empty()
                    .append(target.getNameWithDescription(faction)
                            .withStyle(RelationUtil.getRelation(faction, target).formatting))
                    .append(DatChatFormatting.TextColour.ERROR + " already has an invite from you"));
        }

        faction.addInvite(target);

        c.getSource().sendSuccess(() ->
                        Component.literal(DatChatFormatting.TextColour.INFO + "Successfully invited ")
                                .append(target.getNameWithDescription(faction)
                                        .withStyle(RelationUtil.getRelation(faction, target).formatting)
                                ).append(DatChatFormatting.TextColour.INFO + " to the faction"),
                false
        );

        if (target.isPlayerOnline()) {
            target.getServerPlayer().sendSystemMessage(
                    Component.literal(DatChatFormatting.TextColour.INFO + "You have been invited to join ")
                            .append(
                                    faction.getNameWithDescription(target.getFaction())
                                            .withStyle(RelationUtil.getRelation(target, faction).formatting)
                            ).append("\n")
                            .append(DatChatFormatting.TextColour.INFO + "You can accept using ")
                            .append(FactionCommandUtils.wrapCommand("/f join " + faction.getName()))
            );
        }

        return 1;
    }

    /* ========================================= */
    /* Uninvite
    /* ========================================= */

    /**
     * Build the uninvite command
     */
    static LiteralArgumentBuilder<CommandSourceStack> buildUninviteCommand() {
        return Commands.literal("remove")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_UNINVITE) && fPlayer.getRole().hasPermission(ERolePermissions.UNINVITE);
                })
                .then(
                        Commands.argument(TARGET_PLAYER_ARG, StringArgumentType.word())
                                .suggests(DatSuggestionProviders.fPlayerProvider)
                                .executes(c -> executeUninvite(c, c.getArgument(TARGET_PLAYER_ARG, String.class)))
                );
    }

    /**
     * Execute the uninvite command
     * @param c The command context
     * @return 1 if successful
     */
    private static int executeUninvite(CommandContext<CommandSourceStack> c, final String targetName) {
        final ServerPlayer player = c.getSource().getPlayer();
        final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
        final Faction faction = fPlayer.getFaction();

        final FactionPlayer passedTarget = FPlayerCollection.getInstance().getByName(targetName);
        if (passedTarget == null) {
            c.getSource().sendFailure(Component.literal("Cannot find a player with that name"));
            return 2;
        }

        final FactionInviteEvent.Pre event = new FactionInviteEvent.Pre(
                c.getSource().getPlayer(),
                faction,
                passedTarget,
                FactionInviteEvent.EInviteType.UNINVITE
        );
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            final Component reason = event.getDenyReason();
            if (reason != null) {
                throw new CommandRuntimeException(reason);
            } else {
                return 0;
            }
        }

        final FactionPlayer target = event.getPlayer();

        if (!faction.getPlayerInvites().contains(target.getId())) {
            throw new CommandRuntimeException(Component.empty()
                    .append(target.getNameWithDescription(faction)
                            .withStyle(RelationUtil.getRelation(faction, target).formatting))
                    .append(DatChatFormatting.TextColour.ERROR + " does not have an invite from you")
            );

        }

        faction.removeInvite(target);

        c.getSource().sendSuccess(() ->
                        Component.literal(DatChatFormatting.TextColour.INFO + "Successfully revoked ")
                                .append(
                                        target.getNameWithDescription(faction)
                                                .withStyle(RelationUtil.getRelation(faction, target).formatting)
                                ).append(DatChatFormatting.TextColour.INFO + " invite to the faction"),
                false
        );

        if (target.isPlayerOnline()) {
            target.getServerPlayer().sendSystemMessage(
                    Component.literal(DatChatFormatting.TextColour.ERROR + "Your invitation to join ")
                            .append(faction.getNameWithDescription(target.getFaction())
                                            .withStyle(RelationUtil.getRelation(target, faction).formatting))
                            .append(DatChatFormatting.TextColour.ERROR + " has been revoked")
            );
        }

        return 1;
    }
}
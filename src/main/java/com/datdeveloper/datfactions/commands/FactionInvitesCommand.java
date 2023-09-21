package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionInvitePlayerEvent;
import com.datdeveloper.datfactions.api.events.FactionUninvitePlayerEvent;
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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.Comparator;
import java.util.List;

import static com.datdeveloper.datfactions.commands.FactionPermissions.*;

public class FactionInvitesCommand {
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
                .then(buildInviteCommand())
                .then(buildUninviteCommand());

        command.then(subCommand.build());
    }

    /* ========================================= */
    /* Invite list
    /* ========================================= */

    static LiteralArgumentBuilder<CommandSourceStack> buildInviteListCommand() {
        return Commands.literal("list")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_INVITE_LIST_FACTION) && fPlayer.getRole().hasPermission(ERolePermissions.INVITELIST);
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

        ConcurrentHandler.runConcurrentTask(() -> {
            final List<FactionPlayer> values = faction.getPlayerInvites().stream()
                    .map(playerId -> FPlayerCollection.getInstance().getByKey(playerId))
                    .sorted(Comparator.comparing(FactionPlayer::getName))
                    .toList();

            if (values.isEmpty()) {
                sourceStack.sendFailure(
                        Component.empty()
                                .append(
                                        faction.getNameWithDescription(faction)
                                                .withStyle(EFactionRelation.SELF.formatting)
                                )
                                .append(" has not invited any players")
                );
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

    static LiteralArgumentBuilder<CommandSourceStack> buildInviteCommand() {
        return Commands.literal("add")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_INVITE) && fPlayer.getRole().hasPermission(ERolePermissions.INVITE);
                })
                .then(
                        Commands.argument("Target Player", StringArgumentType.word())
                                .suggests(DatSuggestionProviders.fPlayerProvider)
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                                    final Faction faction = fPlayer.getFaction();

                                    final String targetName = c.getArgument("Target Player", String.class);
                                    final FactionPlayer target = FPlayerCollection.getInstance().getByName(targetName);
                                    if (target == null) {
                                        c.getSource().sendFailure(Component.literal("Cannot find a player with that name"));
                                        return 2;
                                    } else if (faction.getPlayers().contains(target)) {
                                        c.getSource().sendFailure(
                                                Component.literal("You cannot invite players that are already in your faction")
                                        );
                                        return 3;
                                    } else if (faction.getPlayerInvites().contains(target.getId())) {
                                        c.getSource().sendFailure(
                                                Component.empty()
                                                        .append(
                                                                target.getNameWithDescription(faction)
                                                                        .withStyle(RelationUtil.getRelation(faction, target).formatting)
                                                        )
                                                        .append(DatChatFormatting.TextColour.ERROR + " already has an invite from you")
                                        );
                                        return 4;
                                    }

                                    final FactionInvitePlayerEvent event = new FactionInvitePlayerEvent(
                                            c.getSource().source,
                                            faction,
                                            target
                                    );
                                    MinecraftForge.EVENT_BUS.post(event);
                                    if (event.isCanceled()) return 0;

                                    faction.addInvite(target.getId());
                                    c.getSource().sendSuccess(() ->
                                            Component.literal(DatChatFormatting.TextColour.INFO + "Successfully invited ")
                                                    .append(
                                                            target.getNameWithDescription(faction)
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
                                })
                );
    }

    /* ========================================= */
    /* Uninvite
    /* ========================================= */

    static LiteralArgumentBuilder<CommandSourceStack> buildUninviteCommand() {
        return Commands.literal("remove")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_UNINVITE) && fPlayer.getRole().hasPermission(ERolePermissions.UNINVITE);
                })
                .then(
                        Commands.argument("Target Player", StringArgumentType.word())
                                .suggests(DatSuggestionProviders.fPlayerProvider)
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                                    final Faction faction = fPlayer.getFaction();

                                    final String targetName = c.getArgument("Target Player", String.class);
                                    final FactionPlayer target = FPlayerCollection.getInstance().getByName(targetName);
                                    if (target == null) {
                                        c.getSource().sendFailure(Component.literal("Cannot find a player with that name"));
                                        return 2;
                                    } else if (!faction.getPlayerInvites().contains(target.getId())) {
                                        c.getSource().sendFailure(
                                                Component.empty()
                                                        .append(
                                                                target.getNameWithDescription(faction)
                                                                        .withStyle(RelationUtil.getRelation(faction, target).formatting)
                                                        )
                                                        .append(DatChatFormatting.TextColour.ERROR + " does not have an invite from you")
                                        );
                                        return 3;
                                    }

                                    final FactionUninvitePlayerEvent event = new FactionUninvitePlayerEvent(
                                            c.getSource().source,
                                            faction,
                                            target
                                    );
                                    MinecraftForge.EVENT_BUS.post(event);
                                    if (event.isCanceled()) return 0;

                                    faction.addInvite(target.getId());
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
                                                        .append(
                                                                faction.getNameWithDescription(target.getFaction())
                                                                        .withStyle(RelationUtil.getRelation(target, faction).formatting)
                                                        )
                                                        .append(DatChatFormatting.TextColour.ERROR + " has been revoked")
                                        );
                                    }

                                    return 1;
                                })
                );
    }
}
package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionInvitePlayerEvent;
import com.datdeveloper.datfactions.api.events.FactionPlayerChangeRoleEvent;
import com.datdeveloper.datfactions.api.events.FactionUninvitePlayerEvent;
import com.datdeveloper.datfactions.commands.suggestions.DatSuggestionProviders;
import com.datdeveloper.datfactions.commands.suggestions.FactionRoleSuggestionProvider;
import com.datdeveloper.datfactions.commands.suggestions.OwnFPlayerSuggestionProvider;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datfactions.factionData.permissions.FactionRole;
import com.datdeveloper.datfactions.factionData.relations.EFactionRelation;
import com.datdeveloper.datfactions.util.RelationUtil;
import com.datdeveloper.datmoddingapi.asyncTask.AsyncHandler;
import com.datdeveloper.datmoddingapi.command.util.Pager;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.Comparator;
import java.util.List;

import static com.datdeveloper.datfactions.commands.FactionPermissions.*;

public class FactionPlayersCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {

        final LiteralArgumentBuilder<CommandSourceStack> subCommand = Commands.literal("invites")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer()) && DatPermissions.hasAnyPermissions(commandSourceStack.getPlayer(), FACTION_LIST_PLAYERS, FACTION_PROMOTE, FACTION_DEMOTE, FACTION_SET_ROLE, FACTION_KICK))
                        return false;
                    final FactionPlayer fPlayer = getPlayerOrTemplate(commandSourceStack.getPlayer());
                    final Faction faction = fPlayer.getFaction();
                    return faction != null && fPlayer.getRole().hasAnyPermissions(ERolePermissions.LISTPLAYERS, ERolePermissions.PROMOTE, ERolePermissions.DEMOTE, ERolePermissions.SETROLE, ERolePermissions.KICK);
                })
                .then(buildListCommand())
                .then(buildPromoteCommand())
                .then(buildDemoteCommand())
                .then(buildSetRoleCommand())
                .then(buildKickCommand());

        command.then(subCommand.build());
    }

    /* ========================================= */
    /* list
    /* ========================================= */

    static LiteralArgumentBuilder<CommandSourceStack> buildListCommand() {
        return Commands.literal("list")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_LIST_PLAYERS) && fPlayer.getRole().hasPermission(ERolePermissions.LISTPLAYERS);
                })
                .then(
                        Commands.argument("Page", IntegerArgumentType.integer(1))
                                .executes(c -> executeList(c.getSource(), c.getArgument("Page", Integer.class)))
                )
                .executes(c -> executeList(c.getSource(), 1));
    }

    private static int executeList(final CommandSourceStack sourceStack, final int page) {
        final ServerPlayer player = sourceStack.getPlayer();
        final FactionPlayer fPlayer = getPlayerOrTemplate(player);
        final Faction faction = fPlayer.getFaction();

        AsyncHandler.runAsyncTask(() -> {
            final List<FactionPlayer> values = faction.getPlayers().stream()
                    .sorted(Comparator.comparingInt((FactionPlayer player2) -> faction.getRoleIndex(player2.getRoleId())).thenComparing(FactionPlayer::getName))
                    .toList();

            if (values.isEmpty()) {
                sourceStack.sendFailure(
                        Component.empty()
                                .append(
                                        faction.getNameWithDescription(faction)
                                                .withStyle(EFactionRelation.SELF.formatting)
                                )
                                .append(" does not have any players")
                );
                return;
            }

            final Pager<FactionPlayer> pager = new Pager<>(
                    "/f players list",
                    "Players",
                    values,
                    (playerEl -> Component.empty()
                            .append(
                                    playerEl.getNameWithDescription(faction)
                                            .withStyle(playerEl.isPlayerOnline() ? ChatFormatting.GREEN : ChatFormatting.RED)
                            ))
            );
            pager.sendPage(page, sourceStack.source);
        });

        return 1;
    }

    /* ========================================= */
    /* Promote
    /* ========================================= */

    static LiteralArgumentBuilder<CommandSourceStack> buildPromoteCommand() {
        return Commands.literal("promote")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_PROMOTE) && fPlayer.getRole().hasPermission(ERolePermissions.PROMOTE);
                })
                .then(
                        Commands.argument("Target Player", StringArgumentType.word())
                                .suggests(new OwnFPlayerSuggestionProvider(true))
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                                    final Faction faction = fPlayer.getFaction();

                                    final String targetName = c.getArgument("Target Player", String.class);
                                    final FactionPlayer target = FPlayerCollection.getInstance().getByName(targetName);
                                    if (target == null) {
                                        c.getSource().sendFailure(Component.literal("Cannot find a player with that name"));
                                        return 2;
                                    } else if (!faction.getPlayers().contains(target)) {
                                        c.getSource().sendFailure(
                                                Component.literal("That player is not in your faction")
                                        );
                                        return 3;
                                    }

                                    final FactionRole newRole;
                                    {
                                        final int newRoleIndex = faction.getRoleIndex(target.getRoleId()) - 1;
                                        if (newRoleIndex <= faction.getRoleIndex(fPlayer.getRoleId())) {
                                            c.getSource().sendFailure(
                                                    Component.literal("You are not allowed to promote that player")
                                            );
                                            return 4;
                                        }

                                        newRole = faction.getRoles().get(newRoleIndex);
                                    }

                                    executeSetRole(c, faction, target, newRole, FactionPlayerChangeRoleEvent.EChangeRoleReason.PROMOTE);

                                    return 1;
                                })
                );
    }

    /* ========================================= */
    /* Demote
    /* ========================================= */

    static LiteralArgumentBuilder<CommandSourceStack> buildSetRoleCommand() {
        return Commands.literal("setrole")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_SET_ROLE) && fPlayer.getRole().hasPermission(ERolePermissions.SETROLE);
                })
                .then(
                        Commands.argument("Target Player", StringArgumentType.word())
                                .suggests(new OwnFPlayerSuggestionProvider(true))
                                .then(
                                        Commands.argument("New Role", StringArgumentType.word())
                                                .suggests(new FactionRoleSuggestionProvider(true, false))
                                                .executes(c -> {
                                                    final ServerPlayer player = c.getSource().getPlayer();
                                                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                                                    final Faction faction = fPlayer.getFaction();

                                                    final String targetName = c.getArgument("Target Player", String.class);
                                                    final FactionPlayer target = FPlayerCollection.getInstance().getByName(targetName);
                                                    if (target == null) {
                                                        c.getSource().sendFailure(Component.literal("Cannot find a player with that name"));
                                                        return 2;
                                                    } else if (!faction.getPlayers().contains(target)) {
                                                        c.getSource().sendFailure(
                                                                Component.literal("That player is not in your faction")
                                                        );
                                                        return 3;
                                                    }

                                                    final String newRoleName = c.getArgument("New Role", String.class);
                                                    {
                                                        final int result = checkPlayerCanSetToRole(c.getSource(), fPlayer, newRoleName);
                                                        if (result != 0) return result + 3;
                                                    }

                                                    final FactionRole newRole = faction.getRoleByName(newRoleName);

                                                    executeSetRole(c, faction, target, newRole, FactionPlayerChangeRoleEvent.EChangeRoleReason.SET);

                                                    return 1;
                                                })
                                )
                );
    }

    /* ========================================= */
    /* Set Role
    /* ========================================= */

    static LiteralArgumentBuilder<CommandSourceStack> buildDemoteCommand() {
        return Commands.literal("remove")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_DEMOTE) && fPlayer.getRole().hasPermission(ERolePermissions.DEMOTE);
                })
                .then(
                        Commands.argument("Target Player", StringArgumentType.word())
                                .suggests(new OwnFPlayerSuggestionProvider(true))
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                                    final Faction faction = fPlayer.getFaction();

                                    final String targetName = c.getArgument("Target Player", String.class);
                                    final FactionPlayer target = FPlayerCollection.getInstance().getByName(targetName);
                                    if (target == null) {
                                        c.getSource().sendFailure(Component.literal("Cannot find a player with that name"));
                                        return 2;
                                    } else if (!faction.getPlayers().contains(target)) {
                                        c.getSource().sendFailure(
                                                Component.literal("That player is not in your faction")
                                        );
                                        return 3;
                                    }

                                    final FactionRole newRole;
                                    {
                                        final int targetRoleIndex = faction.getRoleIndex(target.getRoleId());
                                        if (targetRoleIndex <= faction.getRoleIndex(fPlayer.getRoleId())) {
                                            c.getSource().sendFailure(
                                                    Component.literal("You are not allowed to demote that player")
                                            );
                                            return 4;
                                        } else if (targetRoleIndex == (faction.getRoles().size() - 1)) {
                                            c.getSource().sendFailure(
                                                    Component.literal("You cannot demote a recruit")
                                            );
                                        }

                                        newRole = faction.getRoles().get(targetRoleIndex + 1);
                                    }


                                    executeSetRole(c, faction, target, newRole, FactionPlayerChangeRoleEvent.EChangeRoleReason.DEMOTE);

                                    return 1;
                                })
                );
    }

    /* ========================================= */
    /* Util
    /* ========================================= */

    private static int executeSetRole(final CommandContext<CommandSourceStack> c, final Faction faction, final FactionPlayer target, FactionRole newRole, final FactionPlayerChangeRoleEvent.EChangeRoleReason reason) {
        final FactionPlayerChangeRoleEvent event = new FactionPlayerChangeRoleEvent(
                c.getSource().source,
                target,
                newRole,
                reason
        );
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return 0;

        newRole = event.getNewRole();
        target.setRole(newRole.getId());
        c.getSource().sendSuccess(
                Component.literal(DatChatFormatting.TextColour.INFO + "Successfully set the role of ")
                        .append(
                                target.getNameWithDescription(faction)
                                        .withStyle(EFactionRelation.SELF.formatting)
                        )
                        .append(DatChatFormatting.TextColour.INFO + " to ")
                        .append(
                                newRole.getNameWithDescription()
                                        .withStyle(ChatFormatting.DARK_PURPLE)
                        ),
                true
        );

        if (target.isPlayerOnline()) {
            target.getServerPlayer().sendSystemMessage(
                    Component.literal(DatChatFormatting.TextColour.INFO + "Your role has been changed to ")
                            .append(
                                    newRole.getNameWithDescription()
                                            .withStyle(ChatFormatting.DARK_PURPLE)
                            )
            );
        }

        return 1;
    }


    private static int checkPlayerCanSetToRole(final CommandSourceStack source, final FactionPlayer fPlayer, final String parentRoleName) {
        final Faction faction = fPlayer.getFaction();

        final int playerRoleIndex = faction.getRoleIndex(fPlayer.getRoleId());
        final int parentRoleIndex = faction.getRoleIndexByName(parentRoleName);

        if (parentRoleIndex < 0) {
            source.sendFailure(Component.literal("Failed to find a role named " + parentRoleName));
            return 1;
        } else if (parentRoleIndex < playerRoleIndex) {
            source.sendFailure(Component.literal("You cannot set a player to a role at or above your own the faction hierarchy"));
            return 2;
        }

        return 0;
    }
}
package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionChangeOwnerEvent;
import com.datdeveloper.datfactions.api.events.FactionPlayerChangeMembershipEvent;
import com.datdeveloper.datfactions.api.events.FactionPlayerChangeRoleEvent;
import com.datdeveloper.datfactions.commands.suggestions.FactionRoleSuggestionProvider;
import com.datdeveloper.datfactions.commands.suggestions.OwnFPlayerSuggestionProvider;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.factiondata.permissions.ERolePermissions;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import com.datdeveloper.datfactions.factiondata.relations.EFactionRelation;
import com.datdeveloper.datfactions.util.RelationUtil;
import com.datdeveloper.datmoddingapi.command.util.Pager;
import com.datdeveloper.datmoddingapi.concurrentTask.ConcurrentHandler;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import java.util.Comparator;
import java.util.List;

import static com.datdeveloper.datfactions.commands.FactionPermissions.*;

/**
 * Commands for managing players in a faction
 */
public class FactionPlayersCommand {
    /**
     * The argument for the player being targeted by the command
     */
    static final String TARGET_ARG = "Target Player";

    private static final CommandSyntaxException UNKNOWN_PLAYER_EXCEPTION = new SimpleCommandExceptionType(Component.literal("Cannot find a player with that name")).create();

    private static final CommandSyntaxException NOT_SAME_FACTION_EXCEPTION = new SimpleCommandExceptionType(Component.literal("That player is not in your faction")).create();
    private static final CommandRuntimeException BAD_AUTHORITY_EXCEPTION = new CommandRuntimeException(Component.literal("You are not allowed to change the role of that player"));

    /**
     * Visitor to register the command
     */
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        command.then(Commands.literal("players")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasAnyPermissions(commandSourceStack.getPlayer(), FACTION_LIST_PLAYERS, FACTION_PROMOTE, FACTION_DEMOTE, FACTION_SET_ROLE, FACTION_SET_OWNER, FACTION_KICK)))
                        return false;

                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    final Faction faction = fPlayer.getFaction();
                    return faction != null
                            && fPlayer.getRole().hasAnyPermissions(ERolePermissions.LISTPLAYERS, ERolePermissions.PROMOTE, ERolePermissions.DEMOTE, ERolePermissions.SETROLE, ERolePermissions.KICK);
                })
                .then(buildListCommand())
                .then(buildPromoteCommand())
                .then(buildDemoteCommand())
                .then(buildSetRoleCommand())
                .then(buildSetOwnerCommand())
                .then(buildKickCommand())
                .build()
        );
    }

    /* ========================================= */
    /* list                                      */
    /* ========================================= */

    /**
     * The argument used for the page number
     * <br>
     * Only used by list
     */
    static final String PAGE_ARG = "Page";

    /**
     * Build the list command
     */
    static LiteralArgumentBuilder<CommandSourceStack> buildListCommand() {
        return Commands.literal("list")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);

                    return DatPermissions.hasPermission(player, FACTION_LIST_PLAYERS)
                            && fPlayer.getRole().hasPermission(ERolePermissions.LISTPLAYERS);
                })
                .then(Commands.argument(PAGE_ARG, IntegerArgumentType.integer(1))
                                .executes(c -> executeList(c.getSource(), c.getArgument(PAGE_ARG, Integer.class))))
                .executes(c -> executeList(c.getSource(), 1));
    }

    /**
     * Handle the list command
     * <p>
     *     This command is executed concurrently to ensure the cost of processing the players doesn't slow down the
     *     server
     * </p>
     * @param sourceStack The caller of the command
     * @param page The page of the list to view
     * @return 1 for success
     */
    private static int executeList(final CommandSourceStack sourceStack, final int page) {
        final ServerPlayer player = sourceStack.getPlayer();
        final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
        final Faction faction = fPlayer.getFaction();

        ConcurrentHandler.runConcurrentTask(() -> {
            final List<FactionPlayer> values = faction.getPlayers().stream()
                    .sorted(Comparator.comparingInt((FactionPlayer sortPlayer) -> sortPlayer.getRole().getRoleLevel()).thenComparing((FactionPlayer sortPlayer) -> sortPlayer.getRole().getName()).thenComparing(FactionPlayer::getName))
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
                                            .withStyle(playerEl.isPlayerOnline()
                                                    ? ChatFormatting.GREEN
                                                    : ChatFormatting.RED)
                            ))
            );
            pager.sendPage(page, sourceStack.source);
        });

        return 1;
    }

    /* ========================================= */
    /* Promote                                   */
    /* ========================================= */

    /**
     * Build the promote command
     */
    static LiteralArgumentBuilder<CommandSourceStack> buildPromoteCommand() {
        return Commands.literal("promote")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);

                    return DatPermissions.hasPermission(player, FACTION_PROMOTE)
                            && fPlayer.getRole().hasPermission(ERolePermissions.PROMOTE);
                })
                .then(Commands.argument(TARGET_ARG, StringArgumentType.word())
                                .suggests(new OwnFPlayerSuggestionProvider(true))
                                .executes(c -> executePromote(c, c.getArgument(TARGET_ARG, String.class)))
                );
    }

    /**
     * Execute the promote command
     * @param c The command context
     * @param targetName The name of the player being promoted
     * @return 1 if successful
     * @throws CommandSyntaxException When a player name is given of whom does not exist or is not a member of the
     *                                instigators faction
     */
    private static int executePromote(final CommandContext<CommandSourceStack> c, final String targetName) throws CommandSyntaxException {
        final ServerPlayer player = c.getSource().getPlayer();
        final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
        final Faction faction = fPlayer.getFaction();

        final FactionPlayer target = FPlayerCollection.getInstance().getByName(targetName);
        if (target == null) {
            throw UNKNOWN_PLAYER_EXCEPTION;
        } else if (!faction.getPlayers().contains(target)) {
            throw NOT_SAME_FACTION_EXCEPTION;
        } else if (!faction.hasAuthorityOver(fPlayer, target)) {
            // The player needs to have authority over the target's current role
            // This should cover when someone tries to promote the owner
            throw BAD_AUTHORITY_EXCEPTION;
        }

        final FactionRole newRole = target.getRole().getParent();

        return setRole(c, faction, fPlayer, target, newRole, FactionPlayerChangeRoleEvent.EChangeRoleReason.PROMOTE, true);
    }

    /* ========================================= */
    /* Demote                                    */
    /* ========================================= */

    /**
     * Build the demote command
     */
    static LiteralArgumentBuilder<CommandSourceStack> buildDemoteCommand() {
        return Commands.literal("demote")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_DEMOTE) && fPlayer.getRole().hasPermission(ERolePermissions.DEMOTE);
                })
                .then(
                        Commands.argument(TARGET_ARG, StringArgumentType.word())
                                .suggests(new OwnFPlayerSuggestionProvider(true))
                                .executes(c -> executeDemote(c, c.getArgument(TARGET_ARG, String.class)))
                );
    }

    /**
     * Execute the demote command
     * @param c The command context
     * @param targetName The name of the player being demoted
     * @return 1 if successful
     * @throws CommandSyntaxException When a player name is given of whom does not exist or is not a member of the
     *                                instigators faction
     */
    private static int executeDemote(final CommandContext<CommandSourceStack> c, final String targetName) throws CommandSyntaxException {
        final ServerPlayer player = c.getSource().getPlayer();
        final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
        final Faction faction = fPlayer.getFaction();

        final FactionPlayer target = FPlayerCollection.getInstance().getByName(targetName);

        if (target == null) {
            throw UNKNOWN_PLAYER_EXCEPTION;
        } else if (!faction.getPlayers().contains(target)) {
            throw NOT_SAME_FACTION_EXCEPTION;
        } else if (!faction.hasAuthorityOver(fPlayer, target)) {
            throw BAD_AUTHORITY_EXCEPTION;
        }

        FactionRole newRole;
        try {
            newRole = target.getRole().getChild(0);
        } catch (final IndexOutOfBoundsException e) {
            newRole = null;
        }

        return setRole(c, faction, fPlayer, target, newRole, FactionPlayerChangeRoleEvent.EChangeRoleReason.DEMOTE, true);
    }

    /* ========================================= */
    /* Set Role                                  */
    /* ========================================= */

    /**
     * The name of the role the player is being set to
     * <br>
     * Only used by Set Role
     */
    static final String NEW_ROLE_ARG = "New Role";

    /**
     * Build the Set Role command
     */
    static LiteralArgumentBuilder<CommandSourceStack> buildSetRoleCommand() {
        return Commands.literal("setrole")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_SET_ROLE) && fPlayer.getRole().hasPermission(ERolePermissions.SETROLE);
                })
                .then(
                        Commands.argument(TARGET_ARG, StringArgumentType.word())
                                .suggests(new OwnFPlayerSuggestionProvider(true))
                                .then(
                                        Commands.argument(NEW_ROLE_ARG, StringArgumentType.word())
                                                .suggests(new FactionRoleSuggestionProvider(true, false))
                                                .executes(c -> setRole(c, c.getArgument(TARGET_ARG, String.class), c.getArgument(NEW_ROLE_ARG, String.class)))
                                )
                );
    }

    /**
     * Execute the Set Role command
     * @param c The command context
     * @param targetName The name of the player whose role is being changed
     * @param newRoleName The name of the new role
     * @return 1 if successful
     * @throws CommandSyntaxException When a player name is given of whom does not exist or is not a member of the
     *                                instigators faction
     */
    private static int setRole(final CommandContext<CommandSourceStack> c, final String targetName, final String newRoleName) throws CommandSyntaxException {
        final ServerPlayer player = c.getSource().getPlayer();
        final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
        final Faction faction = fPlayer.getFaction();

        final FactionPlayer target = FPlayerCollection.getInstance().getByName(targetName);

        if (target == null) {
            throw UNKNOWN_PLAYER_EXCEPTION;
        } else if (!faction.getPlayers().contains(target)) {
            throw NOT_SAME_FACTION_EXCEPTION;
        } else if (!faction.hasAuthorityOver(fPlayer, target)) {
            throw BAD_AUTHORITY_EXCEPTION;
        }

        final FactionRole newRole = faction.getRoleByName(newRoleName);

        if (newRole == null) {
            throw new CommandRuntimeException(Component.literal("That role doesn't exist"));
        }

        return setRole(c, faction, fPlayer, target, newRole, FactionPlayerChangeRoleEvent.EChangeRoleReason.SET, true);
    }

    /* ========================================= */
    /* Set Owner
    /* ========================================= */

    /**
     * Build the Set Owner command
     */
    static LiteralArgumentBuilder<CommandSourceStack> buildSetOwnerCommand() {
        return Commands.literal("setowner")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);

                    return DatPermissions.hasPermission(player, FACTION_SET_OWNER)
                            && fPlayer.getRole().equals(fPlayer.getFaction().getOwnerRole());
                })
                .then(
                        Commands.argument(TARGET_ARG, StringArgumentType.word())
                                .suggests(new OwnFPlayerSuggestionProvider(true))
                                .executes(c -> executeSetOwner(c, c.getArgument(TARGET_ARG, String.class)))
                );
    }

    /**
     * Execute the Set Owner command
     * @param c The command context
     * @param targetName The name of the player being made owner
     * @return 1 if successful
     * @throws CommandSyntaxException When a player name is given of whom does not exist or is not a member of the
     *                                instigators faction
     */
    private static int executeSetOwner(final CommandContext<CommandSourceStack> c, final String targetName) throws CommandSyntaxException {
        final ServerPlayer player = c.getSource().getPlayer();
        final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
        final Faction faction = fPlayer.getFaction();

        final FactionPlayer target = FPlayerCollection.getInstance().getByName(targetName);

        if (target == null) {
            throw UNKNOWN_PLAYER_EXCEPTION;
        } else if (!faction.getPlayers().contains(target)) {
            throw NOT_SAME_FACTION_EXCEPTION;
        }

        final FactionChangeOwnerEvent event = new FactionChangeOwnerEvent.Pre(c.getSource().getPlayer(), faction, target);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return 0;

        // There does exist a case where the owner has no children, so we gotta make sure they then get assigned a role
        FactionRole newRole;
        try {
            newRole = faction.getOwnerRole().getChild(0);
        } catch (final IndexOutOfBoundsException e) {
            newRole = faction.getDefaultRole();
        }

        setRole(c, faction, fPlayer, fPlayer, newRole, FactionPlayerChangeRoleEvent.EChangeRoleReason.CHANGE_OWNER, false);
        setRole(c, faction, fPlayer, target, faction.getOwnerRole(), FactionPlayerChangeRoleEvent.EChangeRoleReason.CHANGE_OWNER, false);

        c.getSource().sendSuccess(() ->
                        Component.literal(DatChatFormatting.TextColour.INFO + "Successfully made ")
                                .append(
                                        target.getNameWithDescription(faction)
                                                .withStyle(EFactionRelation.SELF.formatting)
                                )
                                .append(DatChatFormatting.TextColour.INFO + " to ")
                                .append(" the owner of ")
                                .append(
                                        faction.getNameWithDescription(faction)
                                                .withStyle(EFactionRelation.SELF.formatting)
                                ),
                false
        );

        if (target.isPlayerOnline()) {
            target.getServerPlayer().sendSystemMessage(
                    Component.literal(DatChatFormatting.TextColour.INFO + "You are now the owner of ")
                            .append(
                                    faction.getNameWithDescription(faction)
                                            .withStyle(EFactionRelation.SELF.formatting)
                            )
            );
        }

        return 1;
    }

    /* ========================================= */
    /* Kick                                      */
    /* ========================================= */

    /**
     * Build the Kick command
     */
    static LiteralArgumentBuilder<CommandSourceStack> buildKickCommand() {
        return Commands.literal("kick")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_KICK) && fPlayer.getRole().hasPermission(ERolePermissions.KICK);
                })
                .then(
                        Commands.argument(TARGET_ARG, StringArgumentType.word())
                                .suggests(new OwnFPlayerSuggestionProvider(true))
                                .executes(c -> executeKick(c, c.getArgument(TARGET_ARG, String.class)))
                );
    }

    /**
     * Execute the kick command
     * @param c The command context
     * @param targetName The name of the player being kicked
     * @return 1 if successful
     * @throws CommandSyntaxException When a player name is given of whom does not exist or is not a member of the
     *                                instigators faction
     */
    private static int executeKick(final CommandContext<CommandSourceStack> c, final String targetName) throws CommandSyntaxException {
        final ServerPlayer player = c.getSource().getPlayer();
        final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
        final Faction faction = fPlayer.getFaction();

        final FactionPlayer target = FPlayerCollection.getInstance().getByName(targetName);

        if (target == null) {
            throw UNKNOWN_PLAYER_EXCEPTION;
        } else if (!faction.getPlayers().contains(target)) {
            throw NOT_SAME_FACTION_EXCEPTION;
        }

        final FactionPlayerChangeMembershipEvent.Pre event = new FactionPlayerChangeMembershipEvent.Pre(c.getSource().getPlayer(),
                target,
                null,
                null,
                FactionPlayerChangeMembershipEvent.EChangeFactionReason.KICK);
        MinecraftForge.EVENT_BUS.post(event);

        final Event.Result result = event.getResult();

        if (result == Event.Result.DENY) {
            final Component reason = event.getDenyReason();
            if (reason != null) {
                throw new CommandRuntimeException(reason);
            } else {
                return 0;
            }
        } else if (faction.hasAuthorityOver(fPlayer, target)) {
            throw new CommandRuntimeException(Component.literal("You are not allowed to kick that player"));
        }

        final Faction newFaction = event.getNewFaction();
        final FactionRole role = event.getNewRole();

        fPlayer.setFaction(
                newFaction,
                role,
                FactionPlayerChangeMembershipEvent.EChangeFactionReason.KICK
        );

        c.getSource().sendSuccess(() ->
                        Component.literal(DatChatFormatting.TextColour.INFO + "Successfully kicked ")
                                .append(
                                        target.getNameWithDescription(faction)
                                                .withStyle(RelationUtil.getRelation(faction, target).formatting)
                                )
                                .append(" from the faction"),
                false
        );

        return 1;
    }

    /* ========================================= */
    /* Util
    /* ========================================= */

    /**
     * Set the given targets role to the given role
     * @param c The command context (for sending success or failure messages)
     * @param faction The faction the player's belong to
     * @param fPlayer The player instigating the role change
     * @param target The target of the role change
     * @param passedRole The new role being applied to the target
     * @param reason The reason for the role change
     * @param notify Whether to notify the player's of the successful role change
     * @return 1 if successful
     */
    private static int setRole(final CommandContext<CommandSourceStack> c,
                               final Faction faction,
                               final FactionPlayer fPlayer,
                               final FactionPlayer target,
                               final FactionRole passedRole,
                               final FactionPlayerChangeRoleEvent.EChangeRoleReason reason,
                               final boolean notify) {
        final FactionPlayerChangeRoleEvent.Pre event = new FactionPlayerChangeRoleEvent.Pre(
                c.getSource().getPlayer(),
                target,
                passedRole,
                reason
        );
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            final Component denyReason = event.getDenyReason();
            if (denyReason != null) {
                throw new CommandRuntimeException(denyReason);
            } else {
                return 0;
            }
        }

        final FactionRole newRole = event.getNewRole() == null ? faction.getDefaultRole() : event.getNewRole();

        if (!faction.hasAuthorityOver(fPlayer.getRole(), newRole)) {
            // The player needs to have authority over the targets new role
            throw new CommandRuntimeException(Component.literal("You are not allowed to set the player to ")
                    .append(newRole.getNameWithDescription()));
        }

        target.setRole(newRole, reason);

        if (notify) {
            c.getSource().sendSuccess(() ->
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
                    false
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
        }

        return 1;
    }
}

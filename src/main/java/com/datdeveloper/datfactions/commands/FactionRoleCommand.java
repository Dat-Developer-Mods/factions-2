package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.*;
import com.datdeveloper.datfactions.commands.suggestions.DatSuggestionProviders;
import com.datdeveloper.datfactions.commands.suggestions.FactionRoleSuggestionProvider;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factionData.relations.EFactionRelation;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datfactions.factionData.permissions.FactionRole;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.datdeveloper.datfactions.commands.FactionPermissions.*;

public class FactionRoleCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {

        final LiteralArgumentBuilder<CommandSourceStack> subCommand = Commands.literal("roles")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasAnyPermissions(commandSourceStack.getPlayer(), FACTION_ROLE_ADD, FACTION_ROLE_REMOVE, FACTION_ROLE_RENAME, FACTION_ROLE_LIST, FACTION_ROLE_INFO, FACTION_ROLE_MODIFY_PERMISSIONS, FACTION_ROLE_REORDER)))
                        return false;
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return fPlayer.hasFaction() && fPlayer.getRole().hasAnyPermissions(ERolePermissions.ROLECREATE, ERolePermissions.ROLEREMOVE, ERolePermissions.ROLERENAME, ERolePermissions.ROLELIST, ERolePermissions.ROLEINFO, ERolePermissions.ROLEMODIFYPERMISSIONS, ERolePermissions.ROLEREORDER);
                })
                .then(buildRoleAddCommand())
                .then(buildRoleRemoveCommand())
                .then(buildRoleRenameCommand())
                .then(buildRoleListCommand())
                .then(buildRoleInfoCommand())
                .then(buildRolePermissionsCommand())
                .then(buildRoleSetParentCommand());

        command.then(subCommand.build());
    }

    /* ========================================= */
    /* Role Add
    /* ========================================= */

    static LiteralArgumentBuilder<CommandSourceStack> buildRoleAddCommand() {
        return Commands.literal("add")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_ROLE_ADD) && fPlayer.getRole().hasPermission(ERolePermissions.ROLECREATE);
                })
                .then(
                        Commands.argument("Name", StringArgumentType.word())
                                .then(
                                        Commands.argument("Parent Role", StringArgumentType.word())
                                                .suggests(new FactionRoleSuggestionProvider(true, true))
                                                .executes(c -> {
                                                    final ServerPlayer player = c.getSource().getPlayer();
                                                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                                                    final Faction faction = fPlayer.getFaction();

                                                    final String parentRoleName = c.getArgument("Parent Role", String.class);
                                                    {
                                                        final int result = checkPlayerCanUseParent(c.getSource(), fPlayer, parentRoleName);
                                                        if (result != 0) return result + 3;
                                                    }
                                                    return addRole(c.getSource(), faction, c.getArgument("Name", String.class), faction.getRoleByName(parentRoleName));
                                                })
                                )
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
                                    final Faction faction = fPlayer.getFaction();

                                    final List<FactionRole> roles = faction.getRoles();

                                    if (faction.getRoleIndex(fPlayer.getRoleId()) == roles.size() - 1) {
                                        c.getSource().sendFailure(Component.literal("You cannot create new roles as the recruit role"));
                                        return 5;
                                    }

                                    return addRole(c.getSource(), faction, c.getArgument("Name", String.class), roles.get(roles.size() - 1));
                                })
                );
    }

    static int addRole(final CommandSourceStack source, final Faction faction, final String newRoleName, final FactionRole parent) {
        {
            final int result = checkRoleNameValid(source, faction, newRoleName);
            if (result != 0) {
                return result;
            }
        }

        final FactionRoleCreateEvent event = new FactionRoleCreateEvent(source.source, faction, newRoleName, parent);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return 0;

        final FactionRole newRole = faction.createNewRole(event.getNewRoleName(), event.getNewRoleParent());
        source.sendSuccess(Component.literal(DatChatFormatting.TextColour.INFO + "Successfully Created new role ")
                .append(newRole.getNameWithDescription().withStyle(ChatFormatting.DARK_PURPLE)),
                false
        );

        return 1;
    }

    /* ========================================= */
    /* Role Remove
    /* ========================================= */

    private static LiteralArgumentBuilder<CommandSourceStack> buildRoleRemoveCommand() {
        return Commands.literal("remove")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_ROLE_REMOVE) && fPlayer.getRole().hasPermission(ERolePermissions.ROLEREMOVE);
                })
                .then(
                        Commands.argument("Role", StringArgumentType.word())
                                .suggests(new FactionRoleSuggestionProvider(true, false))
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                                    final Faction faction = fPlayer.getFaction();

                                    final String roleName = c.getArgument("Role", String.class);

                                    {
                                        final int result = checkPlayerCanModifyRole(c.getSource(), fPlayer, roleName, false);
                                        if (result != 0) {
                                            return result + 1;
                                        }
                                    }

                                    final FactionRole role = faction.getRoleByName(roleName);

                                    final FactionRoleRemoveEvent event = new FactionRoleRemoveEvent(c.getSource().source, faction, role);
                                    MinecraftForge.EVENT_BUS.post(event);
                                    if (event.isCanceled()) return 0;

                                    faction.removeRole(role);

                                    c.getSource().sendSuccess(
                                            Component.literal(DatChatFormatting.TextColour.INFO + "Successfully removed ")
                                                    .append(role.getNameWithDescription().withStyle(ChatFormatting.DARK_PURPLE)),
                                            false
                                    );

                                    return 1;
                                })
                );
    }

    /* ========================================= */
    /* Role Rename
    /* ========================================= */

    private static LiteralArgumentBuilder<CommandSourceStack> buildRoleRenameCommand() {
        return Commands.literal("rename")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_ROLE_RENAME) && fPlayer.getRole().hasPermission(ERolePermissions.ROLERENAME);
                })
                .then(
                        Commands.argument("Role", StringArgumentType.word())
                                .suggests(new FactionRoleSuggestionProvider(true, false))
                                .then(
                                        Commands.argument("New Name", StringArgumentType.word())
                                                .executes(c -> {
                                                    final ServerPlayer player = c.getSource().getPlayer();
                                                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                                                    final Faction faction = fPlayer.getFaction();

                                                    final String roleName = c.getArgument("Role", String.class);
                                                    {
                                                        final int result = checkPlayerCanModifyRole(c.getSource(), fPlayer, roleName, true);
                                                        if (result != 0) {
                                                            return result + 1;
                                                        }
                                                    }
                                                    final FactionRole role = faction.getRoleByName(roleName);

                                                    final String newName = c.getArgument("New Name", String.class);
                                                    final FactionRoleChangeNameEvent event = new FactionRoleChangeNameEvent(c.getSource().source, faction, role, newName);
                                                    MinecraftForge.EVENT_BUS.post(event);
                                                    if (event.isCanceled()) return 0;
                                                    role.setName(newName);

                                                    c.getSource().sendSuccess(
                                                            Component.literal(DatChatFormatting.TextColour.INFO + "Successfully changed " + roleName + " to ")
                                                                    .append(role.getNameWithDescription().withStyle(ChatFormatting.DARK_PURPLE)),
                                                            false
                                                    );

                                                    return 1;
                                                })
                                )
                );
    }

    /* ========================================= */
    /* Role List
    /* ========================================= */

    private static LiteralArgumentBuilder<CommandSourceStack> buildRoleListCommand() {
        return Commands.literal("list")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_ROLE_LIST) && fPlayer.getRole().hasPermission(ERolePermissions.ROLELIST);
                })
                .executes(c -> {
                    final ServerPlayer player = c.getSource().getPlayer();
                    final Faction faction = FactionCommandUtils.getPlayerOrTemplate(player).getFaction();
                    final MutableComponent message = Component.empty()
                            .append(
                                    faction.getNameWithDescription(faction)
                                            .withStyle(EFactionRelation.SELF.formatting)
                                            .append(DatChatFormatting.TextColour.INFO + " roles: ")
                            );

                    final List<MutableComponent> roles = faction.getRoles().stream()
                            .map(factionRole -> factionRole.getNameWithDescription().withStyle(ChatFormatting.DARK_PURPLE))
                            .toList();
                    message.append(ComponentUtils.formatList(roles, ComponentUtils.DEFAULT_SEPARATOR));

                    c.getSource().sendSystemMessage(message);

                    return 1;
                });
    }

    /* ========================================= */
    /* Role Info
    /* ========================================= */

    private static LiteralArgumentBuilder<CommandSourceStack> buildRoleInfoCommand() {
        return Commands.literal("info")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_ROLE_INFO) && fPlayer.getRole().hasPermission(ERolePermissions.ROLEINFO);
                })
                .then(
                        Commands.argument("Role", StringArgumentType.word())
                                .suggests(new FactionRoleSuggestionProvider(false, true))
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                                    final Faction faction = fPlayer.getFaction();

                                    final String roleName = c.getArgument("Role", String.class);
                                    {
                                        final int result = checkPlayerCanModifyRole(c.getSource(), fPlayer, roleName, true);
                                        if (result != 0) {
                                            return result + 1;
                                        }
                                    }
                                    final FactionRole role = faction.getRoleByName(roleName);

                                    c.getSource().sendSystemMessage(role.getChatSummary());
                                    return 1;
                                })
                )
                .executes(c -> {
                    final ServerPlayer player = c.getSource().getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);

                    final FactionRole role = fPlayer.getRole();

                    c.getSource().sendSystemMessage(role.getChatSummary());
                    return 1;
                });
    }

    /* ========================================= */
    /* Set Permissions
    /* ========================================= */

    private static LiteralArgumentBuilder<CommandSourceStack> buildRolePermissionsCommand() {
        return Commands.literal("permissions")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_ROLE_MODIFY_PERMISSIONS) && fPlayer.getRole().hasPermission(ERolePermissions.ROLEMODIFYPERMISSIONS);
                })
                .then(
                        Commands.argument("Role", StringArgumentType.word())
                                .suggests(new FactionRoleSuggestionProvider(true, false))
                                .then(buildRolePermissionsListCommand())
                                .then(buildRolePermissionsAddCommand())
                                .then(buildRolePermissionsRemoveCommand())
                                .then(buildRolePermissionsSetAdminCommand())
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                                    final Faction faction = fPlayer.getFaction();

                                    final String roleName = c.getArgument("Role", String.class);
                                    {
                                        final int result = checkPlayerCanModifyRole(c.getSource(), fPlayer, roleName, true);
                                        if (result != 0) {
                                            return result + 1;
                                        }
                                    }
                                    final FactionRole role = faction.getRoleByName(roleName);
                                    return 1;
                                })
                );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildRolePermissionsListCommand() {
        return Commands.literal("list")
                .executes(c -> {
                    final ServerPlayer player = c.getSource().getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    final Faction faction = fPlayer.getFaction();

                    final String roleName = c.getArgument("Role", String.class);
                    {
                        final int result = checkPlayerCanModifyRole(c.getSource(), fPlayer, roleName, true);
                        if (result != 0) {
                            return result + 1;
                        }
                    }
                    final FactionRole role = faction.getRoleByName(roleName);

                    if (role.isAdministrator()) {
                        c.getSource().sendSystemMessage(
                                Component.empty()
                                        .append(
                                                role.getNameWithDescription()
                                                        .withStyle(ChatFormatting.DARK_PURPLE)
                                        )
                                        .append(DatChatFormatting.TextColour.INFO + " is an administrator and thus has all the permissions")
                        );
                    } else {
                        final List<MutableComponent> permissions = role.getPermissions().stream()
                                .sorted()
                                .map(ERolePermissions::getChatComponent)
                                .toList();

                        c.getSource().sendSystemMessage(
                                Component.empty()
                                        .append(
                                                role.getNameWithDescription()
                                                        .withStyle(ChatFormatting.DARK_PURPLE)
                                        )
                                        .append(DatChatFormatting.TextColour.INFO + " has the following permissions:").append("\n")
                                        .append(ComponentUtils.formatList(permissions, ComponentUtils.DEFAULT_SEPARATOR))
                        );
                    }

                    return 1;
                });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildRolePermissionsAddCommand() {
        return Commands.literal("add")
                .then(
                        Commands.argument("Permission", StringArgumentType.word())
                                .suggests(DatSuggestionProviders.permissionProvider)
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                                    final Faction faction = fPlayer.getFaction();

                                    final String roleName = c.getArgument("Role", String.class);
                                    {
                                        final int result = checkPlayerCanModifyRole(c.getSource(), fPlayer, roleName, true);
                                        if (result != 0) {
                                            return result + 1;
                                        }
                                    }
                                    final FactionRole role = faction.getRoleByName(roleName);

                                    final String permissionName = c.getArgument("Permission", String.class);
                                    final ERolePermissions permission;
                                    try {
                                        permission = ERolePermissions.valueOf(permissionName.toUpperCase());
                                    } catch (final IllegalArgumentException ignored) {
                                        c.getSource().sendFailure(Component.literal("Unknown role permission: " + permissionName));
                                        return 6;
                                    }

                                    // Check they have permission to use the permission
                                    if (!fPlayer.getRole().hasPermission(permission)) {
                                        c.getSource().sendFailure(Component.literal("You may only give role permissions you already have"));
                                        return 7;
                                    } else if (role.hasPermission(permission)) {
                                        c.getSource().sendFailure(Component.literal("That role already has that permission"));
                                        return 8;
                                    }

                                    final FactionRoleAddPermissionsEvent event = new FactionRoleAddPermissionsEvent(c.getSource().source, faction, role, new HashSet<>(Set.of(permission)));
                                    MinecraftForge.EVENT_BUS.post(event);
                                    if (event.isCanceled()) return 0;

                                    for (final ERolePermissions newPermission : event.getPermissions()) {
                                        role.addPermission(newPermission);
                                    }

                                    c.getSource().sendSuccess(
                                            Component.literal(DatChatFormatting.TextColour.INFO + "Successfully added permission"),
                                            false
                                    );

                                    return 1;
                                })
                );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildRolePermissionsRemoveCommand() {
        return Commands.literal("remove")
                .then(
                        Commands.argument("Permission", StringArgumentType.word())
                                .suggests(DatSuggestionProviders.permissionProvider)
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                                    final Faction faction = fPlayer.getFaction();

                                    final String roleName = c.getArgument("Role", String.class);
                                    {
                                        final int result = checkPlayerCanModifyRole(c.getSource(), fPlayer, roleName, true);
                                        if (result != 0) {
                                            return result + 1;
                                        }
                                    }
                                    final FactionRole role = faction.getRoleByName(roleName);

                                    final String permissionName = c.getArgument("Permission", String.class);
                                    final ERolePermissions permission;
                                    try {
                                        permission = ERolePermissions.valueOf(permissionName.toUpperCase());
                                    } catch (final IllegalArgumentException ignored) {
                                        c.getSource().sendFailure(Component.literal("Unknown role permission: " + permissionName));
                                        return 6;
                                    }

                                    // Check they have permission to use the permission
                                    if (!fPlayer.getRole().hasPermission(permission)) {
                                        c.getSource().sendFailure(Component.literal("You may only remove role permissions you already have"));
                                        return 7;
                                    } else if (!role.hasPermission(permission)) {
                                        c.getSource().sendFailure(Component.literal("That role doesn't have that permission to remove"));
                                        return 8;
                                    }

                                    final FactionRoleRemovePermissionsEvent event = new FactionRoleRemovePermissionsEvent(c.getSource().source, faction, role, new HashSet<>(Set.of(permission)));
                                    MinecraftForge.EVENT_BUS.post(event);
                                    if (event.isCanceled()) return 0;

                                    for (final ERolePermissions newPermission : event.getPermissions()) {
                                        role.removePermission(newPermission);
                                    }

                                    c.getSource().sendSuccess(
                                            Component.literal(DatChatFormatting.TextColour.INFO + "Successfully removed permission"),
                                            false
                                    );

                                    return 1;
                                })
                );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildRolePermissionsSetAdminCommand() {
        return Commands.literal("setadmin")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return fPlayer.getRole().isAdministrator();
                })
                .then(
                        Commands.argument("Admin", BoolArgumentType.bool())
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                                    final Faction faction = fPlayer.getFaction();

                                    final String roleName = c.getArgument("Role", String.class);
                                    {
                                        final int result = checkPlayerCanModifyRole(c.getSource(), fPlayer, roleName, true);
                                        if (result != 0) {
                                            return result + 1;
                                        }
                                    }
                                    final FactionRole role = faction.getRoleByName(roleName);

                                    final boolean newAdmin = c.getArgument("Admin", Boolean.class);

                                    final FactionRoleSetAdminEvent event = new FactionRoleSetAdminEvent(c.getSource().source, faction, role, newAdmin);
                                    MinecraftForge.EVENT_BUS.post(event);
                                    if (event.isCanceled()) return 0;

                                    role.setAdministrator(newAdmin);

                                    c.getSource().sendSuccess(
                                            Component.literal(DatChatFormatting.TextColour.INFO + "Successfully set ")
                                                    .append(role.getNameWithDescription().withStyle(ChatFormatting.DARK_PURPLE))
                                                    .append(DatChatFormatting.TextColour.INFO + " to be " + (newAdmin ? "" : "not ") + "an admin"),
                                            false
                                    );

                                    return 1;
                                })
                );
    }

    /* ========================================= */
    /* Reorder
    /* ========================================= */

    private static LiteralArgumentBuilder<CommandSourceStack> buildRoleSetParentCommand() {
        return Commands.literal("reorder")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_ROLE_REORDER) && fPlayer.getRole().hasPermission(ERolePermissions.ROLEREORDER);
                })
                .then(
                        Commands.argument("Role", StringArgumentType.word())
                                .suggests(new FactionRoleSuggestionProvider(true, false))
                                .then(
                                        Commands.argument("New Parent", StringArgumentType.word())
                                                .suggests(new FactionRoleSuggestionProvider(true, true))
                                                .executes(c -> {
                                                    final ServerPlayer player = c.getSource().getPlayer();
                                                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(player);
                                                    final Faction faction = fPlayer.getFaction();

                                                    final String roleName = c.getArgument("Role", String.class);
                                                    {
                                                        final int result = checkPlayerCanModifyRole(c.getSource(), fPlayer, roleName, false);
                                                        if (result != 0) {
                                                            return result + 1;
                                                        }
                                                    }
                                                    final FactionRole role = faction.getRoleByName(roleName);

                                                    final String parentRoleName = c.getArgument("New Parent", String.class);
                                                    {
                                                        final int result = checkPlayerCanUseParent(c.getSource(), fPlayer, parentRoleName);
                                                        if (result != 0) return result + 5;
                                                    }
                                                    FactionRole newParentRole = faction.getRoleByName(parentRoleName);

                                                    if (newParentRole.equals(role)) {
                                                        c.getSource().sendFailure(Component.literal("A role cannot be it's own parent"));
                                                        return 9;
                                                    }

                                                    final FactionRoleChangeOrderEvent event = new FactionRoleChangeOrderEvent(c.getSource().source, faction, role, newParentRole);
                                                    MinecraftForge.EVENT_BUS.post(event);
                                                    if (event.isCanceled()) return 0;

                                                    newParentRole = event.getNewParent();

                                                    faction.setRoleParent(role.getId(), newParentRole.getId());
                                                    c.getSource().sendSuccess(
                                                            Component.literal(DatChatFormatting.TextColour.INFO + "Successfully changed the parent of ")
                                                                    .append(role.getNameWithDescription().withStyle(ChatFormatting.DARK_PURPLE)),
                                                            false
                                                    );

                                                    return 1;
                                                })
                                )
                );
    }

    /* ========================================= */
    /* Util
    /* ========================================= */

    /**
     * Check the player is allowed to modify the given role
     * @param source The CommandSource calling the command
     * @param fPlayer The player to check for
     * @param roleName The name of the role
     * @param allowRecruit Whether to allow the player to modify the recruit role
     * @return a positive integer representing a return code, where 0 is success and everything else is a failure
     */
    private static int checkPlayerCanModifyRole(final CommandSourceStack source, final FactionPlayer fPlayer, final String roleName, final boolean allowRecruit) {
        final Faction faction = fPlayer.getFaction();
        final int roleIndex = faction.getRoleIndexByName(roleName);

        if (roleIndex < 0) {
            source.sendFailure(Component.literal("Failed to find a role named " + roleName));
            return 1;
        } else if (roleIndex < faction.getRoleIndex(fPlayer.getRoleId())) {
            source.sendFailure(Component.literal("You do not have permissions to modify that role"));
            return 2;
        } else if (!allowRecruit && roleIndex == faction.getRoles().size() - 1) {
            source.sendFailure(Component.literal("You cannot modify the recruit role"));
            return 3;
        } else if (roleIndex == 0) {
            source.sendFailure(Component.literal("You cannot modify the owner role"));
            return 4;
        }

        return 0;
    }

    private static int checkPlayerCanUseParent(final CommandSourceStack source, final FactionPlayer fPlayer, final String parentRoleName) {
        final Faction faction = fPlayer.getFaction();

        final int playerRoleIndex = faction.getRoleIndex(fPlayer.getRoleId());
        final int parentRoleIndex = faction.getRoleIndexByName(parentRoleName);

        if (parentRoleIndex < 0) {
            source.sendFailure(Component.literal("Failed to find a parent role named " + parentRoleName));
            return 1;
        } else if (parentRoleIndex == faction.getRoles().size() - 1) {
            source.sendFailure(Component.literal("You cannot use the recruit role as the role parent"));
            return 2;
        } else if (parentRoleIndex < playerRoleIndex) {
            source.sendFailure(Component.literal("You cannot use a role higher than yours in the faction hierarchy as the role parent"));
            return 3;
        }

        return 0;
    }

    /**
     * Check the given name is a valid role name
     * @param source The CommandSource calling the command
     * @param faction The faction the role is for
     * @param roleName The name to check
     * @return a positive integer representing a return code, where 0 is success and everything else is a failure
     */
    public static int checkRoleNameValid(final CommandSourceStack source, final Faction faction, final String roleName) {
        if (faction.getRoles().size() >= FactionsConfig.getMaxFactionRoles()) {
            source.sendFailure(Component.literal("Your faction cannot have more than " + FactionsConfig.getMaxFactionRoles() + " roles"));
            return 1;
        } else if (roleName.length() > FactionsConfig.getMaxFactionRoleNameLength()) {
            source.sendFailure(Component.literal("Role names cannot be longer than " + FactionsConfig.getMaxFactionRoleNameLength()));
            return 2;
        } else if (faction.getRoleByName(roleName) != null) {
            source.sendFailure(Component.literal("A role called " + roleName + " already exists"));
            return 3;
        }

        return 0;
    }
}

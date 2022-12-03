package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.Datfactions;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionNode.PermissionResolver;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = Datfactions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FactionPermissions {
    /* ========================================= */
    /* Resolvers
    /* ========================================= */
    public static final PermissionResolver<Boolean> PLAYER_OWNER = ((player, playerUUID, context) -> player != null && player.hasPermissions(Commands.LEVEL_OWNERS));
    public static final PermissionResolver<Boolean> PLAYER_ADMIN = ((player, playerUUID, context) -> player != null && player.hasPermissions(Commands.LEVEL_ADMINS));
    public static final PermissionResolver<Boolean> PLAYER_OP = ((player, playerUUID, context) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionResolver<Boolean> PLAYER_MOD = ((player, playerUUID, context) -> player != null && player.hasPermissions(Commands.LEVEL_MODERATORS));
    public static final PermissionResolver<Boolean> PLAYER_ALL = ((player, playerUUID, context) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));

    /* ========================================= */
    /* Permission Nodes
    /* ========================================= */
    // General
    public static final PermissionNode<Boolean> FACTION_LIST = createNode("datfactions.list");
    public static final PermissionNode<Boolean> FACTION_INFO = createNode("datfactions.info");
    public static final PermissionNode<Boolean> FACTION_PLAYER_INFO = createNode("datfactions.playerInfo");
    public static final PermissionNode<Boolean> FACTION_MAP = createNode("datfactions.map");

    // Basic Faction
    public static final PermissionNode<Boolean> FACTION_JOIN = createNode("datfactions.faction.join");
    public static final PermissionNode<Boolean> FACTION_LEAVE = createNode("datfactions.faction.leave");
    public static final PermissionNode<Boolean> FACTION_HOME = createNode("datfactions.faction.home");

    // Faction Management
    public static final PermissionNode<Boolean> FACTION_CREATE = createNode("datfactions.faction.create");
    public static final PermissionNode<Boolean> FACTION_SET_NAME = createNode("datfactions.faction.setName");
    public static final PermissionNode<Boolean> FACTION_SET_DESC = createNode("datfactions.faction.setDesc");
    public static final PermissionNode<Boolean> FACTION_SET_MOTD = createNode("datfactions.faction.setMotd");
    public static final PermissionNode<Boolean> FACTION_SET_HOME = createNode("datfactions.faction.setHome");
    public static final PermissionNode<Boolean> FACTION_DISBAND = createNode("datfactions.faction.disband");

    // Roles
    public static final PermissionNode<Boolean> FACTION_ROLE_ADD = createNode("datfactions.faction.role.add");
    public static final PermissionNode<Boolean> FACTION_ROLE_REMOVE = createNode("datfactions.faction.role.remove");
    public static final PermissionNode<Boolean> FACTION_ROLE_REORDER = createNode("datfactions.faction.role.reorder");
    public static final PermissionNode<Boolean> FACTION_ROLE_PERMISSIONS = createNode("datfactions.faction.role.permissions");

    // User Management
    public static final PermissionNode<Boolean> FACTION_PROMOTE = createNode("datfactions.faction.player.promote");
    public static final PermissionNode<Boolean> FACTION_DEMOTE = createNode("datfactions.faction.player.demote");
    public static final PermissionNode<Boolean> FACTION_SET_ROLE = createNode("datfactions.faction.player.setrole");
    public static final PermissionNode<Boolean> FACTION_KICK = createNode("datfactions.faction.player.kick");
    public static final PermissionNode<Boolean> FACTION_INVITE = createNode("datfactions.faction.player.invite");
    public static final PermissionNode<Boolean> FACTION_UNINVITE = createNode("datfactions.faction.player.invite");

    // Claims
    public static final PermissionNode<Boolean> FACTION_CLAIM_ONE = createNode("datfactions.faction.claim.one");
    public static final PermissionNode<Boolean> FACTION_CLAIM_AUTO = createNode("datfactions.faction.claim.auto");
    public static final PermissionNode<Boolean> FACTION_CLAIM_SQUARE = createNode("datfactions.faction.claim.square");
    public static final PermissionNode<Boolean> FACTION_UNCLAIM_ONE = createNode("datfactions.faction.unclaim.one");
    public static final PermissionNode<Boolean> FACTION_UNCLAIM_SQUARE = createNode("datfactions.faction.unclaim.square");
    public static final PermissionNode<Boolean> FACTION_UNCLAIM_LEVEL = createNode("datfactions.faction.unclaim.level");
    public static final PermissionNode<Boolean> FACTION_UNCLAIM_ALL = createNode("datfactions.faction.unclaim.all");

    /* ========================================= */
    /* Permission Builders
    /* ========================================= */

    private static PermissionNode<Boolean> createNode(final String node) {
        return nodeBuilder(node, PLAYER_ALL);
    }
    private static PermissionNode<Boolean> createOpNode(final String node) {
        return nodeBuilder(node, PLAYER_OP);
    }

    private static PermissionNode<Boolean> nodeBuilder(final String node, final PermissionResolver<Boolean> resolver) {
        return new PermissionNode<>(Datfactions.MOD_ID, node, PermissionTypes.BOOLEAN, resolver);
    }

    public static Predicate<CommandSourceStack> hasPermission(final PermissionNode<Boolean> node) {
        return (source) -> DatPermissions.hasPermission(source.source, node);
    }

    public static void registerPermissionNodes(final PermissionGatherEvent.Nodes event) {
        event.addNodes(
                FACTION_LIST,
                FACTION_INFO,
                FACTION_PLAYER_INFO,
                FACTION_MAP,
                FACTION_JOIN,
                FACTION_LEAVE,
                FACTION_HOME,
                FACTION_CREATE,
                FACTION_SET_NAME,
                FACTION_SET_DESC,
                FACTION_SET_MOTD,
                FACTION_SET_HOME,
                FACTION_DISBAND,
                FACTION_ROLE_ADD,
                FACTION_ROLE_REMOVE,
                FACTION_ROLE_REORDER,
                FACTION_ROLE_PERMISSIONS,
                FACTION_PROMOTE,
                FACTION_DEMOTE,
                FACTION_SET_ROLE,
                FACTION_KICK,
                FACTION_INVITE,
                FACTION_UNINVITE,
                FACTION_CLAIM_ONE,
                FACTION_CLAIM_AUTO,
                FACTION_CLAIM_SQUARE,
                FACTION_UNCLAIM_ONE,
                FACTION_UNCLAIM_SQUARE,
                FACTION_UNCLAIM_LEVEL,
                FACTION_UNCLAIM_ALL
        );
    }
}

package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.Datfactions;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionNode;

import static com.datdeveloper.datmoddingapi.permissions.DatPermissions.*;

@Mod.EventBusSubscriber(modid = Datfactions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FactionPermissions {
    /* ========================================= */
    /* Player Permission Nodes
    /* ========================================= */
    // General
    public static final PermissionNode<Boolean> FACTION_LIST = createBasicNode(Datfactions.MOD_ID, "datfactions.list");
    public static final PermissionNode<Boolean> FACTION_INFO = createBasicNode(Datfactions.MOD_ID, "datfactions.info");
    public static final PermissionNode<Boolean> FACTION_PLAYER_INFO = createBasicNode(Datfactions.MOD_ID, "datfactions.playerInfo");
    public static final PermissionNode<Boolean> FACTION_PLAYER_INVITES = createBasicNode(Datfactions.MOD_ID, "datfactions.playerInvites");
    public static final PermissionNode<Boolean> FACTION_MAP = createBasicNode(Datfactions.MOD_ID, "datfactions.map");

    // Basic Faction
    public static final PermissionNode<Boolean> FACTION_JOIN = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.join");
    public static final PermissionNode<Boolean> FACTION_LEAVE = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.leave");
    public static final PermissionNode<Boolean> FACTION_HOME = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.home");
    public static final PermissionNode<Boolean> FACTION_CHAT = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.chat");

    // Faction Management
    public static final PermissionNode<Boolean> FACTION_CREATE = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.create");
    public static final PermissionNode<Boolean> FACTION_SET_NAME = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.setName");
    public static final PermissionNode<Boolean> FACTION_SET_DESC = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.setDesc");
    public static final PermissionNode<Boolean> FACTION_SET_MOTD = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.setMotd");
    public static final PermissionNode<Boolean> FACTION_SET_HOME = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.setHome");
    public static final PermissionNode<Boolean> FACTION_DISBAND = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.disband");

    // Relations
    public static final PermissionNode<Boolean> FACTION_RELATION_LIST = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.relation.list");
    public static final PermissionNode<Boolean> FACTION_RELATION_WISHES = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.relation.wishes");
    public static final PermissionNode<Boolean> FACTION_RELATION_ALLY = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.relation.ally");
    public static final PermissionNode<Boolean> FACTION_RELATION_TRUCE = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.relation.truce");
    public static final PermissionNode<Boolean> FACTION_RELATION_NEUTRAL = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.relation.neutral");
    public static final PermissionNode<Boolean> FACTION_RELATION_ENEMY = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.relation.enemy");

    // Roles
    public static final PermissionNode<Boolean> FACTION_ROLE_ADD = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.role.add");
    public static final PermissionNode<Boolean> FACTION_ROLE_REMOVE = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.role.remove");
    public static final PermissionNode<Boolean> FACTION_ROLE_RENAME = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.role.rename");
    public static final PermissionNode<Boolean> FACTION_ROLE_LIST = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.role.list");
    public static final PermissionNode<Boolean> FACTION_ROLE_INFO = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.role.info");
    public static final PermissionNode<Boolean> FACTION_ROLE_REORDER = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.role.reorder");
    public static final PermissionNode<Boolean> FACTION_ROLE_MODIFY_PERMISSIONS = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.role.permissions");

    // User Management
    public static final PermissionNode<Boolean> FACTION_LIST_PLAYERS = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.player.list");
    public static final PermissionNode<Boolean> FACTION_PROMOTE = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.player.promote");
    public static final PermissionNode<Boolean> FACTION_DEMOTE = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.player.demote");
    public static final PermissionNode<Boolean> FACTION_SET_ROLE = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.player.setrole");
    public static final PermissionNode<Boolean> FACTION_SET_OWNER = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.player.setowner");
    public static final PermissionNode<Boolean> FACTION_KICK = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.player.kick");

    // Flags

    public static final PermissionNode<Boolean> FACTION_FLAG_LIST = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.flags.list");
    public static final PermissionNode<Boolean> FACTION_FLAG_ADD = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.flags.add");
    public static final PermissionNode<Boolean> FACTION_FLAG_REMOVE = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.flags.remove");

    // Invites
    public static final PermissionNode<Boolean> FACTION_INVITE = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.invite.invite");
    public static final PermissionNode<Boolean> FACTION_UNINVITE = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.invite.uninvite");
    public static final PermissionNode<Boolean> FACTION_INVITE_LIST_FACTION = createBasicNode(Datfactions.MOD_ID, "datFactions.faction.invite.list");

    // Claims
    public static final PermissionNode<Boolean> FACTION_CLAIM_ONE = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.claim.one");
    public static final PermissionNode<Boolean> FACTION_CLAIM_AUTO = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.claim.auto");
    public static final PermissionNode<Boolean> FACTION_CLAIM_SQUARE = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.claim.square");
    public static final PermissionNode<Boolean> FACTION_UNCLAIM_ONE = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.unclaim.one");
    public static final PermissionNode<Boolean> FACTION_UNCLAIM_SQUARE = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.unclaim.square");
    public static final PermissionNode<Boolean> FACTION_UNCLAIM_LEVEL = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.unclaim.level");
    public static final PermissionNode<Boolean> FACTION_UNCLAIM_ALL = createBasicNode(Datfactions.MOD_ID, "datfactions.faction.unclaim.all");

    /* ========================================= */
    /* Admin Permission Nodes
    /* ========================================= */

    // Player
    public static final PermissionNode<Boolean> ADMIN_PLAYER_SETPOWER = createOpNode(Datfactions.MOD_ID, "datfactions.admin.player.setpower");
    public static final PermissionNode<Boolean> ADMIN_PLAYER_SETMAXPOWER = createOpNode(Datfactions.MOD_ID, "datfactions.admin.player.setmaxpower");
    public static final PermissionNode<Boolean> ADMIN_PLAYER_SETFACTION = createModNode(Datfactions.MOD_ID, "datfactions.admin.player.setfaction");
    public static final PermissionNode<Boolean> ADMIN_PLAYER_SETROLE = createModNode(Datfactions.MOD_ID, "datfactions.admin.player.setrole");
    public static final PermissionNode<Boolean> ADMIN_PLAYER_SETAUTOCLAIM = createOpNode(Datfactions.MOD_ID, "datfactions.admin.player.setautoclaim");
    public static final PermissionNode<Boolean> ADMIN_PLAYER_SETCHATMODE = createOpNode(Datfactions.MOD_ID, "datfactions.admin.player.setchatmode");
    public static final PermissionNode<Boolean> ADMIN_PLAYER_SETCHUNKALERTMODE = createOpNode(Datfactions.MOD_ID, "datfactions.admin.player.setchunkalertmode");
    public static final PermissionNode<Boolean> ADMIN_PLAYER_DELETE = createOpNode(Datfactions.MOD_ID, "datfactions.admin.player.delete");

    // Faction
    public static final PermissionNode<Boolean> ADMIN_FACTION_SETNAME = createModNode(Datfactions.MOD_ID, "datfactions.admin.faction.setname");
    public static final PermissionNode<Boolean> ADMIN_FACTION_SETDESCRIPTION = createModNode(Datfactions.MOD_ID, "datfactions.admin.faction.setdescription");
    public static final PermissionNode<Boolean> ADMIN_FACTION_SETMOTD = createModNode(Datfactions.MOD_ID, "datfactions.admin.faction.setmotd");
    public static final PermissionNode<Boolean> ADMIN_FACTION_SETBONUSPOWER = createOpNode(Datfactions.MOD_ID, "datfactions.admin.faction.setbonuspower");
    public static final PermissionNode<Boolean> ADMIN_FACTION_SETHOME = createModNode(Datfactions.MOD_ID, "datfactions.admin.faction.sethome.self");
    public static final PermissionNode<Boolean> ADMIN_FACTION_SETHOMECOORD = createModNode(Datfactions.MOD_ID, "datfactions.admin.faction.sethome.coord");
    public static final PermissionNode<Boolean> ADMIN_FACTION_INVITE_LIST = createModNode(Datfactions.MOD_ID, "datfactions.admin.faction.invites.list");
    public static final PermissionNode<Boolean> ADMIN_FACTION_INVITE_ADD = createOpNode(Datfactions.MOD_ID, "datfactions.admin.faction.invites.add");
    public static final PermissionNode<Boolean> ADMIN_FACTION_INVITE_REMOVE = createOpNode(Datfactions.MOD_ID, "datfactions.admin.faction.invites.remove");
    public static final PermissionNode<Boolean> ADMIN_FACTION_ROLE_LIST = createModNode(Datfactions.MOD_ID, "datfactions.admin.faction.role.list");
    public static final PermissionNode<Boolean> ADMIN_FACTION_ROLE_ADD = createOpNode(Datfactions.MOD_ID, "datfactions.admin.faction.role.add");
    public static final PermissionNode<Boolean> ADMIN_FACTION_ROLE_REMOVE = createOpNode(Datfactions.MOD_ID, "datfactions.admin.faction.role.remove");
    public static final PermissionNode<Boolean> ADMIN_FACTION_ROLE_REORDER = createOpNode(Datfactions.MOD_ID, "datfactions.admin.faction.role.reorder");
    public static final PermissionNode<Boolean> ADMIN_FACTION_ROLE_PERMISSIONS = createOpNode(Datfactions.MOD_ID, "datfactions.admin.faction.role.permissions");
    public static final PermissionNode<Boolean> ADMIN_FACTION_FLAG_LIST = createModNode(Datfactions.MOD_ID, "datfactions.admin.faction.flag.list");
    public static final PermissionNode<Boolean> ADMIN_FACTION_FLAG_ADD = createModNode(Datfactions.MOD_ID, "datfactions.admin.faction.flag.add");
    public static final PermissionNode<Boolean> ADMIN_FACTION_FLAG_REMOVE = createModNode(Datfactions.MOD_ID, "datfactions.admin.faction.flag.remove");
    public static final PermissionNode<Boolean> ADMIN_FACTION_RELATIONS_LISTS = createModNode(Datfactions.MOD_ID, "datfactions.admin.faction.relation.list");
    public static final PermissionNode<Boolean> ADMIN_FACTION_RELATIONS_SET = createModNode(Datfactions.MOD_ID, "datfactions.admin.faction.relation.set");
    public static final PermissionNode<Boolean> ADMIN_FACTION_PLAYER_LIST = createModNode(Datfactions.MOD_ID, "datfactions.admin.faction.player.list");
    public static final PermissionNode<Boolean> ADMIN_FACTION_PLAYER_KICK = createModNode(Datfactions.MOD_ID, "datfactions.admin.faction.player.kick");
    public static final PermissionNode<Boolean> ADMIN_FACTION_PLAYER_SETOWNER = createOpNode(Datfactions.MOD_ID, "datfactions.admin.faction.player.setowner");
    public static final PermissionNode<Boolean> ADMIN_FACTION_PLAYER_SETROLE = createOpNode(Datfactions.MOD_ID, "datfactions.admin.faction.player.setrole");
    public static final PermissionNode<Boolean> ADMIN_FACTION_CLAIM_ONE = createModNode(Datfactions.MOD_ID, "datfactions.admin.faction.claim.one");
    public static final PermissionNode<Boolean> ADMIN_FACTION_CLAIM_SQUARE = createOpNode(Datfactions.MOD_ID, "datfactions.admin.faction.claim.square");
    public static final PermissionNode<Boolean> ADMIN_FACTION_UNCLAIM_ONE = createModNode(Datfactions.MOD_ID, "datfactions.admin.faction.unclaim.one");
    public static final PermissionNode<Boolean> ADMIN_FACTION_UNCLAIM_SQUARE = createOpNode(Datfactions.MOD_ID, "datfactions.admin.faction.unclaim.square");
    public static final PermissionNode<Boolean> ADMIN_FACTION_UNCLAIM_ALL = createOpNode(Datfactions.MOD_ID, "datfactions.admin.faction.unclaim.all");
    public static final PermissionNode<Boolean> ADMIN_FACTION_MESSAGE = createModNode(Datfactions.MOD_ID, "datfactions.admin.faction.message");
    public static final PermissionNode<Boolean> ADMIN_FACTION_DISBAND = createOpNode(Datfactions.MOD_ID, "datfactions.admin.faction.disband");

    // Level
    public static final PermissionNode<Boolean> ADMIN_LEVEL_CONFIGURE = createOpNode(Datfactions.MOD_ID, "datfactions.admin.level.configure");
    public static final PermissionNode<Boolean> ADMIN_LEVEL_SETCHUNKOWNER = createOpNode(Datfactions.MOD_ID, "datfactions.admin.level.setchunkowner");
    public static final PermissionNode<Boolean> ADMIN_LEVEL_RELEASEALL = createOpNode(Datfactions.MOD_ID, "datfactions.admin.level.releaseall");

    // Util
    public static final PermissionNode<Boolean> ADMIN_MAP = createOpNode(Datfactions.MOD_ID, "datfactions.admin.map");
    public static final PermissionNode<Boolean> ADMIN_BUILDOVERRIDE = createOpNode(Datfactions.MOD_ID, "datfactions.admin.buildoverride");
    public static final PermissionNode<Boolean> ADMIN_CHATSPY = createModNode(Datfactions.MOD_ID, "datfactions.admin.chatspy");

    public static void registerPermissionNodes(final PermissionGatherEvent.Nodes event) {
        event.addNodes(
                FACTION_LIST,
                FACTION_INFO,
                FACTION_PLAYER_INFO,
                FACTION_MAP,
                FACTION_PLAYER_INVITES,

                FACTION_JOIN,
                FACTION_HOME,
                FACTION_LEAVE,
                FACTION_CHAT,

                FACTION_CREATE,
                FACTION_SET_NAME,
                FACTION_SET_DESC,
                FACTION_SET_MOTD,
                FACTION_SET_HOME,
                FACTION_DISBAND,

                FACTION_ROLE_ADD,
                FACTION_ROLE_REMOVE,
                FACTION_ROLE_RENAME,
                FACTION_ROLE_LIST,
                FACTION_ROLE_INFO,
                FACTION_ROLE_REORDER,
                FACTION_ROLE_MODIFY_PERMISSIONS,

                FACTION_LIST_PLAYERS,
                FACTION_PROMOTE,
                FACTION_DEMOTE,
                FACTION_SET_ROLE,
                FACTION_SET_OWNER,
                FACTION_KICK,

                FACTION_INVITE_LIST_FACTION,
                FACTION_INVITE,
                FACTION_UNINVITE,

                FACTION_RELATION_LIST,
                FACTION_RELATION_WISHES,
                FACTION_RELATION_ALLY,
                FACTION_RELATION_TRUCE,
                FACTION_RELATION_NEUTRAL,
                FACTION_RELATION_ENEMY,

                FACTION_FLAG_LIST,
                FACTION_FLAG_ADD,
                FACTION_FLAG_REMOVE,

                FACTION_CLAIM_ONE,
                FACTION_CLAIM_AUTO,
                FACTION_CLAIM_SQUARE,
                FACTION_UNCLAIM_ONE,
                FACTION_UNCLAIM_SQUARE,
                FACTION_UNCLAIM_LEVEL,
                FACTION_UNCLAIM_ALL,

                ADMIN_PLAYER_SETPOWER,
                ADMIN_PLAYER_SETMAXPOWER,
                ADMIN_PLAYER_SETFACTION,
                ADMIN_PLAYER_SETROLE,
                ADMIN_PLAYER_SETAUTOCLAIM,
                ADMIN_PLAYER_SETCHATMODE,
                ADMIN_PLAYER_SETCHUNKALERTMODE,
                ADMIN_PLAYER_DELETE,

                ADMIN_FACTION_SETNAME,
                ADMIN_FACTION_SETDESCRIPTION,
                ADMIN_FACTION_SETMOTD,
                ADMIN_FACTION_SETBONUSPOWER,
                ADMIN_FACTION_SETHOME,
                ADMIN_FACTION_SETHOMECOORD,
                ADMIN_FACTION_INVITE_LIST,
                ADMIN_FACTION_INVITE_ADD,
                ADMIN_FACTION_INVITE_REMOVE,
                ADMIN_FACTION_ROLE_LIST,
                ADMIN_FACTION_ROLE_ADD,
                ADMIN_FACTION_ROLE_REMOVE,
                ADMIN_FACTION_ROLE_REORDER,
                ADMIN_FACTION_ROLE_PERMISSIONS,
                ADMIN_FACTION_FLAG_LIST,
                ADMIN_FACTION_FLAG_ADD,
                ADMIN_FACTION_FLAG_REMOVE,
                ADMIN_FACTION_RELATIONS_LISTS,
                ADMIN_FACTION_RELATIONS_SET,
                ADMIN_FACTION_PLAYER_LIST,
                ADMIN_FACTION_PLAYER_KICK,
                ADMIN_FACTION_PLAYER_SETOWNER,
                ADMIN_FACTION_PLAYER_SETROLE,
                ADMIN_FACTION_CLAIM_ONE,
                ADMIN_FACTION_CLAIM_SQUARE,
                ADMIN_FACTION_UNCLAIM_ONE,
                ADMIN_FACTION_UNCLAIM_SQUARE,
                ADMIN_FACTION_UNCLAIM_ALL,
                ADMIN_FACTION_MESSAGE,
                ADMIN_FACTION_DISBAND,

                ADMIN_LEVEL_CONFIGURE,
                ADMIN_LEVEL_SETCHUNKOWNER,
                ADMIN_LEVEL_RELEASEALL,

                ADMIN_MAP,
                ADMIN_BUILDOVERRIDE,
                ADMIN_CHATSPY
        );
    }
}

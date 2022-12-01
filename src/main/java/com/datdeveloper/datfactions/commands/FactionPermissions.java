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

@Mod.EventBusSubscriber(modid = Datfactions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FactionPermissions {
    /* ========================================= */
    /* Resolvers
    /* ========================================= */
    public static final PermissionResolver<Boolean> PLAYEROWNER = ((player, playerUUID, context) -> player != null && player.hasPermissions(Commands.LEVEL_OWNERS));
    public static final PermissionResolver<Boolean> PLAYERADMIN = ((player, playerUUID, context) -> player != null && player.hasPermissions(Commands.LEVEL_ADMINS));
    public static final PermissionResolver<Boolean> PLAYEROP = ((player, playerUUID, context) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
    public static final PermissionResolver<Boolean> PLAYERMOD = ((player, playerUUID, context) -> player != null && player.hasPermissions(Commands.LEVEL_MODERATORS));
    public static final PermissionResolver<Boolean> PLAYERALL = ((player, playerUUID, context) -> player != null && player.hasPermissions(Commands.LEVEL_ALL));

    /* ========================================= */
    /* Permission Nodes
    /* ========================================= */
    // General
    public static final PermissionNode<Boolean> FACTIONLIST = createNode("datfactions.list");
    public static final PermissionNode<Boolean> FACTIONINFO = createNode("datfactions.info");
    public static final PermissionNode<Boolean> FACTIONPLAYERINFO = createNode("datfactions.playerInfo");
    public static final PermissionNode<Boolean> FACTIONMAP = createNode("datfactions.map");

    // Basic Faction
    public static final PermissionNode<Boolean> FACTIONJOIN = createNode("datfactions.faction.join");
    public static final PermissionNode<Boolean> FACTIONLEAVE = createNode("datfactions.faction.leave");
    public static final PermissionNode<Boolean> FACTIONHOME = createNode("datfactions.faction.home");

    // Faction Management
    public static final PermissionNode<Boolean> FACTIONCREATE = createNode("datfactions.faction.create");
    public static final PermissionNode<Boolean> FACTIONSETNAME = createNode("datfactions.faction.setName");
    public static final PermissionNode<Boolean> FACTIONSETDESC = createNode("datfactions.faction.setDesc");
    public static final PermissionNode<Boolean> FACTIONSETMOTD = createNode("datfactions.faction.setMotd");
    public static final PermissionNode<Boolean> FACTIONSETHOME = createNode("datfactions.faction.setHome");
    public static final PermissionNode<Boolean> FACTIONDISBAND = createNode("datfactions.faction.disband");

    // Roles
    public static final PermissionNode<Boolean> FACTIONROLEADD = createNode("datfactions.faction.rank.add");
    public static final PermissionNode<Boolean> FACTIONROLEREMOVE = createNode("datfactions.faction.rank.remove");
    public static final PermissionNode<Boolean> FACTIONROLEREORDER = createNode("datfactions.faction.rank.reorder");
    public static final PermissionNode<Boolean> FACTIONROLEPERMISSIONS = createNode("datfactions.faction.rank.permissions");

    // User Management
    public static final PermissionNode<Boolean> FACTIONPROMOTE = createNode("datfactions.faction.player.promote");
    public static final PermissionNode<Boolean> FACTIONDEMOTE = createNode("datfactions.faction.player.demote");
    public static final PermissionNode<Boolean> FACTIONSETRANK = createNode("datfactions.faction.player.setRank");
    public static final PermissionNode<Boolean> FACTIONKICK = createNode("datfactions.faction.player.kick");
    public static final PermissionNode<Boolean> FACTIONINVITE = createNode("datfactions.faction.player.invite");
    public static final PermissionNode<Boolean> FACTIONUNINVITE = createNode("datfactions.faction.player.invite");

    // Claims
    public static final PermissionNode<Boolean> FACTIONCLAIMONE = createNode("datfactions.faction.claim.one");
    public static final PermissionNode<Boolean> FACTIONCLAIMAUTO = createNode("datfactions.faction.claim.auto");
    public static final PermissionNode<Boolean> FACTIONCLAIMSQUARE = createNode("datfactions.faction.claim.square");
    public static final PermissionNode<Boolean> FACTIONUNCLAIMONE = createNode("datfactions.faction.unclaim.one");
    public static final PermissionNode<Boolean> FACTIONUNCLAIMSQUARE = createNode("datfactions.faction.unclaim.square");
    public static final PermissionNode<Boolean> FACTIONUNCLAIMLEVEL = createNode("datfactions.faction.unclaim.level");
    public static final PermissionNode<Boolean> FACTIONUNCLAIMALL = createNode("datfactions.faction.unclaim.all");

    /* ========================================= */
    /* Permission Builders
    /* ========================================= */

    private static PermissionNode<Boolean> createNode(final String node) {
        return nodeBuilder(node, PLAYERALL);
    }
    private static PermissionNode<Boolean> createOpNode(final String node) {
        return nodeBuilder(node, PLAYEROP);
    }

    private static PermissionNode<Boolean> nodeBuilder(final String node, final PermissionResolver<Boolean> resolver) {
        return new PermissionNode<>(Datfactions.MODID, node, PermissionTypes.BOOLEAN, resolver);
    }

    public static Predicate<CommandSourceStack> hasPermission(final PermissionNode<Boolean> node) {
        return (source) -> DatPermissions.hasPermission(source.source, node);
    }

    public static void registerPermissionNodes(final PermissionGatherEvent.Nodes event) {
        event.addNodes(
            FACTIONLIST,
            FACTIONINFO,
            FACTIONPLAYERINFO,
            FACTIONMAP,
            FACTIONJOIN,
            FACTIONLEAVE,
            FACTIONHOME,
            FACTIONCREATE,
            FACTIONSETNAME,
            FACTIONSETDESC,
            FACTIONSETMOTD,
            FACTIONSETHOME,
            FACTIONDISBAND,
                FACTIONROLEADD,
                FACTIONROLEREMOVE,
                FACTIONROLEREORDER,
                FACTIONROLEPERMISSIONS,
            FACTIONPROMOTE,
            FACTIONDEMOTE,
            FACTIONSETRANK,
            FACTIONKICK,
            FACTIONINVITE,
            FACTIONCLAIMONE,
            FACTIONCLAIMAUTO,
            FACTIONCLAIMSQUARE,
            FACTIONUNCLAIMONE,
            FACTIONUNCLAIMSQUARE,
            FACTIONUNCLAIMLEVEL,
            FACTIONUNCLAIMALL
        );
    }
}

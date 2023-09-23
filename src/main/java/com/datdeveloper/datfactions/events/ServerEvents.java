package com.datdeveloper.datfactions.events;

import com.datdeveloper.datfactions.Datfactions;
import com.datdeveloper.datfactions.commands.FactionPermissions;
import com.datdeveloper.datfactions.commands.FactionsCommand;
import com.datdeveloper.datfactions.factiondata.*;
import com.datdeveloper.datfactions.factiondata.permissions.ERolePermissions;
import com.datdeveloper.datfactions.factiondata.relations.EFactionRelation;
import com.datdeveloper.datfactions.util.RelationUtil;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.PistonEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;

import java.util.*;

/**
 * Events for dealing with server stuff
 */
@Mod.EventBusSubscriber(modid = Datfactions.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {
    /* ========================================= */
    /* Commands & Permissions
    /* ========================================= */

    /**
     * Register permissions
     */
    @SubscribeEvent
    public static void registerPermissionNodes(final PermissionGatherEvent.Nodes event) {
        FactionPermissions.registerPermissionNodes(event);
    }

    /**
     * Register commands
     */
    @SubscribeEvent
    public static void registerCommands(final RegisterCommandsEvent event) {
        FactionsCommand.register(event.getDispatcher());
    }

    /* ========================================= */
    /* Chunk Mob Protection
    /* ========================================= */

    /**
     * A set of mob categories containing all the categories that represent animals
     */
    private static final Set<MobCategory> animalCategories = Set.of(
            MobCategory.AMBIENT,
            MobCategory.AXOLOTLS,
            MobCategory.CREATURE,
            MobCategory.WATER_CREATURE,
            MobCategory.WATER_AMBIENT,
            MobCategory.UNDERGROUND_WATER_CREATURE
    );

    /**
     * A set of mob ategories containing all the categories that represent hostile mobs
     */
    private static final Set<MobCategory> hostileCategories = Set.of(
            MobCategory.MONSTER
    );

    /**
     * Block mobs from spawning on land owned by factions that have the "NOANIMALS" or "NOMONSTERS" flags
     */
    @SubscribeEvent
    public static void blockMobSpawn(final MobSpawnEvent.FinalizeSpawn event) {
        final FactionLevel level = FLevelCollection.getInstance().getByKey(event.getEntity().level().dimension());
        final Faction chunkOwner = level.getChunkOwningFaction(new ChunkPos(new BlockPos((int) event.getX(), (int) event.getY(), (int) event.getZ())));

        final MobCategory category = event.getEntity().getType().getCategory();

        if (chunkOwner.hasFlag(EFactionFlags.NOANIMALS) && animalCategories.contains(category)
                || chunkOwner.hasFlag(EFactionFlags.NOMONSTERS) && hostileCategories.contains(category)) {
            event.setSpawnCancelled(true);
        }
    }

    /* ========================================= */
    /* Mob Grief
    /* ========================================= */

    /**
     * Block mobs from griefing on land owned by factions that have the "NOMOBGRIEF" flag
     */
    @SubscribeEvent
    public static void blockMobGrief(final EntityMobGriefingEvent event) {
        final Entity mob = event.getEntity();
        final FactionLevel level = FLevelCollection.getInstance().getByKey(mob.level().dimension());
        final Faction chunkOwner = level.getChunkOwningFaction(new ChunkPos(mob.getOnPos()));

        if (chunkOwner.hasFlag(EFactionFlags.NOMOBGRIEF)) {
            event.setResult(Event.Result.DENY);
        }
    }

    /* ========================================= */
    /* Explosive
    /* ========================================= */

    /**
     * Block explosions from breaking blocks on land owned by factions that have the "EXPLOSIONPROOF" flag
     */
    @SubscribeEvent
    public static void blockExplosion(final ExplosionEvent.Detonate event) {
        final FactionLevel level = FLevelCollection.getInstance().getByKey(event.getLevel().dimension());
        final Map<ChunkPos, Set<BlockPos>> chunkMap = new HashMap<>();

        for (final BlockPos blockPos : event.getAffectedBlocks()) {
            chunkMap.computeIfAbsent(new ChunkPos(blockPos), chunkPos -> new HashSet<>()).add(blockPos);
        }

        chunkMap.forEach((key, value) -> {
            final Faction chunkOwner = level.getChunkOwningFaction(key);
            if (chunkOwner.hasFlag(EFactionFlags.EXPLOSIONPROOF)) event.getAffectedBlocks().removeAll(value);
        });
    }

    /* ========================================= */
    /* Player Griefing
    /* ========================================= */

    /**
     * Test if a player can build on this
     *
     * @param fPlayer        The faction player to check
     * @param chunkOwner     The faction that owns the land
     * @param testPermission The permission to ensure the player has
     * @return True if the player has permission
     */
    private static boolean checkPlayerHasBuildPermission(final FactionPlayer fPlayer, final Faction chunkOwner, final ERolePermissions testPermission) {
        final Faction faction = fPlayer.getFaction();

        // Anyone can build
        if (chunkOwner.hasFlag(EFactionFlags.OPENBUILD)) return true;

        // If not owner then only allow allies
        if (!Objects.equals(chunkOwner, faction)
                && (RelationUtil.getMutualRelation(faction, chunkOwner) != EFactionRelation.ALLY)) {
                return false;
        }

        // Ensure player has build permission
        return fPlayer.getRole().hasPermission(testPermission);
    }

    // Breaking
    /**
     * Prevent players from breaking blocks on land they don't have permission to build on
     */
    @SubscribeEvent
    public static void blockBreak(final BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof final ServerPlayer player) || player instanceof FakePlayer) return;
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);

        final FactionLevel level = FLevelCollection.getInstance().getByKey(((ServerLevel) event.getLevel()).dimension());
        final Faction chunkOwner = level.getChunkOwningFaction(new ChunkPos(event.getPos()));

        if (checkPlayerHasBuildPermission(fPlayer, chunkOwner, ERolePermissions.BUILD))
            return;

        final Faction faction = fPlayer.getFaction();
        fPlayer.sendHotbarMessage(
                Component.literal(DatChatFormatting.TextColour.ERROR + "You do not have permission to build on chunks owned by ")
                        .append(
                                chunkOwner.getNameWithDescription(faction)
                                        .withStyle(RelationUtil.getRelation(faction, chunkOwner).formatting)
                        )
        );
        event.setCanceled(true);
    }

    // Placing
    /**
     * Prevents players from placing blocks on land they don't have permission to build on
     */
    @SubscribeEvent
    public static void blockPlace(final BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() == null || !(event.getEntity() instanceof final ServerPlayer player) || player instanceof FakePlayer) return;

        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);

        final FactionLevel level = FLevelCollection.getInstance().getByKey(((ServerLevel) event.getLevel()).dimension());
        final Faction chunkOwner = level.getChunkOwningFaction(new ChunkPos(event.getPos()));

        if (checkPlayerHasBuildPermission(fPlayer, chunkOwner, ERolePermissions.BUILD))
            return;

        final Faction faction = fPlayer.getFaction();
        fPlayer.sendHotbarMessage(
                Component.literal(DatChatFormatting.TextColour.ERROR + "You do not have permission to build on chunks owned by ")
                        .append(
                                chunkOwner.getNameWithDescription(faction)
                                        .withStyle(RelationUtil.getRelation(faction, chunkOwner).formatting)
                        )
        );
        event.setCanceled(true);
    }

    /**
     * Prevents players from placing multiblocks on land they don't have permission to build on
     */
    @SubscribeEvent
    public static void multiBlockPlace(final BlockEvent.EntityMultiPlaceEvent event) {
        if (event.getEntity() == null || !(event.getEntity() instanceof final ServerPlayer player) || player instanceof FakePlayer) return;

        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
        final Faction faction = fPlayer.getFaction();

        final Set<ChunkPos> chunks = new HashSet<>();

        final FactionLevel level = FLevelCollection.getInstance().getByKey(((ServerLevel) event.getLevel()).dimension());

        for (final BlockSnapshot blockSnapshot : event.getReplacedBlockSnapshots()) {
            chunks.add(new ChunkPos(blockSnapshot.getPos()));
        }

        for (final ChunkPos chunkPos : chunks){
            final Faction chunkOwner = level.getChunkOwningFaction(chunkPos);
            if (!checkPlayerHasBuildPermission(fPlayer, chunkOwner, ERolePermissions.BUILD)) {
                fPlayer.sendHotbarMessage(
                        Component.literal(DatChatFormatting.TextColour.ERROR + "You do not have permission to build on chunks owned by ")
                                .append(
                                        chunkOwner.getNameWithDescription(faction)
                                                .withStyle(RelationUtil.getRelation(faction, chunkOwner).formatting)
                                )
                );

                event.setCanceled(true);
                return;
            }
        }
    }

    // Interacting
    /**
     * Prevent players from interacting with blocks on land they don't have permission to build on
     */
    @SubscribeEvent
    public static void blockInteract(final PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof final ServerPlayer player) || player instanceof FakePlayer) return;
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);

        final FactionLevel level = FLevelCollection.getInstance().getByKey(event.getLevel().dimension());
        final Faction chunkOwner = level.getChunkOwningFaction(new ChunkPos(event.getPos()));

        if (checkPlayerHasBuildPermission(
                fPlayer,
                chunkOwner,
                event.getLevel().getBlockEntity(event.getPos()) instanceof Container
                        ? ERolePermissions.CONTAINERS
                        : ERolePermissions.INTERACT
        )) return;

        final Faction faction = fPlayer.getFaction();
//        fPlayer.sendHotbarMessage(
//                Component.literal(DatChatFormatting.TextColour.ERROR + "You do not have permission to build on chunks owned by ")
//                        .append(
//                                chunkOwner.getNameWithDescription(faction)
//                                        .withStyle(RelationUtil.getRelation(faction, chunkOwner).formatting)
//                        )
//        );
        event.setUseBlock(Event.Result.DENY);
    }

    /**
     * Prevents players from modifying a block's state using a tool
     */
    @SubscribeEvent
    public static void preventToolInteraction(final BlockEvent.BlockToolModificationEvent event) {
        if (!(event.getPlayer() instanceof final ServerPlayer player) || player instanceof FakePlayer) return;
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);

        final FactionLevel level = FLevelCollection.getInstance().getByKey(event.getPlayer().level().dimension());
        final Faction chunkOwner = level.getChunkOwningFaction(new ChunkPos(event.getPos()));

        if (checkPlayerHasBuildPermission(
                fPlayer,
                chunkOwner,
                ERolePermissions.INTERACT
        )) return;

        final Faction faction = fPlayer.getFaction();
        fPlayer.sendHotbarMessage(
                Component.literal(DatChatFormatting.TextColour.ERROR + "You do not have permission to interact with blocks owned by ")
                        .append(
                                chunkOwner.getNameWithDescription(faction)
                                        .withStyle(RelationUtil.getRelation(faction, chunkOwner).formatting)
                        )
        );
        event.setCanceled(true);
    }


    // Farmland Trample
    /**
     * Prevent players from trampling farmland on land they don't have permission to build on
     * <br>
     * Not registered by default, registration is controlled with the preventCropTrampling config option
     *
     * @see Datfactions#registerOptionalEvents()
     * @see com.datdeveloper.datfactions.FactionsConfig#preventCropTrampling
     */
    public static void trampleFarmland(final BlockEvent.FarmlandTrampleEvent event) {
        if (event.getEntity() == null || !(event.getEntity() instanceof final ServerPlayer player) || player instanceof FakePlayer) return;

        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);

        final FactionLevel level = FLevelCollection.getInstance().getByKey(((ServerLevel) event.getLevel()).dimension());
        final Faction chunkOwner = level.getChunkOwningFaction(new ChunkPos(event.getPos()));

        if (checkPlayerHasBuildPermission(fPlayer, chunkOwner, ERolePermissions.BUILD))
            return;

        final Faction faction = fPlayer.getFaction();
        fPlayer.sendHotbarMessage(
                Component.literal(DatChatFormatting.TextColour.ERROR + "You cannot trample farmland on chunks owned by ")
                        .append(
                                chunkOwner.getNameWithDescription(faction)
                                        .withStyle(RelationUtil.getRelation(faction, chunkOwner).formatting)
                        )
        );
        event.setCanceled(true);
    }

    // Piston
    /**
     * Prevent pistons from moving blocks between land borders
     * <br>
     * Not registered by default, registration is controlled with the preventPistonGrief config option
     *
     * @see Datfactions#registerOptionalEvents()
     * @see com.datdeveloper.datfactions.FactionsConfig#preventPistonGrief
     */
    public static void blockPiston(final PistonEvent.Pre event) {
        final PistonStructureResolver structureHelper = event.getStructureHelper();
        if (structureHelper == null) return;
        structureHelper.resolve();

        final FactionLevel level = FLevelCollection.getInstance().getByKey(((ServerLevel) event.getLevel()).dimension());
        final Faction pistonFaction = level.getChunkOwningFaction(new ChunkPos(event.getPos()));

        final Set<ChunkPos> chunks = new HashSet<>();

        // Filter down to just chunks, so we don't need to lookup as many chunk owners
        for (final BlockPos toPush : structureHelper.getToPush()) {
            chunks.add(new ChunkPos(toPush));
        }

        for (final BlockPos toDestroy : structureHelper.getToDestroy()) {
            chunks.add(new ChunkPos(toDestroy));
        }

        for (final ChunkPos chunkPos : chunks){
            final Faction chunkOwner = level.getChunkOwningFaction(chunkPos);
            if (!chunkOwner.hasFlag(EFactionFlags.OPENBUILD) && !chunkOwner.equals(pistonFaction)) {
                event.setCanceled(true);
                return;
            }
        }
    }
}

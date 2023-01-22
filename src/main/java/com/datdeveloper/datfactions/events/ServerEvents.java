package com.datdeveloper.datfactions.events;

import com.datdeveloper.datfactions.Datfactions;
import com.datdeveloper.datfactions.commands.FactionPermissions;
import com.datdeveloper.datfactions.commands.FactionsCommand;
import com.datdeveloper.datfactions.factionData.*;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datfactions.factionData.relations.EFactionRelation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.PistonEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.datdeveloper.datfactions.Datfactions.logger;

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

    private static final Set<MobCategory> animalCategories = Set.of(
            MobCategory.AMBIENT,
            MobCategory.AXOLOTLS,
            MobCategory.CREATURE,
            MobCategory.WATER_CREATURE,
            MobCategory.WATER_AMBIENT,
            MobCategory.UNDERGROUND_WATER_CREATURE
    );

    private static final Set<MobCategory> hostileCategories = Set.of(
            MobCategory.MONSTER
    );

    @SubscribeEvent
    public static void blockMobSpawn(final LivingSpawnEvent.CheckSpawn event) {
        final FactionLevel level = FLevelCollection.getInstance().getByKey(event.getEntity().getLevel().dimension());
        final Faction chunkOwner = level.getChunkOwningFaction(new ChunkPos(new BlockPos(event.getX(), event.getY(), event.getZ())));

        final MobCategory category = event.getEntity().getType().getCategory();

        if (chunkOwner.hasFlag(EFactionFlags.NOANIMALS) && animalCategories.contains(category)) {
            event.setResult(Event.Result.DENY);
        } else if (chunkOwner.hasFlag(EFactionFlags.NOMONSTERS) && hostileCategories.contains(category)) {
            event.setResult(Event.Result.DENY);
        }
    }

    /* ========================================= */
    /* Mob Grief
    /* ========================================= */

    @SubscribeEvent
    public static void blockMobGrief(final EntityMobGriefingEvent event) {
        final Entity mob = event.getEntity();
        final FactionLevel level = FLevelCollection.getInstance().getByKey(mob.getLevel().dimension());
        final Faction chunkOwner = level.getChunkOwningFaction(new ChunkPos(mob.getOnPos()));

        if (chunkOwner.hasFlag(EFactionFlags.NOMOBGRIEF)) {
            event.setResult(Event.Result.DENY);
        }
    }

    /* ========================================= */
    /* Explosive
    /* ========================================= */
    @SubscribeEvent
    public static void blockExplosion(final ExplosionEvent.Detonate event) {
        final FactionLevel level = FLevelCollection.getInstance().getByKey(event.getLevel().dimension());
        final Map<ChunkPos, Set<BlockPos>> chunkMap = new HashMap<>();

        for (final BlockPos blockPos : event.getAffectedBlocks()) {
            chunkMap.computeIfAbsent(new ChunkPos(blockPos), (chunkPos) -> new HashSet<>()).add(blockPos);
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
     * Test if an entity can build on this
     * @param entity The entity to check
     * @param level The level the block is in
     * @param pos The position of the block
     * @param testPermission The permission to ensure the entity has
     * @return True if the entity has permission
     */
    private static boolean checkEntityHasBuildPermission(final Entity entity, final Level level, final BlockPos pos, final ERolePermissions testPermission) {
        if (!(entity instanceof ServerPlayer player)) return true;
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
        final Faction faction = fPlayer.getFaction();

        final FactionLevel fLevel = FLevelCollection.getInstance().getByKey(level.dimension());

        final Faction chunkOwner = fLevel.getChunkOwningFaction(new ChunkPos(pos));
        return chunkOwner.hasFlag(EFactionFlags.OPENBUILD)
                || (faction.equals(chunkOwner) && fPlayer.getRole().hasPermission(testPermission))
                || (faction.getRelation(chunkOwner).getRelation() == EFactionRelation.ALLY && chunkOwner.getRelation(faction).getRelation() == EFactionRelation.ALLY);
    }

    // Breaking
    @SubscribeEvent
    public static void blockBreak(final BlockEvent.BreakEvent event) {
        if (checkEntityHasBuildPermission(event.getPlayer(), (Level) event.getLevel(), event.getPos(), ERolePermissions.BUILD)) return;

        event.setCanceled(true);
    }

    // Placing
    @SubscribeEvent
    public static void blockPlace(final BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() == null) return;

        event.setCanceled(!checkEntityHasBuildPermission(event.getEntity(), (Level) event.getLevel(), event.getPos(), ERolePermissions.BUILD));
    }

    @SubscribeEvent
    public static void multiBlockPlace(final BlockEvent.EntityMultiPlaceEvent event) {
        if (event.getEntity() == null) return;
        for (final BlockSnapshot blockSnapshot : event.getReplacedBlockSnapshots()) {
            if (!checkEntityHasBuildPermission(event.getEntity(), (Level) event.getLevel(), blockSnapshot.getPos(), ERolePermissions.BUILD)) {
                event.setCanceled(true);
                return;
            }
        }
    }
    
    // Farmland Trample
    @SubscribeEvent
    public static void trampleFarmland(final BlockEvent.FarmlandTrampleEvent event) {
        event.setCanceled(!checkEntityHasBuildPermission(event.getEntity(), (Level) event.getLevel(), event.getPos(), ERolePermissions.BUILD));
    }

    // Interacting
    @SubscribeEvent
    public static void blockInteract(final PlayerInteractEvent.RightClickBlock event) {
        event.setCanceled(!checkEntityHasBuildPermission(event.getEntity(), event.getLevel(), event.getPos(), ERolePermissions.INTERACT));
    }

    // Piston
    @SubscribeEvent public static void blockPiston(final PistonEvent.Pre event) {
        event.getStructureHelper().
    }

}

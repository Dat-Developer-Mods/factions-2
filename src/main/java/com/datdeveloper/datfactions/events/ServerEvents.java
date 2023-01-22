package com.datdeveloper.datfactions.events;

import com.datdeveloper.datfactions.Datfactions;
import com.datdeveloper.datfactions.commands.FactionPermissions;
import com.datdeveloper.datfactions.commands.FactionsCommand;
import com.datdeveloper.datfactions.factionData.*;
import com.datdeveloper.datfactions.factionData.FLevelCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;

import java.util.HashSet;
import java.util.Set;

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
}

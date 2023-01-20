package com.datdeveloper.datfactions.events;

import com.datdeveloper.datfactions.Datfactions;
import com.datdeveloper.datfactions.commands.FactionPermissions;
import com.datdeveloper.datfactions.commands.FactionsCommand;
import com.datdeveloper.datfactions.factionData.EFactionFlags;
import com.datdeveloper.datfactions.factionData.FLevelCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;

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

    Set<MobCategory> animalCategories = Set.of(
            MobCategory.AMBIENT,
            MobCategory.AXOLOTLS,
            MobCategory.CREATURE,
            MobCategory.WATER_CREATURE,
            MobCategory.WATER_AMBIENT,
            MobCategory.UNDERGROUND_WATER_CREATURE
    );

    @SubscribeEvent
    public static void blockMobs(final LivingSpawnEvent.CheckSpawn event) {
        final FactionLevel level = FLevelCollection.getInstance().getByKey(((ServerLevel)event.getLevel()).dimension());
        event.getEntity().getType().getCategory()
        final Faction chunkOwner = level.getChunkOwningFaction(new ChunkPos(new BlockPos(event.getX(), event.getY(), event.getZ())));
        if (chunkOwner.hasFlag(EFactionFlags.NOANIMALS) && event.getSpawner().getSpawnerEntity().getClass().isAssignableFrom(Animal.class)) ;
    }
}

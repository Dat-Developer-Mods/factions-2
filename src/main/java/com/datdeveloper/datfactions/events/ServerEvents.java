package com.datdeveloper.datfactions.events;

import com.datdeveloper.datfactions.Datfactions;
import com.datdeveloper.datfactions.commands.FactionPermissions;
import com.datdeveloper.datfactions.commands.FactionsCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;

@Mod.EventBusSubscriber(modid = Datfactions.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {
    /**
     * Register permissions
     */
    @SubscribeEvent
    public static void registerPermissionNodes(PermissionGatherEvent.Nodes event) {
        FactionPermissions.registerPermissionNodes(event);
    }

    /**
     * Register commands
     */
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        FactionsCommand.register(event.getDispatcher());
    }
}

package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.util.MapUtil;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_MAP;

/**
 * A command to show the player a map of the chunks
 */
public class FactionMapCommand {
    /**
     * Visitor to register the command
     */
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        command.then(Commands.literal("map")
                .requires(source -> source.source instanceof ServerPlayer && DatPermissions.hasPermission(source.source, FACTION_MAP))
                .executes(FactionMapCommand::run)
                .build());
    }

    /**
     * Execute the map command
     * @param c The command context
     * @return 1 if successful
     */
    private static int run(final CommandContext<CommandSourceStack> c) {
        final ServerPlayer player = c.getSource().getPlayer();
        final FactionPlayer factionPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
        player.sendSystemMessage(new MapUtil(new ChunkPos(player.getOnPos()),
                player.getYRot(),
                player.level().dimension(),
                factionPlayer).build());
        return 1;
    }
}

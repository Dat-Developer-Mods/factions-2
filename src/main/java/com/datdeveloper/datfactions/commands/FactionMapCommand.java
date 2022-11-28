package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.util.MapUtil;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONMAP;

public class FactionMapCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("map")
                .requires((source) -> source.source instanceof ServerPlayer && DatPermissions.hasPermission(source.source, FACTIONMAP))
                .executes(c -> {
                    final ServerPlayer player = c.getSource().getPlayer();
                    final FactionPlayer factionPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
                    final MapUtil map = new MapUtil(new ChunkPos(player.getOnPos()), player.getYRot(), player.getLevel().dimension(), factionPlayer);

                    player.sendSystemMessage(map.build());
                    return 1;
                })
                .build();

        command.then(subCommand);
    }


}

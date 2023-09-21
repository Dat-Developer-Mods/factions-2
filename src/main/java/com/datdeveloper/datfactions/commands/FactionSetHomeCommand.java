package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionSetHomeEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factiondata.*;
import com.datdeveloper.datfactions.factiondata.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_SET_HOME;

public class FactionSetHomeCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {

        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("sethome")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_SET_HOME)))
                        return false;
                    final FactionPlayer fPlayer1 = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return fPlayer1.hasFaction() && fPlayer1.getRole().hasPermission(ERolePermissions.SETHOME);
                })
                .executes(c -> {
                    final ServerPlayer player = c.getSource().getPlayer();
                    final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
                    final Faction faction = fPlayer.getFaction();

                    FactionLevel newHomeLevel = FLevelCollection.getInstance().getByKey(player.level().dimension());
                    BlockPos newHomePos = player.getOnPos().above();

                    final FactionSetHomeEvent event = new FactionSetHomeEvent(c.getSource().source, faction, newHomeLevel, newHomePos);
                    MinecraftForge.EVENT_BUS.post(event);
                    if (event.isCanceled()) return 0;

                    newHomeLevel = event.getNewHomeLevel();
                    newHomePos = event.getNewHomePos();

                    if (!event.isSkipDefaultChecks()
                            && (newHomeLevel.getSettings().isHomeRequiresOwnedChunk()
                                    && !faction.equals(newHomeLevel.getChunkOwningFaction(new ChunkPos(newHomePos))))) {
                            c.getSource().sendFailure(Component.literal("You can only set your faction home on chunks you own"));
                            return -1;

                    }

                    faction.setFactionHome(newHomeLevel.getId(), newHomePos);
                    c.getSource().sendSuccess(() -> Component.literal(DatChatFormatting.TextColour.INFO + "Successfully set your faction's home pos")
                    ,false);
                    return 1;
                }).build();

        command.then(subCommand);
    }
}

package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionSetHomeEvent;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

import java.util.function.Predicate;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONSETHOME;

public class FactionSetHomeCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final Predicate<CommandSourceStack> predicate = commandSourceStack -> {
            if (!(commandSourceStack.isPlayer()) && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTIONSETHOME))
                return false;
            final FactionPlayer fPlayer = getPlayerOrTemplate(commandSourceStack.getPlayer());
            return fPlayer.hasFaction() && fPlayer.getRole().hasPermission(ERolePermissions.SETHOME);
        };

        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("sethome")
                .requires(predicate)
                .executes(c -> {
                    final ServerPlayer player = c.getSource().getPlayer();
                    final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
                    final Faction faction = fPlayer.getFaction();

                    final ResourceKey<Level> newHomeLevel = player.getLevel().dimension();
                    final BlockPos newHomePos = player.getOnPos();

                    final FactionSetHomeEvent event = new FactionSetHomeEvent(c.getSource().source, faction, newHomeLevel, newHomePos);
                    MinecraftForge.EVENT_BUS.post(event);
                    if (event.isCanceled()) return 0;

                    faction.setFactionHome(event.getNewHomeLevel(), event.getNewHomePos());
                    c.getSource().sendSuccess(MutableComponent.create(ComponentContents.EMPTY)
                            .append(DatChatFormatting.TextColour.INFO + "Successfully set your faction's home pos")
                    ,false);
                    return 1;
                }).build();

        command.then(subCommand);
    }
}

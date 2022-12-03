package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionPlayerHomeEvent;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.delayedEvents.DelayedEventsHandler;
import com.datdeveloper.datmoddingapi.delayedEvents.DelayedTeleportEvent;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_HOME;

public class FactionHomeCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {

        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("home")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer()) && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_HOME))
                        return false;
                    final FactionPlayer fPlayer1 = getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return fPlayer1.hasFaction() && fPlayer1.getRole().hasPermission(ERolePermissions.HOME);
                })
                .executes(c -> {
                    final ServerPlayer player = c.getSource().getPlayer();
                    final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
                    final Faction faction = fPlayer.getFaction();

                    final FactionPlayerHomeEvent event = new FactionPlayerHomeEvent(c.getSource().source, fPlayer);
                    MinecraftForge.EVENT_BUS.post(event);
                    if (event.isCanceled()) return 0;

                    DelayedEventsHandler.addEvent(new DelayedTeleportEvent(faction.getHomeLocation(), faction.getHomeLevel(), player, FactionsConfig.getTeleportDelay()));

                    c.getSource().sendSuccess(MutableComponent.create(ComponentContents.EMPTY)
                            .append(DatChatFormatting.TextColour.INFO + "Teleporting to your faction's home in " + FactionsConfig.getTeleportDelay() + " seconds")
                    ,false);
                    return 1;
                }).build();

        command.then(subCommand);
    }
}

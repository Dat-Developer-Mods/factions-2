package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionDisbandEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factionData.*;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datfactions.factionData.relations.EFactionRelation;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_DISBAND;

public class FactionDisbandCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {

        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("disband")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_DISBAND)))
                        return false;
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    final Faction faction = fPlayer.getFaction();
                    return faction != null && !faction.hasFlag(EFactionFlags.PERMANENT) && fPlayer.getRole().hasPermission(ERolePermissions.DISBAND);
                })
                .then(
                        Commands.argument("Are you sure?", BoolArgumentType.bool())
                                .executes(c -> {
                                    final ServerPlayer player = c.getSource().getPlayer();
                                    final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
                                    final Faction faction = fPlayer.getFaction();

                                    final boolean areYouSure = BoolArgumentType.getBool(c, "Are you sure?");
                                    if (!areYouSure) return 2;

                                    final FactionDisbandEvent event = new FactionDisbandEvent(c.getSource().source, faction);
                                    MinecraftForge.EVENT_BUS.post(event);
                                    if (event.isCanceled()) return 0;

                                    FactionCollection.getInstance().disbandFaction(faction.getId());
                                    return 1;
                                })
                )
                .executes(c -> {
                    final ServerPlayer player = c.getSource().getPlayer();
                    final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
                    final Faction faction = fPlayer.getFaction();

                    c.getSource().sendSuccess(Component.literal(
                            DatChatFormatting.TextColour.ERROR + "Are you sure you want to disband ")
                                    .append(
                                            faction.getNameWithDescription(faction)
                                                    .withStyle(EFactionRelation.SELF.formatting)
                                    )
                                    .append(DatChatFormatting.TextColour.ERROR + "? This action is not reversible\n")
                                    .append(DatChatFormatting.TextColour.ERROR + "Use ")
                                    .append(FactionCommandUtils.wrapCommand("/f disband true", "/f disband "))
                                    .append(DatChatFormatting.TextColour.ERROR + " if you are")

                    ,false);
                    return 1;
                }).build();

        command.then(subCommand);
    }
}

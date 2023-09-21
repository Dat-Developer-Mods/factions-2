package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionChangeMotdEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.factiondata.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_SET_MOTD;

public class FactionMotdCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {

        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("motd")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_SET_MOTD)))
                        return false;
                    final FactionPlayer fPlayer1 = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return fPlayer1.hasFaction() && fPlayer1.getRole().hasPermission(ERolePermissions.SETMOTD);
                })
                .then(Commands.argument("MOTD", StringArgumentType.greedyString())
                        .executes(c -> {
                            final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
                            final Faction faction = fPlayer.getFaction();
                            final String newMotd = c.getArgument("MOTD", String.class);

                            if (newMotd.length() > FactionsConfig.getMaxFactionMotdLength()) {
                                c.getSource().sendFailure(Component.literal("Your faction MOTD cannot be longer than " + FactionsConfig.getMaxFactionMotdLength()));
                                return 2;
                            }

                            final FactionChangeMotdEvent event = new FactionChangeMotdEvent(c.getSource().source, faction, newMotd);
                            MinecraftForge.EVENT_BUS.post(event);
                            if (event.isCanceled()) return 0;

                            faction.setMotd(event.getNewMotd());
                            // No message here, setting the MOTD should send a factionwide message
                            return 1;
                        })).build();

        command.then(subCommand);
        command.then(FactionCommandUtils.buildRedirect("setmotd", subCommand));
    }
}

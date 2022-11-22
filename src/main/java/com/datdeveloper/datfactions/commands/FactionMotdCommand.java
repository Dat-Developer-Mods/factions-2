package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionChangeDescriptionEvent;
import com.datdeveloper.datfactions.api.events.FactionChangeMotdEvent;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONSETDESC;
import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONSETMOTD;

public class FactionMotdCommand extends BaseFactionCommand {
    static void register(LiteralArgumentBuilder<CommandSourceStack> command) {
        LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("motd")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer()) && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTIONSETMOTD)) return false;
                    FactionPlayer player = FPlayerCollection.getInstance().getPlayer(commandSourceStack.getPlayer());
                    return player.hasFaction() && player.getRole().hasPermission(ERolePermissions.SETMOTD);
                })
                .then(Commands.argument("motd", StringArgumentType.greedyString())
                        .executes(c -> {
                            FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
                            Faction faction = fPlayer.getFaction();
                            String newMotd = c.getArgument("motd", String.class);

                            if (newMotd.length() > FactionsConfig.getMaxFactionMotdLength()) {
                                c.getSource().sendFailure(Component.literal("Your faction MOTD cannot be longer than " + FactionsConfig.getMaxFactionMotdLength() + " characters"));
                                return 1;
                            }

                            FactionChangeMotdEvent event = new FactionChangeMotdEvent(c.getSource().source, faction, newMotd);
                            MinecraftForge.EVENT_BUS.post(event);
                            if (event.isCancelable()) return 0;

                            faction.setDescription(event.getNewMotd());
                            return 0;
                        })).build();

        command.then(subCommand);
        command.then(Commands.literal("setmotd").redirect(subCommand));
    }
}

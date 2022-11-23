package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionChangeNameEvent;
import com.datdeveloper.datfactions.commands.arguments.NewFactionNameArgument;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONSETNAME;

public class FactionNameCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("name")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer()) && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTIONSETNAME)) return false;
                    final FactionPlayer player = FPlayerCollection.getInstance().getPlayer(commandSourceStack.getPlayer());
                    return player.hasFaction() && player.getRole().hasPermission(ERolePermissions.SETNAME);
                })
                .then(Commands.argument("name", new NewFactionNameArgument())
                        .executes(c -> {
                            final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
                            final Faction faction = fPlayer.getFaction();
                            final String newName = c.getArgument("name", String.class);

                            if (newName.length() > FactionsConfig.getMaxFactionMotdLength()) {
                                c.getSource().sendFailure(Component.literal("Your faction name cannot be longer than " + FactionsConfig.getMaxFactionNameLength() + " characters"));
                                return 2;
                            }

                            final FactionChangeNameEvent event = new FactionChangeNameEvent(c.getSource().source, faction, newName);
                            MinecraftForge.EVENT_BUS.post(event);
                            if (event.isCancelable()) return 0;

                            faction.setDescription(event.getNewName());
                            return 1;
                        })).build();

        command.then(subCommand);
        command.then(Commands.literal("setname").redirect(subCommand));
    }
}

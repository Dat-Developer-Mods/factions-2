package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionChangeDescriptionEvent;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONSETDESC;

public class FactionDescriptionCommand extends BaseFactionCommand {
    static void register(LiteralArgumentBuilder<CommandSourceStack> command) {
        LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("desc")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer()) && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTIONSETDESC)) return false;
                    FactionPlayer player = FPlayerCollection.getInstance().getPlayer(commandSourceStack.getPlayer());
                    return player.hasFaction() && player.getRole().hasPermission(ERolePermissions.SETDESC);
                })
                .then(Commands.argument("description", StringArgumentType.greedyString())
                        .executes(c -> {
                            FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
                            Faction faction = fPlayer.getFaction();
                            String newDescription = c.getArgument("description", String.class);

                            if (newDescription.length() > FactionsConfig.getMaxFactionDescriptionLength()) {
                                c.getSource().sendFailure(Component.literal("Your faction description cannot be longer than " + FactionsConfig.getMaxFactionDescriptionLength() + " characters"));
                                return 1;
                            }

                            FactionChangeDescriptionEvent event = new FactionChangeDescriptionEvent(c.getSource().source, faction, newDescription);
                            MinecraftForge.EVENT_BUS.post(event);
                            if (event.isCancelable()) return 0;

                            faction.setDescription(event.getNewDescription());
                            return 0;
                        })).build();

        command.then(subCommand);
        command.then(Commands.literal("setdesc").redirect(subCommand));
    }
}

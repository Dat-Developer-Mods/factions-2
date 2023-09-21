package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionChangeDescriptionEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.factiondata.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_SET_DESC;

public class FactionDescriptionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("desc")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_SET_DESC)))
                        return false;
                    final FactionPlayer fPlayer1 = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return fPlayer1.hasFaction() && fPlayer1.getRole().hasPermission(ERolePermissions.SETDESC);
                })
                .then(Commands.argument("Description", StringArgumentType.greedyString())
                        .executes(c -> {
                            final String newDescription = c.getArgument("Description", String.class);
                            final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
                            final Faction faction = fPlayer.getFaction();

                            final FactionChangeDescriptionEvent.Pre pre = new FactionChangeDescriptionEvent.Pre(c.getSource().source, faction, newDescription);
                            MinecraftForge.EVENT_BUS.post(pre);
                            if (pre.isCanceled()) return 0;

                            if (!pre.isSkipDefaultChecks() && newDescription.length() > FactionsConfig.getMaxFactionNameLength()) {
                                c.getSource().sendFailure(Component.literal("Your faction description cannot be longer than " + FactionsConfig.getMaxFactionDescriptionLength()));
                                return 2;
                            }

                            final String oldDescription = faction.getDescription();

                            faction.setDescription(pre.getNewDescription());

                            final FactionChangeDescriptionEvent.Post post = new FactionChangeDescriptionEvent.Post(c.getSource().source, faction, pre.getNewDescription(), oldDescription);
                            MinecraftForge.EVENT_BUS.post(post);

                            c.getSource().sendSuccess(() -> MutableComponent.create(ComponentContents.EMPTY)
                                    .append(DatChatFormatting.TextColour.INFO + "Successfully set your faction's description")
                            ,false);
                            return 1;
                        })).build();

        command.then(subCommand);
        command.then(FactionCommandUtils.buildRedirect("setdesc", subCommand));
    }
}

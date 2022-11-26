package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.util.RelationUtil;
import com.datdeveloper.datfactions.api.events.ChangeFactionMembershipEvent;
import com.datdeveloper.datfactions.api.events.CreateFactionEvent;
import com.datdeveloper.datfactions.commands.arguments.NewFactionNameArgument;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionCollection;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONCREATE;

public class FactionCreateCommand extends BaseFactionCommand{
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        command.then(Commands.literal("create")
                .requires((commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTIONCREATE))) return false;
                    final FactionPlayer fPlayer = getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return !fPlayer.hasFaction();
                }))
                .then(Commands.argument("name", new NewFactionNameArgument())
                        .executes(c -> {
                            final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());

                            // Check Name
                            final String nameArg = c.getArgument("name", String.class);

                            final CreateFactionEvent event = new CreateFactionEvent(c.getSource().source, nameArg);
                            MinecraftForge.EVENT_BUS.post(event);
                            if (event.isCanceled()) return 0;

                            final Faction newFaction = FactionCollection.getInstance().createFaction(event.getName());

                            final ChangeFactionMembershipEvent changeFactionMembershipEvent = new ChangeFactionMembershipEvent(c.getSource().source, fPlayer, newFaction, newFaction.getOwnerRole(), ChangeFactionMembershipEvent.EChangeFactionReason.CREATE);
                            MinecraftForge.EVENT_BUS.post(event);

                            fPlayer.setFaction(newFaction.getId(), newFaction.getOwnerRole().getId());

                            c.getSource().sendSuccess(MutableComponent.create(ComponentContents.EMPTY)
                                    .append(DatChatFormatting.TextColour.INFO + "Successfully created faction ")
                                    .append(
                                            newFaction.getNameWithDescription(newFaction)
                                                    .withStyle(RelationUtil.getRelation(newFaction, newFaction).formatting)
                                    ).append("\n")
                                    .append(DatChatFormatting.TextColour.INFO + "Add a description with ")
                                    .append(FactionCommandUtils.wrapCommand("/f desc <description>", "/f desc ")).append("\n")
                                    .append(DatChatFormatting.TextColour.INFO + "and invite people using ")
                                    .append(FactionCommandUtils.wrapCommand("/f invite <player name>", "/f invite "))
                            , false);

                            return 1;
                        })
                ));
    }
}

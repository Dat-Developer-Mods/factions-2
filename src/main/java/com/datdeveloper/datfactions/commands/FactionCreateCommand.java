package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionChangeMembershipEvent;
import com.datdeveloper.datfactions.api.events.FactionCreateEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionCollection;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.util.RelationUtil;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
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
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(c -> {
                            // Check Name
                            final String newName = c.getArgument("name", String.class);

                            if (newName.length() > FactionsConfig.getMaxFactionNameLength()) {
                                c.getSource().sendFailure(Component.literal("Your faction name cannot be longer than " + FactionsConfig.getMaxFactionNameLength()));
                                return 2;
                            } else if (FactionCollection.getInstance().isNameTaken(newName)) {
                                c.getSource().sendFailure(Component.literal("A faction with that name already exists"));
                                return 3;
                            }

                            final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());

                            final FactionCreateEvent event = new FactionCreateEvent(c.getSource().source, newName);
                            MinecraftForge.EVENT_BUS.post(event);
                            if (event.isCanceled()) return 0;

                            final Faction newFaction = FactionCollection.getInstance().createFaction(event.getName());

                            final FactionChangeMembershipEvent factionChangeMembershipEvent = new FactionChangeMembershipEvent(c.getSource().source, fPlayer, newFaction, newFaction.getOwnerRole(), FactionChangeMembershipEvent.EChangeFactionReason.CREATE);
                            MinecraftForge.EVENT_BUS.post(factionChangeMembershipEvent);

                            fPlayer.setFaction(newFaction.getId(), newFaction.getOwnerRole().getId(), FactionChangeMembershipEvent.EChangeFactionReason.CREATE);

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

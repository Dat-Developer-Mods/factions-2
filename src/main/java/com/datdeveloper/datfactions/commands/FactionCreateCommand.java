package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.Datfactions;
import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.Util.RelationUtil;
import com.datdeveloper.datfactions.api.events.ChangeFactionMembershipEvent;
import com.datdeveloper.datfactions.api.events.CreateFactionEvent;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionCollection;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONCREATE;
import static com.mojang.brigadier.arguments.StringArgumentType.string;

public class FactionCreateCommand extends BaseFactionCommand{
    static void register(LiteralArgumentBuilder<CommandSourceStack> command) {
        command.then(Commands.literal("create")
                .requires((commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTIONCREATE))) return false;
                    FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(commandSourceStack.getPlayer());
                    return !fPlayer.hasFaction();
                }))
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(c -> {
                            FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());

                            // Check Name
                            String nameArg = c.getArgument("name", String.class);
                            if (nameArg.length() > FactionsConfig.getMaxFactionNameLength()) {
                                c.getSource().sendFailure(Component.literal("Your faction name cannot be longer than " + FactionsConfig.getMaxFactionNameLength() + " characters"));
                                return 3;
                            }

                            CreateFactionEvent event = new CreateFactionEvent(c.getSource().source, nameArg);
                            MinecraftForge.EVENT_BUS.post(event);
                            if (event.isCanceled()) return 0;

                            Faction newFaction = FactionCollection.getInstance().createFaction(event.getName());

                            ChangeFactionMembershipEvent changeFactionMembershipEvent = new ChangeFactionMembershipEvent(c.getSource().source, fPlayer, newFaction, newFaction.getOwnerRole(), ChangeFactionMembershipEvent.EChangeFactionReason.CREATE);
                            MinecraftForge.EVENT_BUS.post(event);

                            fPlayer.setFaction(newFaction.getId(), newFaction.getOwnerRole().getId());

                            c.getSource().sendSuccess(MutableComponent.create(ComponentContents.EMPTY)
                                    .append(DatChatFormatting.TextColour.INFO + "Successfully created faction ")
                                    .append(RelationUtil.wrapFactionName(newFaction, newFaction))
                                    .append(DatChatFormatting.TextColour.INFO + "\nAdd a description with ")
                                    .append(wrapCommand("/f desc <description>", "/f desc "))
                                    .append(DatChatFormatting.TextColour.INFO + "\nand invite people using ")
                                    .append(wrapCommand("/f invite <player name>", "/f invite "))
                            , false);

                            return 1;
                        })
                ));
    }
}

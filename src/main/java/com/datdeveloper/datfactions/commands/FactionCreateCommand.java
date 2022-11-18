package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.Datfactions;
import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.Util.RelationUtil;
import com.datdeveloper.datfactions.api.events.CreateFactionEvent;
import com.datdeveloper.datfactions.database.FlatDatabase;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionCollection;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.ChatColours;
import com.mojang.brigadier.builder.ArgumentBuilder;
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
    public static final PermissionNode<Boolean> PERMISSIONNODE = new PermissionNode<>(Datfactions.MODID, "datfactions.create", PermissionTypes.BOOLEAN, null);

    static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("create")
                .requires((commandSourceStack -> commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTIONCREATE)))
                .then(Commands.argument("name", string())
                            .executes(c -> {
                                FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());

                                if (fPlayer.getFactionId() != null) {
                                    c.getSource().sendFailure(Component.literal("You cannot create a faction while you are in a faction"));
                                    return 2;
                                }

                                // Check Name
                                String nameArg = c.getArgument("name", String.class);
                                if (nameArg.length() > FactionsConfig.getMaxFactionNameLength()) {
                                    c.getSource().sendFailure(Component.literal("Your faction name cannot be greater than " + FactionsConfig.getMaxFactionNameLength() + " characters"));
                                    return 3;
                                }

                                CreateFactionEvent event = new CreateFactionEvent(c.getSource().source, nameArg);
                                MinecraftForge.EVENT_BUS.post(event);
                                if (event.isCanceled()) return 0;

                                Faction newFaction = FactionCollection.getInstance().createFaction(event.getName());
                                new FlatDatabase().saveFaction(newFaction);

                                c.getSource().sendSuccess(MutableComponent.create(ComponentContents.EMPTY)
                                        .append(ChatColours.TextColour.INFO + "Successfully created faction ")
                                        .append(RelationUtil.wrapFactionName(newFaction, newFaction))
                                        .append(ChatColours.TextColour.INFO + "\nAdd a description with ")
                                        .append(wrapCommand("/f desc <description>", "/f desc "))
                                        .append(ChatColours.TextColour.INFO + "\nand invite people using ")
                                        .append(wrapCommand("/f invite <player name>", "/f invite "))
                                , false);
                                return 1;
                            })
                ).executes(c -> {
                    c.getSource().sendFailure(Component.literal("You must provide a faction name"));
                    return 1;
                });
    }
}

package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionChangeNameEvent;
import com.datdeveloper.datfactions.factionData.*;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
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

import java.util.function.Predicate;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONSETNAME;

public class FactionNameCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final Predicate<CommandSourceStack> predicate = commandSourceStack -> {
            if (!(commandSourceStack.isPlayer()) && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTIONSETNAME))
                return false;
            final FactionPlayer fPlayer = getPlayerOrTemplate(commandSourceStack.getPlayer());
            return fPlayer.hasFaction() && fPlayer.getRole().hasPermission(ERolePermissions.SETNAME);
        };

        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("name")
                .requires(predicate)
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
                            final Faction faction = fPlayer.getFaction();

                            final FactionChangeNameEvent event = new FactionChangeNameEvent(c.getSource().source, faction, newName);
                            MinecraftForge.EVENT_BUS.post(event);
                            if (event.isCanceled()) return 0;

                            faction.setName(event.getNewName());
                            c.getSource().sendSuccess(MutableComponent.create(ComponentContents.EMPTY)
                                    .append(DatChatFormatting.TextColour.INFO + "Successfully set your faction's name to ")
                                    .append(EFactionRelation.SELF.formatting + event.getNewName())
                            ,false);
                            return 1;
                        })).build();

        command.then(subCommand);
        command.then(Commands.literal("setname").requires(predicate).redirect(subCommand));
    }
}

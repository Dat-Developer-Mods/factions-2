package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionChangeDescriptionEvent;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.command.arguments.LimitedStringArgument;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.MinecraftForge;

import java.util.function.Predicate;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONSETDESC;

public class FactionDescriptionCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final Predicate<CommandSourceStack> predicate = commandSourceStack -> {
            if (!(commandSourceStack.isPlayer()) && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTIONSETDESC))
                return false;
            final FactionPlayer fPlayer = getPlayerOrTemplate(commandSourceStack.getPlayer());
            return fPlayer.hasFaction() && fPlayer.getRole().hasPermission(ERolePermissions.SETDESC);
        };
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("desc")
                .requires(predicate)
                .then(Commands.argument("description", LimitedStringArgument.greedyString(FactionsConfig.getMaxFactionDescriptionLength()))
                        .executes(c -> {
                            final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
                            final Faction faction = fPlayer.getFaction();
                            final String newDescription = c.getArgument("description", String.class);

                            final FactionChangeDescriptionEvent event = new FactionChangeDescriptionEvent(c.getSource().source, faction, newDescription);
                            MinecraftForge.EVENT_BUS.post(event);
                            if (event.isCanceled()) return 0;

                            faction.setDescription(event.getNewDescription());
                            c.getSource().sendSuccess(MutableComponent.create(ComponentContents.EMPTY)
                                    .append(DatChatFormatting.TextColour.INFO + "Successfully set your faction's description")
                            ,false);
                            return 1;
                        })).build();

        command.then(subCommand);
        command.then(Commands.literal("setdesc").requires(predicate).redirect(subCommand));
    }
}

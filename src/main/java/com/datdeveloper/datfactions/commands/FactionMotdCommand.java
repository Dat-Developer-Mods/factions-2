package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionChangeMotdEvent;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.command.arguments.LimitedStringArgument;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.common.MinecraftForge;

import java.util.function.Predicate;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONSETMOTD;

public class FactionMotdCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final Predicate<CommandSourceStack> predicate = commandSourceStack -> {
            if (!(commandSourceStack.isPlayer()) && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTIONSETMOTD))
                return false;
            final FactionPlayer fPlayer = getPlayerOrTemplate(commandSourceStack.getPlayer());
            return fPlayer.hasFaction() && fPlayer.getRole().hasPermission(ERolePermissions.SETMOTD);
        };

        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("motd")
                .requires(predicate)
                .then(Commands.argument("motd", LimitedStringArgument.greedyString(FactionsConfig.getMaxFactionMotdLength()))
                        .executes(c -> {
                            final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
                            final Faction faction = fPlayer.getFaction();
                            final String newMotd = c.getArgument("motd", String.class);

                            final FactionChangeMotdEvent event = new FactionChangeMotdEvent(c.getSource().source, faction, newMotd);
                            MinecraftForge.EVENT_BUS.post(event);
                            if (event.isCanceled()) return 0;

                            faction.setMotd(event.getNewMotd());
                            // No message here, setting the MOTD should send a factionwide message
                            return 1;
                        })).build();

        command.then(subCommand);
        command.then(Commands.literal("setmotd").requires(predicate).redirect(subCommand));
    }
}

package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionSetHomeEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.exceptions.FactionHomeLocationException;
import com.datdeveloper.datfactions.factiondata.*;
import com.datdeveloper.datfactions.factiondata.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_SET_HOME;

/**
 * A command that allows a player to set their faction's home
 */
public class FactionSetHomeCommand {
    /**
     * Visitor to register the command
     */
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("sethome")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_SET_HOME)))
                        return false;
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return fPlayer.hasFaction() && fPlayer.getRole().hasPermission(ERolePermissions.SETHOME);
                })
                .executes(FactionSetHomeCommand::run).build();

        command.then(subCommand);
    }

    /**
     * Handle the set home command
     * @param c The command context
     * @return 1 for success
     */
    private static int run(final CommandContext<CommandSourceStack> c) {
        final ServerPlayer player = c.getSource().getPlayer();
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
        final Faction faction = fPlayer.getFaction();

        FactionLevel newHomeLevel = FLevelCollection.getInstance().getByKey(player.level().dimension());
        BlockPos newHomePos = player.getOnPos().above();

        final FactionSetHomeEvent.Pre event = new FactionSetHomeEvent.Pre(c.getSource().getPlayer(), faction, newHomeLevel, newHomePos);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            final Component reason = event.getDenyReason();
            if (reason != null) {
                throw new CommandRuntimeException(reason);
            } else {
                return 0;
            }
        }

        newHomeLevel = event.getNewHomeLevel();
        newHomePos = event.getNewHomePos();

        try {
            faction.setFactionHome(newHomeLevel, newHomePos);
        } catch (final FactionHomeLocationException e) {
            throw new CommandRuntimeException(Component.literal("You cannot set your faction's home on a chunk your faction does not own."));
        }


        c.getSource().sendSuccess(() -> Component.literal(DatChatFormatting.TextColour.INFO + "Successfully set your faction's home")
                , false);
        return 1;
    }
}

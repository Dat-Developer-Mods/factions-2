package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionPlayerHomeEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.delayedEvents.FactionHomeTeleportDelayedEvent;
import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.factiondata.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.delayedEvents.DelayedEventsHandler;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_HOME;

/**
 * A command that allows a player to travel to their faction's home
 */
public class FactionHomeCommand {
    /**
     * Visitor to register the command
     */
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        command.then(Commands.literal("home")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_HOME)))
                        return false;
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return fPlayer.hasFaction() && fPlayer.getRole().hasPermission(ERolePermissions.HOME);
                })
                .executes(FactionHomeCommand::run).build());
    }

    /**
     * Handle home command
     * @param c The command context
     * @return 1 if success
     */
    private static int run(final CommandContext<CommandSourceStack> c) {
        final ServerPlayer player = c.getSource().getPlayer();
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
        final Faction faction = fPlayer.getFaction();

        final FactionPlayerHomeEvent.Pre event = new FactionPlayerHomeEvent.Pre(c.getSource().getPlayer(), fPlayer);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            final Component reason = event.getDenyReason();
            if (reason != null) {
                throw new CommandRuntimeException(reason);
            } else {
                return 0;
            }
        }

        DelayedEventsHandler.addEvent(new FactionHomeTeleportDelayedEvent(player, faction));

        c.getSource().sendSuccess(() -> MutableComponent.create(ComponentContents.EMPTY)
                        .append(DatChatFormatting.TextColour.INFO + "Teleporting to your faction's home in " + FactionsConfig.getTeleportDelay() + " seconds"),
                false);
        return 1;
    }
}

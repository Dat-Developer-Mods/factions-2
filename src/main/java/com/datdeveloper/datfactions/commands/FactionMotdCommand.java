package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionChangeMotdEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.exceptions.StringTooLongException;
import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.factiondata.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_SET_MOTD;

/**
 * A command that allows a player to view or set the MOTD of their faction
 */
public class FactionMotdCommand {
    /** The argument for the MOTD */
    static final String MOTD_ARG = "New MOTD";

    /**
     * Visitor to register the command
     */
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        command.then(Commands.literal("motd")
                .requires(commandSourceStack -> {
                    if (!commandSourceStack.isPlayer()) return false;

                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return fPlayer.hasFaction();
                })
                .then(Commands.argument(MOTD_ARG, StringArgumentType.greedyString())
                        .requires(commandSourceStack -> {
                            if (!DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_SET_MOTD)) return false;

                            final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                            return fPlayer.getRole().hasPermission(ERolePermissions.SETMOTD);
                        })
                        .executes(c -> setMotd(c, c.getArgument(MOTD_ARG, String.class)))
                )
                .executes(FactionMotdCommand::seeMotd).build());
    }

    /**
     * Send the MOTD to the player
     * @param c The command context
     * @return 1 for success
     */
    private static int seeMotd(final CommandContext<CommandSourceStack> c) {
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
        final Faction faction = fPlayer.getFaction();

        c.getSource().sendSystemMessage(Component.literal(faction.getMotd()));

        return 1;
    }

    /**
     * Handle setting the MOTD of the player's faction
     * @param c The command context
     * @param newMotd The new MOTD of the faction
     * @return 1 for success
     * @throws CommandSyntaxException When the new MOTD is too long
     */
    private static int setMotd(final CommandContext<CommandSourceStack> c, String newMotd) throws CommandSyntaxException {
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
        final Faction faction = fPlayer.getFaction();

        final FactionChangeMotdEvent.Pre event = new FactionChangeMotdEvent.Pre(c.getSource().getPlayer(), faction, newMotd);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            final Component reason = event.getDenyReason();
            if (reason != null) {
                throw new CommandRuntimeException(reason);
            } else {
                return 0;
            }
        }

        newMotd = event.getNewMotd();

        try {
            faction.setMotd(newMotd);
        } catch (final StringTooLongException e) {
            throw new SimpleCommandExceptionType(Component.literal("Your faction MOTD cannot be longer than " + e.getMaxLength())).create();
        }

        // No message here, setting the MOTD should send a faction-wide message
        return 1;
    }
}

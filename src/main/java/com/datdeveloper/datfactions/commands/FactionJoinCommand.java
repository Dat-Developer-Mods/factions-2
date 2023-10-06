package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionPlayerChangeMembershipEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factiondata.*;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import com.datdeveloper.datfactions.factiondata.relations.EFactionRelation;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
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
import net.minecraftforge.eventbus.api.Event;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_JOIN;

/**
 * A command to allow players to join a faction
 */
public class FactionJoinCommand {
    /** The argument for the target faction */
    static final String TARGET_FACTION_ARG = "Target Faction";

    /**
     * Visitor to register the command
     */
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        command.then(Commands.literal("join")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_JOIN))) return false;
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return !fPlayer.hasFaction();
                })
                .then(Commands.argument(TARGET_FACTION_ARG, StringArgumentType.word())
                        .suggests((context, builder) -> {
                            FactionCollection.getInstance().getAll().values().stream()
                                    .filter(faction -> faction.hasFlag(EFactionFlags.OPEN) || faction.hasInvitedPlayer(context.getSource().getPlayer().getUUID()))
                                    .forEach(faction -> builder.suggest(faction.getName()));
                            return builder.buildFuture();
                        })
                        .executes(c -> run(c, c.getArgument(TARGET_FACTION_ARG, String.class))))
                .build());
    }

    /**
     * Execute the join command
     * @param c The command context
     * @param targetName The name of the target faction
     * @return 1 if successful
     */
    private static int run(final CommandContext<CommandSourceStack> c, final String targetName) throws CommandSyntaxException {
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());

        final Faction passedTarget = FactionCollection.getInstance().getByName(targetName);

        if (passedTarget == null) {
            throw new SimpleCommandExceptionType(Component.literal("Cannot find a faction with that name")).create();
        }

        final FactionPlayerChangeMembershipEvent.Pre event = new FactionPlayerChangeMembershipEvent.Pre(
                c.getSource().getPlayer(),
                fPlayer,
                passedTarget,
                passedTarget.getDefaultRole(),
                FactionPlayerChangeMembershipEvent.EChangeFactionReason.JOIN
        );
        MinecraftForge.EVENT_BUS.post(event);

        final Faction target = event.getNewFaction();
        final FactionRole targetRole = event.getNewRole();
        final Event.Result result = event.getResult();

        if (result == Event.Result.DENY) {
            final Component reason = event.getDenyReason();
            if (reason != null) {
                throw new CommandRuntimeException(reason);
            } else {
                return 0;
            }
        } else if (result == Event.Result.DEFAULT && target != null && !(target.hasFlag(EFactionFlags.OPEN) && target.hasInvitedPlayer(fPlayer.getId()))) {
                throw new CommandRuntimeException(Component.literal("You are not allowed to join that faction"));
        }

        fPlayer.setFaction(
                target,
                targetRole,
                FactionPlayerChangeMembershipEvent.EChangeFactionReason.JOIN
        );

        if (target != null) {
            c.getSource().sendSuccess(() ->
                Component.literal(DatChatFormatting.TextColour.INFO + "Successfully joined ")
                        .append(target.getNameWithDescription(target)
                                        .withStyle(EFactionRelation.SELF.formatting)),
                false
            );
        } else {
            c.getSource().sendSuccess(() ->
                Component.literal(DatChatFormatting.TextColour.INFO + "You are no longer a part of a faction"),
                false
            );
        }

        return 1;
    }
}

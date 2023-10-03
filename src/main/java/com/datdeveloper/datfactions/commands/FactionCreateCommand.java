package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionCreateEvent;
import com.datdeveloper.datfactions.api.events.FactionPlayerChangeMembershipEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.exceptions.FactionNameTakenException;
import com.datdeveloper.datfactions.exceptions.StringTooLongException;
import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionCollection;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.util.RelationUtil;
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

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_CREATE;

/**
 * A command that allows the player to create a new faction
 */
public class FactionCreateCommand {
    /** The argument string for the faction name */
    static final String FACTION_NAME_ARGUMENT = "Faction Name";

    /**
     * Visitor to register the command
     */
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        command.then(Commands.literal("create")
                .requires((commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_CREATE))) return false;

                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return !fPlayer.hasFaction();
                }))
                .then(Commands.argument(FACTION_NAME_ARGUMENT, StringArgumentType.word())
                        .executes(c -> run(c, c.getArgument(FACTION_NAME_ARGUMENT, String.class)))
                ));
    }

    /**
     * Handle command
     * @param c The command context
     * @return 1 if success
     * @throws CommandSyntaxException When the name is too long
     */
    private static int run(final CommandContext<CommandSourceStack> c, String factionName) throws CommandSyntaxException {
        final FactionCreateEvent.Pre event = new FactionCreateEvent.Pre(c.getSource().getPlayer(), factionName);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            final Component reason = event.getDenyReason();
            if (reason != null) {
                throw new CommandRuntimeException(reason);
            } else {
                return 0;
            }
        }

        factionName = event.getName();

        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());

        final Faction newFaction = FactionCollection.getInstance().createFaction(factionName);

        try {
            fPlayer.setFaction(newFaction, newFaction.getOwnerRole(), FactionPlayerChangeMembershipEvent.EChangeFactionReason.CREATE);
        } catch (final StringTooLongException e) {
            throw new SimpleCommandExceptionType(
                    Component.literal("Your faction name cannot be longer than " + e.getMaxLength())
            ).create();
        } catch (final FactionNameTakenException e) {
            throw new CommandRuntimeException(Component.literal("A faction with that name already exists"));
        }

        c.getSource().sendSuccess(() -> Component.literal(DatChatFormatting.TextColour.INFO + "Successfully created faction ")
                        .append(
                                newFaction.getNameWithDescription(newFaction)
                                        .withStyle(RelationUtil.getRelation(newFaction, newFaction).formatting)
                        ).append("\n")
                        .append(DatChatFormatting.TextColour.INFO + "Add a description with ")
                        .append(FactionCommandUtils.wrapCommand("/f desc <description>", "/f desc ")).append("\n")
                        .append(DatChatFormatting.TextColour.INFO + "and invite people using ")
                        .append(FactionCommandUtils.wrapCommand("/f invites add <player name>", "/f invites add ")),
                false);

        return 1;
    }
}

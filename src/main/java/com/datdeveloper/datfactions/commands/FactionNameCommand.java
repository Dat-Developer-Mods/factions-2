package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionChangeNameEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.exceptions.FactionNameTakenException;
import com.datdeveloper.datfactions.exceptions.StringTooLongException;
import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.factiondata.permissions.ERolePermissions;
import com.datdeveloper.datfactions.factiondata.relations.EFactionRelation;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_SET_NAME;

/**
 * A command that allows a player to set the name of their faction
 */
public class FactionNameCommand {
    /** The argument for the new faction name */
    static final String FACTION_NAME_ARG = "New Faction Name";

    /**
     * Visitor to register the command
     */
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("name")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_SET_NAME)))
                        return false;
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return fPlayer.hasFaction() && fPlayer.getRole().hasPermission(ERolePermissions.SETNAME);
                })
                .then(Commands.argument(FACTION_NAME_ARG, StringArgumentType.word())
                        .executes(c -> run(c, c.getArgument(FACTION_NAME_ARG, String.class)))).build();

        command.then(subCommand);
        command.then(FactionCommandUtils.buildRedirect("setname", subCommand));
    }

    /**
     * Handle the faction name command
     * @param c The command context
     * @param newName The new name of the faction
     * @return 1 if success
     * @throws CommandSyntaxException When the new name is too long
     */
    private static int run(final CommandContext<CommandSourceStack> c, String newName) throws CommandSyntaxException {
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
        final Faction faction = fPlayer.getFaction();

        final FactionChangeNameEvent.Pre event = new FactionChangeNameEvent.Pre(c.getSource().getPlayer(), faction, newName);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            final Component reason = event.getDenyReason();
            if (reason != null) {
                throw new CommandRuntimeException(reason);
            } else {
                return 0;
            }
        }

        newName = event.getNewName();

        try {
            faction.setName(newName);
        } catch (final StringTooLongException e) {
            throw new SimpleCommandExceptionType(Component.literal("Your faction name cannot be longer than "
                    + e.getMaxLength() + " characters")).create();
        } catch (final FactionNameTakenException e) {
            throw new CommandRuntimeException(Component.literal("A faction with that name already exists"));
        }

        c.getSource().sendSuccess(() -> MutableComponent.create(ComponentContents.EMPTY)
                        .append(DatChatFormatting.TextColour.INFO + "Successfully set your faction's name to ")
                        .append(EFactionRelation.SELF.formatting + faction.getName())
                , false);
        return 1;
    }
}

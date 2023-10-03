package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionChangeDescriptionEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.exceptions.StringTooLongException;
import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.factiondata.permissions.ERolePermissions;
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

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_SET_DESC;

/**
 * A command that allows a player to set the description of their faction
 */
public class FactionDescriptionCommand {
    /** The argument string for the description */
    static final String DESCRIPTION_ARG = "Description";

    /**
     * Visitor to register the command
     */
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("desc")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_SET_DESC)))
                        return false;
                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return fPlayer.hasFaction() && fPlayer.getRole().hasPermission(ERolePermissions.SETDESC);
                })
                .then(Commands.argument(DESCRIPTION_ARG, StringArgumentType.greedyString())
                        .executes(c -> run(c, c.getArgument(DESCRIPTION_ARG, String.class)))).build();

        command.then(subCommand);
        command.then(FactionCommandUtils.buildRedirect("setdesc", subCommand));
    }

    /**
     * Handle the command
     * @param c The command context
     * @return 1 if successful
     * @throws CommandSyntaxException When the description is too long
     */
    private static int run(final CommandContext<CommandSourceStack> c, String newDescription) throws CommandSyntaxException {
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
        final Faction faction = fPlayer.getFaction();

        final FactionChangeDescriptionEvent.Pre event = new FactionChangeDescriptionEvent.Pre(c.getSource().getPlayer(), faction, newDescription);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            final Component reason = event.getDenyReason();
            if (reason != null) {
                throw new CommandRuntimeException(reason);
            } else {
                return 0;
            }
        }

        newDescription = event.getNewDescription();

        try {
            faction.setDescription(newDescription);
        } catch (final StringTooLongException e) {
            throw new SimpleCommandExceptionType(Component.literal("Your faction description cannot be longer than " + FactionsConfig.getMaxFactionDescriptionLength())).create();
        }

        c.getSource().sendSuccess(() -> MutableComponent.create(ComponentContents.EMPTY)
                        .append(DatChatFormatting.TextColour.INFO + "Successfully set your faction's description")
                , false);
        return 1;
    }
}

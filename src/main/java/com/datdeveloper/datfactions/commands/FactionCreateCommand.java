package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.api.events.FactionPlayerChangeMembershipEvent;
import com.datdeveloper.datfactions.api.events.FactionCreateEvent;
import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_CREATE;

public class FactionCreateCommand {
    /** The argument string for the faction name */
    static final String FACTION_NAME_ARGUMENT = "Faction Name";

    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        command.then(Commands.literal("create")
                .requires((commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_CREATE))) return false;

                    final FactionPlayer fPlayer = FactionCommandUtils.getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return !fPlayer.hasFaction();
                }))
                .then(Commands.argument(FACTION_NAME_ARGUMENT, StringArgumentType.word())
                        .executes(FactionCreateCommand::run)
                ));
    }

    private static int run(final CommandContext<CommandSourceStack> c) {
        final String factionName = c.getArgument("Faction Name", String.class);

        final FactionCreateEvent.Pre event = new FactionCreateEvent.Pre(c.getSource().getPlayer(), factionName);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())

        if (factionName.length() > FactionsConfig.getMaxFactionNameLength()) {
            c.getSource().sendFailure(Component.literal("Your faction name cannot be longer than " + FactionsConfig.getMaxFactionNameLength()));
            return 2;
        } else if (FactionCollection.getInstance().isNameTaken(factionName)) {
            c.getSource().sendFailure(Component.literal("A faction with that name already exists"));
            return 3;
        }

        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());

        final FactionCreateEvent event = new FactionCreateEvent(c.getSource().source, factionName);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return 0;

        final Faction newFaction = FactionCollection.getInstance().createFaction(event.getName());

        final FactionPlayerChangeMembershipEvent factionPlayerChangeMembershipEvent = new FactionPlayerChangeMembershipEvent(c.getSource().source, fPlayer, newFaction, newFaction.getOwnerRole(), FactionPlayerChangeMembershipEvent.EChangeFactionReason.CREATE);
        MinecraftForge.EVENT_BUS.post(factionPlayerChangeMembershipEvent);

        fPlayer.setFaction(newFaction.getId(), newFaction.getOwnerRole().getId(), FactionPlayerChangeMembershipEvent.EChangeFactionReason.CREATE);

        c.getSource().sendSuccess(() -> MutableComponent.create(ComponentContents.EMPTY)
                        .append(DatChatFormatting.TextColour.INFO + "Successfully created faction ")
                        .append(
                                newFaction.getNameWithDescription(newFaction)
                                        .withStyle(RelationUtil.getRelation(newFaction, newFaction).formatting)
                        ).append("\n")
                        .append(DatChatFormatting.TextColour.INFO + "Add a description with ")
                        .append(FactionCommandUtils.wrapCommand("/f desc <description>", "/f desc ")).append("\n")
                        .append(DatChatFormatting.TextColour.INFO + "and invite people using ")
                        .append(FactionCommandUtils.wrapCommand("/f invites add <player name>", "/f invites add "))
                , false);

        return 1;
    }
}

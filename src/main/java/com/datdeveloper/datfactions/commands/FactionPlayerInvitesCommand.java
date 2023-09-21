package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.commands.util.FactionCommandUtils;
import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionCollection;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.util.RelationUtil;
import com.datdeveloper.datmoddingapi.concurrentTask.ConcurrentHandler;
import com.datdeveloper.datmoddingapi.command.util.Pager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_PLAYER_INVITES;

public class FactionPlayerInvitesCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("myinvites")
                .requires(FactionPermissions.hasPermission(FACTION_PLAYER_INVITES))
                .then(Commands.argument("Page", IntegerArgumentType.integer(1))
                        .executes(c -> execute(c, c.getArgument("Page", Integer.class))))
                .executes(c -> execute(c, 1))
                .build();

        command.then(subCommand);
        command.then(FactionCommandUtils.buildRedirect("pinvites", subCommand));
        command.then(FactionCommandUtils.buildRedirect("playerinvites", subCommand));
    }

    private static int execute(final CommandContext<CommandSourceStack> context, final int page) {
        final FactionPlayer player = FPlayerCollection.getInstance().getPlayer(context.getSource().getPlayer());
        ConcurrentHandler.runConcurrentTask(() -> {
            final Collection<Faction> factions = FactionCollection.getInstance().getAll().values().stream()
                    .filter(faction -> faction.getPlayerInvites().contains(player.getId()))
                    .sorted(Comparator.comparing(Faction::getName))
                    .collect(Collectors.toList());

            if (factions.isEmpty()) {
                context.getSource().sendFailure(Component.literal("You have not been invited to any factions"));
                return;
            }

            final Pager<Faction> pager = new Pager<>("/f myinvites", "Invites", factions, faction ->
                    faction.getNameWithDescription(player.getFaction())
                        .withStyle(RelationUtil.getRelation(player, faction).formatting)
            );

            pager.sendPage(page, context.getSource().source);
        });
        return 1;
    }
}

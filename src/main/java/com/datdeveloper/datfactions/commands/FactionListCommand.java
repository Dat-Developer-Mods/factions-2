package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.factionData.*;
import com.datdeveloper.datmoddingapi.asyncTask.AsyncHandler;
import com.datdeveloper.datmoddingapi.command.util.Pager;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
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

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_LIST;

public class FactionListCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("list")
                .requires(FactionPermissions.hasPermission(FACTION_LIST))
                .then(Commands.argument("Page", IntegerArgumentType.integer(1))
                        .executes(c -> execute(c, c.getArgument("Page", Integer.class))))
                .executes(c -> execute(c, 1))
                .build();

        command.then(subCommand);
    }

    private static int execute(final CommandContext<CommandSourceStack> context, final int page) {
        final FactionPlayer player = FPlayerCollection.getInstance().getPlayer(context.getSource().getPlayer());
        AsyncHandler.runAsyncTask(() -> {
            final Collection<Faction> factions = FactionCollection.getInstance().getAll().values().stream()
                    .filter(faction -> !faction.hasFlag(EFactionFlags.DEFAULT))
                    .sorted(Comparator.comparing(Faction::getName))
                    .collect(Collectors.toList());

            if (factions.isEmpty()) {
                context.getSource().sendFailure(Component.literal("This server has no factions"));
                return;
            }

            final Pager<Faction> pager = new Pager<>("/f list", "Factions", factions, (faction) ->
                    faction.getNameWithDescription(player.getFaction())
                    .withStyle(faction.isAnyoneOnline() ? DatChatFormatting.PlayerColour.ONLINE : DatChatFormatting.PlayerColour.OFFLINE)
            );

            pager.sendPage(page, context.getSource().source);
        });
        return 1;
    }
}

package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.factionData.*;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datmoddingapi.asyncTask.AsyncHandler;
import com.datdeveloper.datmoddingapi.command.util.Pager;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

import static com.datdeveloper.datfactions.commands.FactionPermissions.*;

public class FactionRelationCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {

        final LiteralArgumentBuilder<CommandSourceStack> subCommand = Commands.literal("relations")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer()) && DatPermissions.hasAnyPermissions(commandSourceStack.getPlayer(), FACTION_RELATION_WISHES, FACTION_RELATION_ALLY, FACTION_RELATION_TRUCE, FACTION_RELATION_NEUTRAL, FACTION_RELATION_ENEMY))
                        return false;
                    final FactionPlayer fPlayer = getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return fPlayer.hasFaction() && fPlayer.getRole().hasAnyPermissions(ERolePermissions.RELATIONWISHES, ERolePermissions.RELATIONALLY, ERolePermissions.RELATIONTRUCE, ERolePermissions.RELATIONNEUTRAL, ERolePermissions.RELATIONENEMY);
                })
                .then(buildRelationWishesCommand())
                .then(buildRelationAllyCommand());

        command.then(subCommand.build());
    }

    /* ========================================= */
    /* Relation Wishes
    /* ========================================= */

    static LiteralArgumentBuilder<CommandSourceStack> buildRelationWishesCommand() {
        return Commands.literal("wishes")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_RELATION_WISHES) && fPlayer.getRole().hasPermission(ERolePermissions.RELATIONWISHES);
                })
                .then(
                        Commands.argument("Page", IntegerArgumentType.integer(1))
                                .executes(c -> executeWishes(c.getSource(), c.getArgument("Page", Integer.class)))
                )
                .executes(c -> executeWishes(c.getSource(), 1));
    }

    private static int executeWishes(final CommandSourceStack sourceStack, final int page) {
        final ServerPlayer player = sourceStack.getPlayer();
        final FactionPlayer fPlayer = getPlayerOrTemplate(player);
        final Faction faction = fPlayer.getFaction();

        AsyncHandler.runAsyncTask(() -> {
            final List<Faction> values = FactionCollection.getInstance().getAll().values().stream()
                    .filter(factionEl -> {
                        final FactionRelation relationFrom = factionEl.getRelation(faction);
                        final FactionRelation relationTo = faction.getRelation(factionEl);
                        return (relationFrom != null && (relationTo == null || relationTo.getRelation() != relationFrom.getRelation()));
                    })
                    .toList();
            final Pager<Faction> pager = new Pager<>(
                    "/f relation wishes",
                    "Relation wishes",
                    values,
                    (factionEl -> {
                            final FactionRelation relationFrom = factionEl.getRelation(faction);
                            final FactionRelation relationTo = faction.getRelation(factionEl);
                            final MutableComponent component = Component.empty()
                                    .append(
                                            factionEl.getNameWithDescription(faction)
                                                    .withStyle(EFactionRelation.NEUTRAL.formatting)
                                    );

                            final MutableComponent fromComponent;
                            switch (relationFrom.getRelation()) {
                                case ALLY -> fromComponent = Component.literal(" regard us as an ally");
                                case TRUCE -> fromComponent = Component.literal(" wants a truce with us");
                                case ENEMY -> fromComponent = Component.literal(" regard us as an enemy");
                                default ->
                                        throw new IllegalStateException("Unexpected value: " + relationFrom.getRelation());
                            }
                            component.append(fromComponent.append(", ").withStyle(relationFrom.getRelation().formatting));

                            final EFactionRelation eRelationTo = relationTo != null ? relationTo.getRelation() : EFactionRelation.NEUTRAL;

                            final MutableComponent toComponent = Component.literal("but ");
                            switch (eRelationTo) {
                                case ALLY -> toComponent.append("we think of them as an ally");
                                case TRUCE -> toComponent.append("we want a truce with them");
                                case NEUTRAL -> toComponent.append("we are neutral with them");
                                case ENEMY -> toComponent.append("we think of them as an enemy");
                            }
                            component.append(toComponent.withStyle(eRelationTo.formatting));

                            return component;
                    })
            );
            pager.sendPage(page, sourceStack.source);
        });

        return 1;
    }

    /* ========================================= */
    /* Relation Ally
    /* ========================================= */

    /* ========================================= */
    /* Relation Truce
    /* ========================================= */

    /* ========================================= */
    /* Relation Neutral
    /* ========================================= */

    /* ========================================= */
    /* Relation Enemy
    /* ========================================= */
}
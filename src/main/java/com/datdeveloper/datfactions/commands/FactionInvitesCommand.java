package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionChangeRelationEvent;
import com.datdeveloper.datfactions.commands.suggestions.DatSuggestionProviders;
import com.datdeveloper.datfactions.factionData.EFactionFlags;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionCollection;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datfactions.factionData.relations.EFactionRelation;
import com.datdeveloper.datfactions.factionData.relations.FactionRelation;
import com.datdeveloper.datfactions.util.RelationUtil;
import com.datdeveloper.datmoddingapi.asyncTask.AsyncHandler;
import com.datdeveloper.datmoddingapi.command.util.Pager;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

import static com.datdeveloper.datfactions.commands.FactionPermissions.*;

public class FactionInvitesCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {

        final LiteralArgumentBuilder<CommandSourceStack> subCommand = Commands.literal("invites")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer()) && DatPermissions.hasAnyPermissions(commandSourceStack.getPlayer(), FACTION_INVITE, FACTION_UNINVITE, FACTION_INVITE_LIST_FACTION))
                        return false;
                    final FactionPlayer fPlayer = getPlayerOrTemplate(commandSourceStack.getPlayer());
                    final Faction faction = fPlayer.getFaction();
                    return faction != null && !faction.hasFlag(EFactionFlags.UNRELATEABLE) && fPlayer.getRole().hasAnyPermissions(ERolePermissions.INVITE, ERolePermissions.UNINVITE, ERolePermissions.INVITELIST);
                })
                .then(buildInviteListCommand())
                .then(buildInviteCommand())
                .then(buildUninviteCommand());

        command.then(subCommand.build());
    }

    /* ========================================= */
    /* Invite list
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

            if (values.isEmpty()) {
                sourceStack.sendFailure(Component.literal("There are no reciprocated relations towards your faction"));
                return;
            }

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
                                case ALLY -> fromComponent = Component.literal(" regard us as an ally,");
                                case TRUCE -> fromComponent = Component.literal(" wants a truce with us,");
                                case ENEMY -> fromComponent = Component.literal(" regard us as an enemy,");
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

    static LiteralArgumentBuilder<CommandSourceStack> buildRelationAllyCommand() {
        return Commands.literal("ally")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_RELATION_ALLY) && fPlayer.getRole().hasPermission(ERolePermissions.RELATIONALLY);
                })
                .then(
                        Commands.argument("Target Faction", StringArgumentType.word())
                                .suggests(DatSuggestionProviders.factionProvider)
                                .executes(c -> executeRelation(c, EFactionRelation.ALLY))
                );
    }

    /* ========================================= */
    /* Relation Truce
    /* ========================================= */

    static LiteralArgumentBuilder<CommandSourceStack> buildRelationTruceCommand() {
        return Commands.literal("truce")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_RELATION_TRUCE) && fPlayer.getRole().hasPermission(ERolePermissions.RELATIONTRUCE);
                })
                .then(
                        Commands.argument("Target Faction", StringArgumentType.word())
                                .suggests(DatSuggestionProviders.factionProvider)
                                .executes(c -> executeRelation(c, EFactionRelation.TRUCE))
                );
    }

    /* ========================================= */
    /* Relation Neutral
    /* ========================================= */

    static LiteralArgumentBuilder<CommandSourceStack> buildRelationNeutralCommand() {
        return Commands.literal("neutral")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_RELATION_NEUTRAL) && fPlayer.getRole().hasPermission(ERolePermissions.RELATIONNEUTRAL);
                })
                .then(
                        Commands.argument("Target Faction", StringArgumentType.word())
                                .suggests(DatSuggestionProviders.factionProvider)
                                .executes(c -> executeRelation(c, EFactionRelation.NEUTRAL))
                );
    }

    /* ========================================= */
    /* Relation Enemy
    /* ========================================= */

    static LiteralArgumentBuilder<CommandSourceStack> buildRelationEnemyCommand() {
        return Commands.literal("enemy")
                .requires(commandSourceStack -> {
                    final ServerPlayer player = commandSourceStack.getPlayer();
                    final FactionPlayer fPlayer = getPlayerOrTemplate(player);
                    return DatPermissions.hasPermission(player, FACTION_RELATION_ENEMY) && fPlayer.getRole().hasPermission(ERolePermissions.RELATIONENEMY);
                })
                .then(
                        Commands.argument("Target Faction", StringArgumentType.word())
                                .suggests(DatSuggestionProviders.factionProvider)
                                .executes(c -> executeRelation(c, EFactionRelation.ENEMY))
                );
    }

    /* ========================================= */
    /* Util
    /* ========================================= */

    /**
     * Execute relation change
     * @param c The command context
     * @param relation The new relation
     * @return the return code representing the result
     */
    private static int executeRelation(final CommandContext<CommandSourceStack> c, EFactionRelation relation) {
        final ServerPlayer player = c.getSource().getPlayer();
        final FactionPlayer fPlayer = getPlayerOrTemplate(player);
        final Faction faction = fPlayer.getFaction();

        final String targetName = c.getArgument("Target Faction", String.class);
        final Faction target = FactionCollection.getInstance().getByName(targetName);
        if (target == null) {
            c.getSource().sendFailure(Component.literal("Cannot find a faction with that name"));
            return 2;
        } else if (target.equals(faction)) {
            c.getSource().sendFailure(
                    Component.literal("You cannot make a relation with yourself")
            );
            return 3;
        } else if (target.hasFlag(EFactionFlags.UNRELATEABLE)) {
            c.getSource().sendFailure(
                    Component.literal("You cannot make relations with ")
                            .append(
                                    target.getNameWithDescription(faction)
                                            .withStyle(RelationUtil.getRelation(faction, target).formatting)
                            )
            );
            return 4;
        }

        final FactionRelation currentRelation = faction.getRelation(target);
        if (currentRelation != null && currentRelation.getRelation() == relation) {
            c.getSource().sendFailure(
                    Component.literal("You are already " + relation.adjective + " with ")
                            .append(
                                    target.getNameWithDescription(faction)
                                            .withStyle(currentRelation.getRelation().formatting)
                            )
            );
            return 5;
        }

        final FactionChangeRelationEvent event = new FactionChangeRelationEvent(c.getSource().source, faction, target, relation);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return 0;

        relation = event.getNewRelation();
        faction.setRelation(target, relation);
        target.informRelation(faction, relation);
        final MutableComponent message = Component.literal(DatChatFormatting.TextColour.INFO + "Successfully ");

        switch (relation) {
            case ALLY, ENEMY -> message.append("declared ")
                    .append(
                            target.getNameWithDescription(faction)
                                    .withStyle(relation.formatting)
                    )
                    .append(" an " + relation.name().toLowerCase());
            case TRUCE -> message.append("declared a truce with ")
                    .append(
                            target.getNameWithDescription(faction)
                                    .withStyle(relation.formatting)
                    );
            case NEUTRAL -> message.append("removed relation with ")
                    .append(
                            target.getNameWithDescription(faction)
                                    .withStyle(relation.formatting)
                    );
        }

        c.getSource().sendSuccess(
                message,
                true
        );

        return 1;
    }
}
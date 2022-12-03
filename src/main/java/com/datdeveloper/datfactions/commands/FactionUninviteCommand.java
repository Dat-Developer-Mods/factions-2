package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionUninvitePlayerEvent;
import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.datdeveloper.datfactions.factionData.permissions.ERolePermissions;
import com.datdeveloper.datfactions.util.RelationUtil;
import com.datdeveloper.datmoddingapi.permissions.DatPermissions;
import com.datdeveloper.datmoddingapi.util.DatChatFormatting;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTION_UNINVITE;

public class FactionUninviteCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("uninvite")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTION_UNINVITE))) return false;
                    final FactionPlayer fPlayer = getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return fPlayer.hasFaction() && fPlayer.getRole().hasPermission(ERolePermissions.UNINVITE);
                })
                .then(Commands.argument("Target Player", GameProfileArgument.gameProfile())
                        .suggests((context, builder) -> {
                            final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(context.getSource().getPlayer());
                            final Faction faction = fPlayer.getFaction();

                            faction.getPlayerInvites().stream()
                                    .map((id) -> FPlayerCollection.getInstance().getByKey(id))
                                    .filter(FactionPlayer::isPlayerOnline)
                                    .forEach(player -> {
                                        builder.suggest(player.getName());
                                    });

                            // Get factions
                            return builder.buildFuture();
                        })
                        .executes(c -> {
                            final GameProfile profile = GameProfileArgument.getGameProfiles(c, "Target Player")
                                    .stream().findFirst().orElse(null);
                            if (profile == null) {
                                c.getSource().sendFailure(Component.literal("Failed to find player"));
                                return 2;
                            }
                            final FactionPlayer player = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
                            final Faction faction = player.getFaction();
                            final FactionPlayer target = FPlayerCollection.getInstance().getByKey(profile.getId());

                            if (!faction.hasInvitedPlayer(target.getId())) {
                                c.getSource().sendFailure(Component.literal("That player doesn't have an invite from your faction"));
                                return 3;
                            }

                            final FactionUninvitePlayerEvent event = new FactionUninvitePlayerEvent(
                                    c.getSource().source,
                                    faction,
                                    target
                            );
                            MinecraftForge.EVENT_BUS.post(event);
                            if (event.isCanceled()) return 0;

                            faction.addInvite(target.getId());
                            c.getSource().sendSuccess(
                                    Component.literal(DatChatFormatting.TextColour.INFO + "Successfully uninvited ")
                                            .append(
                                                    target.getNameWithDescription(faction)
                                                            .withStyle(RelationUtil.getRelation(faction, target).formatting)
                                            ).append(DatChatFormatting.TextColour.INFO + " from the faction"),
                                    true
                            );

                            if (target.isPlayerOnline()) {
                                target.getServerPlayer().sendSystemMessage(
                                        Component.literal(DatChatFormatting.TextColour.INFO + "Your invite from ")
                                                .append(
                                                        faction.getNameWithDescription(target.getFaction())
                                                                .withStyle(RelationUtil.getRelation(target, faction).formatting)
                                                )
                                                .append(DatChatFormatting.TextColour.INFO + " has been revoked")
                                );
                            }

                            return 1;
                        }))
                .build();

        command.then(subCommand);
    }
}

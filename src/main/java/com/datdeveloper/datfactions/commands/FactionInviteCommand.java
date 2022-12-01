package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.api.events.FactionInvitePlayerEvent;
import com.datdeveloper.datfactions.commands.suggestions.DatSuggestionProviders;
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

import static com.datdeveloper.datfactions.commands.FactionPermissions.FACTIONINVITE;

public class FactionInviteCommand extends BaseFactionCommand {
    static void register(final LiteralArgumentBuilder<CommandSourceStack> command) {
        final LiteralCommandNode<CommandSourceStack> subCommand = Commands.literal("invite")
                .requires(commandSourceStack -> {
                    if (!(commandSourceStack.isPlayer() && DatPermissions.hasPermission(commandSourceStack.getPlayer(), FACTIONINVITE))) return false;
                    final FactionPlayer fPlayer = getPlayerOrTemplate(commandSourceStack.getPlayer());
                    return fPlayer.hasFaction() && fPlayer.getRole().hasPermission(ERolePermissions.INVITE);
                })
                .then(Commands.argument("Target Player", GameProfileArgument.gameProfile())
                        .suggests(DatSuggestionProviders.fPlayerProvider)
                        .executes(c -> {
                            final String targetName = c.getArgument("Target Player", String.class);
                            final GameProfile profile = GameProfileArgument.getGameProfiles(c, "Target Player")
                                    .stream().findFirst().orElse(null);
                            if (profile == null) {
                                c.getSource().sendFailure(Component.literal("Failed to find player"));
                                return 2;
                            }
                            final FactionPlayer player = FPlayerCollection.getInstance().getPlayer(c.getSource().getPlayer());
                            final Faction faction = player.getFaction();
                            final FactionPlayer target = FPlayerCollection.getInstance().getByKey(profile.getId());


                            final FactionInvitePlayerEvent event = new FactionInvitePlayerEvent(
                                    c.getSource().source,
                                    faction,
                                    target
                            );
                            MinecraftForge.EVENT_BUS.post(event);
                            if (event.isCanceled()) return 0;

                            faction.addInvite(target.getId());
                            c.getSource().sendSuccess(
                                    Component.literal(DatChatFormatting.TextColour.INFO + "Successfull invited ")
                                            .append(
                                                    target.getNameWithDescription(faction)
                                                            .withStyle(RelationUtil.getRelation(faction, target).formatting)
                                            ).append(DatChatFormatting.TextColour.INFO + " to the faction"),
                                    true
                            );

                            return 1;
                        }))
                .build();

        command.then(subCommand);
    }
}

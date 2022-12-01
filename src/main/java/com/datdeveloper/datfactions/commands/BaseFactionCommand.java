package com.datdeveloper.datfactions.commands;

import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class BaseFactionCommand {

    /**
     * Get the player or the player template if the player hasn't been registered yet
     * @param player the serverplayer to get
     * @return the player or the player template
     */
    protected static FactionPlayer getPlayerOrTemplate(final ServerPlayer player) {
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
        return fPlayer != null ? fPlayer : FPlayerCollection.getInstance().getTemplate();
    }

    /**
     *
     * Workaround for <a href="https://github.com/Mojang/brigadier/issues/46">https://github.com/Mojang/brigadier/issues/46</a>
     * <br>
     * Adapted from <a href="https://github.com/PaperMC/Velocity/blob/8abc9c80a69158ebae0121fda78b55c865c0abad/proxy/src/main/java/com/velocitypowered/proxy/util/BrigadierUtils.java#L38">Velocity</a>
     * <a href="https://github.com/PaperMC/Velocity/blob/8abc9c80a69158ebae0121fda78b55c865c0abad/LICENSE">MIT License</a>
     */
    protected static LiteralCommandNode<CommandSourceStack> buildRedirect(final String alias, final LiteralCommandNode<CommandSourceStack> destination) {
        // Redirects only work for nodes with children, but break the top argument-less command.
        // Manually adding the root command after setting the redirect doesn't fix it.
        // See https://github.com/Mojang/brigadier/issues/46). Manually clone the node instead.
        final LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(alias)
                .requires(destination.getRequirement())
                .forward(
                        destination.getRedirect(),
                        destination.getRedirectModifier(),
                        destination.isFork()
                )
                .executes(destination.getCommand());
        for (final CommandNode<CommandSourceStack> child : destination.getChildren()) {
            builder.then(child);
        }
        return builder.build();
    }
}

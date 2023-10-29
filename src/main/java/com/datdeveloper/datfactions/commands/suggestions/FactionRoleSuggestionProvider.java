package com.datdeveloper.datfactions.commands.suggestions;

import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.Faction;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.datdeveloper.datfactions.factiondata.permissions.FactionRole;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;

import java.util.concurrent.CompletableFuture;

/**
 * Suggestions provider for faction role names
 */
public class FactionRoleSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    /**
     * Limit the roles to ones that the player is above in hierarchy
     */
    final boolean limitToBelowPlayer;

    /**
     * Allow the player's own role to be suggested
     */
    final boolean allowSelf;

    public FactionRoleSuggestionProvider(final boolean limitToBelowPlayer, final boolean allowSelf) {
        this.limitToBelowPlayer = limitToBelowPlayer;
        this.allowSelf = allowSelf;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(context.getSource().getPlayer());
        final Faction faction = fPlayer.getFaction();
        final int playerRoleLevel = fPlayer.getRole().getRoleLevel();

        faction.getRoles().values().stream()
                .filter(role -> !limitToBelowPlayer
                        || role.getRoleLevel() < playerRoleLevel
                        || (allowSelf && role.equals(fPlayer.getRole())))
                .map(FactionRole::getName)
                .forEach(builder::suggest);

        return builder.buildFuture();
    }
}

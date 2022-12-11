package com.datdeveloper.datfactions.commands.suggestions;

import com.datdeveloper.datfactions.factionData.EFactionFlags;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;

import java.util.concurrent.CompletableFuture;

/**
 * Suggestions provider for faction role names
 */
public class FactionFlagSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    /**
     * Limit the roles to ones that the player is above in hierarchy
     */
    final boolean allowAdmin;

    public FactionFlagSuggestionProvider(final boolean allowAdmin) {
        this.allowAdmin = allowAdmin;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {

        for (final EFactionFlags flag : EFactionFlags.values()) {
            if (allowAdmin || !flag.admin) {
                builder.suggest(flag.name().toLowerCase());
            }
        }

        return builder.buildFuture();
    }
}

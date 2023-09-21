package com.datdeveloper.datfactions.commands.suggestions;

import com.datdeveloper.datfactions.factiondata.permissions.ERolePermissions;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;

import java.util.concurrent.CompletableFuture;

/**
 * Suggestions provider for faction role permissions
 */
public class FactionPermissionSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        for (final ERolePermissions value : ERolePermissions.values()) {
            builder.suggest(value.name().toLowerCase(), value.getChatComponent());
        }

        return builder.buildFuture();
    }
}

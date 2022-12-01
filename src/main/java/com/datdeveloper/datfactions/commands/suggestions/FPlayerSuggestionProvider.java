package com.datdeveloper.datfactions.commands.suggestions;

import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class FPlayerSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    /**
     * Exclude the player that called the command
     */
    private final boolean excludeCaller;

    /**
     * @param excludeCaller Exclude the player that called the command
     */
    public FPlayerSuggestionProvider(final boolean excludeCaller) {
        this.excludeCaller = excludeCaller;
    }
    @Override
    public CompletableFuture<Suggestions> getSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) throws CommandSyntaxException {
        final ServerPlayer player = context.getSource().getPlayer();
        FPlayerCollection.getInstance().getAll().values().stream()
                .filter(factionPlayer -> !excludeCaller || !Objects.equals(player != null ? player.getUUID() : null, factionPlayer.getId()))
                .map(FactionPlayer::getName)
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}

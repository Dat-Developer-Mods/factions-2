package com.datdeveloper.datfactions.commands.suggestions;

import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * A suggestion provider for faction player names in the same faction as the caller
 */
public class OwnFPlayerSuggestionProvider extends FPlayerSuggestionProvider {
    /**
     * @param excludeCaller Exclude the player that called the command
     */
    public OwnFPlayerSuggestionProvider(final boolean excludeCaller) {
        super(excludeCaller);
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        final ServerPlayer player = context.getSource().getPlayer();
        final FactionPlayer fPlayer = FPlayerCollection.getInstance().getPlayer(player);
        fPlayer.getFaction().getPlayers().stream()
                .filter(factionPlayer -> !excludeCaller || !Objects.equals(player != null ? player.getUUID() : null, factionPlayer.getId()))
                .map(FactionPlayer::getName)
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}

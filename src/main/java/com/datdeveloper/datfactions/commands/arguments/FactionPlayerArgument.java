package com.datdeveloper.datfactions.commands.arguments;

import com.datdeveloper.datfactions.factionData.FPlayerCollection;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionCollection;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * An argument referring to a faction
 * Supports referencing a faction by its name, or it's UUID
 */
public class FactionPlayerArgument implements ArgumentType<FactionPlayer> {
    public static final SimpleCommandExceptionType ERROR_UNKNOWN_PLAYER = new SimpleCommandExceptionType(Component.literal("Cannot find a player with that name"));

    @Override
    public FactionPlayer parse(final StringReader reader) throws CommandSyntaxException {
        final String identifier = reader.readUnquotedString();

        FactionPlayer player;
        try {
            final UUID uuid = UUID.fromString(identifier);
            player = FPlayerCollection.getInstance().getByKey(uuid);
            if (player != null) return player;
        } catch (final IllegalArgumentException ignored) {}

        player = FPlayerCollection.getInstance().getByName(identifier);

        if (player == null) throw ERROR_UNKNOWN_PLAYER.create();

        return player;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        if (!(context.getSource() instanceof SharedSuggestionProvider)) return Suggestions.empty();

        final Map<UUID, FactionPlayer> potentials = FPlayerCollection.getInstance().getAll();
        final Stream<String> strings = potentials.values().stream()
                .map(FactionPlayer::getName);

        return SharedSuggestionProvider.suggest(strings, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return List.of("example", "long-example", "dd12be42-52a9-4a91-a8a1-11c01849e498");
    }
}

package com.datdeveloper.datfactions.commands.arguments;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionCollection;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * An argument referring to a faction
 * Supports referencing a faction by its name, or it's UUID
 */
public class FactionArgument implements ArgumentType<Faction> {
    public static final SimpleCommandExceptionType ERROR_UNKNOWN_FACTION = new SimpleCommandExceptionType(Component.literal("Cannot find a faction with that name"));


    @Override
    public Faction parse(final StringReader reader) throws CommandSyntaxException {
        final String identifier = reader.readUnquotedString();

        Faction faction;
        try {
            final UUID uuid = UUID.fromString(identifier);
            faction = FactionCollection.getInstance().getByKey(uuid);
            if (faction != null) return faction;
        } catch (final IllegalArgumentException ignored) {}

        faction = FactionCollection.getInstance().getByName(identifier);

        if (faction == null) throw ERROR_UNKNOWN_FACTION.createWithContext(reader);

        return faction;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        if (!(context.getSource() instanceof SharedSuggestionProvider suggestionProvider)) return Suggestions.empty();

        final Map<UUID, Faction> potentials = FactionCollection.getInstance().getAll();
        final Stream<String> strings = potentials.values().stream()
                .map(Faction::getName);

        return SharedSuggestionProvider.suggest(strings, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return List.of("example", "long-example", "dd12be42-52a9-4a91-a8a1-11c01849e498");
    }
}

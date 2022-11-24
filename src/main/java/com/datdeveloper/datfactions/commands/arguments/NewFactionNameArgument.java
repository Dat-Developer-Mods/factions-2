package com.datdeveloper.datfactions.commands.arguments;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.factionData.FactionCollection;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * An argument representing a potential faction name<br>
 * Only allows names upto the configured maximum name length, containing permitted characters (a-z, A-Z, 0-9, -, and _),
 * and names that don't yet belong to a faction
 */
public class NewFactionNameArgument implements ArgumentType<String> {
    private static final Set<Character> allowedCharacters = new HashSet<>(List.of(
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', '_'
    ));

    private static final SimpleCommandExceptionType ERROR_ILLEGAL_CHARACTER = new SimpleCommandExceptionType(Component.literal("Faction name contains illegal character, a faction name can only contain, letters, numbers, underscore and dashes"));
    private static final SimpleCommandExceptionType ERROR_NAME_TOO_LONG = new SimpleCommandExceptionType(Component.literal("A faction name cannot be longer than " + FactionsConfig.getMaxFactionNameLength() + " characters"));
    private static final SimpleCommandExceptionType ERROR_FACTION_EXISTS = new SimpleCommandExceptionType(Component.literal("A faction already exists with that name"));

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        final StringBuilder name = new StringBuilder();
        while (reader.canRead() && reader.peek() != ' ') {
            final char nextChar = reader.read();
            if (allowedCharacters.contains(nextChar)) name.append(nextChar);
            else throw ERROR_ILLEGAL_CHARACTER.createWithContext(reader);
        }

        if (name.length() > FactionsConfig.getMaxFactionNameLength()) throw ERROR_NAME_TOO_LONG.create();

        final String nameString = name.toString();
        if (FactionCollection.getInstance().isNameTaken(nameString)) throw ERROR_FACTION_EXISTS.create();

        return nameString;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return List.of("Example", "Long-Example", "Another_Long_Example");
    }
}

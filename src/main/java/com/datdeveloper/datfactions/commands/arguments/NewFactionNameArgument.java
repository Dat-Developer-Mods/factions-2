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

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewFactionNameArgument implements ArgumentType<String> {
    private static final Set<Character> allowedCharacters = new HashSet<>(List.of(
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', '_'
    ));

    public static final SimpleCommandExceptionType ERROR_ILLEGAL_CHARACTER = new SimpleCommandExceptionType(Component.literal("Faction name contains illegal character, a faction name can only contain, letters, numbers, underscore and dashes"));
    public static final SimpleCommandExceptionType ERROR_FACTION_TOO_LONG = new SimpleCommandExceptionType(Component.literal("A faction name cannot be longer than " + FactionsConfig.getMaxFactionNameLength()));
    public static final SimpleCommandExceptionType ERROR_FACTION_EXISTS = new SimpleCommandExceptionType(Component.literal("A faction already exists with that name"));

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        StringBuilder name = new StringBuilder();
        while (reader.canRead() && reader.peek() != ' ') {
            char nextChar = reader.read();
            if (allowedCharacters.contains(nextChar)) name.append(nextChar);
            else throw ERROR_ILLEGAL_CHARACTER.createWithContext(reader);
        }

        if (name.length() > FactionsConfig.getMaxFactionNameLength()) throw ERROR_FACTION_TOO_LONG.createWithContext(reader);


        String nameString = name.toString();
        if (FactionCollection.getInstance().isNameTaken(nameString)) throw ERROR_FACTION_EXISTS.createWithContext(reader);

        return nameString;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return Suggestions.empty();
    }

    @Override
    public Collection<String> getExamples() {
        return List.of("Example", "Long-Example", "Another_Long_Example");
    }
}

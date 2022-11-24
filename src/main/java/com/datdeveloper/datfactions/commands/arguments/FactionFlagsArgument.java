package com.datdeveloper.datfactions.commands.arguments;

import com.datdeveloper.datfactions.factionData.EFactionFlags;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * An argument representing a faction flag
 */
public class FactionFlagsArgument implements ArgumentType<EFactionFlags> {
    private static final SimpleCommandExceptionType ERROR_UNKNOWN_FLAG = new SimpleCommandExceptionType(Component.literal("That flag doesn't exist"));
    private final boolean admin;

    FactionFlagsArgument(final boolean admin) {
        this.admin = admin;
    }
    
    @Override
    public EFactionFlags parse(final StringReader reader) throws CommandSyntaxException {
        final String flag = reader.readUnquotedString();
        try {
            return EFactionFlags.valueOf(flag.toUpperCase());
        } catch (final IllegalArgumentException error) {
            throw ERROR_UNKNOWN_FLAG.createWithContext(reader);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        final Stream<String> strings = Arrays.stream(EFactionFlags.values())
                .filter(flag -> !flag.admin || admin)
                .map(flag -> flag.name().toLowerCase());
        return SharedSuggestionProvider.suggest(strings, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return List.of(EFactionFlags.FRIENDLYFIRE.name().toLowerCase());
    }

    public static class Info implements ArgumentTypeInfo<FactionFlagsArgument, Info.Template> {
        @Override
        public void serializeToNetwork(final Template pTemplate, final FriendlyByteBuf pBuffer) {
            pBuffer.writeBoolean(pTemplate.admin);
        }

        @Override
        public Template deserializeFromNetwork(final FriendlyByteBuf pBuffer) {
            final boolean admin = pBuffer.readBoolean();
            return new Template(admin);
        }

        @Override
        public void serializeToJson(final Template pTemplate, final JsonObject pJson) {
            pJson.addProperty("admin", pTemplate.admin);
        }

        @Override
        public Template unpack(final FactionFlagsArgument pArgument) {
            return new Template(pArgument.admin);
        }

        public class Template implements ArgumentTypeInfo.Template<FactionFlagsArgument> {
            boolean admin;

            Template(final boolean admin) {
                this.admin = admin;
            }

            @Override
            public FactionFlagsArgument instantiate(final CommandBuildContext pContext) {
                return new FactionFlagsArgument(this.admin);
            }

            @Override
            public ArgumentTypeInfo<FactionFlagsArgument, ?> type() {
                return Info.this;
            }
        }
    }
}

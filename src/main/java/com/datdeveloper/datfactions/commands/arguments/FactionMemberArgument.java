package com.datdeveloper.datfactions.commands.arguments;

import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionCollection;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
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

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * An argument referring to a member in a given faction
 * Supports referencing a faction player by their name or UUID
 */
public class FactionMemberArgument implements ArgumentType<FactionPlayer> {
    public static final SimpleCommandExceptionType ERROR_UNKNOWN_PLAYER = new SimpleCommandExceptionType(Component.literal("Cannot find a player in the faction with that name"));

    /**
     * The faction the members are from
     */
    final Faction faction;

    public FactionMemberArgument(final Faction faction) {
        this.faction = faction;
    }

    @Override
    public FactionPlayer parse(final StringReader reader) throws CommandSyntaxException {
        final String identifier = reader.readUnquotedString();

        Stream<FactionPlayer> stream = faction.getPlayers().stream();
        try {
            final UUID id = UUID.fromString(identifier);
            stream = stream.filter(playerElement -> playerElement.getId().equals(id));
        } catch (final IllegalArgumentException ignored) {
            stream = stream.filter(playerElement -> playerElement.getName().equals(identifier));
        }
        final FactionPlayer player = stream.findFirst()
                .orElse(null);

        if (player == null) throw ERROR_UNKNOWN_PLAYER.create();

        return player;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        if (!(context.getSource() instanceof SharedSuggestionProvider)) return Suggestions.empty();

        final Stream<String> strings = faction.getPlayers().stream()
                .map(FactionPlayer::getName);

        return SharedSuggestionProvider.suggest(strings, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return List.of("example", "long-example", "dd12be42-52a9-4a91-a8a1-11c01849e498");
    }

    public static class Info implements ArgumentTypeInfo<FactionMemberArgument, FactionMemberArgument.Info.Template> {
        @Override
        public void serializeToNetwork(final FactionMemberArgument.Info.Template pTemplate, final FriendlyByteBuf pBuffer) {
            pBuffer.writeUUID(pTemplate.factionId);
        }

        @Override
        public FactionMemberArgument.Info.Template deserializeFromNetwork(final FriendlyByteBuf pBuffer) {
            final UUID factionId = pBuffer.readUUID();
            return new FactionMemberArgument.Info.Template(factionId);
        }

        @Override
        public void serializeToJson(final FactionMemberArgument.Info.Template pTemplate, final JsonObject pJson) {
            pJson.addProperty("factionId", pTemplate.factionId.toString());
        }

        @Override
        public FactionMemberArgument.Info.Template unpack(final FactionMemberArgument pArgument) {
            return new FactionMemberArgument.Info.Template(pArgument.faction.getId());
        }

        public class Template implements ArgumentTypeInfo.Template<FactionMemberArgument> {
            UUID factionId;

            Template(final UUID factionId) {
                this.factionId = factionId;
            }

            @Override
            public FactionMemberArgument instantiate(final CommandBuildContext pContext) {
                return new FactionMemberArgument(FactionCollection.getInstance().getByKey(this.factionId));
            }

            @Override
            public ArgumentTypeInfo<FactionMemberArgument, ?> type() {
                return FactionMemberArgument.Info.this;
            }
        }
    }
}

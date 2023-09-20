package com.datdeveloper.datfactions.database;

import com.datdeveloper.datfactions.database.jsonAdapters.BlockPosAdapter;
import com.datdeveloper.datfactions.database.jsonAdapters.ChunkPosAdapter;
import com.datdeveloper.datfactions.database.jsonAdapters.DatUUIDTypeAdapter;
import com.datdeveloper.datfactions.database.jsonAdapters.ResourceKeyAdapterFactory;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionLevel;
import com.datdeveloper.datfactions.factionData.FactionLevelSettings;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.datdeveloper.datfactions.Datfactions.logger;

public class FlatFileDatabase extends Database {
    final Path savePath;
    final Gson gson;

    /**
     * Build a Gson instance with all the config we want
     * @return the Gson instance
     */
    private Gson buildGson() {
        return new GsonBuilder().setPrettyPrinting()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .enableComplexMapKeySerialization()

                .registerTypeAdapter(BlockPos.class, new BlockPosAdapter())
                .registerTypeAdapter(ChunkPos.class, new ChunkPosAdapter())
                .registerTypeAdapterFactory(new ResourceKeyAdapterFactory())
                .registerTypeAdapter(UUID.class, new DatUUIDTypeAdapter())
                .create();
    }

    public FlatFileDatabase(final Path savePath) {
        this.savePath = savePath;
        gson = buildGson();

        setupPaths();
    }

    /**
     * Get the path to the Faction data
     * @return the path to the Faction data
     */
    private Path getFactionsPath() {
        return savePath.resolve("factions");
    }

    /**
     * Get the path to the FactionPlayer data
     * @return the path to the FactionPlayer data
     */
    private Path getPlayersPath() {
        return savePath.resolve("players");
    }

    /**
     * Get the path to the FactionLevel data
     * @return the path to the FactionLevel data
     */
    private Path getLevelsPath() {
        return savePath.resolve("levels");
    }

    /**
     * Get a list of all the files in the given directory with the names transformed into their UUID
     * @param path the path to get all the files from
     * @return a list of UUIDs
     */
    private List<UUID> getAllFilesInPathAsUUID(final Path path) {
        try (final Stream<Path> files = Files.list(path)) {
            return files.filter(dir -> dir.toFile().isFile())
                    .map(dir -> {
                        final String name = dir.getFileName().toString();
                        final int index = name.lastIndexOf('.');
                        return index != -1 ? name.substring(0, index) : name;
                    })
                    .filter(name -> !List.of("template", "default-level-settings").contains(name))
                    .map(name -> {
                        try {
                            return UUID.fromString(name);
                        } catch (final IllegalArgumentException ignored) {
                            logger.warn("Found a file with a butchered name: " + name + ", ignoring");
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (final IOException e) {
            throw new RuntimeException("Failed to get files in " + path, e);
        }
    }

    /**
     * Ensure all the paths exist
     */
    private void setupPaths() {
        if(!Files.exists(savePath)) {
            try {
                Files.createDirectory(savePath);
                Files.createDirectory(getFactionsPath());
                Files.createDirectory(getPlayersPath());
                Files.createDirectory(getLevelsPath());
            } catch (final IOException e) {
                throw new RuntimeException("Unable to create faction data in the world directory, can we access it?", e);
            }
        }
    }

    @Override
    public void nukeDatabase() {
        try (final Stream<Path> fileStream = Files.walk(savePath)) {
            //noinspection ResultOfMethodCallIgnored
            fileStream
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (final IOException e) {
            logger.warn("Failed to nuke Flatfile database");
        }
        setupPaths();
    }

    /* ========================================= */
    /* Factions
    /* ========================================= */

    @Override
    public void storeFaction(final Faction faction) {
        final Path filePath = getFactionsPath().resolve(faction.getId() + ".json");

        try (final FileWriter writer = new FileWriter(filePath.toFile())) {
            gson.toJson(faction, writer);
        } catch (final IOException e) {
            logger.error("Failed to write faction " + faction.getName() + " (" + faction.getId() + ") to disk, the latest changes will disappear after a server reload");
        }
    }

    @Override
    public void deleteFaction(final Faction faction) {
        final Path filePath = getFactionsPath().resolve(faction.getId() + ".json");
        try {
            Files.deleteIfExists(filePath);
        } catch (final IOException e) {
            logger.error("Failed to delete faction " + faction.getName() + " (" + faction.getId() + ") from the disk, they may still be loaded next server restart");
        }
    }

    @Override
    @Nullable
    public Faction loadFaction(final UUID factionId) {
        final Path filePath = getFactionsPath().resolve(factionId.toString() + ".json");
        if (!(Files.exists(filePath) && Files.isRegularFile(filePath))) return null;

        try (final Reader reader = new FileReader(filePath.toFile())) {
            return gson.fromJson(reader, Faction.class);
        } catch (final JsonSyntaxException e) {
            logger.warn("Failed to load faction " + factionId + ", assuming corrupt and discarding");
        } catch (final IOException ignored) {
        }

        return null;
    }

    @Override
    @Nullable
    public Faction loadFactionTemplate() {
        final Path filePath = getFactionsPath().resolve("template.json");
        if (!(Files.exists(filePath) && Files.isRegularFile(filePath))) return null;

        try (final Reader reader = new FileReader(filePath.toFile())) {
            return gson.fromJson(reader, Faction.class);
        } catch (final JsonSyntaxException e) {
            logger.warn("Failed to load faction template, assuming corrupt and discarding");
        } catch (final IOException ignored) {
        }

        return null;
    }

    @Override
    public void storeFactionTemplate(final Faction template) {
        final Path filePath = getFactionsPath().resolve("template.json");

        try (final FileWriter writer = new FileWriter(filePath.toFile())) {
            gson.toJson(template, writer);
        } catch (final IOException e) {
            logger.error("Failed to write faction template to disk");
        }
    }


    @Override
    public List<UUID> getAllStoredFactions() {
        return getAllFilesInPathAsUUID(getFactionsPath());
    }

    /* ========================================= */
    /* Players
    /* ========================================= */

    @Override
    public void storePlayer(final FactionPlayer player) {
        final Path filePath = getPlayersPath().resolve(player.getId() + ".json");

        try (final FileWriter writer = new FileWriter(filePath.toFile())) {
            gson.toJson(player, writer);
        } catch (final IOException e) {
            logger.error("Failed to write player " + player.getLastName() + " (" + player.getId() + ") to disk, the latest changes will disappear after a server reload");
        }
    }

    @Override
    public void deletePlayer(final FactionPlayer player) {
        final Path filePath = getPlayersPath().resolve(player.getId() + ".json");
        try {
            Files.deleteIfExists(filePath);
        } catch (final IOException e) {
            logger.error("Failed to delete player " + player.getLastName() + " (" + player.getId() + ") from the disk, they may still be loaded next server restart");
        }
    }

    @Override
    @Nullable
    public FactionPlayer loadPlayer(final UUID playerId) {
        final Path filePath = getPlayersPath().resolve(playerId.toString() + ".json");
        if (!(Files.exists(filePath) && Files.isRegularFile(filePath))) return null;

        try (final Reader reader = new FileReader(filePath.toFile())) {
            return gson.fromJson(reader, FactionPlayer.class);
        } catch (final JsonSyntaxException e) {
            logger.warn("Failed to load player " + playerId + ", assuming corrupt and discarding");
        } catch (final IOException ignored) {
        }

        return null;
    }

    @Override
    @Nullable
    public FactionPlayer loadPlayerTemplate() {
        final Path filePath = getPlayersPath().resolve("template.json");
        if (!(Files.exists(filePath) && Files.isRegularFile(filePath))) return null;

        try (final Reader reader = new FileReader(filePath.toFile())) {
            return gson.fromJson(reader, FactionPlayer.class);
        } catch (final JsonSyntaxException e) {
            logger.warn("Failed to load player template, assuming corrupt and discarding");
        } catch (final IOException ignored) {
        }

        return null;
    }

    @Override
    public void storePlayerTemplate(final FactionPlayer template) {
        final Path filePath = getPlayersPath().resolve("template.json");

        try (final FileWriter writer = new FileWriter(filePath.toFile())) {
            gson.toJson(template, writer);
        } catch (final IOException e) {
            logger.error("Failed to write player template to disk");
        }
    }

    @Override
    public List<UUID> getAllStoredPlayers() {
        return getAllFilesInPathAsUUID(getPlayersPath());
    }

    /* ========================================= */
    /* Levels
    /* ========================================= */

    @Override
    public void storeLevel(final FactionLevel level) {
        final Path filePath = getLevelsPath().resolve(URLEncoder.encode(level.getId().location().toString(), StandardCharsets.UTF_8) + ".json");

        try (final FileWriter writer = new FileWriter(filePath.toFile())) {
            gson.toJson(level, writer);
        } catch (final IOException e) {
            logger.error("Failed to write level " + level.getId() + " to disk, the latest changes will disappear after a server reload");
        }
    }

    @Override
    public void deleteLevel(final FactionLevel level) {
        final Path filePath = getLevelsPath().resolve(URLEncoder.encode(level.getId().location().toString(), StandardCharsets.UTF_8) + ".json");
        try {
            Files.deleteIfExists(filePath);
        } catch (final IOException e) {
            logger.error("Failed to delete level data for " + level.getId());
        }
    }

    @Override
    @Nullable
    public FactionLevel loadLevel(final ResourceKey<Level> levelId) {
        final Path filePath = getLevelsPath().resolve(URLEncoder.encode(levelId.location().toString(), StandardCharsets.UTF_8) + ".json");
        if (!(Files.exists(filePath) && Files.isRegularFile(filePath))) return null;

        try (final Reader reader = new FileReader(filePath.toFile())) {
            return gson.fromJson(reader, FactionLevel.class);
        } catch (final JsonSyntaxException e) {
            logger.warn("Failed to load level " + levelId.location() + ", assuming corrupt and discarding");
        } catch (final IOException ignored) {
        }

        return null;
    }

    @Override
    public List<ResourceKey<Level>> getAllStoredLevels() {
        try (final Stream<Path> files = Files.list(getLevelsPath())) {
            return files.filter(dir -> dir.toFile().isFile())
                    .map(dir -> {
                        final String name = dir.getFileName().toString();
                        final int index = name.lastIndexOf('.');
                        return index != -1 ? name.substring(0, index) : name;
                    })
                    .map(name -> ResourceKey.create(Registries.DIMENSION, new ResourceLocation(URLDecoder.decode(name, Charset.defaultCharset()))))
                    .collect(Collectors.toList());
        } catch (final IOException e) {
            throw new RuntimeException("Failed to get files in " + getLevelsPath(), e);
        }
    }

    @Override
    @Nullable
    public FactionLevelSettings loadLevelDefaultSettings() {
        final Path filePath = getLevelsPath().resolve("default-level-settings.json");
        if (!(Files.exists(filePath) && Files.isRegularFile(filePath))) return null;

        try (final Reader reader = new FileReader(filePath.toFile())) {
            return gson.fromJson(reader, FactionLevelSettings.class);
        } catch (final JsonSyntaxException e) {
            logger.warn("Failed to parse " + filePath + " assuming corrupt and discarding");
        } catch (final IOException ignored) {}

        return null;
    }

    @Override
    public void storeDefaultSettings(final FactionLevelSettings defaultSettings) {
        final Path filePath = getLevelsPath().resolve("default-level-settings.json");

        try (final FileWriter writer = new FileWriter(filePath.toFile())) {
            gson.toJson(defaultSettings, writer);
        } catch (final IOException e) {
            logger.error("Failed to write default level settings to disk, the latest changes will disappear after a server reload");
        }
    }

    @Override
    public void close() {
        // Don't need to do anything
    }
}

package com.datdeveloper.datfactions.database;

import com.datdeveloper.datfactions.database.jsonAdapters.BlockPosAdapter;
import com.datdeveloper.datfactions.database.jsonAdapters.ResourceKeyAdapter;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionLevel;
import com.datdeveloper.datfactions.factionData.FactionLevelSettings;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlatDatabase extends Database {
    private static Logger logger = LogUtils.getLogger();
    Path savePath;
    Gson gson;

    /**
     * Build a Gson instance with all the config we want
     * @return the Gson instance
     */
    private Gson buildGson() {
        return new GsonBuilder().setPrettyPrinting()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .serializeNulls()
                .enableComplexMapKeySerialization()

                .registerTypeAdapter(BlockPos.class, new BlockPosAdapter())
                .registerTypeAdapter(ResourceKey.class, new ResourceKeyAdapter())
                .registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
                .create();
    }

    private Path getFactionsPath() {
        return savePath.resolve("factions");
    }
    private Path getPlayersPath() {
        return savePath.resolve("players");
    }
    private Path getLevelsPath() {
        return savePath.resolve("levels");
    }

    private List<UUID> getAllFilesInPathAsUUID(Path path) {
        try (Stream<Path> files = Files.list(path)) {
            return files.filter(dir -> dir.toFile().isFile())
                    .map(dir -> {
                        String name = dir.getFileName().toString();
                        int index = name.lastIndexOf('.');
                        return index != -1 ? name.substring(0, index) : name;
                    })
                    .map(UUID::fromString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to get files in " + path, e);
        }
    }

    private void setupPaths() {
        if(!Files.exists(savePath)) {
            try {
                Files.createDirectory(savePath);
                Files.createDirectory(getFactionsPath());
                Files.createDirectory(getPlayersPath());
                Files.createDirectory(getLevelsPath());
            } catch (IOException e) {
                throw new RuntimeException("Unable to create faction data in the world directory, can we access it?", e);
            }
        }
    }

    public FlatDatabase(Path savePath) {
        this.savePath = savePath;
        gson = buildGson();

        setupPaths();
    }

    private void writeEntity() {

    }

    public void saveFaction(Faction faction) {

    }

    @Override
    public void storeFaction(Faction faction) {

    }

    @Override
    public void deleteFaction(Faction faction) {

    }

    @Override
    @Nullable
    public Faction loadFaction(UUID factionId) {
        Path filePath = getFactionsPath().resolve(factionId.toString() + ".json");
        if (!(Files.exists(filePath) && Files.isRegularFile(filePath))) return null;

        try (Reader reader = new FileReader(filePath.toFile())) {
            return gson.fromJson(reader, Faction.class);
        } catch (JsonSyntaxException e) {
            logger.warn("Failed to load faction " + factionId + ", assuming corrupt and discarding");
        } catch (IOException ignored) {
        }

        return null;
    }

    @Override
    @Nullable
    public Faction loadFactionTemplate() {
        Path filePath = getFactionsPath().resolve("template.json");
        if (!(Files.exists(filePath) && Files.isRegularFile(filePath))) return null;

        try (Reader reader = new FileReader(filePath.toFile())) {
            return gson.fromJson(reader, Faction.class);
        } catch (JsonSyntaxException e) {
            logger.warn("Failed to load faction template, assuming corrupt and discarding");
        } catch (IOException ignored) {
        }

        return null;
    }

    @Override
    public void storeFactionTemplate(Faction template) {

    }


    @Override
    public List<UUID> getAllStoredFactions() {
        return getAllFilesInPathAsUUID(getFactionsPath());
    }

    @Override
    public void storePlayer(FactionPlayer player) {

    }

    @Override
    public void deletePlayer(FactionPlayer player) {

    }

    @Override
    @Nullable
    public FactionPlayer loadPlayer(UUID playerId) {
        Path filePath = getPlayersPath().resolve(playerId.toString() + ".json");
        if (!(Files.exists(filePath) && Files.isRegularFile(filePath))) return null;

        try (Reader reader = new FileReader(filePath.toFile())) {
            return gson.fromJson(reader, FactionPlayer.class);
        } catch (JsonSyntaxException e) {
            logger.warn("Failed to load player " + playerId + ", assuming corrupt and discarding");
        } catch (IOException ignored) {
        }

        return null;
    }

    @Override
    public List<UUID> getAllStoredPlayers() {
        return getAllFilesInPathAsUUID(getPlayersPath());
    }

    @Override
    public void storeLevel(FactionLevel level) {

    }

    @Override
    public void deleteLevel(FactionLevel level) {

    }

    @Override
    @Nullable
    public FactionLevel loadLevel(ResourceKey<Level> levelId) {
        Path filePath = getPlayersPath().resolve(URLEncoder.encode(levelId.location().toString(), StandardCharsets.UTF_8) + ".json");
        if (!(Files.exists(filePath) && Files.isRegularFile(filePath))) return null;

        try (Reader reader = new FileReader(filePath.toFile())) {
            return gson.fromJson(reader, FactionLevel.class);
        } catch (JsonSyntaxException e) {
            logger.warn("Failed to load level " + levelId.location() + ", assuming corrupt and discarding");
        } catch (IOException ignored) {
        }

        return null;
    }

    @Override
    public List<ResourceKey<Level>> getAllStoredLevels() {
        try (Stream<Path> files = Files.list(getLevelsPath())) {
            return files.filter(dir -> dir.toFile().isFile())
                    .map(dir -> {
                        String name = dir.getFileName().toString();
                        int index = name.lastIndexOf('.');
                        return index != -1 ? name.substring(0, index) : name;
                    })
                    .map(name -> {
                        return ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(URLDecoder.decode(name, Charset.defaultCharset())));
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to get files in " + getLevelsPath(), e);
        }
    }

    @Override
    @Nullable
    public FactionLevelSettings loadLevelDefaultSettings() {
        Path filePath = getLevelsPath().resolve("defaultSettings.json");
        if (!(Files.exists(filePath) && Files.isRegularFile(filePath))) return null;

        try (Reader reader = new FileReader(filePath.toFile())) {
            return gson.fromJson(reader, FactionLevelSettings.class);
        } catch (JsonSyntaxException e) {
            logger.warn("Failed to parse " + filePath + " assuming corrupt and discarding");
        } catch (IOException e) {}

        return null;
    }

    @Override
    public void storeDefaultSettings(FactionLevelSettings defaultSettings) {

    }
}

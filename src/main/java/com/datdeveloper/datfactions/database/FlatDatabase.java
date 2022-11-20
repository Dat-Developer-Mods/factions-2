package com.datdeveloper.datfactions.database;

import com.datdeveloper.datfactions.database.jsonAdapters.BlockPosAdapter;
import com.datdeveloper.datfactions.database.jsonAdapters.ResourceKeyAdapter;
import com.datdeveloper.datfactions.factionData.Faction;
import com.datdeveloper.datfactions.factionData.FactionLevel;
import com.datdeveloper.datfactions.factionData.FactionPlayer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlatDatabase extends Database {
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
        this.savePath = savePath.resolve("datfactions");
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
    public Faction loadFaction(UUID factionId) {
        return null;
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
    public FactionPlayer loadPlayer(UUID playerId) {
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
    public FactionLevel loadLevel(ResourceKey<Level> levelId) {
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
}

package com.datdeveloper.datfactions.factionData;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FPlayerCollection {
    Logger logger = LogUtils.getLogger();

    Map<UUID, FactionPlayer> players;
    FactionPlayer template;

    // Singleton
    static final FPlayerCollection instance = new FPlayerCollection();
    public static FPlayerCollection getInstance() {
        return instance;
    }

    FPlayerCollection() {
        players = new HashMap<>();
        template = new FactionPlayer(UUID.randomUUID(), "null");
    }

    public boolean isPlayerRegistered(UUID player) {
        return players.containsKey(player);
    }

    public boolean isPlayerRegistered(ServerPlayer player) {
        return isPlayerRegistered(player.getUUID());
    }

    public void registerNewPlayer(ServerPlayer player) {
        FactionPlayer newPlayer = new FactionPlayer(player, template);
        players.put(player.getUUID(), newPlayer);
        FactionIndex.getInstance().updatePlayer(newPlayer);
        logger.info("Registered new factions player: " + player.getName().getString());
    }

    public FactionPlayer getPlayer(UUID playerId) {
        return players.get(playerId);
    }

    public FactionPlayer getPlayer(ServerPlayer player) {
        return getPlayer(player.getUUID());
    }

    public Map<UUID, FactionPlayer> getPlayers() {
        return this.players;
    }
}

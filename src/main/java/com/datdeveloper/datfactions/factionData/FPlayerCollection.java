package com.datdeveloper.datfactions.factionData;

import com.datdeveloper.datfactions.database.Database;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.util.List;
import java.util.UUID;

public class FPlayerCollection extends BaseCollection<UUID, FactionPlayer> {
    Logger logger = LogUtils.getLogger();

    FactionPlayer template;

    // Singleton
    static final FPlayerCollection instance = new FPlayerCollection();
    public static FPlayerCollection getInstance() {
        return instance;
    }

    FPlayerCollection() {
        template = new FactionPlayer(UUID.randomUUID(), "null");
    }

    public boolean isPlayerRegistered(UUID player) {
        return map.containsKey(player);
    }

    public boolean isPlayerRegistered(ServerPlayer player) {
        return isPlayerRegistered(player.getUUID());
    }

    public void registerNewPlayer(ServerPlayer player) {
        FactionPlayer newPlayer = new FactionPlayer(player, template);
        map.put(player.getUUID(), newPlayer);
        FactionIndex.getInstance().updatePlayer(newPlayer);
        logger.info("Registered new factions player: " + player.getName().getString());
    }

    public FactionPlayer getPlayer(ServerPlayer player) {
        return this.getByKey(player.getUUID());
    }

    /* ========================================= */
    /* Setup and teardown
    /* ========================================= */

    @Override
    public void initialise() {
        List<UUID> storedPlayers = Database.instance.getAllStoredPlayers();
        for (UUID playerId : storedPlayers) {
            FactionPlayer player = Database.instance.loadPlayer(playerId);
            if (player == null) continue;

            if (player.hasFaction() && FactionCollection.getInstance().getByKey(player.factionId) == null) {
                logger.warn("Player " + player.lastName + " (" + player.id + ") belongs to a faction that no longer exists, they will be corrected to no faction");
                player.setFaction(null, null);
            }

            map.put(playerId, player);
        }
    }

    @Override
    public void uninitialise() {

    }
}

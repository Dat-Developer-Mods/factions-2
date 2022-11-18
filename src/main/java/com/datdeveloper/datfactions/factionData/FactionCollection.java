package com.datdeveloper.datfactions.factionData;

import com.datdeveloper.datfactions.api.events.ChangeFactionMembershipEvent;
import com.datdeveloper.datfactions.factionData.permissions.FactionRole;
import net.minecraftforge.common.MinecraftForge;

import java.util.Set;
import java.util.UUID;

public class FactionCollection extends BaseCollection<Faction>{

    Faction template;

    Faction WILDERNESS;
    Faction SAFEZONE;

    static FactionCollection instance = new FactionCollection();
    public static FactionCollection getInstance() {
        return instance;
    }

    public Faction createFaction(String name) {
        UUID factionId = UUID.randomUUID();
        Faction newFaction = new Faction(factionId, name);

        FactionIndex.getInstance().addFaction(newFaction);

        return newFaction;
    }

    public void disbandFaction(UUID factionId) {
        Faction faction = map.remove(factionId);
        Set<FactionPlayer> players = faction.getPlayers();
        FactionIndex.getInstance().deleteFaction(factionId);

        for (FactionPlayer player : players) {
            ChangeFactionMembershipEvent event = new ChangeFactionMembershipEvent(null, player, faction, null, null, ChangeFactionMembershipEvent.EChangeFactionReason.DISBAND);
            MinecraftForge.EVENT_BUS.post(event);

            Faction newFaction = event.getNewFaction();
            FactionRole newRole = event.getNewRole();
            player.setFaction(newFaction != null ? newFaction.getId() : null, newRole != null ? newRole.getId() : null);
        }
    }

    @Override
    void initialise() {

    }
}

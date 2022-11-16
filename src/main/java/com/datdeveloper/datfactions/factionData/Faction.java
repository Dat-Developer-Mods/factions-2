package com.datdeveloper.datfactions.factionData;

import com.datdeveloper.datfactions.permissions.FactionRole;
import net.minecraft.core.BlockPos;

import java.util.List;

public class Faction {
    String name;
    String description;
    String motd;

    long creationTime;

    BlockPos homeLocation;

    List<FactionRole> roles;
    List<EFactionFlags> flags;

    public int getPower() {
        return 0;
    }

    public int getLandWorthInWorld(int worldId) {

        return 0;
    }
}

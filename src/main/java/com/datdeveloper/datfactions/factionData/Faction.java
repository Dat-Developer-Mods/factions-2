package com.datdeveloper.datfactions.factionData;

import com.datdeveloper.datfactions.permissions.FactionRole;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Faction {
    String name;
    String description;
    String motd;

    long creationTime;

    BlockPos homeLocation;

    List<FactionRole> roles;

    public int getPower() {
        return 0;
    }

    public int getLandWorth() {

        return 0;
    }
}

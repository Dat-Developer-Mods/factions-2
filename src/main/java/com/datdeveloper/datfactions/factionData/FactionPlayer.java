package com.datdeveloper.datfactions.factionData;

import java.util.UUID;

public class FactionPlayer {
    String lastName;
    long lastActiveTime;

    transient boolean autoClaim;

    int power;
    UUID factionId;
    Integer roleIndex;
}

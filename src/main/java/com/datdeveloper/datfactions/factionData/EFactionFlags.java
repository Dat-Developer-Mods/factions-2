package com.datdeveloper.datfactions.factionData;

public enum EFactionFlags {
    OPEN(false),
    RESERVED(false);

    public final boolean admin;
    EFactionFlags(boolean admin) {
        this.admin = admin;
    }
}

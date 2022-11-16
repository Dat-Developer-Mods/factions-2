package com.datdeveloper.datfactions.Util;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.UUID;

public abstract class SavableEntity implements Serializable {
    UUID id;
    boolean dirty = false;

    public void markDirty() {
        dirty = true;
    }

    abstract Path getSaveSubDirectory();
}

package com.datdeveloper.datfactions.factionData;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

public class ChunkKey {
    int chunkX, chunkZ;

    @Override
    public int hashCode() {
        return Objects.hash(chunkX, chunkZ);
    }
}

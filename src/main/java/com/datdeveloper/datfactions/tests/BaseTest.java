package com.datdeveloper.datfactions.tests;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class BaseTest {
    protected static ServerPlayer makeMockServerPlayer(final Player player) {
        return new ServerPlayer(player.getServer(), player.getServer().getLevel(player.getLevel().dimension()), new GameProfile(UUID.randomUUID(), player.getName().getString()), null) {
            /**
             * Returns {@code true} if the player is in spectator mode.
             */
            public boolean isSpectator() {
                return false;
            }

            public boolean isCreative() {
                return true;
            }
        };
    }
}

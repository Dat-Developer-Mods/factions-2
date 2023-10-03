package com.datdeveloper.datfactions.api.events;

import com.datdeveloper.datfactions.factiondata.EFPlayerChatMode;
import com.datdeveloper.datfactions.factiondata.FactionPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Events for when a player changes its chat mode
 */
public abstract class FactionPlayerSetChatModeEvent extends FactionPlayerEvent {
    /** The new chat mode of the player */
    EFPlayerChatMode newChatMode;

    /**
     * @param player     The player the event is for
     * @param newChatMode The new chat mode the player is switching to
     */
    protected FactionPlayerSetChatModeEvent(@NotNull final FactionPlayer player,
                                            final EFPlayerChatMode newChatMode) {
        super(player);
        this.newChatMode = newChatMode;
    }

    public EFPlayerChatMode getNewChatMode() {
        return newChatMode;
    }

    /**
     * Get the chat mode of the player from before it changes
     * @return The previous chat mode of the player
     */
    public abstract EFPlayerChatMode getOldChatMode();

    /**
     * Fired before a player changes their chat mode
     * <br>
     * The purpose of this event is to allow modifying/checking when a player changes their chat mode
     * For example, checking a player hasn't been locked out of a specific chat mode by another mod
     * <p>
     *     This event {@linkplain HasResult has a result}.<br>
     *     To change the result of this event, use {@link #setResult}. Results are interpreted in the following manner:
     * </p>
     * <ul>
     *     <li>Allow - The check will succeed, and the player will change to the given chat mode</li>
     *     <li>Default - The player will change chat mode if they have permission in their faction to use that chat mode</li>
     *     <li>Deny - The check will fail, and the player's chat mode will not change</li>
     * </ul>
     * <p>
     *     When setting the result to deny, you should provide a reason with {@link #setDenyReason(Component)} to
     *     allow commands to give a reason for not finishing.<br>
     *
     *     If no reason is given then no feedback will be given to the player
     * </p>
     */
    @HasResult
    public static class Pre extends FactionPlayerSetChatModeEvent implements IFactionPreEvent, IFactionEventDenyReason {
        /** The instigator of the action (if there is one) */
        private final ServerPlayer instigator;

        /** A reason for why the event was denied */
        private Component denyReason = null;

        /**
         * @param instigator  The player that instigated the event
         * @param player      The player the event is for
         * @param newChatMode The new chat mode the player is switching to
         */
        public Pre(@Nullable final ServerPlayer instigator,
                      @NotNull final FactionPlayer player,
                      final EFPlayerChatMode newChatMode) {
            super(player, newChatMode);
            this.instigator = instigator;
        }

        public void setNewChatMode(final EFPlayerChatMode newChatMode) {
            this.newChatMode = newChatMode;
        }

        /** {@inheritDoc} */
        @Override
        public EFPlayerChatMode getOldChatMode() {
            return player.getChatMode();
        }

        /** {@inheritDoc} */
        @Override
        public @Nullable ServerPlayer getInstigator() {
            return instigator;
        }

        /** {@inheritDoc} */
        @Override
        public Component getDenyReason() {
            return denyReason;
        }

        /** {@inheritDoc} */
        @Override
        public void setDenyReason(final Component denyReason) {
            this.denyReason = denyReason;
        }
    }

    /**
     * Fired after a player changes their chat mode
     * <br>
     * The intention of this event is to allow observing changes to the player's chat mode to update other resources
     */
    public static class Post extends FactionPlayerSetChatModeEvent {
        /** The previous chat mode before the change */
        private final EFPlayerChatMode oldChatMode;

        /**
         * @param player      The player the event is for
         * @param newChatMode The new chat mode the player is switching to
         */
        public Post(@NotNull final FactionPlayer player, final EFPlayerChatMode oldChatMode, final EFPlayerChatMode newChatMode) {
            super(player, newChatMode);
            this.oldChatMode = oldChatMode;
        }

        /** {@inheritDoc} */
        @Override
        public EFPlayerChatMode getOldChatMode() {
            return oldChatMode;
        }
    }
}

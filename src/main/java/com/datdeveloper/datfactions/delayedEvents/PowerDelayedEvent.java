package com.datdeveloper.datfactions.delayedEvents;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.factionData.*;
import com.datdeveloper.datfactions.util.PowerUtil;
import com.datdeveloper.datmoddingapi.delayedEvents.TimeDelayedEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.Map;

/**
 * A delayed event to passively increase a player's power as they play on the server
 */
public class PowerDelayedEvent extends TimeDelayedEvent {
    final FactionPlayer fPlayer;

    public PowerDelayedEvent(final FactionPlayer fPlayer) {
        super(FactionsConfig.getPlayerPassivePowerGainInterval());
        this.fPlayer = fPlayer;
    }

    @Override
    public void execute() {
        final Faction faction = fPlayer.getFaction();

        final int basePower = FactionsConfig.getPlayerPassivePowerGainAmount();
        final int baseMaxPower = FactionsConfig.getPlayerPassiveMaxPowerGainAmount();
        final Map<String, Float> multipliers = new HashMap<>();

        // Level multipliers
        if (fPlayer.isPlayerOnline()){
            final ServerPlayer player = fPlayer.getServerPlayer();

            final FactionLevel level = FLevelCollection.getInstance().getByKey(player.getLevel().dimension());
            multipliers.put("Level", level.getSettings().getPassivePowerGainMultiplier());


            final Faction chunkOwner = level.getChunkOwningFaction(new ChunkPos(player.getOnPos()));
            if (chunkOwner.hasFlag(EFactionFlags.BONUSPOWER)) {
                multipliers.put("Bonus", FactionsConfig.getBonusPowerFlagDeathMultiplier());
            }
        }

        // Faction multipliers
        if (fPlayer.hasFaction()) {
            final float roleAlpha = (faction.getRoleIndex(fPlayer.getRoleId()) / (float) (faction.getRoles().size() - 1));
            multipliers.put("Role (" + fPlayer.getRole().getName() + ")", ((1 - roleAlpha) * FactionsConfig.getPassiveMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.OWNER) + roleAlpha * FactionsConfig.getPassiveMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.RECRUIT)));
        } else {
            multipliers.put("No Faction", FactionsConfig.getPassiveMultiplier(FactionsConfig.EPlayerPowerGainMultiplierType.NOFACTION));
        }

        PowerUtil.handlePowerChange(fPlayer, basePower, baseMaxPower, multipliers);

        this.exeTime = System.currentTimeMillis() + (((long) FactionsConfig.getPlayerPassivePowerGainInterval()) * 1000L);
    }

    @Override
    public boolean canExecute() {
        return super.canExecute() && fPlayer.isPlayerOnline();
    }

    @Override
    public boolean shouldRequeue(final boolean hasFinished) {
        return fPlayer.isPlayerOnline();
    }
}

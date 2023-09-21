package com.datdeveloper.datfactions.delayedEvents;

import com.datdeveloper.datfactions.FactionsConfig;
import com.datdeveloper.datfactions.factiondata.FactionCollection;
import com.datdeveloper.datmoddingapi.delayedEvents.TimeDelayedEvent;

import static com.datdeveloper.datfactions.Datfactions.logger;

/**
 * A delayed event to clean up factions who've expired
 */
public class FactionCleanUpDelayedEvent extends TimeDelayedEvent {
    public FactionCleanUpDelayedEvent() {
        // Execute immediately
        super(0);
    }

    @Override
    public void execute() {
        logger.info("Cleaning up expired factions");

        final long factionOfflineExpiryTime = FactionsConfig.getFactionOfflineExpiryTime() * 1000L;

        FactionCollection.getInstance().getAll().values().stream()
                .filter(faction -> System.currentTimeMillis() - faction.getLastOnline() > factionOfflineExpiryTime)
                .forEach(faction -> {
                    logger.info("Faction " + faction.getName() + " has expired");
                    FactionCollection.getInstance().disbandFaction(faction.getId());
                });

        this.exeTime = System.currentTimeMillis() + (14400L * 1000L);
    }

    @Override
    public boolean canExecute() {
        return !FactionCollection.getInstance().getAll().isEmpty() && super.canExecute();
    }

    @Override
    public boolean shouldRequeue(final boolean hasFinished) {
        return true;
    }
}

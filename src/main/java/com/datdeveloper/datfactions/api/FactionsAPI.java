package com.datdeveloper.datfactions.api;

import com.datdeveloper.datfactions.factiondata.FLevelCollection;
import com.datdeveloper.datfactions.factiondata.FPlayerCollection;
import com.datdeveloper.datfactions.factiondata.FactionCollection;

public class FactionsAPI {
    /**
     * Get the instance of the FactionCollection <br>
     * This class contains the functions for managing the factions on the server
     * @return the FactionCollection instance
     */
    static FactionCollection getFactionCollection() {
        return FactionCollection.getInstance();
    }

    /**
     * Get the instance of the FPlayerCollection <br>
     * This class contains the functions for managing the players' faction data on the server
     * @return the FPlayerCollection instance
     */
    static FPlayerCollection getPlayerCollection() {
        return FPlayerCollection.getInstance();
    }

    /**
     * Get the instance of the FLevelCollection <br>
     * This class contains the function for managing the levels' faction data on the server
     * @return the FLevelCollection instance
     */
    static FLevelCollection getLevelCollection() {
        return FLevelCollection.getInstance();
    }
}

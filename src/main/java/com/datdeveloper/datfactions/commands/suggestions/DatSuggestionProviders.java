package com.datdeveloper.datfactions.commands.suggestions;

public class DatSuggestionProviders {

    private DatSuggestionProviders() {
        throw new IllegalStateException("Utility class");
    }
    public static final FactionSuggestionProvider factionProvider = new FactionSuggestionProvider();
    public static final FPlayerSuggestionProvider fPlayerProvider = new FPlayerSuggestionProvider(false);
    public static final OwnFPlayerSuggestionProvider ownFPlayerProvider = new OwnFPlayerSuggestionProvider(false);
    public static final FactionPermissionSuggestionProvider permissionProvider = new FactionPermissionSuggestionProvider();
    public static final FactionFlagSuggestionProvider flagSuggestionProvider = new FactionFlagSuggestionProvider(false);
}

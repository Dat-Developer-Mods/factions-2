package com.datdeveloper.datfactions.commands.suggestions;

public class DatSuggestionProviders {
    public static final FactionSuggestionProvider factionProvider = new FactionSuggestionProvider();
    public static final FPlayerSuggestionProvider fPlayerProvider = new FPlayerSuggestionProvider(false);
    public static final OwnFPlayerSuggestionProvider ownFPlayerProvider = new OwnFPlayerSuggestionProvider(false);
    public static final FactionPermissionSuggestionProvider permissionProvider = new FactionPermissionSuggestionProvider();
}

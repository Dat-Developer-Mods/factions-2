# TODO:
## Events
* ~~Rip out SkipChecks annotation and replace with allow,deny,default system~~
* ~~Implement pre and post~~

## Flags
* Replace with class for modability

## Commands
* Put all executes into class functions
* Use constants for parameter names
* Implement Top Command
  * Allow sorting
* Implement command for broadcasting a message to the faction
* Implement admin commands
* Fix flag command
  * Maybe implement the ability to add or remove multiple flags

## Chat type
* Implement chat type channels to allow mods to add their own

## Localisation
* ~~Implement localisation system in DatModdingAPI~~
  * ~~See if it's possible to copy the default minecraft one~~
  * ~~Implement template system~~
  * ~~Allow Colours & formatting~~
  * ~~Allow inserting existing components~~
* Localise all strings

## Permissions
* ~~Check it's possible to use with luckyperms~~

## Land
* Add config for disallowing separating land clusters
* Handle unclaiming logic in the unclaim and not an event

## Database
* Make storage more generic
  * Maybe allow passing a custom storage name
  * Use the visitor pattern

## Infiniverse
* ~~Check API Hasn't changed~~

## Level
* Add kill multiplier to level
* Add minimum members to claim config
* Switch to section and add config for 3d claims

## Faction
* Move build permission check to faction instead of event
* Permissions
  * Add permissions command for allies, enemies, and other
  * Maybe replace with class for modability
* Implement levels for sendFactionWideMessage

## Relations
* Strip out builder system for explicit but usable translation system

## Misc
* ~~Add utility in DatModdingAPI for notifications~~
  * ~~Move chunkalert enum~~
* Show MOTD on player join
* Add ability for other mods to register faction commands
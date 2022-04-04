# ManhuntBase

### A base manhunt plugin created for 1.18.2

## Features:

- Support for multiple runners \n
- Support for multiple hunters
- Tracker compass that works in all dimensions
- Automatic win / lose detection
- Custom messages for team join
- Changeable commands in config.yml
- Support for adding twists

## Ingame Commands:

```
Runner Commands: 
Default: /runner Arguments: join / leave / add / remove / list / clear

> join: Join the runner team
> leave: Leave the runner team
> add: Adds specified player to runner team
> remove: removes specified player from runner team
> list: lists all runners
> clear: clear all runners

Hunter Commands: 
Default: /hunter Arguments: join / leave / add / remove / list clear

> join: Join the hunter team
> leave: Leave the hunter team
> add: Adds specified player to hunter team
> remove: removes specified player from hunter team
> list: lists all hunters
> clear: clear all hunters

Manhunt Commands:
Default: /manhunt Arguments: start / stop / reset / time (displays time elapsed)

> start: Starts a manhunt
> stop: Stops a manhunt
> reset: Resets all runners / hunters and stops manhunt
> time: Displays time elapsed
```

### Default config.yml:

```
command-aliases:
  runner: ["/runner","/r"] #Aliases for the runner command
  hunter: ["/hunter","/h"] #Aliases for the hunter command

  manhunt: ["manhunt","/mh"] #Aliases for the manhunt command

defaults:
  runner: "" #Values: "", player name, all #default team
  hunter: all

command-permissions:
  require-op: true #values: true / false #If op is needed for the /runner, /hunter, /manhunt commands

message:
  broadcast-join-team: true #Broadcast to all players when a player joins team
  broadcast-winning-team: true #Broadcasts the team that won

  broadcast-message-join: "&a%s has joined %s" #%s and %s for playername and team name
  broadcast-message-remove: "&c%s has been removed from %s"  #%s and %s for playername and team name
  message-to-joined-player: "&aYou have joined %s " #Use %s for team name
  message-to-removed-player: "&cYou have been removed from %s" #Use %s for team name
  message-on-win: "§aThe %s has won!" #Use %s for team name

save-on-reload: true #Save hunters and runners on plugin reload

allow-multiple-runners: true #If more than one runner is allowed
require-all-runners-to-die: true #If all runners are required to die to end a multiple runner game

values:
  runners: [9636]
  hunters: []
```

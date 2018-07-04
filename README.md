# HologramsPlus

HologramPlus is an extension of [randombyte-developer/holograms](https://github.com/randombyte-developer/) that lets you add placeholders to your holograms.

[![](http://happyzleaf.com/uploads/hologramsplus.jpg "Click to watch the video")](https://streamable.com/gc7zs)

Installation
-
Download [HologramsPlus-1.0.0.jar](https://ore.spongepowered.org/happyzlife/HologramsPlus/versions).<br>
Download [holograms-3.1.5.jar](https://ore.spongepowered.org/RandomByte/Holograms/versions).<br>
Download [PlaceholderAPI-4.4.jar](https://ore.spongepowered.org/rojo8399/PlaceholderAPI/versions).

Usage
-
1) Create your hologram in the same way you do always, and use any placeholder you need (but pay attention to [this](#restrictions)).<br>
Example: `/holograms create Hello there! Currently there are %server_online% players connected.`
2) Register the newly created hologram, by standing within 10 blocks from the entity, using the command `/hologramsplus select enable` and selecting what you want to modify.
3) Profit! Now your hologram will be updated every tot seconds (according to your config), with the new placeholders.
4) If for any reason you want to disable the placeholders of an hologram, simply run the command `/hologramsplus select disable` and choose the desired hologram.

Restrictions
-
Due to the nature of the holograms, they cannot let a player being the source of the placeholders. In other words this mean that the number of placeholders actually available are restricted to the one that doesn't rely on players.<br>
Some examples can be found [here](https://github.com/rojo8399/PlaceholderAPI/wiki/Placeholders#server), [here](https://github.com/happyzleaf/PixelmonPlaceholders/wiki/Placeholders#general-pixelmon-info), and [here](https://github.com/happyzleaf/PixelmonPlaceholders/wiki/Placeholders#general-pok%C3%A9mon-info).<br>

I'm not saying there's no way of actually achieving the use of subjects in the holograms, because technically there are a couple of ways.<br>
The first is by sending different packets per player, but it's way over the original function of this plugin.<br>
The second is by parsing the placeholders using the nearest player, which isn't such a bad idea, but it can be achieved relatively easy by other placeholders. If you're interested in seeing it, let me know, I could implement that if people keeps to ask.

Commands
-
Command|Permission|Usage
---|---|---
`/hologramsplus select <enable/disable>`|`hologramsplus.command.select`|See [usage](#usage).
`/hologramsplus refresh`|`hologramsplus.command.refresh`|Forces to refresh all the placeholders.

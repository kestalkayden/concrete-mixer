# Concrete Mixer

A single-block concrete factory for Minecraft 26.1.x — Fabric + NeoForge.

Drop in sand, gravel, and dye — or just concrete powder — plus water from an internal 10-bucket tank, and the mixer produces colored concrete blocks furnace-style. No redstone or external power required.

## Highlights

- **Two crafting paths**: raw (sand + gravel + dye) or powder shortcut (concrete powder + water)
- **10-bucket water tank** — vanilla buckets, modded canisters, or pipe-fed
- **Standard fluid capability** — works with Pipez, Create, Mekanism, Tech Reborn, EnderIO, Jade, etc.
- **Hopper-friendly**: top/sides push inputs, bottom pulls output, sides pull empty buckets
- **Color-lock output** to avoid mid-batch color mixing
- **Redstone-pausable**, contents preserved on break
- **Recipe**: 8 iron ingots ringing a cauldron

## Building

```
./gradlew buildAll
```

Outputs land in `fabric/build/libs/` and `neoforge/build/libs/`.

## License

CC0-1.0.

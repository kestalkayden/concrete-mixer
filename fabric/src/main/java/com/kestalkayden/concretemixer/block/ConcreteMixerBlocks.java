package com.kestalkayden.concretemixer.block;

import java.util.Optional;

import com.kestalkayden.concretemixer.ConcreteMixerFabric;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootTable;

public final class ConcreteMixerBlocks {
    public static ConcreteMixerBlock CONCRETE_MIXER;
    public static BlockItem CONCRETE_MIXER_ITEM;

    private ConcreteMixerBlocks() {}

    public static void register() {
        Identifier id = Identifier.fromNamespaceAndPath(ConcreteMixerFabric.MOD_ID, "concrete_mixer");
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        ResourceKey<Item>  itemKey  = ResourceKey.create(Registries.ITEM, id);
        ResourceKey<LootTable> lootKey = ResourceKey.create(Registries.LOOT_TABLE,
            Identifier.fromNamespaceAndPath(ConcreteMixerFabric.MOD_ID, "blocks/concrete_mixer"));

        CONCRETE_MIXER = Registry.register(BuiltInRegistries.BLOCK, id,
            new ConcreteMixerBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE)
                    .setId(blockKey)
                    .overrideLootTable(Optional.of(lootKey))
                    .mapColor(MapColor.METAL)));

        CONCRETE_MIXER_ITEM = Registry.register(BuiltInRegistries.ITEM, id,
            new BlockItem(CONCRETE_MIXER,
                new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
    }
}

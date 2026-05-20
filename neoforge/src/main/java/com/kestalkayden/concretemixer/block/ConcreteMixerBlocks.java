package com.kestalkayden.concretemixer.block;

import java.util.Optional;

import com.kestalkayden.concretemixer.ConcreteMixerNeoForge;

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
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ConcreteMixerBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ConcreteMixerNeoForge.MOD_ID);
    public static final DeferredRegister.Items  ITEMS  = DeferredRegister.createItems(ConcreteMixerNeoForge.MOD_ID);

    public static final DeferredBlock<ConcreteMixerBlock> CONCRETE_MIXER = BLOCKS.register("concrete_mixer", id -> {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        ResourceKey<LootTable> lootKey = ResourceKey.create(Registries.LOOT_TABLE,
            Identifier.fromNamespaceAndPath(ConcreteMixerNeoForge.MOD_ID, "blocks/concrete_mixer"));
        return new ConcreteMixerBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE)
                .setId(blockKey)
                .overrideLootTable(Optional.of(lootKey))
                .mapColor(MapColor.METAL));
    });

    public static final DeferredItem<BlockItem> CONCRETE_MIXER_ITEM = ITEMS.register("concrete_mixer", id -> {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
        return new BlockItem(CONCRETE_MIXER.get(),
            new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
    });

    private ConcreteMixerBlocks() {}
}

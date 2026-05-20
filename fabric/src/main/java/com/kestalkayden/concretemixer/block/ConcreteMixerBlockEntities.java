package com.kestalkayden.concretemixer.block;

import com.kestalkayden.concretemixer.ConcreteMixerFabric;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ConcreteMixerBlockEntities {
    public static BlockEntityType<ConcreteMixerBlockEntity> CONCRETE_MIXER_BE;

    private ConcreteMixerBlockEntities() {}

    public static void register() {
        CONCRETE_MIXER_BE = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(ConcreteMixerFabric.MOD_ID, "concrete_mixer"),
            FabricBlockEntityTypeBuilder.create(ConcreteMixerBlockEntity::new,
                ConcreteMixerBlocks.CONCRETE_MIXER
            ).build());
    }
}

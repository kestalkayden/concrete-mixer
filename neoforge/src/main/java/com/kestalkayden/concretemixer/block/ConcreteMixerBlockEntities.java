package com.kestalkayden.concretemixer.block;

import com.kestalkayden.concretemixer.ConcreteMixerNeoForge;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ConcreteMixerBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ConcreteMixerNeoForge.MOD_ID);

    public static BlockEntityType<ConcreteMixerBlockEntity> CONCRETE_MIXER_BE;

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ConcreteMixerBlockEntity>> CONCRETE_MIXER_BE_HOLDER =
        BES.register("concrete_mixer", () -> {
            CONCRETE_MIXER_BE = new BlockEntityType<>(ConcreteMixerBlockEntity::new,
                ConcreteMixerBlocks.CONCRETE_MIXER.get());
            return CONCRETE_MIXER_BE;
        });

    private ConcreteMixerBlockEntities() {}
}

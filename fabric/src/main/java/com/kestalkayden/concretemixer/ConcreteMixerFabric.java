package com.kestalkayden.concretemixer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kestalkayden.concretemixer.block.ConcreteMixerBlockEntities;
import com.kestalkayden.concretemixer.block.ConcreteMixerBlocks;
import com.kestalkayden.concretemixer.block.ConcreteMixerFluidStorage;
import com.kestalkayden.concretemixer.menu.ConcreteMixerMenus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;

public class ConcreteMixerFabric implements ModInitializer {
    public static final String MOD_ID = "concretemixer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Concrete Mixer (Fabric)");

        ConcreteMixerBlocks.register();
        ConcreteMixerBlockEntities.register();
        ConcreteMixerMenus.register();

        // Expose the water tank to Fabric's fluid-transfer API so pipe mods (Pipez, etc.) and
        // hover-info mods (Jade) can read and refill the tank.
        FluidStorage.SIDED.registerForBlockEntity(
            (be, dir) -> new ConcreteMixerFluidStorage(be),
            ConcreteMixerBlockEntities.CONCRETE_MIXER_BE);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(output -> {
            output.insertAfter(Items.CAULDRON,
                ConcreteMixerBlocks.CONCRETE_MIXER_ITEM.getDefaultInstance());
        });
    }
}

package com.kestalkayden.concretemixer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kestalkayden.concretemixer.block.ConcreteMixerBlockEntities;
import com.kestalkayden.concretemixer.block.ConcreteMixerBlocks;
import com.kestalkayden.concretemixer.client.ConcreteMixerScreen;
import com.kestalkayden.concretemixer.menu.ConcreteMixerMenus;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(ConcreteMixerNeoForge.MOD_ID)
public class ConcreteMixerNeoForge {
    public static final String MOD_ID = "concretemixer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public ConcreteMixerNeoForge(IEventBus modBus) {
        LOGGER.info("Initializing Concrete Mixer (NeoForge)");

        ConcreteMixerBlocks.BLOCKS.register(modBus);
        ConcreteMixerBlocks.ITEMS.register(modBus);
        ConcreteMixerBlockEntities.BES.register(modBus);
        ConcreteMixerMenus.MENUS.register(modBus);

        modBus.addListener(ConcreteMixerNeoForge::onBuildCreativeTabs);

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            modBus.addListener(ConcreteMixerNeoForge::onRegisterMenuScreens);
        }
    }

    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ConcreteMixerMenus.CONCRETE_MIXER_MENU, ConcreteMixerScreen::new);
    }

    private static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.insertAfter(new ItemStack(Items.CAULDRON),
                new ItemStack(ConcreteMixerBlocks.CONCRETE_MIXER_ITEM.get()),
                net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}

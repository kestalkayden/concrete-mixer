package com.kestalkayden.concretemixer.client;

import com.kestalkayden.concretemixer.menu.ConcreteMixerMenus;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class ConcreteMixerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(ConcreteMixerMenus.CONCRETE_MIXER_MENU, ConcreteMixerScreen::new);
    }
}

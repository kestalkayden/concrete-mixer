package com.kestalkayden.concretemixer.menu;

import com.kestalkayden.concretemixer.ConcreteMixerFabric;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class ConcreteMixerMenus {
    public static MenuType<ConcreteMixerMenu> CONCRETE_MIXER_MENU;

    private ConcreteMixerMenus() {}

    public static void register() {
        CONCRETE_MIXER_MENU = Registry.register(
            BuiltInRegistries.MENU,
            Identifier.fromNamespaceAndPath(ConcreteMixerFabric.MOD_ID, "concrete_mixer"),
            new MenuType<>(ConcreteMixerMenu::new, FeatureFlags.VANILLA_SET));
    }
}

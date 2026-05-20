package com.kestalkayden.concretemixer.menu;

import com.kestalkayden.concretemixer.ConcreteMixerNeoForge;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ConcreteMixerMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
        DeferredRegister.create(Registries.MENU, ConcreteMixerNeoForge.MOD_ID);

    public static MenuType<ConcreteMixerMenu> CONCRETE_MIXER_MENU;

    public static final DeferredHolder<MenuType<?>, MenuType<ConcreteMixerMenu>> CONCRETE_MIXER_MENU_HOLDER =
        MENUS.register("concrete_mixer", () -> {
            CONCRETE_MIXER_MENU = new MenuType<>(ConcreteMixerMenu::new, FeatureFlags.VANILLA_SET);
            return CONCRETE_MIXER_MENU;
        });

    private ConcreteMixerMenus() {}
}

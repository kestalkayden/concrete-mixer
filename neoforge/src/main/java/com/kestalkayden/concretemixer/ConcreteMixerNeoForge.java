package com.kestalkayden.concretemixer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ConcreteMixerNeoForge.MOD_ID)
public class ConcreteMixerNeoForge {
    public static final String MOD_ID = "concretemixer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public ConcreteMixerNeoForge(IEventBus modBus) {
        LOGGER.info("Initializing Concrete Mixer (NeoForge)");
    }
}

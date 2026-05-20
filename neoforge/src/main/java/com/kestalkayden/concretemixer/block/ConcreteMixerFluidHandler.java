package com.kestalkayden.concretemixer.block;

import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/** NeoForge unified-transfer adapter exposing the mixer's water tank to pipe mods and HUD mods.
 *  Insert-only of {@link Fluids#WATER}; extraction is denied (the mixer consumes its own water
 *  internally via the recipe loop, never gives any back).
 *
 *  NeoForge ResourceHandler uses millibuckets directly (unlike Fabric's droplets), matching
 *  our internal {@code waterMb} field 1:1 — no unit conversion needed.
 *
 *  Transactions are not snapshot-tracked — inserts commit immediately. */
public class ConcreteMixerFluidHandler implements ResourceHandler<FluidResource> {
    private final ConcreteMixerBlockEntity be;

    public ConcreteMixerFluidHandler(ConcreteMixerBlockEntity be) {
        this.be = be;
    }

    @Override
    public int size() { return 1; }

    @Override
    public FluidResource getResource(int slot) {
        return be.getWaterMb() > 0 ? FluidResource.of(Fluids.WATER) : FluidResource.EMPTY;
    }

    @Override
    public long getAmountAsLong(int slot) {
        return be.getWaterMb();
    }

    @Override
    public long getCapacityAsLong(int slot, FluidResource resource) {
        return ConcreteMixerBlockEntity.TANK_CAPACITY_MB;
    }

    @Override
    public boolean isValid(int slot, FluidResource resource) {
        return !resource.isEmpty() && resource.getFluid() == Fluids.WATER;
    }

    @Override
    public int insert(int slot, FluidResource resource, int amount, TransactionContext tx) {
        if (resource.isEmpty() || resource.getFluid() != Fluids.WATER) return 0;
        int currentMb = be.getWaterMb();
        int roomMb = ConcreteMixerBlockEntity.TANK_CAPACITY_MB - currentMb;
        int accepted = Math.min(amount, roomMb);
        if (accepted <= 0) return 0;
        be.addWaterMb(accepted);
        return accepted;
    }

    @Override
    public int extract(int slot, FluidResource resource, int amount, TransactionContext tx) {
        return 0;  // insert-only — the mixer drinks its own water; outsiders can't pull.
    }
}

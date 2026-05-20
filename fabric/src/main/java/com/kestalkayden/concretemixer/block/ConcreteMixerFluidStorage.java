package com.kestalkayden.concretemixer.block;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.level.material.Fluids;

/** Fabric Transfer-API adapter exposing the mixer's water tank to pipe mods.
 *  Insert-only of {@link Fluids#WATER}; extraction is denied (the mixer consumes its own water
 *  internally via the recipe loop, never gives any back).
 *
 *  Transactions are not snapshot-tracked — inserts commit immediately. A pipe that rolls back
 *  a transfer would leave the water in the tank as "phantom" gain; acceptable risk for v0.1
 *  given how rare rollback-after-insert is in practice. */
public class ConcreteMixerFluidStorage implements SingleSlotStorage<FluidVariant> {
    private final ConcreteMixerBlockEntity be;

    /** Fabric Transfer uses droplets where 1 bucket = 81000 droplets; convert via this constant. */
    private static final long DROPLETS_PER_MB = FluidConstants.BUCKET / 1000;

    public ConcreteMixerFluidStorage(ConcreteMixerBlockEntity be) {
        this.be = be;
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext tx) {
        if (!resource.isOf(Fluids.WATER)) return 0;
        long currentMb = be.getWaterMb();
        long roomMb = ConcreteMixerBlockEntity.TANK_CAPACITY_MB - currentMb;
        long roomDroplets = roomMb * DROPLETS_PER_MB;
        long acceptedDroplets = Math.min(maxAmount, roomDroplets);
        long acceptedMb = acceptedDroplets / DROPLETS_PER_MB;
        if (acceptedMb <= 0) return 0;
        be.addWaterMb((int) acceptedMb);
        return acceptedMb * DROPLETS_PER_MB;
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext tx) {
        return 0;  // insert-only — the mixer drinks its own water; outsiders can't pull.
    }

    @Override
    public boolean isResourceBlank() {
        return be.getWaterMb() == 0;
    }

    @Override
    public FluidVariant getResource() {
        return be.getWaterMb() > 0 ? FluidVariant.of(Fluids.WATER) : FluidVariant.blank();
    }

    @Override
    public long getAmount() {
        return be.getWaterMb() * (long) DROPLETS_PER_MB;
    }

    @Override
    public long getCapacity() {
        return ConcreteMixerBlockEntity.TANK_CAPACITY_MB * (long) DROPLETS_PER_MB;
    }
}

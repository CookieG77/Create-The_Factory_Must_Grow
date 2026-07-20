package com.drmangotea.tfmg.base;

import net.neoforged.neoforge.energy.EnergyStorage;

/**
 * Simple Forge Energy sink for FE-powered machines. It accepts energy from the outside but does
 * not let anything extract it back out ({@code maxExtract == 0}); the owning machine drains it
 * internally each tick via {@link #consumeAll()} to measure how much power it received.
 */
public class MachineEnergyStorage extends EnergyStorage {

    public MachineEnergyStorage(int capacity) {
        super(capacity, capacity, 0);
    }

    /**
     * Removes and returns all currently stored energy. Used by the machine to determine how much
     * power it received since the last tick.
     */
    public int consumeAll() {
        int consumed = this.energy;
        this.energy = 0;
        return consumed;
    }

    public void setEnergy(int amount) {
        this.energy = Math.max(0, Math.min(amount, this.capacity));
    }
}

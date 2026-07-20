package com.drmangotea.tfmg.content.electricity.network.large_switch;

import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;

import static com.drmangotea.tfmg.content.electricity.network.large_switch.LargeSwitchBlock.IS_MAIN_PART;
import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class LargeSwitchBlockEntity extends KineticBlockEntity {

    public boolean closed = false;

    public LerpedFloat visualAngle = LerpedFloat.angular();
    public float angle = 900;

    final boolean isMainPart;

    private final SwitchEnergyStorage energyStorage = new SwitchEnergyStorage();

    public LargeSwitchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        isMainPart = state.getValue(IS_MAIN_PART);
        visualAngle.setValue(90);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                TFMGBlockEntities.LARGE_SWITCH.get(),
                (be, side) -> be.getEnergyStorage(side)
        );
    }

    /**
     * The face of this half of the switch that external FE consumers/providers connect to.
     */
    public Direction getConnectiveFace() {
        Direction facing = getBlockState().getValue(HORIZONTAL_FACING);
        return isMainPart ? facing.getOpposite() : facing;
    }

    public IEnergyStorage getEnergyStorage(Direction side) {
        if (side != null && side != getConnectiveFace())
            return null;
        return energyStorage;
    }

    /**
     * Whether the switch is currently letting energy pass through. Only the main part computes
     * this; the secondary part reads the main part's value.
     */
    public boolean isClosed() {
        if (isMainPart)
            return closed;
        if (level != null && level.getBlockEntity(getBlockPos().relative(getBlockState().getValue(HORIZONTAL_FACING).getOpposite())) instanceof LargeSwitchBlockEntity main)
            return main.closed;
        return false;
    }

    /**
     * @return the IEnergyStorage sitting on the far side of the OTHER half of the switch, or null.
     */
    private IEnergyStorage getForwardTarget() {
        if (level == null)
            return null;
        Direction facing = getBlockState().getValue(HORIZONTAL_FACING);
        BlockPos targetPos;
        Direction targetSide;
        if (isMainPart) {
            // past the secondary part, in the facing direction
            targetPos = getBlockPos().relative(facing, 2);
            targetSide = facing.getOpposite();
        } else {
            // behind the main part, against the facing direction
            targetPos = getBlockPos().relative(facing.getOpposite(), 2);
            targetSide = facing;
        }
        return level.getCapability(Capabilities.EnergyStorage.BLOCK, targetPos, targetSide);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        angle = compound.getFloat("Angle");
        closed = compound.getBoolean("Closed");
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putFloat("Angle", angle);
        compound.putBoolean("Closed", closed);
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide) {
            visualAngle.chase(angle / 10d, 1d, LerpedFloat.Chaser.EXP);
            visualAngle.tickChaser();
        }
        if (!isMainPart)
            return;
        if (angle < 0)
            angle = 0;
        if (angle > 900)
            angle = 900;
        if (getSpeed() == 0)
            return;

        if (getSpeed() < 0 && angle != 900) {
            angle += getArmSpeed();
        }
        if (getSpeed() > 0 && angle != 0) {
            angle -= getArmSpeed();
        }

        closed = angle == 0;
    }

    public float getArmSpeed() {
        return Math.abs(getSpeed()) * 0.3f;
    }

    /**
     * Forwarding energy storage: passes receive/extract through to whatever storage sits on the
     * far side of the other half of the switch, gated on the switch being closed.
     */
    private class SwitchEnergyStorage implements IEnergyStorage {

        private boolean busy = false;

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (busy || !isClosed())
                return 0;
            IEnergyStorage target = getForwardTarget();
            if (target == null)
                return 0;
            busy = true;
            try {
                return target.receiveEnergy(maxReceive, simulate);
            } finally {
                busy = false;
            }
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (busy || !isClosed())
                return 0;
            IEnergyStorage target = getForwardTarget();
            if (target == null)
                return 0;
            busy = true;
            try {
                return target.extractEnergy(maxExtract, simulate);
            } finally {
                busy = false;
            }
        }

        @Override
        public int getEnergyStored() {
            if (busy || !isClosed())
                return 0;
            IEnergyStorage target = getForwardTarget();
            if (target == null)
                return 0;
            busy = true;
            try {
                return target.getEnergyStored();
            } finally {
                busy = false;
            }
        }

        @Override
        public int getMaxEnergyStored() {
            if (busy || !isClosed())
                return 0;
            IEnergyStorage target = getForwardTarget();
            if (target == null)
                return 0;
            busy = true;
            try {
                return target.getMaxEnergyStored();
            } finally {
                busy = false;
            }
        }

        @Override
        public boolean canExtract() {
            if (busy || !isClosed())
                return false;
            IEnergyStorage target = getForwardTarget();
            if (target == null)
                return false;
            busy = true;
            try {
                return target.canExtract();
            } finally {
                busy = false;
            }
        }

        @Override
        public boolean canReceive() {
            if (busy || !isClosed())
                return false;
            IEnergyStorage target = getForwardTarget();
            if (target == null)
                return false;
            busy = true;
            try {
                return target.canReceive();
            } finally {
                busy = false;
            }
        }
    }
}

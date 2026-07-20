package com.drmangotea.tfmg.content.machinery.vat.freezer;

import com.drmangotea.tfmg.base.MachineEnergyStorage;
import com.drmangotea.tfmg.config.TFMGConfigs;
import com.drmangotea.tfmg.content.machinery.vat.base.VatBlock;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.List;

public class FreezerBlockEntity extends SmartBlockEntity {

    public MachineEnergyStorage energy;
    public int feReceivedThisTick = 0;
    private boolean wasOperational = false;

    public FreezerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        energy = new MachineEnergyStorage(getFEUsage());
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                TFMGBlockEntities.FREEZER.get(),
                (be, side) -> be.energy
        );
    }

    public static int getFEUsage() {
        return TFMGConfigs.common().machines.freezerFEUsage.get();
    }

    public boolean isOperational() {
        return feReceivedThisTick >= getFEUsage();
    }

    @Override
    public void tick() {
        super.tick();

        feReceivedThisTick = energy.consumeAll();

        if (level == null) return;

        boolean operational = isOperational();
        if (operational != wasOperational) {
            wasOperational = operational;
            if (!level.isClientSide)
                VatBlock.updateVatState(getBlockState(), level, getBlockPos().relative(Direction.DOWN));
        }
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("ForgeEnergy", energy.getEnergyStored());
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        energy.setEnergy(compound.getInt("ForgeEnergy"));
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos()).setMinY(getBlockPos().getY() - 2);
    }

}

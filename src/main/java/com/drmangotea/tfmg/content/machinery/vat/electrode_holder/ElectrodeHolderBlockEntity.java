package com.drmangotea.tfmg.content.machinery.vat.electrode_holder;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.TFMGRegistries;
import com.drmangotea.tfmg.base.MachineEnergyStorage;
import com.drmangotea.tfmg.base.TFMGUtils;
import com.drmangotea.tfmg.config.TFMGConfigs;
import com.drmangotea.tfmg.content.machinery.vat.base.IVatMachine;
import com.drmangotea.tfmg.content.machinery.vat.base.VatBlock;
import com.drmangotea.tfmg.content.machinery.vat.base.VatBlockEntity;
import com.drmangotea.tfmg.content.machinery.vat.electrode_holder.electrode.Electrode;
import com.drmangotea.tfmg.registry.TFMGBlockEntities;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.List;

public class ElectrodeHolderBlockEntity extends SmartBlockEntity implements IVatMachine {

    Electrode electrode = TFMGUtils.getElectrode(TFMG.asResource("none"));

    public MachineEnergyStorage energy;
    public int feReceivedThisTick = 0;
    private boolean wasOperational = false;

    public ElectrodeHolderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        energy = new MachineEnergyStorage(getFEUsage());
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                TFMGBlockEntities.ELECTRODE_HOLDER.get(),
                (be, side) -> side == Direction.UP || side == null ? be.energy : null
        );
    }

    public static int getFEUsage() {
        return TFMGConfigs.common().machines.electrolysisFEUsage.get();
    }

    public boolean setElectrode(ItemStack modeItem, boolean simulate) {
        if (level == null) return false;
        for (Electrode electrode : TFMGRegistries.ELECTRODE_REGISTRY.stream().toList()) {
            if (electrode.getStack().isEmpty()) continue;
            if (modeItem.is(electrode.getStack().getItem())) {
                if (!simulate) {
                    this.electrode = electrode;
                } else return true;
            }
        }
        if (!simulate && hasLevel())
            VatBlock.updateVatState(getBlockState(), getLevel(), getBlockPos().relative(Direction.DOWN));
        sendData();
        return false;
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

        var vatBE = level.getBlockEntity(getBlockPos().relative(Direction.DOWN));
        if (vatBE instanceof VatBlockEntity vat) {
            BlockPos electrodePos = getBlockPos().relative(Direction.DOWN);
            this.electrode.tick(vat.getControllerBE(), this.level, electrodePos, operational, this.level.isClientSide());
        }
    }

    public boolean setElectrode(Electrode electrode, boolean simulate) {
        if (electrode != null) {
            if (!simulate) {
                this.electrode = electrode;
            } else return true;
        }
        if (!simulate && hasLevel())
            VatBlock.updateVatState(getBlockState(), getLevel(), getBlockPos().relative(Direction.DOWN));
        sendData();
        return false;
    }

    @Override
    public void remove() {

        if (level.isClientSide || electrode.getItem() == null)
            return;

        ItemEntity itemToDrop = new ItemEntity(level, getBlockPos().getX() + 0.5f, getBlockPos().getY() + 0.5f, getBlockPos().getZ() + 0.5f, electrode.getStack());

        level.addFreshEntity(itemToDrop);
    }

    boolean isOperational() {
        return feReceivedThisTick >= getFEUsage();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos()).setMinY(getBlockPos().getY() - 2);
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putString("Electrode", electrode.getKey().toString());
        compound.putInt("ForgeEnergy", energy.getEnergyStored());

        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        energy.setEnergy(compound.getInt("ForgeEnergy"));
        setElectrode(TFMGUtils.getElectrode(ResourceLocation.parse(compound.getString("Electrode"))), false);
    }

    @Override
    public String getOperationId() {
        return electrode.getOperationId();
    }

    @Override
    public boolean canOperate(VatBlockEntity vat) {
        return isOperational();
    }

    @Override
    public int getWorkPercentage() {
        int usage = getFEUsage();
        if (usage <= 0)
            return 100;
        return Math.min(100, feReceivedThisTick * 100 / usage);
    }

    @Override
    public void vatUpdated(VatBlockEntity be) {
        IVatMachine.super.vatUpdated(be);
    }

}

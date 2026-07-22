package com.drmangotea.tfmg.base.events;


import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.TFMGRegistries;
import com.drmangotea.tfmg.content.decoration.tanks.TFMGFluidTankBlockEntity;
import com.drmangotea.tfmg.content.decoration.tanks.steel.SteelTankBlockEntity;
import com.drmangotea.tfmg.content.electricity.network.large_switch.LargeSwitchBlockEntity;
import com.drmangotea.tfmg.content.electricity.utilities.electric_pump.ElectricPumpBlockEntity;
import com.drmangotea.tfmg.content.engines.fuels.EngineFuelTypeManager;
import com.drmangotea.tfmg.content.engines.types.AbstractSmallEngineBlockEntity;
import com.drmangotea.tfmg.content.engines.types.large_engine.LargeEngineBlockEntity;
import com.drmangotea.tfmg.content.engines.types.regular_engine.RegularEngineBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.blast_furnace.BlastFurnaceHatchBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.blast_furnace.BlastFurnaceOutputBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.blast_stove.BlastStoveBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.casting_basin.CastingBasinBlockEntity;
import com.drmangotea.tfmg.content.machinery.metallurgy.coke_oven.CokeOvenBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.air_intake.AirIntakeBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.concrete_hose.ConcreteHoseBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.exhaust.ExhaustBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.firebox.FireboxBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.flarestack.FlarestackBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.gas_lamp.GasLampBlockEntity;
import com.drmangotea.tfmg.content.machinery.misc.smokestack.SmokestackBlockEntity;
import com.drmangotea.tfmg.content.machinery.oil_processing.distillation_tower.controller.DistillationControllerBlockEntity;
import com.drmangotea.tfmg.content.machinery.oil_processing.distillation_tower.output.DistillationOutputBlockEntity;
import com.drmangotea.tfmg.content.machinery.vat.base.VatBlockEntity;
import com.drmangotea.tfmg.content.machinery.vat.electrode_holder.ElectrodeHolderBlockEntity;
import com.drmangotea.tfmg.content.machinery.vat.freezer.FreezerBlockEntity;
import com.drmangotea.tfmg.content.items.weapons.explosives.pipe_bomb.PipeBombItem;
import com.drmangotea.tfmg.registry.TFMGDataComponents;
import com.drmangotea.tfmg.registry.TFMGItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;


@EventBusSubscriber
public class TFMGCommonEvents {



    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(EngineFuelTypeManager.ReloadListener.INSTANCE);
    }

    /**
     * A triggered, slime-combined pipe bomb can't be tossed away - cancel the toss and hand it
     * straight back to whoever was trying to get rid of it.
     */
    @SubscribeEvent
    public static void onPipeBombToss(ItemTossEvent event) {
        ItemStack stack = event.getEntity().getItem();
        if (!stack.is(TFMGItems.PIPE_BOMB.get()))
            return;
        if (!stack.has(TFMGDataComponents.PIPE_BOMB_STICKY))
            return;
        if (!stack.has(TFMGDataComponents.PIPE_BOMB_TRAP_TIMER))
            return;

        event.setCanceled(true);
        Player player = event.getPlayer();
        if (!player.getInventory().add(stack))
            player.drop(stack, false);
    }

    /**
     * Pipe bomb explosions are player-only, so nobody's pets/mobs/farms get caught in a prank.
     */
    @SubscribeEvent
    public static void onPipeBombDetonate(ExplosionEvent.Detonate event) {
        if (!PipeBombItem.isPlayerOnlyExplosion())
            return;
        event.getAffectedEntities().removeIf(entity -> !(entity instanceof Player));
    }

    @EventBusSubscriber
    public static class ModBusEvents {
        @net.neoforged.bus.api.SubscribeEvent
        public static void registerCapabilities(RegisterCapabilitiesEvent event) {

            AbstractSmallEngineBlockEntity.registerCapabilities(event);
            DistillationOutputBlockEntity.registerCapabilities(event);
            ConcreteHoseBlockEntity.registerCapabilities(event);
            LargeEngineBlockEntity.registerCapabilities(event);
            CastingBasinBlockEntity.registerCapabilities(event);
            FireboxBlockEntity.registerCapabilities(event);
            DistillationControllerBlockEntity.registerCapabilities(event);
            LargeSwitchBlockEntity.registerCapabilities(event);
            ElectricPumpBlockEntity.registerCapabilities(event);
            ElectrodeHolderBlockEntity.registerCapabilities(event);
            FreezerBlockEntity.registerCapabilities(event);
            SteelTankBlockEntity.registerCapabilities(event);
            TFMGFluidTankBlockEntity.registerCapabilities(event);
            VatBlockEntity.registerCapabilities(event);
            BlastStoveBlockEntity.registerCapabilities(event);
            SmokestackBlockEntity.registerCapabilities(event);
            ExhaustBlockEntity.registerCapabilities(event);
            BlastFurnaceHatchBlockEntity.registerCapabilities(event);
            FlarestackBlockEntity.registerCapabilities(event);
            GasLampBlockEntity.registerCapabilities(event);
            BlastFurnaceOutputBlockEntity.registerCapabilities(event);
            CokeOvenBlockEntity.registerCapabilities(event);
            AirIntakeBlockEntity.registerCapabilities(event);
        }

        @SubscribeEvent
        public static void newRegistry(NewRegistryEvent event) {
            event.register(TFMGRegistries.ELECTRODE_REGISTRY);
        }
    }


}

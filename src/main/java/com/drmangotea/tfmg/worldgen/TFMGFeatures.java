package com.drmangotea.tfmg.worldgen;


import com.drmangotea.tfmg.TFMG;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;


public class TFMGFeatures {


    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, TFMG.MOD_ID);

    public static void register(IEventBus modEventBus) {
        FEATURES.register(modEventBus);
    }
}

package com.drmangotea.tfmg.ponder;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.registry.TFMGBlocks;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class TFMGPonderTags {

    public static final ResourceLocation

            OIL_PROCESSING = loc("oil_processing"),
            ENGINES = loc("engines"),
            METALLURGY = loc("metallurgy"),
            ELECTRIC_MACHINERY = loc("electric_machinery"),
            CHEMICAL_VAT = loc("chemical_vat")
                    ;

    private static ResourceLocation loc(String id) {
        return TFMG.asResource(id);
    }

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {

        PonderTagRegistrationHelper<RegistryEntry<?,?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        PonderTagRegistrationHelper<ItemLike> itemHelper = helper.withKeyFunction(
                RegisteredObjectsHelper::getKeyOrThrow);

        helper.registerTag(OIL_PROCESSING)
                .addToIndex()
                .item(TFMGBlocks.STEEL_DISTILLATION_CONTROLLER, true, false)
                .title("Oil Processing Machinery")
                .description("Block used for refining and mining oil")
                .register();
      //  helper.registerTag(ENGINES)
      //          .addToIndex()
      //          .item(TFMGBlocks.TURBINE_ENGINE, true, false)
      //          .title("Engines")
      //          .description("Engines and equipment related to them")
      //          .register();
        helper.registerTag(METALLURGY)
                .addToIndex()
                .item(TFMGBlocks.BLAST_FURNACE_OUTPUT, true, false)
                .title("Metallurgy")
                .description("Blocks related to processing metal")
                .register();
        helper.registerTag(ELECTRIC_MACHINERY)
                .addToIndex()
                .item(TFMGBlocks.ELECTRIC_PUMP, true, true)
                .title("Electric Machinery")
                .description("Block which use, produce or transfer electricity")
                .register();
        helper.registerTag(CHEMICAL_VAT)
                .addToIndex()
                .item(TFMGBlocks.STEEL_CHEMICAL_VAT, true, false)
                .title("Chemical Vat")
                .description("Chemical vat and machines that expand it")
                .register();


        HELPER.addToTag(OIL_PROCESSING)
                .add(TFMGBlocks.STEEL_DISTILLATION_CONTROLLER)
                .add(TFMGBlocks.STEEL_DISTILLATION_OUTPUT)
                .add(TFMGBlocks.INDUSTRIAL_PIPE)
    ;

        //HELPER.addToTag(ENGINES)
        //        .add(TFMGBlocks.REGULAR_ENGINE)
        //        .add(TFMGBlocks.TURBINE_ENGINE)
        //        .add(TFMGBlocks.RADIAL_ENGINE)
        //        .add(TFMGBlocks.LARGE_ENGINE)
        //        .add(TFMGBlocks.SIMPLE_LARGE_ENGINE)
        //        .add(TFMGBlocks.ENGINE_CONTROLLER)
        //        .add(TFMGBlocks.ENGINE_GEARBOX)
        //        .add(TFMGBlocks.EXHAUST)
        //        .add(TFMGBlocks.AIR_INTAKE)
        //        .add(TFMGItems.OIL_CAN)
        //        .add(TFMGItems.COOLING_FLUID_BOTTLE)
        //        .add(TFMGItems.TURBO);

        HELPER.addToTag(METALLURGY)
                .add(TFMGBlocks.BLAST_FURNACE_OUTPUT)
                .add(TFMGBlocks.BLAST_FURNACE_HATCH)
                .add(TFMGBlocks.FIREPROOF_BRICKS)
                .add(TFMGBlocks.FIREPROOF_BRICK_REINFORCEMENT)
                .add(TFMGBlocks.BLAST_FURNACE_REINFORCEMENT)
                .add(TFMGBlocks.RUSTED_BLAST_FURNACE_REINFORCEMENT)
                .add(TFMGBlocks.BLAST_STOVE)
                .add(TFMGBlocks.CASTING_BASIN);

        HELPER.addToTag(ELECTRIC_MACHINERY)
                .add(TFMGBlocks.ELECTRIC_PUMP)
                .add(TFMGBlocks.LARGE_SWITCH)
        ;
        HELPER.addToTag(CHEMICAL_VAT)
                .add(TFMGBlocks.STEEL_CHEMICAL_VAT)
                .add(TFMGBlocks.CAST_IRON_CHEMICAL_VAT)
                .add(TFMGBlocks.FIREPROOF_CHEMICAL_VAT)
                .add(TFMGBlocks.INDUSTRIAL_MIXER)
                .add(TFMGBlocks.ELECTRODE_HOLDER)
                .add(TFMGItems.COPPER_ELECTRODE)
                .add(TFMGItems.ZINC_ELECTRODE)
                .add(TFMGItems.GRAPHITE_ELECTRODE)
                .add(TFMGItems.MIXER_BLADE)
                .add(TFMGItems.CENTRIFUGE)
        ;


    }

}
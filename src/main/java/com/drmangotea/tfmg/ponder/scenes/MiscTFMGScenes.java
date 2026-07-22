package com.drmangotea.tfmg.ponder.scenes;

import com.drmangotea.tfmg.ponder.TFMGSceneBuilder;
import com.drmangotea.tfmg.registry.TFMGBlocks;
import com.drmangotea.tfmg.registry.TFMGItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MiscTFMGScenes {


    public static void chemical_vat(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("chemical_vat", "Chemical Vat");
        scene.configureBasePlate(0, 0, 7);
        scene.scaleSceneView(0.7f);
        scene.showBasePlate();

        Selection chemical_vat = util.select().fromTo(2, 2, 2, 4, 3, 4);
        Selection mixer = util.select().fromTo(3, 4, 3, 3, 4, 3);

        Selection graphiteElectrodes = util.select().fromTo(3, 4, 2, 3, 4, 2)
                .add(util.select().fromTo(4, 4, 4, 4, 4, 4))
                .add(util.select().fromTo(2, 4, 4, 2, 4, 4));

        Selection copperElectrodes = util.select().fromTo(4, 4, 3, 4, 4, 3)
                .add(util.select().fromTo(2, 4, 3, 2, 4, 3));

        Selection truss = util.select().fromTo(2, 1, 2, 2, 1, 2)
                .add(util.select().fromTo(4, 1, 4, 4, 1, 4))
                .add(util.select().fromTo(4, 1, 2, 4, 1, 2))
                .add(util.select().fromTo(2, 1, 4, 2, 1, 4));

        Selection blazeBurners = util.select().fromTo(2, 1, 3, 2, 1, 3)
                .add(util.select().fromTo(3, 1, 2, 3, 1, 2))
                .add(util.select().fromTo(4, 1, 3, 4, 1, 3))
                .add(util.select().fromTo(3, 1, 4, 3, 1, 4));

        ElementLink<WorldSectionElement> vatElement = scene.world().showIndependentSection(chemical_vat, Direction.DOWN);
        scene.world().showIndependentSection(truss, Direction.DOWN);
        scene.world().rotateSection(vatElement, 0, 180, 0, 0);

        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("The chemical vat is a machine with attachments that can give it many different uses");

        scene.idle(90);

        ElementLink<WorldSectionElement> mixerElement = scene.world().showIndependentSection(mixer, Direction.UP);
        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("First attachment is the industrial mixer");
        scene.idle(80);


        Vec3 mixerPos = util.vector().topOf(util.grid().at(3, 4, 3));
        scene.overlay().showControls(mixerPos, Pointing.DOWN, 20)
                .rightClick()
                .withItem(new ItemStack(TFMGItems.MIXER_BLADE.get()));
        scene.idle(30);

        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("When the mixer blade is inserted, the vat becomes a mixer");
        scene.idle(60);

        scene.overlay().showControls(mixerPos, Pointing.DOWN, 20)
                .rightClick()
                .withItem(new ItemStack(TFMGItems.CENTRIFUGE.get()));
        scene.idle(30);

        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("The industrial mixer can also become a centrifuge");
        scene.idle(80);

        scene.world().hideIndependentSection(mixerElement, Direction.UP);

        scene.idle(30);

        ElementLink<WorldSectionElement> burnerElement = scene.world().showIndependentSection(blazeBurners, Direction.DOWN);

        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("Some recipes need heating");

        scene.idle(70);

        ElementLink<WorldSectionElement> electrolyzerElement = scene.world().showIndependentSection(copperElectrodes, Direction.DOWN);


        Vec3 electrodePos1 = util.vector().topOf(util.grid().at(4, 4, 3));
        Vec3 electrodePos2 = util.vector().topOf(util.grid().at(2, 4, 3));
        scene.overlay().showControls(electrodePos1, Pointing.DOWN, 20)
                .rightClick()
                .withItem(new ItemStack(TFMGItems.COPPER_ELECTRODE.get()));
        scene.overlay().showControls(electrodePos2, Pointing.DOWN, 20)
                .rightClick()
                .withItem(new ItemStack(TFMGItems.COPPER_ELECTRODE.get()));
        scene.overlay().showText(40)
                .attachKeyFrame()
                .text("Placing 2 electrode holders with copper electrodes creates an electrolyzer");

        scene.idle(60);

        scene.world().hideIndependentSection(electrolyzerElement, Direction.UP);

        scene.idle(20);
        ElementLink<WorldSectionElement> arcFurnaceElement = scene.world().showIndependentSection(graphiteElectrodes, Direction.DOWN);
        Vec3 furnacePos1 = util.vector().topOf(util.grid().at(4, 4, 2));
        Vec3 furnacePos2 = util.vector().topOf(util.grid().at(4, 4, 4));
        Vec3 furnacePos3 = util.vector().topOf(util.grid().at(2, 4, 4));
        scene.overlay().showControls(furnacePos1, Pointing.DOWN, 20)
                .rightClick()
                .withItem(new ItemStack(TFMGItems.GRAPHITE_ELECTRODE.get()));
        scene.overlay().showControls(furnacePos2, Pointing.DOWN, 20)
                .rightClick()
                .withItem(new ItemStack(TFMGItems.GRAPHITE_ELECTRODE.get()));
        scene.overlay().showControls(furnacePos3, Pointing.DOWN, 20)
                .rightClick()
                .withItem(new ItemStack(TFMGItems.GRAPHITE_ELECTRODE.get()));
        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("3 graphite electrodes create a blast furnace");

        scene.idle(60);

    }

    public static void electricy_two(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("electricity_two", "Electric Subnetworks");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        Selection generator = util.select().fromTo(4, 1, 3, 2, 1, 3);

        Selection light = util.select().fromTo(0, 1, 3, 0, 2, 2);

        Selection potentiometer = util.select().fromTo(1, 1, 1, 1, 1, 1);
        Selection electricSwitch = util.select().fromTo(1, 1, 2, 1, 1, 2);
        Selection diode = util.select().fromTo(1, 1, 3, 1, 1, 3);
        Selection transformer = util.select().fromTo(1, 1, 4, 1, 1, 4);


        ElementLink<WorldSectionElement> generatorElement = scene.world().showIndependentSection(generator, Direction.DOWN);
        ElementLink<WorldSectionElement> lightElement = scene.world().showIndependentSection(light, Direction.DOWN);
        ElementLink<WorldSectionElement> diodeElement = scene.world().showIndependentSection(diode, Direction.DOWN);

        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("Some blocks have connections from 2 sides");
        scene.idle(60);
        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("These blocks create a new electric network on one of their sides");
        scene.idle(60);
        scene.overlay().showText(90)
                .attachKeyFrame()
                .text("This subnetwork will get all the power from the main network but not the opposite way");
        scene.idle(100);

        //DIODE
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("The most basic block with this ability is the Diode, it has no other extra abilities");
        scene.idle(80);
        //POTENTIOMETER
        scene.world().hideIndependentSection(diodeElement, Direction.NORTH);
        ElementLink<WorldSectionElement> potentiometerElement = scene.world().showIndependentSection(potentiometer, Direction.NORTH);
        scene.world().moveSection(potentiometerElement, new Vec3(0d, 0d, 2d), 0);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Next one is the potentiometer, this one can set the percentage of voltage that gets to the subnetwork");
        scene.idle(80);
        //SWITCH
        scene.world().hideIndependentSection(potentiometerElement, Direction.NORTH);
        ElementLink<WorldSectionElement> switchElement = scene.world().showIndependentSection(electricSwitch, Direction.NORTH);
        scene.world().moveSection(switchElement, new Vec3(0d, 0d, 1d), 0);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("The switch works similarly but with redstone");
        scene.idle(80);
        //TRANSFORMER
        scene.world().hideIndependentSection(switchElement, Direction.NORTH);
        ElementLink<WorldSectionElement> transformerElement = scene.world().showIndependentSection(transformer, Direction.NORTH);
        scene.world().moveSection(transformerElement, new Vec3(0d, 0d, -1d), 0);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("The transformer changes voltage based on the ratio of turns between the primary and secondary coil");
        scene.idle(80);

    }


    public static void electricity(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("electricity", "Electricity");
        scene.showBasePlate();


        Selection generator = util.select().fromTo(5, 1, 4, 4, 1, 4);

        Selection light1 = util.select().fromTo(3, 1, 4, 2, 2, 4);
        Selection light2 = util.select().fromTo(2, 1, 3, 2, 2, 2);
        Selection light3 = util.select().fromTo(2, 1, 0, 2, 2, 1);

        scene.world().showIndependentSection(generator, Direction.DOWN);

        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("A generator creates 2 values, Voltage and Power");
        scene.idle(60);


        //-------------------------Phase 1-------------------------//
        scene.world().showIndependentSection(light1, Direction.DOWN);


        scene.overlay().showText(240)
                .attachKeyFrame()
                .text(
                        """
                                Generator:
                                   Voltage(U) = 200V
                                   Max Power = 8kW
                                Light Bulb:
                                   Voltage(U) = 200V
                                   Current(I) = 2A
                                   Power(P) = 100W
                                   Resistance(R) = 100Ω"""
                )
                .independent(50)
                .colored(PonderPalette.BLUE);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("When load is applied on a generator, it takes its voltage");
        scene.idle(80);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Every an electric device has electrical resistance, light bulbs are 100 Ohm(Ω)");
        scene.idle(80);
        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Current with size of Voltage divided by Resistance will start flowing in the light bulb");

        scene.idle(80);
        //-------------------------Phase 2-------------------------//
        scene.world().showIndependentSection(light2, Direction.DOWN);
        scene.overlay().showText(180)
                .attachKeyFrame()
                .text(
                        """
                                Generator:
                                   Voltage(U) = 200V
                                   Max Power = 8kW
                                Light Bulb 1:
                                   Voltage(U) = 100V
                                   Current(I) = 1A
                                   Resistance(R) = 100Ω
                                   Group=0
                                Light Bulb 2:
                                   Voltage(U) = 100V
                                   Current(I) = 1A
                                   Resistance(R) = 100Ω
                                   Group=0"""
                )
                .independent(50)
                .colored(PonderPalette.BLUE);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Electric components can be connected with groups, by default all blocks are group 0");
        scene.idle(90);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Blocks that share a group split voltage between them(blocks with higher resistance get more of the split voltage)");
        scene.idle(90);
        //-------------------------Phase 3-------------------------//
        scene.overlay().showText(180)
                .attachKeyFrame()
                .text(
                        """
                                Generator:
                                   Voltage(U) = 200V
                                   Max Power = 8kW
                                Light Bulb 1:
                                   Voltage(U) = 200V
                                   Current(I) = 2A
                                   Resistance(R) = 100Ω
                                   Group=1
                                Light Bulb 2:
                                   Voltage(U) = 200V
                                   Current(I) = 2A
                                   Resistance(R) = 100Ω
                                   Group=0"""
                )
                .independent(50)
                .colored(PonderPalette.BLUE);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Groups can be changed using the Configuration Wrench");
        scene.idle(90);
        scene.overlay().showText(80)
                .attachKeyFrame()
                .text("Blocks in their own group keep all the voltage");
        scene.idle(90);


    }

    public static void engines(SceneBuilder builder, SceneBuildingUtil util) {
        TFMGSceneBuilder scene = new TFMGSceneBuilder(builder);
        scene.title("engines", "Engines");
        scene.configureBasePlate(0, 0, 7);


        scene.showBasePlate();


        Selection engine = util.select().fromTo(3, 1, 3, 3, 1, 4);
        Selection engine_front = util.select().fromTo(3, 1, 2, 3, 1, 2);
        Selection engine_front_shaft = util.select().fromTo(2, 1, 2, 2, 1, 2);

        Selection lever = util.select().fromTo(4, 1, 2, 4, 1, 2);
        Selection cog = util.select().fromTo(3, 1, 0, 3, 1, 1);

        Selection fuelTank = util.select().fromTo(4, 1, 3, 5, 2, 3);
        Selection tank = util.select().fromTo(4, 1, 4, 4, 2, 4);
        Selection exhaust = util.select().fromTo(2, 1, 4, 1, 2, 4);
        scene.world().setKineticSpeed(fuelTank, 70);
        scene.world().setKineticSpeed(exhaust, 70);
        scene.world().setKineticSpeed(cog, 70);


        ElementLink<WorldSectionElement> engineElement = scene.world().showIndependentSection(engine, Direction.DOWN);
        ElementLink<WorldSectionElement> engineFrontElement = scene.world().showIndependentSection(engine_front, Direction.DOWN);

        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("To build an engine, start by placing up to 5 engine blocks in a line");

        scene.idle(70);

        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("The engine's tooltip will show you items needed for the assembly");
        scene.idle(60);

        Vec3 enginePos = util.vector().topOf(util.grid().at(3, 1, 2));
        scene.overlay().showControls(enginePos, Pointing.DOWN, 15)
                .rightClick()
                .withItem(new ItemStack(TFMGItems.CRANKSHAFT.get()));
        scene.idle(25);
        scene.overlay().showControls(enginePos, Pointing.DOWN, 15)
                .rightClick()
                .withItem(new ItemStack(TFMGBlocks.STEEL_COGWHEEL.get()));
        scene.idle(25);
        scene.overlay().showControls(enginePos, Pointing.DOWN, 15)
                .rightClick()
                .withItem(new ItemStack(TFMGBlocks.LARGE_STEEL_COGWHEEL.get()));
        scene.idle(40);

        scene.overlay().showControls(enginePos, Pointing.DOWN, 50)
                .rightClick()
                .withItem(new ItemStack(AllItems.EMPTY_SCHEMATIC.get()));


        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("Engine configuration can be changed with a schematic");
        scene.idle(60);


        scene.overlay().showText(60)
                .attachKeyFrame()
                .text("Next step is inserting the cylinders (or turbine blades in turbine engines)");

        ElementLink<WorldSectionElement> engineFrontShaftElement = scene.world().showIndependentSection(engine_front_shaft, Direction.DOWN);
        scene.world().moveSection(engineFrontShaftElement, new Vec3(1d, -2d, 0d), 0);

        BlockPos pos = util.grid().at(3, 1, 2);
        for (int i = 0; i < 12; i++) {
            scene.idle(5);
            scene.tfmgInstructions().addPistonToEngine(pos);

            if (i == 3 || i == 7)
                pos = pos.south();
        }
        scene.idle(35);
        scene.world().moveSection(engineFrontShaftElement, new Vec3(0d, 2d, 0d), 0);
        scene.world().moveSection(engineFrontElement, new Vec3(0d, -2d, 0d), 0);
        //scene.world().hideIndependentSection(engineFrontElement,Direction.DOWN);


        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("Shafts are inserted by right clicking");
        scene.overlay().showControls(enginePos, Pointing.DOWN, 50)
                .rightClick()
                .withItem(new ItemStack(AllBlocks.SHAFT));
        scene.idle(70);

        ElementLink<WorldSectionElement> fuelTankElement = scene.world().showIndependentSection(fuelTank, Direction.DOWN);
        scene.idle(10);
        ElementLink<WorldSectionElement> exhuastElement = scene.world().showIndependentSection(exhaust, Direction.DOWN);

        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("Fuel input and exhaust output can be done from any block");
        scene.idle(70);

        scene.overlay().showText(50)
                .attachKeyFrame()

                .text("Every engine block can be right clicked with certain items to be upgraded");
        scene.idle(60);

        scene.overlay().showControls(util.vector().topOf(util.grid().at(3, 1, 4)), Pointing.DOWN, 40)
                .rightClick()
                .withItem(new ItemStack(TFMGBlocks.INDUSTRIAL_PIPE));

        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("For example industrial pipes make the engine consume fuel from neighboring tanks");
        scene.idle(80);
        scene.world().showIndependentSection(tank, Direction.DOWN);
        scene.idle(30);
        scene.world().setKineticSpeed(engine_front_shaft, 70);
        scene.world().showIndependentSection(lever, Direction.DOWN);
        scene.world().showIndependentSection(cog, Direction.DOWN);
        scene.overlay().showText(128)
                .attachKeyFrame()
                .text("The engine can be started with a redstone signal");
    }


    public static void distillation_tower(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("distillation_tower", "");
        scene.configureBasePlate(0, 0, 6);
        scene.showBasePlate();
        scene.scaleSceneView(.6f);
        Selection burners = util.select().fromTo(3, 1, 3, 4, 1, 4);
        Selection tank = util.select().fromTo(3, 2, 3, 4, 8, 4);
        Selection controller = util.select().fromTo(3, 1, 2, 3, 2, 2);
        Selection output = util.select().fromTo(3, 3, 2, 3, 8, 2);
        Selection oil_tank = util.select().fromTo(0, 1, 0, 2, 3, 4);
        scene.world().setKineticSpeed(oil_tank, 80);

        ElementLink<WorldSectionElement> tankElement = scene.world().showIndependentSection(tank, Direction.DOWN);

        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("The base of a distillation tower is comprised of steel tanks")
                .pointAt(util.vector().blockSurface(util.grid().at(4, 6, 3), Direction.WEST))
                .placeNearTarget();
        scene.idle(80);
        ElementLink<WorldSectionElement> controllerElement = scene.world().showIndependentSection(controller, Direction.DOWN);

        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("Tower is assembled by placing Steel Distillation Controller next to the tanks")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 3), Direction.WEST))
                .placeNearTarget();

        scene.idle(70);
        ElementLink<WorldSectionElement> outputElement = scene.world().showIndependentSection(output, Direction.DOWN);
        scene.overlay().showText(60)
                .attachKeyFrame()
                .text("To finish the multiblock, place up to 6 Distillation outputs and Industrial Pipes between them")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 7, 3), Direction.WEST))
                .placeNearTarget();
        scene.idle(70);


        ElementLink<WorldSectionElement> burnerElement = scene.world().showIndependentSection(burners, Direction.DOWN);
        scene.overlay().showText(60)
                .attachKeyFrame()
                .text("Place a heat source under the tanks to power it, the dial on the tower shows the power level of the structure ")
                .pointAt(util.vector().blockSurface(util.grid().at(3, 1, 2), Direction.WEST))
                .placeNearTarget();
        scene.idle(70);

        scene.world().showIndependentSection(oil_tank, Direction.DOWN);

        scene.overlay().showText(60)
                .attachKeyFrame()
                .text("Oil is inputted into the controller block")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 2, 3), Direction.WEST))
                .placeNearTarget();
        scene.idle(80);


    }

    public static void radial_engines(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("radial_engines", "");
        scene.configureBasePlate(0, 0, 5);
        scene.idle(10);

        scene.showBasePlate();


        Selection engine_small = util.select().fromTo(2, 1, 1, 2, 1, 1);

        Selection engine_large = util.select().fromTo(1, 1, 1, 1, 1, 1);


        Selection engine_lever = util.select().fromTo(3, 1, 0, 3, 1, 0);


        Selection input_pump = util.select().fromTo(3, 1, 2, 3, 1, 2);

        Selection input = util.select().fromTo(3, 1, 1, 3, 1, 1);

        Selection tank_1 = util.select().fromTo(3, 1, 3, 3, 2, 3);

        Selection tank_2 = util.select().fromTo(2, 1, 3, 2, 2, 3);


        scene.world().setKineticSpeed(engine_small, 0);


        ElementLink<WorldSectionElement> engineElement = scene.world().showIndependentSectionImmediately(engine_small);
        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("Radial Engines are a special Type of Engine that doesn't require an exhaust block")
                .pointAt(util.vector().blockSurface(util.grid().at(4, 0, 4), Direction.WEST))
                .placeNearTarget();
        scene.idle(100);


        scene.world().setKineticSpeed(input_pump, 80);
        ElementLink<WorldSectionElement> inputElement = scene.world().showIndependentSection(input, Direction.DOWN);
        scene.idle(50);

        BlockPos inputPos = util.grid().at(2, 1, 1);
        Vec3 topOf = util.vector().topOf(inputPos);
        scene.overlay().showControls(topOf, Pointing.DOWN, 20)
                .rightClick()
                .withItem(new ItemStack(AllItems.WRENCH.get()));

        scene.overlay().showText(70)
                .attachKeyFrame()
                .text("Clicking the Engine from one of its sides will spawn an input slot that can accept fuel and redstone signals")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 1), Direction.WEST))
                .placeNearTarget();
        scene.idle(100);
        scene.overlay().showText(40)
                .attachKeyFrame()
                .text("Regular Radial Engines uses gasoline as fuel")
                .pointAt(util.vector().blockSurface(util.grid().at(2, 1, 1), Direction.WEST))
                .placeNearTarget();


        scene.idle(80);


        ElementLink<WorldSectionElement> inputPumpElement = scene.world().showIndependentSection(input_pump, Direction.DOWN);
        ElementLink<WorldSectionElement> tankElement1 = scene.world().showIndependentSection(tank_1, Direction.DOWN);


        ElementLink<WorldSectionElement> leverElement = scene.world().showIndependentSection(engine_lever, Direction.DOWN);
        scene.world().setKineticSpeed(engine_small, 180);
        scene.world().setKineticSpeed(engine_large, 180);

        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("Engine will start when redstone signal is applied to the input slot or the block itself")
                .pointAt(util.vector().blockSurface(util.grid().at(3, 1, 0), Direction.WEST))
                .placeNearTarget();

        scene.idle(100);

        scene.world().hideIndependentSection(engineElement, Direction.SOUTH);
        scene.world().hideIndependentSection(tankElement1, Direction.SOUTH);

        scene.idle(50);

        ElementLink<WorldSectionElement> largeEngineElement = scene.world().showIndependentSection(engine_large, Direction.DOWN);
        ElementLink<WorldSectionElement> tankElement2 = scene.world().showIndependentSection(tank_2, Direction.DOWN);
        scene.world().moveSection(largeEngineElement, new Vec3(1d, 0d, 0d), 0);
        scene.world().moveSection(tankElement2, new Vec3(1d, 0d, 0d), 0);

        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("The second variant of a radial is The Large Radial Engine which uses kerosene as fuel");
        scene.idle(50);
    }


    public static void large_generator(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("large_generator", "");
        scene.configureBasePlate(0, 0, 7);
        scene.showBasePlate();

        Selection stator = util.select().fromTo(3, 1, 5, 5, 3, 5);
        Selection rotor = util.select().fromTo(4, 2, 3, 4, 2, 3);
        Selection kinetics1 = util.select().fromTo(4, 1, 1, 6, 2, 2);

        Selection kinetics2 = util.select().fromTo(6, 1, 3, 6, 1, 3);
        Selection cables = util.select().fromTo(1, 1, 3, 2, 2, 6);

        scene.world().setKineticSpeed(kinetics1, 120);
        scene.world().setKineticSpeed(kinetics2, 120);
        //scene.world().setKineticSpeed(rotor,120);
        scene.world().showIndependentSection(rotor, Direction.DOWN);

        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("The main part of the Large Generator is the Rotor")
                .pointAt(util.vector().blockSurface(util.grid().at(4, 2, 5), Direction.WEST))
                .placeNearTarget();

        scene.idle(80);

        ElementLink<WorldSectionElement> statorElement = scene.world().showIndependentSection(stator, Direction.DOWN);


        scene.world().moveSection(statorElement, new Vec3(0d, 0d, -2d), 0);

        scene.overlay().showText(75)
                .attachKeyFrame()
                .text("To complete the Large Generator, place a Stator block around the Rotor")
                .pointAt(util.vector().blockSurface(util.grid().at(3, 2, 3), Direction.WEST))
                .placeNearTarget();


        scene.idle(105);

        scene.world().showIndependentSection(kinetics1, Direction.DOWN);
        scene.world().showIndependentSection(kinetics2, Direction.DOWN);

        scene.world().setKineticSpeed(rotor, 120);
        scene.overlay().showText(65)
                .attachKeyFrame()
                .text("Providing rotational power to the Rotor will produce electric energy")
                .pointAt(util.vector().blockSurface(util.grid().at(4, 2, 3), Direction.WEST))
                .placeNearTarget();


        scene.idle(95);


        BlockPos pos = util.grid().at(3, 2, 3);
        Vec3 topOf = util.vector().topOf(pos);
        scene.overlay().showControls(topOf, Pointing.DOWN, 20).rightClick()
                .withItem(new ItemStack(AllItems.WRENCH.get()));


        scene.overlay().showText(60)
                .attachKeyFrame()
                .text("Clicking a side with a wrench will make it the energy output");

        scene.idle(20);
        scene.world().showIndependentSection(cables, Direction.DOWN);
        scene.idle(50);

    }
}

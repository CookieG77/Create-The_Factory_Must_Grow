package com.drmangotea.tfmg.config;


import net.createmod.catnip.config.ConfigBase;

public class MachineConfig extends ConfigBase {

    public final ConfigInt fireExtinguisherClearRadius = i(1, 0, "fireExtinguisherClearRadius", Comments.fireExtinguisherClearRadius);

    public final ConfigInt cokeOvenMaxSize = i(5, 1, "cokeOvenMaxSize", Comments.cokeOvenMaxSize);

    public final ConfigInt electrolysisFEUsage = i(4048, 1, "electrolysisFEUsage", Comments.electrolysisFEUsage);
    public final ConfigInt freezerFEUsage = i(4048, 1, "freezerFEUsage", Comments.freezerFEUsage);
    public final ConfigInt engineMaxLength = i(5, 1, "engineMaxLength", Comments.engineMaxLength);
    public final ConfigInt surfaceScannerScanDepth = i(-64, -512, "surfaceScannerScanDepth", Comments.surfaceScannerScanDepth);

    public final ConfigGroup electric_pump = group(1, "electric_pump", "Electric Pump");
    public final ConfigInt electricPumpFEUsage = i(4048, 1, "electricPumpFEUsage", Comments.electricPumpFEUsage);
    public final ConfigFloat electricPumpStrengthMultiplier = f(2.0f, 0, "electricPumpStrengthMultiplier", Comments.electricPumpStrengthMultiplier);

    public final ConfigGroup firebox = group(1, "firebox", "Firebox");
    public final ConfigBool fireboxExhaustRequirement = b(true, "fireboxExhaustRequirement", Comments.fireboxExhaustRequirement);
    public final ConfigInt fireboxFuelConsumption = i(100, 1, "fireboxFuelConsumption", Comments.fireboxFuelConsumption);

    public final ConfigGroup engines = group(1, "engines", "Engines");
    public final ConfigFloat engineLoudness = f(1,0, "engineLoudness", Comments.engineLoudness);
    public final ConfigFloat engineFuelConsumption = f(100,0, "engineFuelConsumption", Comments.engineFuelConsumption);
    public final ConfigFloat enginePower = f(100,0, "enginePower", Comments.enginePower);


    public final ConfigGroup generators = group(1, "generators", "Generators");
    public final ConfigFloat largeGeneratorModifier = f(4, 0, "largeGeneratorModifier", Comments.largeGenerator);
    public final ConfigFloat largeGeneratorMinSpeed = f(70, 0, "largeGeneratorMinSpeed", Comments.largeGeneratorMinSpeed);
    //
    public final ConfigFloat generatorModifier = f(1.4f, 0, "GeneratorModifier", Comments.generator);
    public final ConfigFloat generatorMinSpeed = f(40, 0, "generatorMinSpeed", Comments.generatorMinSpeed);

    public final ConfigGroup blast_furnace = group(1, "blast_furnace", "Blast Furnace");
    public final ConfigInt blastFurnaceMaxHeight = i(10, 3, "blastFurnaceMaxHeight", Comments.blastFurnaceHeight);
    public final ConfigFloat blastFurnaceHeightSpeedModifier = f(1f, 0.1f, "blastFurnaceHeightSpeedModifier", Comments.blastFurnaceHeightSpeedModifier);
    public final ConfigInt blastFurnaceFuelConsumption = i(600, 1, "blastFurnaceFuelConsumption", Comments.blastFurnaceFuelConsumption);

    @Override
    public String getName() {
        return "machines";
    }


    private static class Comments {
        static String fireExtinguisherClearRadius = "Changes the radius fire extinguishers can remove fire in.";
        static String largeGenerator = "Determines how powerful the large generator is.";
        static String generator = "Determines how powerful the generator is.";
        static String largeGeneratorMinSpeed = "Changes the lowest speed the large generator can work on.";
        static String generatorMinSpeed = "Changes the lowest speed the generator can work on.";
        static String blastFurnaceHeight = "Changes the maximum height of the blast furnace.";
        static String blastFurnaceHeightSpeedModifier = "Sets the maximum time that can be saved by increasing blast furnace height.";
        static String blastFurnaceFuelConsumption = "Determines how many ticks does it take to consume one fuel.";
        static String cokeOvenMaxSize = "Determines the maximum size of coke ovens.";
        static String fireboxExhaustRequirement = "If set to true,fireboxes will require exhaust management.";
        static String fireboxFuelConsumption = "Determines the amount of fuel a firebox needs to run for 3 seconds.";
        static String electrolysisFEUsage = "The Forge Energy per tick required to make electrode holders operational (at full work speed).";
        static String freezerFEUsage = "The Forge Energy per tick required to make freezers operational.";
        static String electricPumpFEUsage = "The Forge Energy per tick the electric pump needs for full strength.";
        static String electricPumpStrengthMultiplier = "Strength of the electric pump relative to Create's mechanical pump (range and pressure).";
        static String engineMaxLength = "The maximum length of engines.";
        static String engineFuelConsumption = "Modifier of engine fuel consumption in %.";
        static String enginePower = "Modifier of engine stress capacity in %.";
        static String surfaceScannerScanDepth = "Y level surface scanner scan at.";
        static String engineLoudness = "Changes the volume of engines.";
    }
}

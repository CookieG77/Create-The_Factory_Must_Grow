package com.drmangotea.tfmg.config;



public class TFMGCommonConfig extends net.createmod.catnip.config.ConfigBase {

    public final MachineConfig machines = nested(0, MachineConfig::new, "Config options for TFMG's machinery");

    @Override
    public String getName() {
        return "common";
    }


}

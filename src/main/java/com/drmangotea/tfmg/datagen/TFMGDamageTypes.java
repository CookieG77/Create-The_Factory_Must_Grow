package com.drmangotea.tfmg.datagen;


import com.drmangotea.tfmg.TFMG;
import com.simibubi.create.foundation.damageTypes.DamageTypeBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class TFMGDamageTypes {
	public static final ResourceKey<DamageType>
			CONCRETE = key("concrete"),
			ACID = key("acid"),
			BLAST_FURNACE = key("blast_furnace"),
			PIPE_BOMB_TRAP = key("pipe_bomb_trap");

	private static ResourceKey<DamageType> key(String name) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, TFMG.asResource(name));
	}

	public static void bootstrap(BootstrapContext<DamageType> ctx) {
		new DamageTypeBuilder(CONCRETE).register(ctx);
		new DamageTypeBuilder(ACID).register(ctx);
		new DamageTypeBuilder(BLAST_FURNACE).register(ctx);
		new DamageTypeBuilder(PIPE_BOMB_TRAP).register(ctx);
	}
}

package com.drmangotea.tfmg.registry;

import java.util.function.UnaryOperator;

import com.drmangotea.tfmg.TFMG;
import com.drmangotea.tfmg.content.items.weapons.flamethrover.FlamethrowerFuel;
import org.jetbrains.annotations.ApiStatus.Internal;

import com.mojang.serialization.Codec;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TFMGDataComponents {
	private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, TFMG.MOD_ID);

	public static final DataComponentType<CompoundTag> FUELS = register(
			"fuels",
			builder -> builder.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
	);
	public static final DataComponentType<CompoundTag> FUEL_TAGS = register(
			"fuel_tags",
			builder -> builder.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
	);
	public static final DataComponentType<Integer> SPOOL_AMOUNT = register(
			"spool_amount",
			builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
	);
	public static final DataComponentType<Integer> COIL_TURNS = register(
			"coil_turns",
			builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
	);
	public static final DataComponentType<Integer> CONFIGURATION_WRENCH_NUMBER = register(
			"number",
			builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT)
	);
	public static final DataComponentType<Integer> LITHIUM_BLADE_TIMER = register(
			"timer",
			builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT)
	);
	/**
	 * Ticks remaining until a renamed (disguised) Pipe Bomb detonates. Absent = fuse not started
	 * yet (still safely nested inside a Package, or never packaged at all). Set automatically the
	 * moment a {@link #PIPE_BOMB_PACKAGED} pipe bomb is observed live outside of Package storage.
	 */
	public static final DataComponentType<Integer> PIPE_BOMB_TRAP_TIMER = register(
			"pipe_bomb_trap_timer",
			builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT)
	);
	/**
	 * The fuse length (in ticks) randomly rolled for this specific bomb when it armed - stored
	 * so progress (for the countdown bar/text color) can be computed relative to its own fuse
	 * rather than a single fixed duration.
	 */
	public static final DataComponentType<Integer> PIPE_BOMB_TRAP_TOTAL_TICKS = register(
			"pipe_bomb_trap_total_ticks",
			builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT)
	);
	/**
	 * Set on the copy sealed into a Package when a renamed pipe bomb is wrapped with cardboard.
	 * Marks it as "safe only while inside a Package" - the instant it's live again (however it
	 * got out - opened normally, the package was destroyed, anything), its fuse starts.
	 */
	public static final DataComponentType<Boolean> PIPE_BOMB_PACKAGED = register(
			"pipe_bomb_packaged",
			builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
	);
	/**
	 * Set by combining a renamed pipe bomb with a slimeball. Once its fuse is running, the
	 * holder can no longer toss/drop it.
	 */
	public static final DataComponentType<Boolean> PIPE_BOMB_STICKY = register(
			"pipe_bomb_sticky",
			builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL)
	);
	public static final DataComponentType<FlamethrowerFuel> FLAMETHROWER = register(
			"flamethrower",
			builder -> builder.persistent(FlamethrowerFuel.CODEC).networkSynchronized(FlamethrowerFuel.STREAM_CODEC)
	);
	public static final DataComponentType<String> FLAMETHROWER_FUEL = register(
			"flamethrower_fuel",
			builder -> builder.persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8)
	);
	public static final DataComponentType<Integer> ACCUMULATOR_STORAGE = register(
			"storage",
			builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT)
	);

	public static final DataComponentType<Integer> RESISTANCE = register(
			"resistance",
			builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT)
	);
	public static final DataComponentType<Integer> AMOUNT = register(
			"amount",
			builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT)
	);
	public static final DataComponentType<Long> POSITION = register(
			"position",
			builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG)
	);

	public static final DataComponentType<Integer> X_POS = register(
			"x_pos",
			builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT)
	);
	public static final DataComponentType<Integer> Y_POS = register(
			"y_pos",
			builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT)
	);
	public static final DataComponentType<Integer> Z_POS = register(
			"z_pos",
			builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT)
	);



	private static <T> DataComponentType<T> register(String name, UnaryOperator<Builder<T>> builder) {
		DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
		DATA_COMPONENTS.register(name, () -> type);
		return type;
	}

	@Internal
	public static void register(IEventBus modEventBus) {
		DATA_COMPONENTS.register(modEventBus);
	}
}

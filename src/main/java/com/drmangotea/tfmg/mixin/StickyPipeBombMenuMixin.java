package com.drmangotea.tfmg.mixin;

import com.drmangotea.tfmg.registry.TFMGDataComponents;
import com.drmangotea.tfmg.registry.TFMGItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A triggered, slime-combined pipe bomb is stuck in whatever slot it's in - it can't be picked
 * up, shift-clicked, or dragged out of the player's inventory at all (not just tossed - see
 * TFMGCommonEvents#onPipeBombToss for the plain Q-drop case, which this doesn't cover).
 */
@Mixin(AbstractContainerMenu.class)
public class StickyPipeBombMenuMixin {

	@Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
	private void tfmg$blockStickyBombMove(int slotId, int button, ClickType clickType, Player player, CallbackInfo ci) {
		AbstractContainerMenu self = (AbstractContainerMenu) (Object) this;
		if (slotId < 0 || slotId >= self.slots.size())
			return;

		Slot slot = self.slots.get(slotId);
		if (slot == null || !slot.hasItem())
			return;

		ItemStack stack = slot.getItem();
		if (!stack.is(TFMGItems.PIPE_BOMB.get()))
			return;
		if (!stack.has(TFMGDataComponents.PIPE_BOMB_STICKY) || !stack.has(TFMGDataComponents.PIPE_BOMB_TRAP_TIMER))
			return;

		if (!player.level().isClientSide)
			player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
					SoundEvents.SLIME_SQUISH, SoundSource.PLAYERS, 0.6F, 1F);
		ci.cancel();
	}
}

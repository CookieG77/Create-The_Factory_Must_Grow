package com.drmangotea.tfmg.base.events;

import com.drmangotea.tfmg.TFMGClient;
import com.drmangotea.tfmg.content.items.weapons.advanced_potato_cannon.AdvancedPotatoCannonItemRenderer;
import com.drmangotea.tfmg.content.items.weapons.explosives.pipe_bomb.PipeBombItem;
import com.drmangotea.tfmg.content.items.weapons.quad_potato_cannon.QuadPotatoCannonItemRenderer;
import com.drmangotea.tfmg.registry.TFMGDataComponents;
import com.drmangotea.tfmg.registry.TFMGItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;


@EventBusSubscriber(Dist.CLIENT)
public class TFMGClientEvents {


	@SubscribeEvent
	public static void onTickPre(ClientTickEvent.Pre event) {
		onTick( true);
	}

	@SubscribeEvent
	public static void onTickPost(ClientTickEvent.Post event) {
		onTick(false);
	}


	public static void onTick(boolean isPreEvent) {
		if (!isGameActive())
			return;

		TFMGClient.QUAD_POTATO_CANNON_RENDER_HANDLER.tick();
		TFMGClient.ADVANCED_POTATO_CANNON_RENDER_HANDLER.tick();
		TFMGClient.FLAMETHROWER_RENDER_HANDLER.tick();
	}
	@SubscribeEvent
	public static void onRenderHotbarOverlay(RenderGuiLayerEvent.Post event) {
		if (!VanillaGuiLayers.HOTBAR.equals(event.getName()))
			return;
		if (!isGameActive())
			return;

		Player localPlayer = Minecraft.getInstance().player;
		Level level = Minecraft.getInstance().level;
		ItemStack bomb = findDisplayedPipeBomb(level, localPlayer);
		if (bomb.isEmpty())
			return;

		int timer = bomb.get(TFMGDataComponents.PIPE_BOMB_TRAP_TIMER);
		Integer total = bomb.get(TFMGDataComponents.PIPE_BOMB_TRAP_TOTAL_TICKS);
		String text = PipeBombItem.trapDisplayText(timer);
		int color = PipeBombItem.trapColor(timer, total == null ? PipeBombItem.MIN_FUSE_TICKS : total);

		GuiGraphics gui = event.getGuiGraphics();
		Font font = Minecraft.getInstance().font;
		int x = (gui.guiWidth() - font.width(text)) / 2;
		int y = gui.guiHeight() - 70;
		gui.drawString(font, text, x, y, color, true);
	}

	private static final double WARNING_RADIUS = 10;

	/**
	 * Which armed pipe bomb gets shown: first the local player's own inventory, checked in slot
	 * order (hotbar 0-8, then offhand) - not whichever is soonest to detonate, just the first
	 * slot that has one. Only if none of those do, fall back to the closest one lying on the
	 * ground within {@link #WARNING_RADIUS} blocks.
	 */
	private static ItemStack findDisplayedPipeBomb(Level level, Player localPlayer) {
		for (int i = 0; i < 9; i++) {
			ItemStack stack = localPlayer.getInventory().getItem(i);
			if (isArmedPipeBomb(stack))
				return stack;
		}
		if (isArmedPipeBomb(localPlayer.getOffhandItem()))
			return localPlayer.getOffhandItem();

		AABB searchBox = localPlayer.getBoundingBox().inflate(WARNING_RADIUS);
		ItemEntity closest = null;
		double closestDistanceSq = Double.MAX_VALUE;
		for (Entity entity : level.getEntities(null, searchBox)) {
			if (!(entity instanceof ItemEntity itemEntity) || !isArmedPipeBomb(itemEntity.getItem()))
				continue;
			double distanceSq = itemEntity.distanceToSqr(localPlayer);
			if (distanceSq < closestDistanceSq) {
				closestDistanceSq = distanceSq;
				closest = itemEntity;
			}
		}
		return closest == null ? ItemStack.EMPTY : closest.getItem();
	}

	private static boolean isArmedPipeBomb(ItemStack stack) {
		return stack.is(TFMGItems.PIPE_BOMB.get()) && stack.has(TFMGDataComponents.PIPE_BOMB_TRAP_TIMER);
	}

	@SubscribeEvent
	public static void PlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
		Player player = event.getEntity();

		if (player != null)
			player.getPersistentData().remove("IsUsingEngineController");
	}

	protected static boolean isGameActive() {
		return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
	}

	@EventBusSubscriber(value = Dist.CLIENT)
	public static class ModBusEvents {
		@SubscribeEvent
		public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
			event.register(TFMGItems.QUAD_POTATO_CANNON, QuadPotatoCannonItemRenderer.DECORATOR);
			event.register(TFMGItems.ADVANCED_POTATO_CANNON, AdvancedPotatoCannonItemRenderer.DECORATOR);
		}
	}
}

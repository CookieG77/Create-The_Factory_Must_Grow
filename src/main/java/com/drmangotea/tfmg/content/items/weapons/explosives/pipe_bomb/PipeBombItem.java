package com.drmangotea.tfmg.content.items.weapons.explosives.pipe_bomb;

import com.drmangotea.tfmg.datagen.TFMGDamageSources;
import com.drmangotea.tfmg.registry.TFMGDataComponents;
import com.drmangotea.tfmg.registry.TFMGEntityTypes;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.box.PackageItem;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public class PipeBombItem extends Item {

    /**
     * Fuse length is randomized per-bomb between these two, at 20 ticks/sec (5s-10s).
     */
    public static final int MIN_FUSE_TICKS = 100;
    public static final int MAX_FUSE_TICKS = 200;
    /**
     * Shared by both the thrown pipe bomb and the disguised trap. Deliberately punchy since
     * neither ever damages blocks ({@link Level.ExplosionInteraction#NONE}) - this only scales
     * entity damage.
     */
    public static final float EXPLOSION_POWER = 6F;
    public static final int MAX_PER_PACKAGE = 5;
    /**
     * Sampled from vanilla's slime_ball.png texture (most common opaque pixel color).
     */
    private static final int SLIME_BALL_COLOR = 0x76BE6D;

    /**
     * Set for the duration of an explode() call below so the ExplosionEvent.Detonate listener
     * (see TFMGCommonEvents) can tell "this is one of ours" apart from any other explosion and
     * strip out every affected entity that isn't a player.
     */
    private static boolean explodingNow = false;

    public static boolean isPlayerOnlyExplosion() {
        return explodingNow;
    }

    /**
     * Explosion helper shared by the thrown pipe bomb and the disguised trap: same damage type
     * (tfmg:pipe_bomb_trap, so ExplosionEvent.Detonate can restrict it to players only), same
     * power, never damages blocks.
     */
    public static void explode(Level level, Entity source, double x, double y, double z) {
        DamageSource damageSource = TFMGDamageSources.pipeBombTrap(level);
        explodingNow = true;
        try {
            level.explode(source, damageSource, null, x, y, z, EXPLOSION_POWER, false, Level.ExplosionInteraction.NONE);
        } finally {
            explodingNow = false;
        }
    }

    public PipeBombItem(Properties p_41383_) {
        super(p_41383_);
    }

    public InteractionResultHolder<ItemStack> use(Level p_43142_, Player p_43143_, InteractionHand p_43144_) {
        ItemStack itemstack = p_43143_.getItemInHand(p_43144_);

        // A lit fuse can't be thrown away, sticky or not - whoever's holding it is stuck with
        // it until it goes off.
        if (itemstack.has(TFMGDataComponents.PIPE_BOMB_TRAP_TIMER))
            return InteractionResultHolder.fail(itemstack);

        // Two disguise-prep interactions, both gated on the bomb being renamed (so a normal
        // pipe bomb always just throws, even if the offhand happens to hold cardboard/slime).
        if (p_43144_ == InteractionHand.MAIN_HAND && itemstack.has(DataComponents.CUSTOM_NAME)
                && !itemstack.has(TFMGDataComponents.PIPE_BOMB_PACKAGED)) {
            ItemStack offhand = p_43143_.getOffhandItem();

            // Combine with a slimeball to make it "sticky" - once its fuse is running, whoever
            // is holding it can't get rid of it by dropping it.
            if (offhand.is(Items.SLIME_BALL)) {
                if (!p_43142_.isClientSide) {
                    itemstack.set(TFMGDataComponents.PIPE_BOMB_STICKY, true);
                    // Swaps the item model to the slime-covered texture - see the "overrides"
                    // entry in pipe_bomb_3d.json.
                    itemstack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
                    offhand.shrink(1);
                    p_43142_.playSound(null, p_43143_.getX(), p_43143_.getY(), p_43143_.getZ(),
                            SoundEvents.SLIME_SQUISH, SoundSource.PLAYERS, 0.6F, 1F);
                }
                return InteractionResultHolder.sidedSuccess(itemstack, p_43142_.isClientSide());
            }

            // Wrap it into a Create Package by holding cardboard in the offhand - up to 5 bombs
            // at once, if that many identical (same name/prep) ones are stacked in hand.
            if (offhand.is(AllItems.CARDBOARD.get())) {
                if (p_43142_.isClientSide)
                    return InteractionResultHolder.sidedSuccess(itemstack, true);

                int packCount = Math.min(itemstack.getCount(), MAX_PER_PACKAGE);
                ItemStack sealed = itemstack.copyWithCount(packCount);
                sealed.set(TFMGDataComponents.PIPE_BOMB_PACKAGED, true);
                ItemStackHandler contents = new ItemStackHandler(9);
                contents.setStackInSlot(0, sealed);
                ItemStack box = PackageItem.containing(contents);

                offhand.shrink(1);
                itemstack.shrink(packCount);

                p_43142_.playSound(null, p_43143_.getX(), p_43143_.getY(), p_43143_.getZ(),
                        SoundEvents.BUNDLE_INSERT, SoundSource.PLAYERS, 0.6F, 1F);

                // Whatever InteractionResultHolder we return here is what the game will place
                // back into this hand once use() returns - so the box has to BE that return
                // value, not something we hand out separately, or it gets clobbered the instant
                // this method exits.
                if (itemstack.isEmpty())
                    return InteractionResultHolder.sidedSuccess(box, false);

                // Only reachable if the player had multiple pipe bombs stacked - the hand keeps
                // the leftover bombs, so the box has to go into the inventory instead.
                if (!p_43143_.getInventory().add(box))
                    p_43143_.drop(box, false);
                return InteractionResultHolder.sidedSuccess(itemstack, false);
            }
        }

        p_43143_.getCooldowns().addCooldown(this, 60);
        p_43142_.playSound((Player)null, p_43143_.getX(), p_43143_.getY(), p_43143_.getZ(), SoundEvents.EGG_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (p_43142_.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!p_43142_.isClientSide) {
            PipeBomb bomb;
                bomb = new PipeBomb(p_43142_, p_43143_, TFMGEntityTypes.PIPE_BOMB.get());

            bomb.setItem(itemstack);
            // Slower than the vanilla egg/snowball speed (1.5F) it was inheriting - gives
            // whoever it's thrown at (or the thrower ducking for cover) a bit more reaction time.
            bomb.shootFromRotation(p_43143_, p_43143_.getXRot(), p_43143_.getYRot(), 0.0F, 0.9F, 1.0F);
            p_43142_.addFreshEntity(bomb);
        }

        p_43143_.awardStat(Stats.ITEM_USED.get(this));
        if (!p_43143_.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, p_43142_.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (level.isClientSide)
            return;

        if (tickTrap(stack, level, entity.getX(), entity.getY(), entity.getZ())) {
            stack.shrink(1);
        }
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        Level level = entity.level();
        if (!level.isClientSide) {
            // entity.getItem() (== this stack) is the live reference backing the entity's synced
            // data - mutating it in place never actually calls ItemEntity#setItem, so the change
            // (arming, losing its name, ticking down) never gets marked dirty and clients keep
            // rendering whatever was last actually synced. Work on a copy and push it back
            // through setItem() so it properly propagates.
            ItemStack working = stack.copy();
            boolean detonated = tickTrap(working, level, entity.getX(), entity.getY(), entity.getZ());
            // No lava, fire, or cactus is stopping this from reaching its own deadline.
            if (working.has(TFMGDataComponents.PIPE_BOMB_TRAP_TIMER))
                entity.setInvulnerable(true);
            if (detonated)
                working.shrink(1);
            entity.setItem(working);
            if (working.isEmpty())
                entity.discard();
        }
        return false;
    }

    /**
     * Advances a renamed pipe bomb's fuse by one tick, if it's running, playing the warning
     * sounds along the way. Returns true if it just detonated (caller must remove the item).
     * <p>
     * Also where the fuse actually gets started: a {@link TFMGDataComponents#PIPE_BOMB_PACKAGED}
     * bomb arms itself the instant it's ticking here at all, regardless of how it came to be
     * loose - opened normally, its Package was destroyed, funneled out by a hopper, anything.
     */
    private static boolean tickTrap(ItemStack stack, Level level, double x, double y, double z) {
        if (stack.has(TFMGDataComponents.PIPE_BOMB_PACKAGED)
                && !stack.has(TFMGDataComponents.PIPE_BOMB_TRAP_TIMER)) {
            int fuse = MIN_FUSE_TICKS + level.random.nextInt(MAX_FUSE_TICKS - MIN_FUSE_TICKS + 1);
            stack.set(TFMGDataComponents.PIPE_BOMB_TRAP_TIMER, fuse);
            stack.set(TFMGDataComponents.PIPE_BOMB_TRAP_TOTAL_TICKS, fuse);
            // The disguise did its job getting it into their hands - reveal what it actually is
            // the moment it starts ticking.
            stack.remove(DataComponents.CUSTOM_NAME);
            // Lit bombs don't stack: a box of several arms all at once, but each one ticks and
            // detonates independently from here on, not as one oversized stack.
            if (!level.isClientSide)
                splitArmedExtras(stack, level, x, y, z);
        }

        Integer timer = stack.get(TFMGDataComponents.PIPE_BOMB_TRAP_TIMER);
        if (timer == null)
            return false;

        if (timer % 10 == 0)
            level.playSound(null, x, y, z, SoundEvents.CREEPER_PRIMED, SoundSource.HOSTILE, 0.3F, 1F);

        if (timer % 20 == 0) {
            boolean finalSecond = timer == 20;
            level.playSound(null, x, y, z, SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.RECORDS,
                    1F, finalSecond ? 2F : 1F);
        }

        int next = timer - 1;
        if (next <= 0) {
            explode(level, null, x, y, z);
            spawnFunFirework(level, x, y, z);
            return true;
        }

        stack.set(TFMGDataComponents.PIPE_BOMB_TRAP_TIMER, next);
        return false;
    }

    /**
     * Peels off every bomb past the first in a freshly-armed stack as its own independent,
     * already-armed dropped item, then trims the original stack down to 1. Each one (including
     * the one that stays behind) rolls its own fuse - a box of 5 detonates as 5 separate,
     * staggered booms, not one synchronized volley.
     */
    private static void splitArmedExtras(ItemStack stack, Level level, double x, double y, double z) {
        int extra = stack.getCount() - 1;
        if (extra <= 0)
            return;
        stack.setCount(1);
        for (int i = 0; i < extra; i++) {
            ItemStack copy = stack.copyWithCount(1);
            int fuse = MIN_FUSE_TICKS + level.random.nextInt(MAX_FUSE_TICKS - MIN_FUSE_TICKS + 1);
            copy.set(TFMGDataComponents.PIPE_BOMB_TRAP_TIMER, fuse);
            copy.set(TFMGDataComponents.PIPE_BOMB_TRAP_TOTAL_TICKS, fuse);

            ItemEntity dropped = new ItemEntity(level, x, y, z, copy);
            dropped.setDeltaMovement(
                    (level.random.nextDouble() - 0.5) * 0.3,
                    0.25,
                    (level.random.nextDouble() - 0.5) * 0.3
            );
            level.addFreshEntity(dropped);
        }
    }

    /**
     * Just for fun - a big, sparkly firework burst at the exact moment of the real explosion.
     * It's a pure visual, it doesn't deal any damage of its own.
     */
    private static void spawnFunFirework(Level level, double x, double y, double z) {
        FireworkExplosion explosion = new FireworkExplosion(
                FireworkExplosion.Shape.LARGE_BALL,
                IntList.of(0xFF3333, 0xFFAA00, 0x33FF33, 0x33AAFF, 0xFF33CC),
                IntList.of(0xFFFFFF),
                true,
                true
        );
        ItemStack fireworkStack = new ItemStack(Items.FIREWORK_ROCKET);
        fireworkStack.set(DataComponents.FIREWORKS, new Fireworks(0, List.of(explosion)));

        // Don't let it fly out and pop on its own random-delayed schedule - trigger the burst
        // (particles + sound) right now, on the same tick as the real explosion, then discard it
        // immediately so it doesn't also try to explode again later on its own.
        FireworkRocketEntity firework = new FireworkRocketEntity(level, x, y, z, fireworkStack);
        level.addFreshEntity(firework);
        level.broadcastEntityEvent(firework, (byte) 17);
        firework.discard();
    }

    /**
     * "4", "3", "2", "1", then "KABOOM" for the entire final second - lining up with the
     * high-pitched warning note at 1 second left, regardless of the bomb's (randomized) total
     * fuse length, since this only ever looks at ticks remaining.
     */
    public static String trapDisplayText(int timer) {
        int secondsRemaining = (timer + 19) / 20; // ceil(timer / 20)
        if (secondsRemaining <= 1)
            return "KABOOM";
        return String.valueOf(secondsRemaining - 1);
    }

    /**
     * Same hue math vanilla uses for durability bars: green when fresh, sliding to red as the
     * fuse runs out. Based on progress through THIS bomb's own (randomized) fuse length, not a
     * fixed tick count, so a freshly-armed 10s bomb reads as "full" just like a 5s one does.
     */
    public static int trapColor(int timer, int totalTicks) {
        float remaining = totalTicks <= 0 ? 0F : Mth.clamp(timer / (float) totalTicks, 0F, 1F);
        return Mth.hsvToRgb(remaining / 3F, 1F, 1F);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (stack.has(TFMGDataComponents.PIPE_BOMB_STICKY))
            tooltip.add(Component.translatable("tfmg.tooltip.pipe_bomb_sticky")
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(SLIME_BALL_COLOR))));
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        // Belt-and-suspenders on top of splitArmedExtras(): a lit bomb can never (re-)merge
        // with another stack once its fuse is running.
        if (stack.has(TFMGDataComponents.PIPE_BOMB_TRAP_TIMER))
            return 1;
        return super.getMaxStackSize(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.has(TFMGDataComponents.PIPE_BOMB_TRAP_TIMER);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        Integer timer = stack.get(TFMGDataComponents.PIPE_BOMB_TRAP_TIMER);
        Integer total = stack.get(TFMGDataComponents.PIPE_BOMB_TRAP_TOTAL_TICKS);
        return trapColor(timer == null ? 0 : timer, total == null ? MIN_FUSE_TICKS : total);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        Integer timer = stack.get(TFMGDataComponents.PIPE_BOMB_TRAP_TIMER);
        Integer total = stack.get(TFMGDataComponents.PIPE_BOMB_TRAP_TOTAL_TICKS);
        if (timer == null || total == null || total <= 0)
            return 0;
        return Mth.clamp(Math.round((timer / (float) total) * 13F), 0, 13);
    }
}

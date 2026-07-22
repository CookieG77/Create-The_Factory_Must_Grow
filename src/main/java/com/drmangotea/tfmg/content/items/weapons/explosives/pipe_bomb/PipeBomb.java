package com.drmangotea.tfmg.content.items.weapons.explosives.pipe_bomb;


import com.drmangotea.tfmg.registry.TFMGDataComponents;
import com.drmangotea.tfmg.registry.TFMGEntityTypes;
import com.drmangotea.tfmg.registry.TFMGItems;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PipeBomb extends ThrowableItemProjectile {

    /**
     * 2 seconds at 20 ticks/sec.
     */
    private static final int FUSE_AFTER_IMPACT_TICKS = 40;
    private static final double BOUNCE_RETAINED_VELOCITY = 0.5D;
    /**
     * Thrown projectiles move via a raw position += velocity each tick with NO collision
     * resolution of their own - the only thing that ever stops one hitting a wall is a
     * predictive raycast that fires onHit() just before that tick's move happens. A damped
     * reflection alone can end up too weak to actually clear the surface it just bounced off
     * before gravity (or just still touching it) pulls it back in, which is what was causing it
     * to sink into geometry. So every bounce is topped up to at least this much speed straight
     * out along the surface it hit, and the position is nudged clear of the surface too.
     */
    private static final double MIN_BOUNCE_SPEED = 0.2D;

    /**
     * -1 until the first impact, then counts down to 0 (explode). Only ever gets set once - the
     * fuse always starts on the very first hit, whether that hit bounces, sticks, or just stops.
     */
    private int fuseTicks = -1;

    /**
     * True once it's had its one bounce (or, if sticky, its one stick) - every impact after that
     * just kills its velocity in place instead of bouncing again.
     */
    private boolean hasBounced = false;

    /**
     * True once a sticky bomb has attached itself to a block face. Skips the tick() safety net
     * (which would otherwise keep shoving it off the surface it's deliberately embedded in) and
     * gravity (so it stays put on walls/ceilings, not just floors).
     */
    private boolean stuckToBlock = false;

    public PipeBomb(EntityType<? extends PipeBomb> entityType, Level level) {
        super(entityType,level);


    }
    public PipeBomb(Level p_37399_, LivingEntity p_37400,EntityType bomb) {
        super(bomb, p_37400, p_37399_);

    }
    public PipeBomb(Level p_37394_, double p_37395_, double p_37396_, double p_37397_) {
        super(TFMGEntityTypes.PIPE_BOMB.get(), p_37395_, p_37396_, p_37397_, p_37394_);
    }


    protected Item getDefaultItem() {
        return TFMGItems.PIPE_BOMB.get();
    }

    private ParticleOptions getParticle() {

        return ParticleTypes.FLAME;
    }

    public void handleEntityEvent(byte p_37402_) {
        if (p_37402_ == 3) {
            ParticleOptions particleoptions = this.getParticle();

            for(int i = 0; i < 8; ++i) {
                this.level().addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }

    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && fuseTicks >= 0) {
            // Safety net: if it still ended up wedged inside a block somehow (corners, thin
            // gaps, a bounce that wasn't enough), don't just leave it there sinking further -
            // give it a shove clear before it can ever get permanently stuck out of sight.
            // Skipped once it's deliberately stuck to a surface - it's meant to be embedded.
            if (!stuckToBlock && !this.level().noCollision(this))
                this.setDeltaMovement(this.getDeltaMovement().add(0, 0.1D, 0));

            if (fuseTicks == 0) {
                // Just a straightforward thrown explosive - normal explosion damage, affects
                // everyone, not the trap's player-only tfmg:pipe_bomb_trap damage type.
                this.level().explode(this, this.getX(), this.getY(), this.getZ(), PipeBombItem.EXPLOSION_POWER, Level.ExplosionInteraction.NONE);
                this.discard();
            } else {
                fuseTicks--;
            }
        }
    }

    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level().isClientSide)
            return;
        // Only the first impact bounces at all - once it's already had its one bounce, every
        // hit after that just kills its velocity instead of ricocheting again.
        if (!hasBounced) {
            hasBounced = true;
            // No clean surface normal to bounce off an entity - just knock it back, dampened,
            // with a minimum speed so it doesn't stall out right on top of them.
            Vec3 bounced = this.getDeltaMovement().scale(-BOUNCE_RETAINED_VELOCITY);
            if (bounced.length() < MIN_BOUNCE_SPEED) {
                bounced = bounced.lengthSqr() < 1.0E-6 ? new Vec3(0, MIN_BOUNCE_SPEED, 0) : bounced.normalize().scale(MIN_BOUNCE_SPEED);
            }
            this.setDeltaMovement(bounced);
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }
        armFuseOnFirstImpact();
    }

    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.level().isClientSide)
            return;
        if (!hasBounced) {
            hasBounced = true;
            if (isSticky())
                stickToSurface(result.getDirection());
            else
                bounceOffSurface(result.getDirection());
        } else if (!stuckToBlock) {
            this.setDeltaMovement(Vec3.ZERO);
        }
        armFuseOnFirstImpact();
    }

    private boolean isSticky() {
        return this.getItem().has(TFMGDataComponents.PIPE_BOMB_STICKY);
    }

    protected void onHit(HitResult result) {
        // Deliberately NOT calling super.onHit() here - the vanilla dispatcher would otherwise
        // also route through here, but the real behavior (bounce + fuse start) belongs entirely
        // to onHitBlock/onHitEntity above so it can tell block hits from entity hits apart.
        if (result.getType() == HitResult.Type.ENTITY)
            this.onHitEntity((EntityHitResult) result);
        else if (result.getType() == HitResult.Type.BLOCK)
            this.onHitBlock((BlockHitResult) result);

        if (!this.level().isClientSide)
            this.level().broadcastEntityEvent(this, (byte) 3);
    }

    private void armFuseOnFirstImpact() {
        if (fuseTicks < 0)
            fuseTicks = FUSE_AFTER_IMPACT_TICKS;
    }

    private void bounceOffSurface(Direction hitFace) {
        Vec3 motion = this.getDeltaMovement();
        double x = motion.x, y = motion.y, z = motion.z;
        switch (hitFace.getAxis()) {
            case X -> x = -x;
            case Y -> y = -y;
            case Z -> z = -z;
        }
        x *= BOUNCE_RETAINED_VELOCITY;
        y *= BOUNCE_RETAINED_VELOCITY;
        z *= BOUNCE_RETAINED_VELOCITY;

        // Guarantee real escape speed straight out along the face it hit, regardless of how
        // little was left after damping - this is what actually prevents it sinking back in.
        Vec3 normal = Vec3.atLowerCornerOf(hitFace.getNormal());
        double outwardSpeed = x * normal.x + y * normal.y + z * normal.z;
        if (outwardSpeed < MIN_BOUNCE_SPEED) {
            double boost = MIN_BOUNCE_SPEED - outwardSpeed;
            x += normal.x * boost;
            y += normal.y * boost;
            z += normal.z * boost;
        }

        this.setDeltaMovement(x, y, z);
        // And physically clear it off the surface it's touching, so next tick doesn't start
        // from (near-)inside the block it just hit.
        this.setPos(this.getX() + normal.x * 0.1D, this.getY() + normal.y * 0.1D, this.getZ() + normal.z * 0.1D);
    }

    /**
     * Sticky variant of a surface impact - instead of bouncing off, it goes dead still and
     * clings right where it hit, gravity disabled, so it stays attached to walls and ceilings
     * exactly as well as floors.
     */
    private void stickToSurface(Direction hitFace) {
        this.setDeltaMovement(Vec3.ZERO);
        this.setNoGravity(true);
        stuckToBlock = true;

        Vec3 normal = Vec3.atLowerCornerOf(hitFace.getNormal());
        this.setPos(this.getX() + normal.x * 0.05D, this.getY() + normal.y * 0.05D, this.getZ() + normal.z * 0.05D);
    }

    @SuppressWarnings("unchecked")
    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        EntityType.Builder<PipeBomb> entityBuilder = (EntityType.Builder<PipeBomb>) builder;
        return entityBuilder.sized(.25f, .25f);
    }
}

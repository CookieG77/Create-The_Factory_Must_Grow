package com.drmangotea.tfmg.content.items.weapons.explosives.pipe_bomb;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class PipeBombRenderer extends EntityRenderer<PipeBomb> {
    private final ItemRenderer itemRenderer;


    public PipeBombRenderer(EntityRendererProvider.Context p_174114_) {
        super(p_174114_);
        this.itemRenderer = p_174114_.getItemRenderer();
    }


    public void render(PipeBomb grenade, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // Tumbling "fake gravity" spin instead of billboarding to face the camera - a
        // camera-locked object can never visibly rotate, so this replaces that entirely.
        // The axis/speed are randomized per-entity (seeded off its id, so it's identical every
        // frame without needing to store anything) so a handful of thrown bombs don't spin in
        // lockstep with each other.
        RandomSource spinRandom = RandomSource.create(grenade.getId());
        float spinAxisYaw = spinRandom.nextFloat() * 360F;
        float spinAxisPitch = spinRandom.nextFloat() * 360F;
        float spinSpeed = 25F + spinRandom.nextFloat() * 20F; // degrees per tick
        float age = grenade.tickCount + partialTick;

        poseStack.mulPose(Axis.YP.rotationDegrees(spinAxisYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(spinAxisPitch));
        poseStack.mulPose(Axis.ZP.rotationDegrees(age * spinSpeed));

        // The bomb's actual carried stack, not a blank default instance - otherwise any
        // stack-dependent look (like the slime-covered texture) would never show up thrown.
        this.itemRenderer.renderStatic(grenade.getItem(), ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, grenade.level(), grenade.getId());

        poseStack.popPose();
        super.render(grenade, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    public ResourceLocation getTextureLocation(PipeBomb p_114654_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

}
package net.mcreator.madokraftmagica.kyubey.entity.client;

import net.minecraft.resources.ResourceLocation;
import net.mcreator.madokraftmagica.MadokraftmagicaMod;
import net.mcreator.madokraftmagica.kyubey.entity.KyubeyEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class KyubeyModel extends AnimatedGeoModel<KyubeyEntity> {

    @Override
    public ResourceLocation getModelResource(KyubeyEntity animatable) {
        return new ResourceLocation(MadokraftmagicaMod.MODID, "geo/kyubey/kyubey.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KyubeyEntity animatable) {
        return new ResourceLocation(MadokraftmagicaMod.MODID, "textures/entity/kyubey/kyubey.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KyubeyEntity animatable) {
        return new ResourceLocation(MadokraftmagicaMod.MODID,
                "animations/kyubey/kyubey_simple.animation.json");
    }
}

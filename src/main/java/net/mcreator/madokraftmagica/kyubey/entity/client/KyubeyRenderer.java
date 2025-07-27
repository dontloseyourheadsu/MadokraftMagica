package net.mcreator.madokraftmagica.kyubey.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.mcreator.madokraftmagica.kyubey.entity.KyubeyEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class KyubeyRenderer extends GeoEntityRenderer<KyubeyEntity> {

    public KyubeyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KyubeyModel());
    }
}

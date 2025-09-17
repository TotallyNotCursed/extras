package xyz.splack.tnc.extras.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xyz.splack.tnc.extras.ModUtils;
import xyz.splack.tnc.extras.entity.GoreyeEntity;

@Environment(EnvType.CLIENT)
public class GoreyeRenderer extends MobRenderer<GoreyeEntity, PlayerModel<GoreyeEntity>> {
    private static final ResourceLocation GOREYE_TEXTURE = ModUtils.getResourceLocation("textures/entity/goreye.png");

    public GoreyeRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(GoreyeEntity entity) {
        return GOREYE_TEXTURE;
    }
}

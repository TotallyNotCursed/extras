package xyz.splack.tnc.extras.mixin;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.splack.tnc.extras.cape.CustomCapeRegistry;

@Mixin(AbstractClientPlayer.class)
@Environment(EnvType.CLIENT)
public abstract class CustomCapeMixin extends Player {

  @Unique private final UUID tnc_extras$uuid = this.getUUID();

  public CustomCapeMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
    super(level, pos, yRot, gameProfile);
  }

  @Inject(method = "getCloakTextureLocation", at = @At("RETURN"), cancellable = true)
  private void injectCloakTexture(CallbackInfoReturnable<ResourceLocation> cir) {
    if (CustomCapeRegistry.hasCape(tnc_extras$uuid)) {
      cir.setReturnValue(CustomCapeRegistry.getCape(tnc_extras$uuid));
    }
  }

  @Inject(method = "getElytraTextureLocation", at = @At("RETURN"), cancellable = true)
  private void injectElytraTexture(CallbackInfoReturnable<ResourceLocation> cir) {
    if (CustomCapeRegistry.hasCape(tnc_extras$uuid)) {
      cir.setReturnValue(CustomCapeRegistry.getCape(tnc_extras$uuid));
    }
  }
}

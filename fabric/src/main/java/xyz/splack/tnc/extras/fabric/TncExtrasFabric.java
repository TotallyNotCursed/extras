package xyz.splack.tnc.extras.fabric;

import net.fabricmc.api.ModInitializer;
import xyz.splack.tnc.extras.TncExtras;

public final class TncExtrasFabric implements ModInitializer {
  @Override
  public void onInitialize() {
    TncExtras.init();
  }
}

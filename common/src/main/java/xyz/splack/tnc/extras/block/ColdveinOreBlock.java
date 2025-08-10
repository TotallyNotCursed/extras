package xyz.splack.tnc.extras.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class ColdveinOreBlock extends Block {

  public ColdveinOreBlock(Properties properties) {
    super(
        properties
            .sound(SoundType.STONE)
            .strength(10.0f)
            .explosionResistance(100.0f)
            .requiresCorrectToolForDrops());
  }
}

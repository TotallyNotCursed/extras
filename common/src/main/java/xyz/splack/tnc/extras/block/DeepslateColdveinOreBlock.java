package xyz.splack.tnc.extras.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class DeepslateColdveinOreBlock extends Block {

    public DeepslateColdveinOreBlock(Properties properties) {
        super(properties
                .sound(SoundType.DEEPSLATE)
                .strength(15.0f)
                .explosionResistance(200.0f)
                .requiresCorrectToolForDrops());
    }
}

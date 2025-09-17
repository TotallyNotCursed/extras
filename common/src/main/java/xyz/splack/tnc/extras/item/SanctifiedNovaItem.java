package xyz.splack.tnc.extras.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.splack.tnc.extras.sound.ModSounds;

public class SanctifiedNovaItem extends Item {

    public static final int RANGE = 20;

    public SanctifiedNovaItem(Properties properties) {
        super(properties.stacksTo(1).fireResistant().rarity(Rarity.EPIC));
    }

    @Override
    public void appendHoverText(
            ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("item.tnc_extras.sanctified_nova.tooltip", RANGE)
                    .withStyle(ChatFormatting.AQUA));
            tooltipComponents.add(Component.translatable("item.tnc_extras.sanctified_nova.tooltip_warning")
                    .withStyle(ChatFormatting.RED));
        } else {
            tooltipComponents.add(
                    Component.translatable("tooltip.tnc_extras.shift").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            // Strike each undead in range
            serverLevel.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(RANGE)).stream()
                    .filter(mob -> mob.getMobType() == MobType.UNDEAD)
                    .forEach(undead -> {
                        if (undead.isAlive()) {
                            // Spawn a lightning bolt at the undead's position
                            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
                            if (lightning != null) {
                                lightning.setPos(undead.getX(), undead.getY(), undead.getZ());
                                if (player instanceof ServerPlayer serverPlayer) {
                                    lightning.setCause(serverPlayer);
                                }
                                lightning.setVisualOnly(true);
                                serverLevel.addFreshEntity(lightning);
                            }
                            // Kill the undead
                            undead.kill();
                        }
                    });

            // Play custom sound
            serverLevel.playSound(
                    null,
                    player.blockPosition(),
                    ModSounds.SANCTIFIED_NOVA_USE.get(),
                    player.getSoundSource(),
                    4.0f,
                    1.0f);

            // Play explosion sound
            serverLevel.playSound(
                    null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE, player.getSoundSource(), 4.0f, 1.0f);

            // Explosion particles and smoke
            serverLevel.sendParticles(
                    ParticleTypes.EXPLOSION_EMITTER, player.getX(), player.getY(), player.getZ(), 1, 0, 0, 0, 0.1);
            serverLevel.sendParticles(
                    ParticleTypes.EXPLOSION, player.getX(), player.getY(), player.getZ(), 20, 2, 2, 2, 0.2);
            serverLevel.sendParticles(
                    ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(), 50, 2, 2, 2, 0.1);
            serverLevel.sendParticles(
                    ParticleTypes.SMOKE, player.getX(), player.getY(), player.getZ(), 30, 2, 2, 2, 0.05);

            // Remove item if not creative
            if (!player.isCreative()) {
                player.getItemInHand(hand).shrink(1);
            }
        }
        return super.use(level, player, hand);
    }
}

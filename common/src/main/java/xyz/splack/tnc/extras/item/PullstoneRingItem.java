package xyz.splack.tnc.extras.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.splack.tnc.extras.sound.PullstoneRingSoundInstance;

public class PullstoneRingItem extends Item {

  private static final int RANGE = 15;
  private static final int COOLDOWN_TICKS = 200; // 10 seconds

  public PullstoneRingItem(Properties properties) {
    super(properties.durability(100).fireResistant().rarity(Rarity.RARE));
  }

  @Override
  public void appendHoverText(
      ItemStack stack,
      @Nullable Level level,
      List<Component> tooltipComponents,
      TooltipFlag isAdvanced) {
    if (Screen.hasShiftDown()) {
      tooltipComponents.add(
          Component.translatable("item.tnc_extras.pullstone_ring.tooltip", RANGE)
              .withStyle(ChatFormatting.LIGHT_PURPLE));
    } else {
      tooltipComponents.add(
          Component.translatable("tooltip.tnc_extras.shift").withStyle(ChatFormatting.GRAY));
    }
  }

  @Override
  public @NotNull UseAnim getUseAnimation(ItemStack stack) {
    return UseAnim.BOW;
  }

  @Override
  public int getUseDuration(ItemStack stack) {
    // A long duration ensures the item can be used continuously until stopped.
    return 72000;
  }

  @Override
  public @NotNull InteractionResultHolder<ItemStack> use(
      Level level, Player player, InteractionHand hand) {
    ItemStack itemStack = player.getItemInHand(hand);
    if (player.getCooldowns().isOnCooldown(this)) {
      return InteractionResultHolder.fail(itemStack);
    }

    player.startUsingItem(hand);

    if (level.isClientSide) {
      Minecraft.getInstance().getSoundManager().play(new PullstoneRingSoundInstance(player));
    }

    return InteractionResultHolder.consume(itemStack);
  }

  @Override
  public void onUseTick(
      Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
    if (level.isClientSide || !(entity instanceof Player player)) {
      return;
    }

    // Attract ItemEntities
    level.getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(RANGE)).stream()
        .filter(itemEntity -> itemEntity.distanceTo(player) < RANGE)
        .forEach(
            itemEntity ->
                itemEntity.setDeltaMovement(
                    (player.getX() - itemEntity.getX()) * 0.1,
                    (player.getY() - itemEntity.getY()) * 0.1,
                    (player.getZ() - itemEntity.getZ()) * 0.1));

    // Damage the ring every second
    if ((getUseDuration(stack) - remainingUseDuration) % 20 == 0) {
      stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
    }
  }

  // Called when the player releases the use button.
  @Override
  public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
    if (entity instanceof Player player) {
      // Apply cooldown when player stops using the item.
      player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
    }
    super.releaseUsing(stack, level, entity, timeCharged);
  }

  /*
   Called when the item has been used for its full duration. While our duration is very long, this
   is still necessary for completeness and in case the item breaks from use.
  */
  @Override
  public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
    if (entity instanceof Player player) {
      // Also apply cooldown here to cover all cases.
      player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
    }
    return super.finishUsingItem(stack, level, entity);
  }
}

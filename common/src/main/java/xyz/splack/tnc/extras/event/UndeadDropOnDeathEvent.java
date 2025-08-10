package xyz.splack.tnc.extras.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.splack.tnc.extras.item.ModItems;

public class UndeadDropOnDeathEvent implements EntityEvent.LivingDeath {
  // Probability of the drop occurring (0.05 = 5% chance)
  private static final float DROP_CHANCE = 0.05f;

  @Override
  public EventResult die(LivingEntity livingEntity, DamageSource damageSource) {
    // Only proceed for undead mobs and if the damage source is from a player
    if (livingEntity instanceof Mob mob
        && mob.getMobType() == MobType.UNDEAD
        && damageSource.getEntity() instanceof Player) {
      if (mob.level().random.nextFloat() < DROP_CHANCE) {
        mob.spawnAtLocation(new ItemStack(ModItems.SANCTIFIED_NOVA.get()));
      }
    }
    // Allow other event handlers to process if not triggered
    return EventResult.pass();
  }
}

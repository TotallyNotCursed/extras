package xyz.splack.tnc.extras.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EndermanJumpscareOnAnimalKillEvent implements EntityEvent.LivingDeath {
  // Probability of the jumpscare occurring (0.05 = 5% chance)
  private static final float JUMPSCARE_CHANCE = 0.05f;

  @Override
  public EventResult die(LivingEntity livingEntity, DamageSource damageSource) {
    // Only trigger if a player kills an animal
    if (damageSource.getEntity() instanceof Player player
        && livingEntity instanceof Animal animal) {
      Level level = animal.level();

      // Check if the jumpscare should occur based on random chance
      if (level.random.nextFloat() < JUMPSCARE_CHANCE) {
        BlockPos blockPos = animal.blockPosition();

        // Create a new Enderman entity at the animal's position
        EnderMan enderman = new EnderMan(EntityType.ENDERMAN, level);
        enderman.setPos(
            blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5); // Center of block
        level.addFreshEntity(enderman);

        // Play Enderman scream sound at the location
        level.playSound(
            null,
            blockPos,
            SoundEvents.ENDERMAN_SCREAM,
            SoundSource.HOSTILE,
            1.0f,
            0.8f + level.random.nextFloat() * 0.4f);

        // Make the Enderman target the player
        enderman.setTarget(player);

        // Force the player to look at the Enderman (client-side effect)
        player.lookAt(EntityAnchorArgument.Anchor.EYES, enderman.getEyePosition());
        /*
         Force the Enderman to look at the player, since targeting doesn't always update head
         rotation immediately.
        */
        enderman.lookAt(EntityAnchorArgument.Anchor.EYES, player.getEyePosition());

        // Remove the animal entity from the world
        animal.discard();
        // Interrupt further event processing
        return EventResult.interruptFalse();
      }
    }
    // Allow other event handlers to process if not triggered
    return EventResult.pass();
  }
}

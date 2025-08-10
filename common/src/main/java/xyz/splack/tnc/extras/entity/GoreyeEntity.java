package xyz.splack.tnc.extras.entity;

import java.util.Objects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import xyz.splack.tnc.extras.ModUtils;

public class GoreyeEntity extends Mob {
  private static int visibleTicks = 0;

  public GoreyeEntity(EntityType<? extends Mob> type, Level level) {
    super(type, level);
    this.setNoGravity(true); // Floats
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 45.0) // High health
        .add(Attributes.MOVEMENT_SPEED, 0.25) // Base speed
        .add(Attributes.FOLLOW_RANGE, 32.0) // Large detection range
        .add(Attributes.KNOCKBACK_RESISTANCE, 1.0); // Cannot be knocked back
  }

  @Override
  public void tick() {
    super.tick();
    boolean isSeen = false;
    Player nearest = this.level().getNearestPlayer(this, 20);
    if (nearest != null && ModUtils.isNightOrDark(this)) {
      double distance = this.distanceTo(nearest);

      // Check if player is looking at the Goreye
      isSeen = ModUtils.lookedAt(this, nearest, 10.0, 8.0);

      if (isSeen) {
        visibleTicks = 100; // Visible for 5 seconds
        this.level()
            .playSound(
                null,
                blockPosition(),
                SoundEvents.GHAST_SCREAM,
                SoundSource.HOSTILE,
                0.8f,
                1.0f); // Scream when spotted
      } else if (distance < 4) {
        // Attack if close and unseen
        nearest.hurt(this.level().damageSources().magic(), 6.0f);
        nearest.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
        nearest.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0));
        nearest.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 1));
        this.level()
            .playSound(
                null, blockPosition(), SoundEvents.ENDERMAN_STARE, SoundSource.HOSTILE, 1.0f, 0.7f);
        this.discard(); // Vanish after attack
      } else {
        // Move toward player
        getNavigation().moveTo(nearest, 1.1);
        if (this.level().random.nextInt(40) == 0) {
          this.level()
              .playSound(
                  null,
                  blockPosition(),
                  SoundEvents.WARDEN_AMBIENT,
                  SoundSource.HOSTILE,
                  0.5f,
                  0.8f); // Ambient sound
        }
        // Teleport toward player sometimes
        if (this.tickCount % 60 == 0 && distance > 4 && distance < 16) {
          if (this.random.nextFloat() < 0.3f) {
            Vec3 direction = nearest.position().subtract(this.position()).normalize();
            Vec3 newPos = this.position().add(direction.scale(5.0));
            if (this.level()
                .noCollision(this, this.getBoundingBox().move(newPos.subtract(this.position())))) {
              this.teleportTo(newPos.x, newPos.y, newPos.z);
              this.level()
                  .playSound(
                      null,
                      blockPosition(),
                      SoundEvents.ENDERMAN_TELEPORT,
                      SoundSource.HOSTILE,
                      1.0f,
                      1.0f);
            }
          }
        }
      }
    }

    // Handle visibility and movement speed
    if (visibleTicks > 0) {
      visibleTicks--;
      setInvisible(false);
      Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED))
          .setBaseValue(0.35); // Faster when seen
    } else {
      setInvisible(true);
      Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED))
          .setBaseValue(0.25); // Normal speed when unseen
      // Spawn particles when invisible and stalking
      if (nearest != null && ModUtils.isNightOrDark(this) && !isSeen) {
        if (this.tickCount % 20 == 0) {
          this.level().broadcastEntityEvent(this, (byte) 10); // Trigger particle event
        }
      }
    }

    // Despawn during daylight or in bright areas
    if (ModUtils.isNightOrDark(this)) {
      this.discard();
    }
  }

  @Override
  public void handleEntityEvent(byte id) {
    if (id == 10) {
      // Spawn smoke particles to hint at presence
      for (int i = 0; i < 5; i++) {
        this.level()
            .addParticle(
                ParticleTypes.SMOKE,
                this.getRandomX(0.5),
                this.getRandomY(),
                this.getRandomZ(0.5),
                0.0,
                0.0,
                0.0);
      }
    } else {
      super.handleEntityEvent(id);
    }
  }

  @Override
  public boolean hurt(DamageSource source, float amount) {
    if (visibleTicks == 0) {
      return false; // Invulnerable when invisible
    }
    return super.hurt(source, amount);
  }
}

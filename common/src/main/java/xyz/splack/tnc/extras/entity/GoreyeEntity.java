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
  private static final double ATTR_MAX_HEALTH = 45.0;
  private static final double ATTR_MOVEMENT_SPEED = 0.25;
  private static final double ATTR_MOVEMENT_SPEED_SEEN = 0.35;
  private static final double ATTR_FOLLOW_RANGE = 32.0;
  private static final double ATTR_KNOCKBACK_RESISTANCE = 1.0;

  private static final int DETECTION_RADIUS = 20;
  private static final double LOOK_DISTANCE = 10.0;
  private static final double LOOK_ANGLE = 8.0;
  private static final int VISIBLE_TICKS_DURATION = 100; // ticks (5s)
  private static final double ATTACK_DISTANCE = 4.0;
  private static final float ATTACK_DAMAGE = 6.0f;

  private static final int SLOW_DURATION = 60;
  private static final int SLOW_AMPLIFIER = 1;
  private static final int BLINDNESS_DURATION = 40;
  private static final int BLINDNESS_AMPLIFIER = 0;
  private static final int WITHER_DURATION = 40;
  private static final int WITHER_AMPLIFIER = 1;

  private static final int AMBIENT_SOUND_CHANCE = 40; // 1 in N per tick check
  private static final int TELEPORT_CHECK_INTERVAL = 60; // every N ticks
  private static final double TELEPORT_MIN_DISTANCE = 4.0;
  private static final double TELEPORT_MAX_DISTANCE = 16.0;
  private static final double TELEPORT_STEP = 5.0;
  private static final float TELEPORT_CHANCE = 0.3f;

  private static final int PARTICLE_EVENT_ID = 10;
  private static final int PARTICLE_SPAWN_INTERVAL = 20;
  private static final int PARTICLE_SPAWN_COUNT = 5;

  private int visibleTicks = 0;

  public GoreyeEntity(EntityType<? extends Mob> type, Level level) {
    super(type, level);
    this.setNoGravity(true); // Floats
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes()
        .add(Attributes.MAX_HEALTH, ATTR_MAX_HEALTH)
        .add(Attributes.MOVEMENT_SPEED, ATTR_MOVEMENT_SPEED)
        .add(Attributes.FOLLOW_RANGE, ATTR_FOLLOW_RANGE)
        .add(Attributes.KNOCKBACK_RESISTANCE, ATTR_KNOCKBACK_RESISTANCE);
  }

  @Override
  public void tick() {
    super.tick();
    boolean isSeen = false;
    Player nearest = this.level().getNearestPlayer(this, DETECTION_RADIUS);
    if (nearest != null && ModUtils.isNightOrDark(this)) {
      double distance = this.distanceTo(nearest);

      // Check if player is looking at the Goreye
      isSeen = ModUtils.lookedAt(this, nearest, LOOK_DISTANCE, LOOK_ANGLE);

      if (isSeen) {
        visibleTicks = VISIBLE_TICKS_DURATION; // Visible for configured duration
        this.level()
            .playSound(
                null,
                blockPosition(),
                SoundEvents.GHAST_SCREAM,
                SoundSource.HOSTILE,
                0.8f,
                1.0f); // Scream when spotted
      } else if (distance < ATTACK_DISTANCE) {
        // Attack if close and unseen
        nearest.hurt(this.level().damageSources().magic(), ATTACK_DAMAGE);
        nearest.addEffect(
            new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOW_DURATION, SLOW_AMPLIFIER));
        nearest.addEffect(
            new MobEffectInstance(MobEffects.BLINDNESS, BLINDNESS_DURATION, BLINDNESS_AMPLIFIER));
        nearest.addEffect(
            new MobEffectInstance(MobEffects.WITHER, WITHER_DURATION, WITHER_AMPLIFIER));
        this.level()
            .playSound(
                null, blockPosition(), SoundEvents.ENDERMAN_STARE, SoundSource.HOSTILE, 1.0f, 0.7f);
        this.discard(); // Vanish after attack
      } else {
        // Move toward player
        getNavigation().moveTo(nearest, 1.1);
        if (this.level().random.nextInt(AMBIENT_SOUND_CHANCE) == 0) {
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
        if (this.tickCount % TELEPORT_CHECK_INTERVAL == 0
            && distance > TELEPORT_MIN_DISTANCE
            && distance < TELEPORT_MAX_DISTANCE) {
          if (this.random.nextFloat() < TELEPORT_CHANCE) {
            Vec3 direction = nearest.position().subtract(this.position()).normalize();
            Vec3 newPos = this.position().add(direction.scale(TELEPORT_STEP));
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
          .setBaseValue(ATTR_MOVEMENT_SPEED_SEEN); // Faster when seen
    } else {
      setInvisible(true);
      Objects.requireNonNull(this.getAttribute(Attributes.MOVEMENT_SPEED))
          .setBaseValue(ATTR_MOVEMENT_SPEED); // Normal speed when unseen
      // Spawn particles when invisible and stalking
      if (nearest != null && ModUtils.isNightOrDark(this) && !isSeen) {
        if (this.tickCount % PARTICLE_SPAWN_INTERVAL == 0) {
          this.level()
              .broadcastEntityEvent(this, (byte) PARTICLE_EVENT_ID); // Trigger particle event
        }
      }
    }

    // Despawn during daylight or in bright areas
    if (!ModUtils.isNightOrDark(this)) {
      this.discard();
    }
  }

  @Override
  public void handleEntityEvent(byte id) {
    if (id == PARTICLE_EVENT_ID) {
      // Spawn smoke particles to hint at presence
      for (int i = 0; i < PARTICLE_SPAWN_COUNT; i++) {
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

package xyz.splack.tnc.extras.item;

import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import xyz.splack.tnc.extras.TncExtras;
import xyz.splack.tnc.extras.sound.ModSounds;

public enum ModArmorMaterials implements ArmorMaterial {
  COLDVEIN(
      "coldvein",
      38,
      new int[] {3, 6, 8, 3},
      13,
      ModSounds.COLDVEIN_ARMOR_EQUIP.get(),
      2.0f,
      0.05f,
      () -> Ingredient.of(ModItems.COLDVEIN.get()));

  // Base durability values for each armor slot: boots, leggings, chestplate, helmet.
  private static final int[] BASE_DURABILITY = {13, 15, 16, 11};

  private final String name;
  private final int durabilityMultiplier;
  private final int[] slotProtections;
  private final int enchantmentValue;
  private final SoundEvent equipSound;
  private final float toughness;
  private final float knockbackResistance;
  private final Supplier<Ingredient> repairIngredient;

  ModArmorMaterials(
      String name,
      int durabilityMultiplier,
      int[] slotProtections,
      int enchantmentValue,
      SoundEvent equipSound,
      float toughness,
      float knockbackResistance,
      Supplier<Ingredient> repairIngredient) {
    this.name = name;
    this.durabilityMultiplier = durabilityMultiplier;
    this.slotProtections = slotProtections;
    this.enchantmentValue = enchantmentValue;
    this.equipSound = equipSound;
    this.toughness = toughness;
    this.knockbackResistance = knockbackResistance;
    this.repairIngredient = repairIngredient;
  }

  @Override
  public int getDurabilityForType(ArmorItem.Type type) {
    return BASE_DURABILITY[type.ordinal()] * durabilityMultiplier;
  }

  @Override
  public int getDefenseForType(ArmorItem.Type type) {
    return slotProtections[type.ordinal()];
  }

  @Override
  public int getEnchantmentValue() {
    return enchantmentValue;
  }

  @Override
  public @NotNull SoundEvent getEquipSound() {
    return equipSound;
  }

  @Override
  public @NotNull Ingredient getRepairIngredient() {
    return repairIngredient.get();
  }

  @Override
  public @NotNull String getName() {
    return TncExtras.MOD_ID + ":" + name;
  }

  @Override
  public float getToughness() {
    return toughness;
  }

  @Override
  public float getKnockbackResistance() {
    return knockbackResistance;
  }
}

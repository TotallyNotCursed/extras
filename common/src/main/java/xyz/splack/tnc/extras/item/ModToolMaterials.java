package xyz.splack.tnc.extras.item;

import java.util.function.Supplier;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public enum ModToolMaterials implements Tier {
    COLDVEIN(4, 1700, 8.5f, 3.5f, 13, () -> Ingredient.of(ModItems.COLDVEIN.get()));
    private final int level, uses, enchantability;
    private final float speed, attackDamage;
    private final Supplier<Ingredient> repairIngredient;

    ModToolMaterials(
            int level,
            int uses,
            float speed,
            float attackDamage,
            int enchantability,
            Supplier<Ingredient> repairIngredient) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return attackDamage;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }
}

package dev.ruyadunyasi.ruya;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * Unicorn: zararsız, eğerli doğan, kendine özel sesleri olan at.
 * Ölünce sadece deri düşürür (bkz. loot_table/entities/unicorn.json), eyer düşürmez.
 */
public class Unicorn extends Horse {

    public Unicorn(EntityType<? extends Horse> type, Level level) {
        super(type, level);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        EntitySpawnReason reason, SpawnGroupData groupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, groupData);
        // Eğerli doğar ve hemen binilebilir
        this.setTamed(true);
        this.setItemSlot(EquipmentSlot.SADDLE, new ItemStack(Items.SADDLE));
        this.setDropChance(EquipmentSlot.SADDLE, 0.0F);
        return data;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return RuyaExtrasMod.UNICORN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return RuyaExtrasMod.UNICORN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return RuyaExtrasMod.UNICORN_DEATH;
    }

    @Override
    protected SoundEvent getAngrySound() {
        return RuyaExtrasMod.UNICORN_HURT;
    }
}

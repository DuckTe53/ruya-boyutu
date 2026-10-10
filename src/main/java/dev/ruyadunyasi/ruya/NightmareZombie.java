package dev.ruyadunyasi.ruya;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;

/**
 * Nightmare Zombisi: normal zombiden daha güçlü, karanlıkta parlayarak görünür,
 * güneşte yanmaz ve vurduğu hedefe Solma + Karanlık etkisi verir (bkz. RuyaBoyutuMod).
 */
public class NightmareZombie extends Zombie {

    public NightmareZombie(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
        // Karanlıkta daha görünür: dışına parlama çizgisi çizilir
        this.setGlowingTag(true);
    }

    public static AttributeSupplier.Builder createNightmareAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)            // normal zombi: 20
                .add(Attributes.ATTACK_DAMAGE, 7.0D)          // normal zombi: 3
                .add(Attributes.MOVEMENT_SPEED, 0.26D)        // normal zombi: 0.23
                .add(Attributes.ARMOR, 6.0D)                  // normal zombi: 2
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4D)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0D);
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }
}

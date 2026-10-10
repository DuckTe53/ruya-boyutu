package dev.ruyadunyasi.ruya;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.Level;

/**
 * Nightmare biyomunda görünen hareketsiz silüet (Herobrine / Steve).
 * Yapay zekası yok, ölümsüz ve sessiz. 15 saniye sonra ya da oyuncu 8 blok yaklaşınca kaybolur.
 */
public class SilhouetteEntity extends Zombie {
    private int lifetime = 20 * 15;

    public SilhouetteEntity(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
        this.setNoAi(true);
        this.setInvulnerable(true);
        this.setSilent(true);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createSilhouetteAttributes() {
        return Zombie.createAttributes();
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            this.lifetime--;
            if (this.lifetime <= 0 || this.level().getNearestPlayer(this, 8.0D) != null) {
                this.discard();
            }
        }
    }
}

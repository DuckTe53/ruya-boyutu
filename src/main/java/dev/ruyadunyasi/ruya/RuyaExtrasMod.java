package dev.ruyadunyasi.ruya;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Function;

/**
 * Unicorn, sesleri ve çağırma yumurtaları. Ayrı bir giriş noktası olduğu için
 * RuyaBoyutuMod.java dosyasına dokunmaz.
 */
public class RuyaExtrasMod implements ModInitializer {
    private static final String MOD_ID = "ruya_boyutu";

    // ---------- Sesler ----------
    public static final SoundEvent UNICORN_AMBIENT = registerSound("unicorn_ambient");
    public static final SoundEvent UNICORN_HURT = registerSound("unicorn_hurt");
    public static final SoundEvent UNICORN_DEATH = registerSound("unicorn_death");

    // ---------- Varlık ----------
    public static final EntityType<Unicorn> UNICORN = registerEntity("unicorn",
            EntityType.Builder.of(Unicorn::new, MobCategory.CREATURE)
                    .sized(1.3964844F, 1.6F)
                    .passengerAttachments(1.44375F)
                    .clientTrackingRange(10));

    // ---------- Çağırma yumurtaları ----------
    public static final Item UNICORN_SPAWN_EGG = registerItem("unicorn_spawn_egg", SpawnEggItem::new,
            new Item.Properties().spawnEgg(UNICORN));
    public static final Item NIGHTMARE_ZOMBIE_SPAWN_EGG = registerItem("nightmare_zombie_spawn_egg", SpawnEggItem::new,
            new Item.Properties().spawnEgg(RuyaBoyutuMod.NIGHTMARE_ZOMBIE));

    private static SoundEvent registerSound(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    private static <T extends Entity> EntityType<T> registerEntity(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, name));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
    }

    private static Item registerItem(String name, Function<Item.Properties, Item> factory, Item.Properties props) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(props.setId(key)));
    }

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(UNICORN, AbstractHorse.createBaseHorseAttributes());

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> {
            entries.accept(UNICORN_SPAWN_EGG);
            entries.accept(NIGHTMARE_ZOMBIE_SPAWN_EGG);
        });
    }
}

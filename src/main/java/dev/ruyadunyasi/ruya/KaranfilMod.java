package dev.ruyadunyasi.ruya;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

/**
 * Karanfil + Kloroful Kovası + Kloroful Kazanı.
 * Ayrı bir giriş noktası olduğu için RuyaBoyutuMod.java dosyasına dokunmaz.
 */
public class KaranfilMod implements ModInitializer {
    private static final String MOD_ID = "ruya_boyutu";

    public static final Block KARANFIL = registerBlock("karanfil", KaranfilBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY), true);

    public static final Block CHLOROFUL_CAULDRON = registerBlock("chloroful_cauldron", ChlorofulCauldronBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON), false);

    public static final Item CHLOROFUL_BUCKET = registerItem("chloroful_bucket", Item::new,
            new Item.Properties().stacksTo(1));

    private static Block registerBlock(String name, Function<BlockBehaviour.Properties, Block> factory,
                                       BlockBehaviour.Properties props, boolean withItem) {
        Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, name);
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        Block block = factory.apply(props.setId(blockKey));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

        if (withItem) {
            ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
            Registry.register(BuiltInRegistries.ITEM, itemKey,
                    new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
        }
        return block;
    }

    private static Item registerItem(String name, Function<Item.Properties, Item> factory, Item.Properties props) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(props.setId(key)));
    }

    @Override
    public void onInitialize() {
        ChlorofulCauldronBlock.registerInteractions();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries ->
                entries.accept(KARANFIL));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries ->
                entries.accept(CHLOROFUL_BUCKET));
    }
}

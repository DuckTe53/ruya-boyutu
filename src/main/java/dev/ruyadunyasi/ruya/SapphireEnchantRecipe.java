package dev.ruyadunyasi.ruya;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

/**
 * Çalışma masası: ortada büyülü kitap, köşeler boş, dört kenara (üst, alt, sol, sağ) Safir.
 * Kitaptaki her büyünün seviyesi 1 artar. Oyun seviyeyi en fazla 255'e kadar saklar.
 */
public class SapphireEnchantRecipe extends CustomRecipe {
    public static final RecipeSerializer<SapphireEnchantRecipe> SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(RuyaBoyutuMod.MOD_ID, "sapphire_enchant"),
            new CustomRecipe.Serializer<>(SapphireEnchantRecipe::new));

    /** Sınıfın erken yüklenip tarif türünün kaydolması için. */
    public static void init() {
    }

    public SapphireEnchantRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != 3 || input.height() != 3) return false;
        ItemStack book = input.getItem(4);
        if (!book.is(Items.ENCHANTED_BOOK)) return false;
        ItemEnchantments enchantments = book.get(DataComponents.STORED_ENCHANTMENTS);
        if (enchantments == null || enchantments.isEmpty()) return false;
        for (Holder<Enchantment> holder : enchantments.keySet()) {
            if (enchantments.getLevel(holder) >= 255) return false;
        }
        for (int slot : new int[]{1, 3, 5, 7}) {
            if (!input.getItem(slot).is(RuyaBoyutuMod.SAPPHIRE)) return false;
        }
        for (int slot : new int[]{0, 2, 6, 8}) {
            if (!input.getItem(slot).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack book = input.getItem(4);
        ItemEnchantments enchantments = book.get(DataComponents.STORED_ENCHANTMENTS);
        ItemStack result = book.copyWithCount(1);
        if (enchantments == null) return result;
        ItemEnchantments.Mutable upgraded = new ItemEnchantments.Mutable(enchantments);
        for (Holder<Enchantment> holder : enchantments.keySet()) {
            upgraded.set(holder, enchantments.getLevel(holder) + 1);
        }
        result.set(DataComponents.STORED_ENCHANTMENTS, upgraded.toImmutable());
        return result;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SERIALIZER;
    }
}

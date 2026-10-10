package dev.ruyadunyasi.ruya;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class RuyaBoyutuMod implements ModInitializer {
    public static final String MOD_ID = "ruya_boyutu";
    private static final String ORIGIN_TAG = "ruya_origin|";

    // Oyun saati: 0 = 06:00. Gece 03:00 = 21000 tick. Pencere yaklaşık 02:30 - 03:30.
    private static final long DREAM_WINDOW_START = 20500L;
    private static final long DREAM_WINDOW_END = 21500L;

    private static final Map<UUID, Long> ARMOR_TICKS = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> CORE_TICKS = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> CHLOROPHYLL_UNTIL = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> SHIELD_UNTIL = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> NIGHTMARE_SOUND_TICKS = new ConcurrentHashMap<>();

    public static final ResourceKey<Biome> NIGHTMARE_BIOME =
            ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(MOD_ID, "nightmare"));

    // ---------- Malzeme etiketleri ----------
    private static final TagKey<net.minecraft.world.item.Item> REPAIRS_SAPPHIRE =
            TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, "repairs_sapphire"));
    private static final TagKey<net.minecraft.world.item.Item> REPAIRS_PALLADIUM =
            TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, "repairs_palladium"));

    // ---------- Zırh ve alet malzemeleri ----------
    // Netherite: dayanıklılık 37, savunma 3/8/6/3, sağlamlık 3, geri tepme direnci 0.1
    // Safir  = Netherite x 0.98 | Paladyum = Netherite x 3 (oyun zırh sınırı toplam 30 puandır)
    private static final ResourceKey<EquipmentAsset> SAPPHIRE_ASSET = ResourceKey.create(EquipmentAssets.ROOT_ID,
            Identifier.fromNamespaceAndPath(MOD_ID, "sapphire"));
    private static final ResourceKey<EquipmentAsset> PALLADIUM_ASSET = ResourceKey.create(EquipmentAssets.ROOT_ID,
            Identifier.fromNamespaceAndPath(MOD_ID, "palladium"));

    public static final ArmorMaterial SAPPHIRE_ARMOR = new ArmorMaterial(36,
            Map.of(ArmorType.BOOTS, 3, ArmorType.LEGGINGS, 6, ArmorType.CHESTPLATE, 8, ArmorType.HELMET, 3, ArmorType.BODY, 19),
            15, SoundEvents.ARMOR_EQUIP_NETHERITE, 2.94F, 0.098F, REPAIRS_SAPPHIRE, SAPPHIRE_ASSET);
    public static final ArmorMaterial PALLADIUM_ARMOR = new ArmorMaterial(111,
            Map.of(ArmorType.BOOTS, 9, ArmorType.LEGGINGS, 18, ArmorType.CHESTPLATE, 24, ArmorType.HELMET, 9, ArmorType.BODY, 57),
            15, SoundEvents.ARMOR_EQUIP_NETHERITE, 9.0F, 0.3F, REPAIRS_PALLADIUM, PALLADIUM_ASSET);
    // Netherite aletleri: dayanıklılık 2031, hız 9.0, hasar bonusu 4.0
    public static final ToolMaterial SAPPHIRE_TOOLS = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            1990, 8.8F, 3.9F, 15, REPAIRS_SAPPHIRE);

    // ---------- Bloklar ----------
    public static final Block NIGHTMARE_LOG = registerBlock("nightmare_log", RotatedPillarBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG));
    public static final Block NIGHTMARE_PLANKS = registerBlock("nightmare_planks", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS));
    public static final Block NIGHTMARE_STAIRS = registerBlock("nightmare_stairs",
            p -> new StairBlock(NIGHTMARE_PLANKS.defaultBlockState(), p), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS));
    public static final Block NIGHTMARE_SLAB = registerBlock("nightmare_slab", SlabBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB));
    public static final Block NIGHTMARE_FENCE = registerBlock("nightmare_fence", FenceBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE));

    public static final Block DREAM_DIRT = registerBlock("dream_dirt", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT));
    public static final Block DREAM_GRASS_BLOCK = registerBlock("dream_grass_block", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK));
    public static final Block DREAM_LOG = registerBlock("dream_log", RotatedPillarBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG));
    public static final Block DREAM_LEAVES = registerBlock("dream_leaves", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(0.2F).sound(net.minecraft.world.level.block.SoundType.GRASS));
    public static final Block DREAM_PLANKS = registerBlock("dream_planks", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS));
    public static final Block DREAM_STAIRS = registerBlock("dream_stairs",
            p -> new StairBlock(DREAM_PLANKS.defaultBlockState(), p), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS));
    public static final Block DREAM_SLAB = registerBlock("dream_slab", SlabBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB));
    public static final Block DREAM_FENCE = registerBlock("dream_fence", FenceBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE));

    public static final Block SAPPHIRE_ORE = registerBlock("sapphire_ore", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_DIAMOND_ORE));
    public static final Block PALLADIUM_ORE = registerBlock("palladium_ore", Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE));

    // ---------- Eşyalar ----------
    public static final Item RAW_SAPPHIRE = register("raw_sapphire", Item::new, new Item.Properties());
    public static final Item SAPPHIRE = register("sapphire", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item PALLADIUM_SCRAP = register("palladium_scrap", Item::new, new Item.Properties());
    public static final Item PALLADIUM_INGOT = register("palladium_ingot", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item PALLADIUM_CORE = register("palladium_core", Item::new, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    public static final Item PALLADIUM_UPGRADE_TEMPLATE = register("palladium_upgrade_template", Item::new,
            new Item.Properties().rarity(Rarity.RARE));
    public static final Item CHLOROFUL = register("chloroful", Item::new, new Item.Properties().stacksTo(16).food(
            new FoodProperties.Builder().nutrition(0).saturationModifier(0.0F).alwaysEdible().build()
    ));

    // Safir aletleri
    public static final Item SAPPHIRE_SWORD = register("sapphire_sword", Item::new,
            new Item.Properties().sword(SAPPHIRE_TOOLS, 3.0F, -2.4F));
    public static final Item SAPPHIRE_PICKAXE = register("sapphire_pickaxe", Item::new,
            new Item.Properties().pickaxe(SAPPHIRE_TOOLS, 1.0F, -2.8F));
    public static final Item SAPPHIRE_AXE = register("sapphire_axe",
            p -> new AxeItem(SAPPHIRE_TOOLS, 5.0F, -3.0F, p), new Item.Properties());
    public static final Item SAPPHIRE_SHOVEL = register("sapphire_shovel",
            p -> new ShovelItem(SAPPHIRE_TOOLS, 1.5F, -3.0F, p), new Item.Properties());
    public static final Item SAPPHIRE_HOE = register("sapphire_hoe",
            p -> new HoeItem(SAPPHIRE_TOOLS, -4.0F, 0.0F, p), new Item.Properties());

    // Safir zırhı
    public static final Item SAPPHIRE_HELMET = register("sapphire_helmet", Item::new,
            new Item.Properties().humanoidArmor(SAPPHIRE_ARMOR, ArmorType.HELMET));
    public static final Item SAPPHIRE_CHESTPLATE = register("sapphire_chestplate", Item::new,
            new Item.Properties().humanoidArmor(SAPPHIRE_ARMOR, ArmorType.CHESTPLATE));
    public static final Item SAPPHIRE_LEGGINGS = register("sapphire_leggings", Item::new,
            new Item.Properties().humanoidArmor(SAPPHIRE_ARMOR, ArmorType.LEGGINGS));
    public static final Item SAPPHIRE_BOOTS = register("sapphire_boots", Item::new,
            new Item.Properties().humanoidArmor(SAPPHIRE_ARMOR, ArmorType.BOOTS));

    // Paladyum zırhı (Safir zırhından demirci masasında yükseltilir)
    public static final Item PALLADIUM_HELMET = register("palladium_helmet", Item::new,
            new Item.Properties().humanoidArmor(PALLADIUM_ARMOR, ArmorType.HELMET).rarity(Rarity.EPIC));
    public static final Item PALLADIUM_CHESTPLATE = register("palladium_chestplate", Item::new,
            new Item.Properties().humanoidArmor(PALLADIUM_ARMOR, ArmorType.CHESTPLATE).rarity(Rarity.EPIC));
    public static final Item PALLADIUM_LEGGINGS = register("palladium_leggings", Item::new,
            new Item.Properties().humanoidArmor(PALLADIUM_ARMOR, ArmorType.LEGGINGS).rarity(Rarity.EPIC));
    public static final Item PALLADIUM_BOOTS = register("palladium_boots", Item::new,
            new Item.Properties().humanoidArmor(PALLADIUM_ARMOR, ArmorType.BOOTS).rarity(Rarity.EPIC));

    // ---------- Sesler ----------
    public static final SoundEvent NIGHTMARE_SCREAM = registerSound("nightmare_scream");
    public static final SoundEvent SNORE = registerSound("snore");

    // ---------- Varlıklar ----------
    public static final EntityType<NightmareZombie> NIGHTMARE_ZOMBIE = registerEntity("nightmare_zombie",
            EntityType.Builder.of(NightmareZombie::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8));

    private static <T extends Entity> EntityType<T> registerEntity(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, name));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
    }

    private static Block registerBlock(String name, Function<BlockBehaviour.Properties, Block> factory,
                                       BlockBehaviour.Properties props) {
        Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, name);
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        Block block = factory.apply(props.setId(blockKey));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
        Registry.register(BuiltInRegistries.ITEM, itemKey,
                new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
        return block;
    }

    private static SoundEvent registerSound(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    private static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties props) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(props.setId(key)));
    }

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(NIGHTMARE_ZOMBIE, NightmareZombie.createNightmareAttributes());

        // Nightmare Zombisi'nin özel saldırısı: vurduğu hedefe Solma + Karanlık
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
            if (!blocked && source.getEntity() instanceof NightmareZombie) {
                entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 20 * 6, 0));
                entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 8, 0));
            }
        });

        // Yaratıcı envanter sekmeleri
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
            entries.accept(RAW_SAPPHIRE);
            entries.accept(SAPPHIRE);
            entries.accept(PALLADIUM_SCRAP);
            entries.accept(PALLADIUM_INGOT);
            entries.accept(PALLADIUM_UPGRADE_TEMPLATE);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(PALLADIUM_CORE);
            entries.accept(SAPPHIRE_PICKAXE);
            entries.accept(SAPPHIRE_AXE);
            entries.accept(SAPPHIRE_SHOVEL);
            entries.accept(SAPPHIRE_HOE);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> {
            entries.accept(SAPPHIRE_SWORD);
            entries.accept(SAPPHIRE_HELMET);
            entries.accept(SAPPHIRE_CHESTPLATE);
            entries.accept(SAPPHIRE_LEGGINGS);
            entries.accept(SAPPHIRE_BOOTS);
            entries.accept(PALLADIUM_HELMET);
            entries.accept(PALLADIUM_CHESTPLATE);
            entries.accept(PALLADIUM_LEGGINGS);
            entries.accept(PALLADIUM_BOOTS);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries ->
                entries.accept(CHLOROFUL));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
            entries.accept(DREAM_PLANKS);
            entries.accept(DREAM_STAIRS);
            entries.accept(DREAM_SLAB);
            entries.accept(DREAM_FENCE);
            entries.accept(NIGHTMARE_PLANKS);
            entries.accept(NIGHTMARE_STAIRS);
            entries.accept(NIGHTMARE_SLAB);
            entries.accept(NIGHTMARE_FENCE);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> {
            entries.accept(DREAM_GRASS_BLOCK);
            entries.accept(DREAM_DIRT);
            entries.accept(DREAM_LOG);
            entries.accept(DREAM_LEAVES);
            entries.accept(NIGHTMARE_LOG);
            entries.accept(SAPPHIRE_ORE);
            entries.accept(PALLADIUM_ORE);
        });

        // Rüya boyutunda düşme hasarı kapalı
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (source.is(DamageTypeTags.IS_FALL) && isInDream(entity.level().dimension())) {
                return false;
            }
            return true;
        });

        // /ruya uyan -> rüyadan uyan, eski yerine dön
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(Commands.literal("ruya")
                        .then(Commands.literal("uyan").executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            if (!isInDream(player.level().dimension())) {
                                player.sendSystemMessage(Component.literal("Zaten uyanıksın."));
                                return 0;
                            }
                            wakeUp(player);
                            return 1;
                        }))));

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                tickSleep(server, player);
                tickPlayer(player);
                tickNightmareAmbience(player);
            }
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(CHLOROFUL) && !world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                CHLOROPHYLL_UNTIL.put(player.getUUID(), world.getGameTime() + 15L * 60L * 20L);
                serverPlayer.removeEffect(MobEffects.POISON);
            }
            return InteractionResult.PASS;
        });
    }

    // ================= RÜYA BOYUTU =================

    private static boolean isInDream(ResourceKey<Level> dim) {
        return dim.identifier().equals(Identifier.fromNamespaceAndPath(MOD_ID, "dream"));
    }

    private static void runAs(MinecraftServer server, ServerPlayer player, String dimension, String tpArgs) {
        String cmd = "execute as " + player.getUUID() + " in " + dimension + " run tp @s " + tpArgs;
        server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), cmd);
    }

    /** Gece 03:00 civarında uyuyan oyuncu horlar ve Rüya boyutuna geçer. */
    private static void tickSleep(MinecraftServer server, ServerPlayer player) {
        if (!player.isSleeping()) return;
        if (isInDream(player.level().dimension())) return;
        if (!player.level().dimension().equals(Level.OVERWORLD)) return;

        long timeOfDay = player.level().getDayTime() % 24000L;
        if (timeOfDay < DREAM_WINDOW_START || timeOfDay > DREAM_WINDOW_END) return;

        int timer = player.getSleepTimer();
        if (timer == 25) {
            player.level().playSound(null, player.blockPosition(), SNORE, SoundSource.PLAYERS, 1.0F, 0.9F);
        }
        if (timer >= 65) {
            rememberOrigin(player);
            player.stopSleeping();
            runAs(server, player, MOD_ID + ":dream", "0 215 0");
            player.sendSystemMessage(Component.literal("Rüya Boyutu'na daldın... Uyanmak için /ruya uyan"));
        }
    }

    private static void rememberOrigin(ServerPlayer player) {
        for (String t : new ArrayList<>(player.getTags())) {
            if (t.startsWith(ORIGIN_TAG)) player.removeTag(t);
        }
        player.addTag(ORIGIN_TAG + player.getBlockX() + "|" + player.getBlockY() + "|" + player.getBlockZ());
    }

    private static void wakeUp(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        if (server == null) return;
        String target = null;
        for (String t : player.getTags()) {
            if (t.startsWith(ORIGIN_TAG)) {
                String[] p = t.substring(ORIGIN_TAG.length()).split("\\|");
                if (p.length == 3) target = p[0] + " " + p[1] + " " + p[2];
            }
        }
        if (target == null) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20 * 40, 0, false, false, false));
            target = "0 250 0";
        }
        runAs(server, player, "minecraft:overworld", target);
        player.sendSystemMessage(Component.literal("Uyandın."));
    }

    private static void tickNightmareAmbience(ServerPlayer player) {
        // Kâbus biyomunda arada korkunç çığlık sesi
        if (!player.level().getBiome(player.blockPosition()).is(NIGHTMARE_BIOME)) return;
        long now = player.level().getGameTime();
        UUID id = player.getUUID();
        long next = NIGHTMARE_SOUND_TICKS.getOrDefault(id, now + 20L * 45L);
        if (now >= next) {
            player.level().playSound(null, player.blockPosition(), NIGHTMARE_SCREAM, SoundSource.AMBIENT, 0.8F, 0.75F + player.getRandom().nextFloat() * 0.5F);
            NIGHTMARE_SOUND_TICKS.put(id, now + 20L * (45L + player.getRandom().nextInt(76)));
        }
    }

    // ================= ÇEKİRDEK / ZIRH =================

    private static boolean hasItem(ServerPlayer player, Item item) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).is(item)) return true;
        }
        return false;
    }

    private static void tickPlayer(ServerPlayer player) {
        long now = player.level().getGameTime();
        UUID id = player.getUUID();

        // Paladyum zırhı: ateş ve karanlık direnci, her 11 dakikada paladyum zehirlenmesi
        boolean wearingPalladium = isWearingPalladiumArmor(player);
        if (wearingPalladium) {
            if (now % 20L == 0L) {
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, false, false, true));
                player.removeEffect(MobEffects.DARKNESS);
            }
            long start = ARMOR_TICKS.computeIfAbsent(id, ignored -> now);
            if (now - start >= 11L * 60L * 20L) {
                if (CHLOROPHYLL_UNTIL.getOrDefault(id, 0L) <= now) {
                    player.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 30, 0, false, true, true));
                }
                ARMOR_TICKS.put(id, now);
            }
        } else {
            ARMOR_TICKS.remove(id);
        }

        // Paladyum Çekirdeği: 3 kalbin altında 100 HP koruma, her 15 dakikada zehirlenme
        boolean hasCore = hasItem(player, PALLADIUM_CORE);
        if (hasCore && player.getHealth() < 6.0F) {
            long until = SHIELD_UNTIL.getOrDefault(id, 0L);
            if (until <= now) {
                SHIELD_UNTIL.put(id, now + 20L * 60L * 10L);
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 20 * 60 * 10, 24, false, true, true));
            }
        }

        if (hasCore) {
            long start = CORE_TICKS.computeIfAbsent(id, ignored -> now);
            if (now - start >= 15L * 60L * 20L) {
                if (CHLOROPHYLL_UNTIL.getOrDefault(id, 0L) <= now) {
                    player.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 30, 0, false, true, true));
                }
                CORE_TICKS.put(id, now);
            }
        } else {
            CORE_TICKS.remove(id);
            SHIELD_UNTIL.remove(id);
        }
    }

    private static boolean isWearingPalladiumArmor(ServerPlayer player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(PALLADIUM_HELMET)
                || player.getItemBySlot(EquipmentSlot.CHEST).is(PALLADIUM_CHESTPLATE)
                || player.getItemBySlot(EquipmentSlot.LEGS).is(PALLADIUM_LEGGINGS)
                || player.getItemBySlot(EquipmentSlot.FEET).is(PALLADIUM_BOOTS);
    }
}

package dev.ruyadunyasi.ruya;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RuyaBoyutuMod implements ModInitializer {
    public static final String MOD_ID = "ruya_boyutu";
    private static final String ORIGIN_TAG = "ruya_origin|";

    // Oyun saati: 0 = 06:00. Gece 03:00 = 21000 tick. Pencere yaklaşık 02:30 - 03:30.
    private static final long DREAM_WINDOW_START = 20500L;
    private static final long DREAM_WINDOW_END = 21500L;

    private static final Map<UUID, Long> ARMOR_TICKS = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> REACTOR_TICKS = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> CHLOROPHYLL_UNTIL = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> SHIELD_UNTIL = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> NIGHTMARE_SOUND_TICKS = new ConcurrentHashMap<>();

    public static final ResourceKey<Biome> NIGHTMARE_BIOME =
            ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(MOD_ID, "nightmare"));

    // ---------- Bloklar ----------
    public static final Block NIGHTMARE_LOG = registerBlock("nightmare_log", true,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG));
    public static final Block NIGHTMARE_PLANKS = registerBlock("nightmare_planks", false,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS));
    public static final Block DREAM_DIRT = registerBlock("dream_dirt", false,
            BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT));
    public static final Block DREAM_GRASS_BLOCK = registerBlock("dream_grass_block", false,
            BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK));
    public static final Block SAPPHIRE_ORE = registerBlock("sapphire_ore", false,
            BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_DIAMOND_ORE));
    public static final Block PALLADIUM_ORE = registerBlock("palladium_ore", false,
            BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE));

    // ---------- Eşyalar ----------
    public static final Item RAW_SAPPHIRE = register("raw_sapphire", new Item.Properties());
    public static final Item SAPPHIRE = register("sapphire", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item PALLADIUM_SCRAP = register("palladium_scrap", new Item.Properties());
    public static final Item PALLADIUM_INGOT = register("palladium_ingot", new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final Item ARC_REACTOR = register("arc_reactor", new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    public static final Item CHLOROFUL = register("chloroful", new Item.Properties().stacksTo(16).food(
            new FoodProperties.Builder().nutrition(0).saturationModifier(0.0F).alwaysEdible().build()
    ));

    // ---------- Sesler ----------
    public static final SoundEvent NIGHTMARE_SCREAM = registerSound("nightmare_scream");
    public static final SoundEvent SNORE = registerSound("snore");

    private static Block registerBlock(String name, boolean pillar, BlockBehaviour.Properties props) {
        Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, name);
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        props = props.setId(blockKey);
        Block block = pillar ? new RotatedPillarBlock(props) : new Block(props);
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

    private static Item register(String name, Item.Properties props) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
        return Registry.register(BuiltInRegistries.ITEM, key, new Item(props.setId(key)));
    }

    @Override
    public void onInitialize() {
        // Yaratıcı envanter sekmeleri
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
            entries.accept(RAW_SAPPHIRE);
            entries.accept(SAPPHIRE);
            entries.accept(PALLADIUM_SCRAP);
            entries.accept(PALLADIUM_INGOT);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries ->
                entries.accept(ARC_REACTOR));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries ->
                entries.accept(CHLOROFUL));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries ->
                entries.accept(NIGHTMARE_PLANKS));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> {
            entries.accept(DREAM_GRASS_BLOCK);
            entries.accept(DREAM_DIRT);
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

    private static boolean isInDream(ResourceKey<net.minecraft.world.level.Level> dim) {
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
        if (!player.level().dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) return;

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
            // Güvenli düşüş için yavaş düşme ver
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

    // ================= REAKTÖR / ZIRH =================

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

        boolean wearingPalladium = isWearingPalladiumArmor(player);
        if (wearingPalladium) {
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

        boolean hasReactor = hasItem(player, ARC_REACTOR);
        if (hasReactor && player.getHealth() < 6.0F) {
            long until = SHIELD_UNTIL.getOrDefault(id, 0L);
            if (until <= now) {
                // 100 HP koruma: Emilim seviye 25 = 100 HP. Süre aynı zamanda bekleme süresi.
                SHIELD_UNTIL.put(id, now + 20L * 60L * 10L);
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 20 * 60 * 10, 24, false, true, true));
            }
        }

        if (hasReactor) {
            long start = REACTOR_TICKS.computeIfAbsent(id, ignored -> now);
            if (now - start >= 15L * 60L * 20L) {
                if (CHLOROPHYLL_UNTIL.getOrDefault(id, 0L) <= now) {
                    player.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 30, 0, false, true, true));
                }
                REACTOR_TICKS.put(id, now);
            }
        } else {
            REACTOR_TICKS.remove(id);
            SHIELD_UNTIL.remove(id);
        }
    }

    private static boolean isWearingPalladiumArmor(ServerPlayer player) {
        // Zırh eşyaları sonraki aşamada eklenecek.
        return false;
    }
}

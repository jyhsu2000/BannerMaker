package club.kid7.bannermaker.util;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.configuration.ConfigManager;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BannerMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BannerUtilTest {

    private ServerMock server;
    private BannerMaker plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(BannerMaker.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
        ConfigManager.reset();
    }

    @Test
    void isBanner_ShouldReturnTrueForBanners() {
        assertTrue(BannerUtil.isBanner(new ItemStack(Material.WHITE_BANNER)));
        assertTrue(BannerUtil.isBanner(new ItemStack(Material.BLACK_BANNER)));
        assertTrue(BannerUtil.isBanner(new ItemStack(Material.BLUE_BANNER)));
    }

    @Test
    void isBanner_ShouldReturnFalseForNonBanners() {
        assertFalse(BannerUtil.isBanner(new ItemStack(Material.STONE)));
        assertFalse(BannerUtil.isBanner(new ItemStack(Material.STICK)));
        assertFalse(BannerUtil.isBanner((ItemStack) null));
    }

    @Test
    void isBannerPatternItemStack_ShouldReturnTrueForPatterns() {
        assertTrue(BannerUtil.isBannerPatternItemStack(new ItemStack(Material.CREEPER_BANNER_PATTERN)));
        assertTrue(BannerUtil.isBannerPatternItemStack(new ItemStack(Material.FLOWER_BANNER_PATTERN)));
    }

    @Test
    void isBannerPatternItemStack_ShouldReturnFalseForNonPatterns() {
        assertFalse(BannerUtil.isBannerPatternItemStack(new ItemStack(Material.STONE)));
        assertFalse(BannerUtil.isBannerPatternItemStack(new ItemStack(Material.WHITE_BANNER)));
    }

    @Test
    void isCraftableInSurvival_ShouldReturnTrueForFewPatterns() {
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_BOTTOM));
        banner.setItemMeta(meta);

        assertTrue(BannerUtil.isCraftableInSurvival(banner));
    }

    @Test
    void isCraftableInSurvival_ShouldReturnFalseForManyPatterns() {
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        for (int i = 0; i < 7; i++) {
            meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_BOTTOM));
        }
        banner.setItemMeta(meta);

        assertFalse(BannerUtil.isCraftableInSurvival(banner));
    }

    @Test
    void getMaterials_ShouldReturnCorrectMaterials() {
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        // 增加一個需要 1 個染料的 pattern
        meta.addPattern(new Pattern(DyeColor.RED, PatternType.CIRCLE));
        // 增加一個需要 3 個染料的 pattern
        meta.addPattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_BOTTOM));
        banner.setItemMeta(meta);

        List<ItemStack> materials = BannerUtil.getMaterials(banner);

        // 預期：1 木棒 + 6 白色羊毛 + 1 紅色染料 + 3 藍色染料
        assertNotNull(materials);

        // 驗證是否有基本材料
        boolean hasStick = materials.stream().anyMatch(i -> i.getType() == Material.STICK && i.getAmount() == 1);
        boolean hasWool = materials.stream().anyMatch(i -> i.getType() == Material.WHITE_WOOL && i.getAmount() == 6);

        // 驗證染料
        boolean hasRedDye = materials.stream().anyMatch(i -> i.getType() == Material.RED_DYE && i.getAmount() == 1);
        boolean hasBlueDye = materials.stream().anyMatch(i -> i.getType() == Material.BLUE_DYE && i.getAmount() == 3);

        assertTrue(hasStick, "Should contain 1 stick");
        assertTrue(hasWool, "Should contain 6 white wool");
        assertTrue(hasRedDye, "Should contain 1 red dye");
        assertTrue(hasBlueDye, "Should contain 3 blue dye");
    }

    @Test
    void hasEnoughMaterials_ShouldReturnTrue_WhenPlayerHasAllMaterials() {
        // 白色旗幟 + 紅色圓形 pattern
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.RED, PatternType.CIRCLE));
        banner.setItemMeta(meta);

        // 在物品欄中放入足夠的材料
        PlayerMock player = server.addPlayer("MaterialPlayer");
        PlayerInventory inv = player.getInventory();
        inv.addItem(new ItemStack(Material.STICK, 1));
        inv.addItem(new ItemStack(Material.WHITE_WOOL, 6));
        inv.addItem(new ItemStack(Material.RED_DYE, 1));

        assertTrue(BannerUtil.hasEnoughMaterials(inv, banner));
    }

    @Test
    void hasEnoughMaterials_ShouldReturnFalse_WhenPlayerMissingMaterials() {
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.RED, PatternType.CIRCLE));
        banner.setItemMeta(meta);

        // 物品欄為空
        PlayerMock player = server.addPlayer("EmptyPlayer");

        assertFalse(BannerUtil.hasEnoughMaterials(player.getInventory(), banner));
    }

    @Test
    void hasEnoughMaterials_ShouldReturnFalse_WhenPartialMaterials() {
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.RED, PatternType.CIRCLE));
        banner.setItemMeta(meta);

        // 只有木棒，缺少羊毛和染料
        PlayerMock player = server.addPlayer("PartialPlayer");
        player.getInventory().addItem(new ItemStack(Material.STICK, 1));

        assertFalse(BannerUtil.hasEnoughMaterials(player.getInventory(), banner));
    }

    @Test
    void craft_ShouldSucceed_WhenPlayerHasEnoughMaterials() {
        // 白色旗幟 + 紅色圓形 pattern
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.RED, PatternType.CIRCLE));
        banner.setItemMeta(meta);

        // 給予足夠材料
        PlayerMock player = server.addPlayer("CraftPlayer");
        PlayerInventory inv = player.getInventory();
        inv.addItem(new ItemStack(Material.STICK, 1));
        inv.addItem(new ItemStack(Material.WHITE_WOOL, 6));
        inv.addItem(new ItemStack(Material.RED_DYE, 1));

        boolean result = BannerUtil.craft(player, banner);

        assertTrue(result, "合成應該成功");
        // 材料應被消耗（木棒和染料被移除，羊毛被移除）
        assertFalse(inv.containsAtLeast(new ItemStack(Material.STICK), 1), "木棒應被消耗");
        assertFalse(inv.containsAtLeast(new ItemStack(Material.WHITE_WOOL), 6), "羊毛應被消耗");
        assertFalse(inv.containsAtLeast(new ItemStack(Material.RED_DYE), 1), "染料應被消耗");
        // 玩家應獲得旗幟
        assertTrue(inv.containsAtLeast(new ItemStack(Material.WHITE_BANNER), 1), "玩家應獲得旗幟");
    }

    @Test
    void craft_ShouldFail_WhenPlayerHasInsufficientMaterials() {
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.RED, PatternType.CIRCLE));
        banner.setItemMeta(meta);

        // 材料不足
        PlayerMock player = server.addPlayer("NoCraftPlayer");

        boolean result = BannerUtil.craft(player, banner);

        assertFalse(result, "材料不足時合成應該失敗");
        // 玩家不應獲得旗幟
        assertFalse(inv(player).containsAtLeast(new ItemStack(Material.WHITE_BANNER), 1), "玩家不應獲得旗幟");
    }

    private PlayerInventory inv(PlayerMock player) {
        return player.getInventory();
    }

    @Test
    void serializeAndDeserialize_ShouldWorkCorrectly() {
        ItemStack originalBanner = new ItemStack(Material.RED_BANNER);
        BannerMeta meta = (BannerMeta) originalBanner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.CROSS));
        originalBanner.setItemMeta(meta);

        String serialized = BannerUtil.serialize(originalBanner);
        assertNotNull(serialized);

        ItemStack deserializedBanner = BannerUtil.deserialize(serialized);
        assertNotNull(deserializedBanner);

        assertEquals(originalBanner.getType(), deserializedBanner.getType());
        BannerMeta deserializedMeta = (BannerMeta) deserializedBanner.getItemMeta();
        assertEquals(meta.numberOfPatterns(), deserializedMeta.numberOfPatterns());
        assertEquals(meta.getPattern(0), deserializedMeta.getPattern(0));
    }
}

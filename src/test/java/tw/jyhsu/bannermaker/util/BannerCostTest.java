package tw.jyhsu.bannermaker.util;

import tw.jyhsu.bannermaker.BannerMaker;
import tw.jyhsu.bannermaker.configuration.ConfigManager;
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
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BannerCostTest {

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
    void getMaterials_ReturnsCorrectMaterialsForMultiPatternBanner() {
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.RED, PatternType.CIRCLE));        // 1 染料
        meta.addPattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_BOTTOM)); // 3 染料
        banner.setItemMeta(meta);

        List<ItemStack> materials = BannerCost.getMaterials(banner);

        assertNotNull(materials);
        boolean hasStick = materials.stream().anyMatch(i -> i.getType() == Material.STICK && i.getAmount() == 1);
        boolean hasWool = materials.stream().anyMatch(i -> i.getType() == Material.WHITE_WOOL && i.getAmount() == 6);
        boolean hasRedDye = materials.stream().anyMatch(i -> i.getType() == Material.RED_DYE && i.getAmount() == 1);
        boolean hasBlueDye = materials.stream().anyMatch(i -> i.getType() == Material.BLUE_DYE && i.getAmount() == 3);

        assertTrue(hasStick, "Should contain 1 stick");
        assertTrue(hasWool, "Should contain 6 white wool");
        assertTrue(hasRedDye, "Should contain 1 red dye");
        assertTrue(hasBlueDye, "Should contain 3 blue dye");
    }

    @Test
    void getMaterials_BricksPattern_UsesFieldMasonedOnModernServer() {
        // 1.21.2+ 的 loom 改用 FIELD_MASONED_BANNER_PATTERN 物品產生 bricks pattern。
        // 測試環境綁定 paper-api 1.21.4，必定走此分支。
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.RED, PatternType.BRICKS));
        banner.setItemMeta(meta);

        List<ItemStack> materials = BannerCost.getMaterials(banner);

        Material fieldMasoned = Material.matchMaterial("FIELD_MASONED_BANNER_PATTERN");
        assertNotNull(fieldMasoned, "測試環境（paper-api 1.21.4）應有 FIELD_MASONED_BANNER_PATTERN");
        assertTrue(
            materials.stream().anyMatch(i -> i.getType() == fieldMasoned),
            "1.21.2+ 應顯示 FIELD_MASONED_BANNER_PATTERN 作為 BRICKS pattern 的合成材料"
        );
        assertFalse(
            materials.stream().anyMatch(i -> i.getType() == Material.BRICK),
            "1.21.2+ 不應再顯示 BRICK 作為 BRICKS pattern 的合成材料"
        );
    }

    @Test
    void getMaterials_BricksPattern_FallsBackToBrickWhenFieldMasonedAbsent() {
        // 模擬 1.21.0 / 1.21.1 環境：FIELD_MASONED_BANNER_PATTERN 物品尚未存在。
        try (MockedStatic<Material> mocked = Mockito.mockStatic(Material.class, Mockito.CALLS_REAL_METHODS)) {
            mocked.when(() -> Material.matchMaterial("FIELD_MASONED_BANNER_PATTERN")).thenReturn(null);

            ItemStack banner = new ItemStack(Material.WHITE_BANNER);
            BannerMeta meta = (BannerMeta) banner.getItemMeta();
            meta.addPattern(new Pattern(DyeColor.RED, PatternType.BRICKS));
            banner.setItemMeta(meta);

            List<ItemStack> materials = BannerCost.getMaterials(banner);

            assertTrue(
                materials.stream().anyMatch(i -> i.getType() == Material.BRICK),
                "舊版（1.21.0 / 1.21.1）找不到 FIELD_MASONED_BANNER_PATTERN 時應 fallback 至 BRICK"
            );
        }
    }

    @Test
    void hasEnoughMaterials_ReturnsTrue_WhenPlayerHasAllMaterials() {
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.RED, PatternType.CIRCLE));
        banner.setItemMeta(meta);

        PlayerMock player = server.addPlayer("MaterialPlayer");
        PlayerInventory inv = player.getInventory();
        inv.addItem(new ItemStack(Material.STICK, 1));
        inv.addItem(new ItemStack(Material.WHITE_WOOL, 6));
        inv.addItem(new ItemStack(Material.RED_DYE, 1));

        assertTrue(BannerCost.hasEnoughMaterials(inv, banner));
    }

    @Test
    void hasEnoughMaterials_ReturnsFalse_WhenPlayerMissingMaterials() {
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.RED, PatternType.CIRCLE));
        banner.setItemMeta(meta);

        PlayerMock player = server.addPlayer("EmptyPlayer");

        assertFalse(BannerCost.hasEnoughMaterials(player.getInventory(), banner));
    }

    @Test
    void hasEnoughMaterials_ReturnsFalse_WhenPartialMaterials() {
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.RED, PatternType.CIRCLE));
        banner.setItemMeta(meta);

        PlayerMock player = server.addPlayer("PartialPlayer");
        player.getInventory().addItem(new ItemStack(Material.STICK, 1));

        assertFalse(BannerCost.hasEnoughMaterials(player.getInventory(), banner));
    }
}

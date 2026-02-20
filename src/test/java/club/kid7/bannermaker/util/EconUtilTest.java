package club.kid7.bannermaker.util;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.configuration.ConfigManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class EconUtilTest {

    private ServerMock server;
    private BannerMaker plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(BannerMaker.class);
    }

    @AfterEach
    void tearDown() {
        ConfigManager.reset();
        MockBukkit.unmock();
    }

    @Test
    void getPrice_ShouldReturnZero_ForNonBanner() {
        plugin.econ = mock(Economy.class);
        ItemStack stone = new ItemStack(Material.STONE);

        double price = EconUtil.getPrice(stone);

        assertEquals(0, price, "非旗幟物品的價格應為 0");
    }

    @Test
    void getPrice_ShouldReturnZero_WhenEconomyDisabled() {
        // econ 為 null（未啟用經濟）
        plugin.econ = null;
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);

        double price = EconUtil.getPrice(banner);

        assertEquals(0, price, "經濟未啟用時價格應為 0");
    }

    @Test
    void getPrice_ShouldReturnBasePrice_WhenMaterialPricesAreZero() {
        plugin.econ = mock(Economy.class);

        // 設定基礎價格
        FileConfiguration config = ConfigManager.get("config");
        config.set("Economy.Price", 100.0);

        ItemStack banner = new ItemStack(Material.WHITE_BANNER);

        double price = EconUtil.getPrice(banner);

        // 基礎價格 100 + 所有材料價格 0 = 100
        assertEquals(100.0, price, 0.01, "應回傳基礎價格 100");
    }

    @Test
    void getPrice_ShouldIncludeMaterialPrices() {
        plugin.econ = mock(Economy.class);

        // 設定基礎價格
        FileConfiguration config = ConfigManager.get("config");
        config.set("Economy.Price", 50.0);

        // 設定材料價格
        FileConfiguration priceConfig = ConfigManager.get("price");
        priceConfig.set("STICK", 1.0);
        priceConfig.set("WOOL.WHITE", 2.0);
        priceConfig.set("DYE.RED", 5.0);

        // 白色旗幟 + 紅色圓形 pattern
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.RED, PatternType.CIRCLE));
        banner.setItemMeta(meta);

        double price = EconUtil.getPrice(banner);

        // 基礎價格 50 + 木棒 1*1 + 白色羊毛 2*6 + 紅色染料 5*1 = 50 + 1 + 12 + 5 = 68
        assertEquals(68.0, price, 0.01, "價格應包含基礎價格與材料價格");
    }

    @Test
    void getPrice_ShouldHandleMultiplePatterns() {
        plugin.econ = mock(Economy.class);

        FileConfiguration config = ConfigManager.get("config");
        config.set("Economy.Price", 0.0);

        FileConfiguration priceConfig = ConfigManager.get("price");
        priceConfig.set("STICK", 0.0);
        priceConfig.set("WOOL.WHITE", 0.0);
        priceConfig.set("DYE.RED", 10.0);
        priceConfig.set("DYE.BLUE", 20.0);

        // 白色旗幟 + 紅色圓形(1 染料) + 藍色底部條紋(3 染料)
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.RED, PatternType.CIRCLE));
        meta.addPattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_BOTTOM));
        banner.setItemMeta(meta);

        double price = EconUtil.getPrice(banner);

        // 紅色染料 10*1 + 藍色染料 20*3 = 10 + 60 = 70
        assertEquals(70.0, price, 0.01, "價格應正確累加多個 pattern 的材料價格");
    }
}

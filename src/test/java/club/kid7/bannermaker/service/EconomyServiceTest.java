package club.kid7.bannermaker.service;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.configuration.ConfigManager;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * EconomyService 的測試僅覆蓋「Vault 不可用」路徑。
 * MockBukkit 不提供 Vault economy provider，無法在單元測試中模擬實際扣款流程。
 */
class EconomyServiceTest {

    private ServerMock server;
    private BannerMaker plugin;
    private EconomyService economyService;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(BannerMaker.class);
        economyService = plugin.getEconomyService();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
        ConfigManager.reset();
    }

    @Test
    void isAvailable_ReturnsFalse_WhenNoVault() {
        assertFalse(economyService.isAvailable(),
            "測試環境無 Vault economy provider，isAvailable 應為 false");
    }

    @Test
    void getPrice_ReturnsZero_ForNonBanner() {
        ItemStack stone = new ItemStack(Material.STONE);
        assertEquals(0.0, economyService.getPrice(stone),
            "非旗幟物品應回傳 0");
    }

    @Test
    void getPrice_ReturnsZero_WhenEconomyUnavailable() {
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.RED, PatternType.CIRCLE));
        banner.setItemMeta(meta);

        // 即便是合法旗幟，Vault 不可用時 getPrice 應 short-circuit 為 0
        assertEquals(0.0, economyService.getPrice(banner),
            "Vault 不可用時應回傳 0，即便旗幟合法");
    }
}

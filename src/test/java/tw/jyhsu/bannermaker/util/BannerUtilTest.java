package tw.jyhsu.bannermaker.util;

import tw.jyhsu.bannermaker.BannerMaker;
import tw.jyhsu.bannermaker.configuration.ConfigManager;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
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
}

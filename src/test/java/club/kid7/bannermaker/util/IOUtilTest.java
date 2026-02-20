package club.kid7.bannermaker.util;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.configuration.ConfigManager;
import club.kid7.bannermaker.service.BannerRepository;
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
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IOUtilTest {

    private ServerMock server;
    private BannerMaker plugin;
    private PlayerMock player;
    private BannerRepository bannerRepository;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(BannerMaker.class);
        player = server.addPlayer("IOTestPlayer");
        bannerRepository = plugin.getBannerRepository();
    }

    @AfterEach
    void tearDown() {
        ConfigManager.reset();
        MockBukkit.unmock();
    }

    private ItemStack createTestBanner() {
        ItemStack banner = new ItemStack(Material.RED_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.CROSS));
        meta.addPattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_BOTTOM));
        banner.setItemMeta(meta);
        return banner;
    }

    @Test
    void saveBanner_ShouldReturnTrue_ForValidBanner() {
        ItemStack banner = createTestBanner();

        boolean result = bannerRepository.saveBanner(player, banner);

        assertTrue(result, "儲存有效旗幟應成功");
    }

    @Test
    void saveBanner_ShouldReturnFalse_ForNonBanner() {
        ItemStack stone = new ItemStack(Material.STONE);

        boolean result = bannerRepository.saveBanner(player, stone);

        assertFalse(result, "儲存非旗幟物品應失敗");
    }

    @Test
    void loadBannerList_ShouldReturnSavedBanners() {
        ItemStack banner = createTestBanner();
        bannerRepository.saveBanner(player, banner);

        List<ItemStack> bannerList = bannerRepository.loadBannerList(player);

        assertFalse(bannerList.isEmpty(), "讀取清單不應為空");
        assertEquals(1, bannerList.size(), "應只有一面旗幟");

        // 驗證旗幟類型與 pattern
        ItemStack loaded = bannerList.get(0);
        assertEquals(Material.RED_BANNER, loaded.getType(), "旗幟類型應一致");

        BannerMeta loadedMeta = (BannerMeta) loaded.getItemMeta();
        assertNotNull(loadedMeta);
        assertEquals(2, loadedMeta.numberOfPatterns(), "應有 2 個 pattern");
        assertEquals(DyeColor.WHITE, loadedMeta.getPattern(0).getColor(), "第一個 pattern 顏色應為白色");
        assertEquals(PatternType.CROSS, loadedMeta.getPattern(0).getPattern(), "第一個 pattern 類型應為 CROSS");
        assertEquals(DyeColor.BLUE, loadedMeta.getPattern(1).getColor(), "第二個 pattern 顏色應為藍色");
        assertEquals(PatternType.STRIPE_BOTTOM, loadedMeta.getPattern(1).getPattern(), "第二個 pattern 類型應為 STRIPE_BOTTOM");
    }

    @Test
    void removeBanner_ShouldRemoveFromStorage() {
        ItemStack banner = createTestBanner();
        bannerRepository.saveBanner(player, banner);

        // 讀取已存的旗幟以取得 key
        List<ItemStack> bannerList = bannerRepository.loadBannerList(player);
        assertEquals(1, bannerList.size());
        String key = BannerUtil.getKey(bannerList.get(0));
        assertNotNull(key, "旗幟應有 key");

        // 刪除
        boolean removed = bannerRepository.removeBanner(player, key);
        assertTrue(removed, "刪除應成功");

        // 再次讀取，應為空
        List<ItemStack> afterRemoval = bannerRepository.loadBannerList(player);
        assertTrue(afterRemoval.isEmpty(), "刪除後清單應為空");
    }

    @Test
    void getBannerCount_ShouldReturnCorrectCount() throws InterruptedException {
        assertEquals(0, bannerRepository.getBannerCount(player), "初始應為 0");

        bannerRepository.saveBanner(player, createTestBanner());
        // 等待 1ms 確保時間戳不同
        Thread.sleep(1);
        bannerRepository.saveBanner(player, new ItemStack(Material.WHITE_BANNER));

        assertEquals(2, bannerRepository.getBannerCount(player), "儲存兩面旗幟後應為 2");
    }

    @Test
    void loadBannerList_ShouldReturnEmpty_WhenNoSavedBanners() {
        List<ItemStack> bannerList = bannerRepository.loadBannerList(player);

        assertTrue(bannerList.isEmpty(), "無已儲存旗幟時應回傳空清單");
    }

    @Test
    void saveBanner_ShouldPreserveBaseColor() {
        // 測試不同底色的旗幟
        ItemStack blueBanner = new ItemStack(Material.BLUE_BANNER);
        bannerRepository.saveBanner(player, blueBanner);

        List<ItemStack> bannerList = bannerRepository.loadBannerList(player);
        assertEquals(1, bannerList.size());
        assertEquals(Material.BLUE_BANNER, bannerList.get(0).getType(), "底色應保持為藍色");
    }
}

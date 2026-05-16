package club.kid7.bannermaker.service;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.configuration.ConfigManager;
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
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BannerRepositoryTest {

    private ServerMock server;
    private BannerMaker plugin;
    private BannerRepository repository;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(BannerMaker.class);
        repository = plugin.getBannerRepository();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
        ConfigManager.reset();
    }

    private ItemStack simpleBanner() {
        ItemStack banner = new ItemStack(Material.RED_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
        banner.setItemMeta(meta);
        return banner;
    }

    @Test
    void saveBanner_ReturnsTrue_ForValidBanner() {
        PlayerMock player = server.addPlayer();
        assertTrue(repository.saveBanner(player, simpleBanner()),
            "合法旗幟應成功儲存");
    }

    @Test
    void saveBanner_ReturnsFalse_ForNonBanner() {
        PlayerMock player = server.addPlayer();
        ItemStack stone = new ItemStack(Material.STONE);
        assertFalse(repository.saveBanner(player, stone),
            "非旗幟物品應拒絕儲存");
    }

    @Test
    void loadBannerList_ReturnsEmpty_WhenNothingSaved() {
        PlayerMock player = server.addPlayer();
        List<ItemStack> banners = repository.loadBannerList(player);
        assertTrue(banners.isEmpty(), "未儲存任何旗幟時清單應為空");
    }

    @Test
    void loadBannerList_ReturnsSavedBanners() {
        PlayerMock player = server.addPlayer();
        repository.saveBanner(player, simpleBanner());

        List<ItemStack> banners = repository.loadBannerList(player);

        assertEquals(1, banners.size(), "應載回剛存入的旗幟");
        assertEquals(Material.RED_BANNER, banners.get(0).getType(), "底色應保留");
    }

    @Test
    void getBannerCount_ReflectsSavedBanners() {
        PlayerMock player = server.addPlayer();
        assertEquals(0, repository.getBannerCount(player));

        repository.saveBanner(player, simpleBanner());
        repository.saveBanner(player, simpleBanner());

        assertEquals(2, repository.getBannerCount(player), "存入兩面旗幟後計數應為 2");
    }

    @Test
    void saveBanner_StoresPatternUsingNamespaceKey() {
        // 新存的 YAML 應使用 namespace key 後綴（如 "stripe_top"），不是已 deprecated 的縮寫（"ts"）
        PlayerMock player = server.addPlayer();
        repository.saveBanner(player, simpleBanner());

        String fileName = "banner" + File.separator + player.getUniqueId() + ".yml";
        FileConfiguration config = ConfigManager.get(fileName);
        String onlyKey = config.getKeys(false).iterator().next();
        List<String> patterns = config.getStringList(onlyKey + ".patterns");

        assertEquals(1, patterns.size());
        String entry = patterns.get(0);
        // 預期格式：<namespace_key>:<DyeColor.name()>
        assertTrue(entry.startsWith("stripe_top:"),
            "Pattern 應以 namespace key 後綴儲存，實際：" + entry);
        assertTrue(entry.endsWith(":WHITE"),
            "Color 應以 DyeColor.name() 儲存，實際：" + entry);
    }

    @Test
    void loadBanner_ReadsLegacyShortIdentifierFormat() {
        // 模擬 v2.5.x 以前儲存的玩家收藏：pattern 用舊縮寫 ("ts" = STRIPE_TOP)
        PlayerMock player = server.addPlayer();
        String fileName = "banner" + File.separator + player.getUniqueId() + ".yml";
        ConfigManager.load(fileName);
        FileConfiguration config = ConfigManager.get(fileName);
        // 手刻一份「舊格式」資料
        config.set("1700000000000.color", "RED");
        config.set("1700000000000.patterns", Arrays.asList("ts:WHITE"));
        ConfigManager.save(fileName);

        List<ItemStack> loaded = repository.loadBannerList(player);

        assertEquals(1, loaded.size(), "舊格式 YAML 應仍可載入");
        ItemStack banner = loaded.get(0);
        assertEquals(Material.RED_BANNER, banner.getType());
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        assertNotNull(meta);
        assertEquals(1, meta.numberOfPatterns());
        assertEquals(DyeColor.WHITE, meta.getPattern(0).getColor());
        // 比對 PatternType 用 Object.equals 避開 interface↔class binary compat 雷區
        Object pt = meta.getPattern(0).getPattern();
        assertTrue(pt.equals(PatternType.STRIPE_TOP),
            "舊縮寫 'ts' 應解析為 STRIPE_TOP，實際：" + pt);
    }
}

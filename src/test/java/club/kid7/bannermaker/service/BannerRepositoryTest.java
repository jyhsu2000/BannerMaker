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
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
}

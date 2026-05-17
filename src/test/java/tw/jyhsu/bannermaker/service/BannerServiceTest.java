package tw.jyhsu.bannermaker.service;

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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BannerServiceTest {

    private ServerMock server;
    private BannerMaker plugin;
    private BannerService bannerService;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(BannerMaker.class);
        bannerService = plugin.getBannerService();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
        ConfigManager.reset();
    }

    private ItemStack simpleBanner() {
        ItemStack banner = new ItemStack(Material.WHITE_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.RED, PatternType.CIRCLE));
        banner.setItemMeta(meta);
        return banner;
    }

    @Test
    void craft_Succeeds_WhenPlayerHasMaterials() {
        PlayerMock player = server.addPlayer();
        PlayerInventory inv = player.getInventory();
        inv.addItem(new ItemStack(Material.STICK, 1));
        inv.addItem(new ItemStack(Material.WHITE_WOOL, 6));
        inv.addItem(new ItemStack(Material.RED_DYE, 1));

        boolean result = bannerService.craft(player, simpleBanner());

        assertTrue(result, "材料充足時 craft 應成功");
        assertTrue(inv.containsAtLeast(new ItemStack(Material.WHITE_BANNER), 1), "玩家應得到旗幟");
    }

    @Test
    void craft_Fails_WhenPlayerLacksMaterials() {
        PlayerMock player = server.addPlayer();

        boolean result = bannerService.craft(player, simpleBanner());

        assertFalse(result, "材料不足時 craft 應失敗");
        assertFalse(player.getInventory().containsAtLeast(new ItemStack(Material.WHITE_BANNER), 1), "玩家不應得到旗幟");
    }

    @Test
    void craft_ConsumesMaterials_OnSuccess() {
        PlayerMock player = server.addPlayer();
        PlayerInventory inv = player.getInventory();
        inv.addItem(new ItemStack(Material.STICK, 1));
        inv.addItem(new ItemStack(Material.WHITE_WOOL, 6));
        inv.addItem(new ItemStack(Material.RED_DYE, 1));

        bannerService.craft(player, simpleBanner());

        assertFalse(inv.containsAtLeast(new ItemStack(Material.STICK), 1), "木棒應被消耗");
        assertFalse(inv.containsAtLeast(new ItemStack(Material.WHITE_WOOL), 1), "羊毛應被消耗");
        assertFalse(inv.containsAtLeast(new ItemStack(Material.RED_DYE), 1), "染料應被消耗");
    }

    @Test
    void buy_Fails_WhenEconomyUnavailable() {
        // MockBukkit 環境無 Vault → economyService.isAvailable() 為 false
        PlayerMock player = server.addPlayer();

        boolean result = bannerService.buy(player, simpleBanner());

        assertFalse(result, "經濟系統不可用時 buy 應失敗");
    }

    @Test
    void sendShareCommand_DeliversMessageToPlayer() {
        PlayerMock player = server.addPlayer();

        bannerService.sendShareCommand(player, simpleBanner());

        // PlayerMock 會記錄收到的訊息；至少有一則
        assertNotNull(player.nextMessage(), "玩家應收到分享訊息");
    }
}

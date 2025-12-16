package club.kid7.bannermaker.util;

import club.kid7.bannermaker.BannerMaker;
import com.cryptomorin.xseries.XMaterial;
import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemBuilderTest {

    private ServerMock server;
    private BannerMaker plugin;

    @BeforeEach
    void setUp() {
        System.setProperty("bstats.relocate.check", "false");
        server = MockBukkit.mock();
        plugin = MockBukkit.load(BannerMaker.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void build_ShouldCreateSimpleItem() {
        ItemStack item = new ItemBuilder(Material.DIAMOND).build();
        assertNotNull(item);
        assertEquals(Material.DIAMOND, item.getType());
        assertEquals(1, item.getAmount());
    }

    @Test
    void build_ShouldCreateFromXMaterial() {
        ItemStack item = new ItemBuilder(XMaterial.EMERALD).build();
        assertNotNull(item);
        assertEquals(Material.EMERALD, item.getType());
    }

    @Test
    void name_ShouldSetDisplayName() {
        String name = "Test Item";
        ItemStack item = new ItemBuilder(Material.STONE).name(name).build();
        ItemMeta meta = item.getItemMeta();
        assertNotNull(meta);
        assertTrue(meta.hasDisplayName());
        assertEquals(name, meta.getDisplayName());
    }

    @Test
    void name_ShouldSetDisplayNameFromComponent() {
        Component name = Component.text("Component Name");
        // Legacy serializer will convert this
        ItemStack item = new ItemBuilder(Material.STONE).name(name).build();
        ItemMeta meta = item.getItemMeta();
        assertNotNull(meta);
        assertEquals("Component Name", meta.getDisplayName());
    }

    @Test
    void lore_ShouldSetLore() {
        ItemStack item = new ItemBuilder(Material.STONE)
            .lore("Line 1", "Line 2")
            .build();
        ItemMeta meta = item.getItemMeta();
        assertNotNull(meta);
        assertTrue(meta.hasLore());
        List<String> lore = meta.getLore();
        assertNotNull(lore);
        assertEquals(2, lore.size());
        assertEquals("Line 1", lore.get(0));
        assertEquals("Line 2", lore.get(1));
    }

    @Test
    void amount_ShouldSetAmount() {
        ItemStack item = new ItemBuilder(Material.STICK).amount(5).build();
        assertEquals(5, item.getAmount());
    }

    @Test
    void enchant_ShouldAddEnchantment() {
        ItemStack item = new ItemBuilder(Material.DIAMOND_SWORD)
            .enchant(Enchantment.SHARPNESS, 5)
            .build();
        ItemMeta meta = item.getItemMeta();
        assertNotNull(meta);
        assertTrue(meta.hasEnchant(Enchantment.SHARPNESS));
        assertEquals(5, meta.getEnchantLevel(Enchantment.SHARPNESS));
    }

    @Test
    void flags_ShouldAddItemFlags() {
        ItemStack item = new ItemBuilder(Material.DIAMOND_SWORD)
            .flags(ItemFlag.HIDE_ENCHANTS)
            .build();
        ItemMeta meta = item.getItemMeta();
        assertNotNull(meta);
        assertTrue(meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS));
    }

    @Test
    void bannerPatterns_ShouldAddPatterns() {
        ItemStack item = new ItemBuilder(Material.WHITE_BANNER)
            .pattern(new Pattern(DyeColor.RED, PatternType.CROSS))
            .build();

        assertEquals(Material.WHITE_BANNER, item.getType());
        BannerMeta meta = (BannerMeta) item.getItemMeta();
        assertNotNull(meta);
        assertEquals(1, meta.numberOfPatterns());
        assertEquals(PatternType.CROSS, meta.getPattern(0).getPattern());
        assertEquals(DyeColor.RED, meta.getPattern(0).getColor());
    }
}

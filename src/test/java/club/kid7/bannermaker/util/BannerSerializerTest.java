package club.kid7.bannermaker.util;

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
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BannerSerializerTest {

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
    void serializeAndDeserialize_RoundTripsBannerCorrectly() {
        ItemStack originalBanner = new ItemStack(Material.RED_BANNER);
        BannerMeta meta = (BannerMeta) originalBanner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.CROSS));
        originalBanner.setItemMeta(meta);

        String serialized = BannerSerializer.serialize(originalBanner);
        assertNotNull(serialized);

        ItemStack deserializedBanner = BannerSerializer.deserialize(serialized);
        assertNotNull(deserializedBanner);

        assertEquals(originalBanner.getType(), deserializedBanner.getType());
        BannerMeta deserializedMeta = (BannerMeta) deserializedBanner.getItemMeta();
        assertEquals(meta.numberOfPatterns(), deserializedMeta.numberOfPatterns());
        assertEquals(meta.getPattern(0), deserializedMeta.getPattern(0));
    }
}

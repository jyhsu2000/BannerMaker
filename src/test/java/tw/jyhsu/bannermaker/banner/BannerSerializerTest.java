package tw.jyhsu.bannermaker.banner;

import tw.jyhsu.bannermaker.BannerMaker;
import tw.jyhsu.bannermaker.configuration.ConfigManager;
import tw.jyhsu.bannermaker.util.SerializationUtil;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    // ===== v2 wire format =====

    @Test
    void serialize_ProducesV2StringWithBmMagicPrefix() {
        ItemStack banner = new ItemStack(Material.RED_BANNER);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.CROSS));
        banner.setItemMeta(meta);

        String serialized = BannerSerializer.serialize(banner);

        // base64("BM\x02...") 永遠以 "Qk0C" 開頭，作為 v2 wire format 的版本標記
        assertNotNull(serialized);
        assertTrue(serialized.startsWith("Qk0C"),
            "v2 wire string 應以 base64('BM\\x02') == 'Qk0C' 開頭，實際：" + serialized);
    }

    @Test
    void serializeAndDeserialize_V2_RoundTripsSinglePattern() {
        ItemStack original = new ItemStack(Material.RED_BANNER);
        BannerMeta meta = (BannerMeta) original.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.CROSS));
        original.setItemMeta(meta);

        ItemStack restored = BannerSerializer.deserialize(BannerSerializer.serialize(original));

        assertEquals(Material.RED_BANNER, restored.getType());
        BannerMeta restoredMeta = (BannerMeta) restored.getItemMeta();
        assertEquals(1, restoredMeta.numberOfPatterns());
        assertEquals(meta.getPattern(0), restoredMeta.getPattern(0));
    }

    @Test
    void serializeAndDeserialize_V2_RoundTripsMultiplePatterns() {
        ItemStack original = new ItemStack(Material.BLUE_BANNER);
        BannerMeta meta = (BannerMeta) original.getItemMeta();
        meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.CROSS));
        meta.addPattern(new Pattern(DyeColor.BLACK, PatternType.BORDER));
        meta.addPattern(new Pattern(DyeColor.YELLOW, PatternType.RHOMBUS));
        original.setItemMeta(meta);

        ItemStack restored = BannerSerializer.deserialize(BannerSerializer.serialize(original));

        assertEquals(Material.BLUE_BANNER, restored.getType());
        BannerMeta restoredMeta = (BannerMeta) restored.getItemMeta();
        assertEquals(3, restoredMeta.numberOfPatterns());
        for (int i = 0; i < 3; i++) {
            assertEquals(meta.getPattern(i), restoredMeta.getPattern(i));
        }
    }

    @Test
    void serializeAndDeserialize_V2_RoundTripsPureBanner() {
        // 無任何 pattern 的純色旗幟
        ItemStack original = new ItemStack(Material.GREEN_BANNER);

        ItemStack restored = BannerSerializer.deserialize(BannerSerializer.serialize(original));

        assertEquals(Material.GREEN_BANNER, restored.getType());
        assertEquals(0, ((BannerMeta) restored.getItemMeta()).numberOfPatterns());
    }

    // ===== v1 backward compat =====

    @Test
    void deserialize_V1_BackwardCompatStillWorks() {
        // 構造一個 v1 wire string：紅底（colorCode=1）+ 白色 STRIPE_LEFT（縮寫 "ls"）
        // 透過 SerializationUtil.objectToBase64 包成跟舊 plugin 一樣的格式
        String v1Payload = "1;ls:15";
        String v1Wire = SerializationUtil.objectToBase64(v1Payload);
        // sanity check: v1 字串以 base64("\xAC\xED...") == "rO0" 開頭
        assertTrue(v1Wire.startsWith("rO0"));

        ItemStack restored = BannerSerializer.deserialize(v1Wire);

        assertEquals(Material.RED_BANNER, restored.getType());
        BannerMeta meta = (BannerMeta) restored.getItemMeta();
        assertEquals(1, meta.numberOfPatterns());
        assertEquals(DyeColor.WHITE, meta.getPattern(0).getColor());
        // 比對 pattern type 用 Object.equals 避開 PatternType interface↔class binary compat 問題
        Object pt = meta.getPattern(0).getPattern();
        assertTrue(pt.equals(PatternType.STRIPE_LEFT));
    }

    // ===== 結構化例外 =====

    @Test
    void deserialize_InvalidBase64_ThrowsInvalidFormat() {
        assertThrows(InvalidBannerFormatException.class,
            () -> BannerSerializer.deserialize("!!! not base64 !!!"));
    }

    @Test
    void deserialize_EmptyString_ThrowsInvalidFormat() {
        assertThrows(InvalidBannerFormatException.class,
            () -> BannerSerializer.deserialize(""));
    }

    @Test
    void deserialize_UnknownPrefix_ThrowsInvalidFormat() {
        // base64 of "XYZ..." — 既不是 v1 的 0xAC 開頭、也不是 v2 的 0x42 0x4D 開頭
        String wire = java.util.Base64.getEncoder().encodeToString(new byte[]{0x58, 0x59, 0x5A});
        assertThrows(InvalidBannerFormatException.class,
            () -> BannerSerializer.deserialize(wire));
    }

    @Test
    void deserialize_V2UnknownVersion_ThrowsInvalidFormat() {
        // BM magic + 未來版本 0xFF
        byte[] bytes = {0x42, 0x4D, (byte) 0xFF, 0x72, 0x65, 0x64};
        String wire = java.util.Base64.getEncoder().encodeToString(bytes);
        assertThrows(InvalidBannerFormatException.class,
            () -> BannerSerializer.deserialize(wire));
    }

    @Test
    void deserialize_V2UnknownBaseColor_ThrowsUnknownColor() {
        // BM magic + version + UTF-8("magenta-pink-special")
        byte[] payload = "magenta-pink-special".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] bytes = new byte[3 + payload.length];
        bytes[0] = 0x42;
        bytes[1] = 0x4D;
        bytes[2] = 0x02;
        System.arraycopy(payload, 0, bytes, 3, payload.length);
        String wire = java.util.Base64.getEncoder().encodeToString(bytes);

        assertThrows(UnknownBannerColorException.class,
            () -> BannerSerializer.deserialize(wire));
    }

    @Test
    void deserialize_V2UnknownPatternKey_ThrowsUnknownPattern() {
        byte[] payload = "red;definitely_not_a_real_pattern:white"
            .getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] bytes = new byte[3 + payload.length];
        bytes[0] = 0x42;
        bytes[1] = 0x4D;
        bytes[2] = 0x02;
        System.arraycopy(payload, 0, bytes, 3, payload.length);
        String wire = java.util.Base64.getEncoder().encodeToString(bytes);

        assertThrows(UnknownBannerPatternException.class,
            () -> BannerSerializer.deserialize(wire));
    }

    @Test
    void deserialize_V2MalformedPatternEntry_ThrowsInvalidFormat() {
        // pattern 部分少了 ':' 分隔符
        byte[] payload = "red;crosswithoutcolon".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] bytes = new byte[3 + payload.length];
        bytes[0] = 0x42;
        bytes[1] = 0x4D;
        bytes[2] = 0x02;
        System.arraycopy(payload, 0, bytes, 3, payload.length);
        String wire = java.util.Base64.getEncoder().encodeToString(bytes);

        assertThrows(InvalidBannerFormatException.class,
            () -> BannerSerializer.deserialize(wire));
    }
}

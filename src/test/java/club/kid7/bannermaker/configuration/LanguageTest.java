package club.kid7.bannermaker.configuration;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.util.TagUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LanguageTest {

    private ServerMock server;
    private BannerMaker plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(BannerMaker.class);

        // 強制設定使用英文，避免 Locale.getDefault() 導致測試環境不一致
        ConfigManager.get("config.yml").set("Language", "en_US");

        // 初始化語言系統 (這會讀取 config.yml)
        new Language(plugin).loadLanguage();
    }

    @AfterEach
    void tearDown() {
        ConfigManager.reset();
        MockBukkit.unmock();
    }

    /**
     * 輔助方法：設定測試用的語言鍵值對
     */
    private void setLanguageKey(String key, String value) {
        // 直接操作 ConfigManager 管理的當前語言設定檔
        // 預設是英文，所以是 language/en_US.yml
        FileConfiguration config = ConfigManager.get("language" + File.separator + "en_US.yml");
        config.set(key, value);
    }

    @Test
    void testLegacyColor() {
        String key = "test.legacy";
        String value = "&cRed Text";
        setLanguageKey(key, value);

        Component result = Language.tl(key);

        // 驗證顏色是否正確解析為紅色
        assertEquals(NamedTextColor.RED, result.color(), "應該解析為紅色");
        assertEquals("Red Text", PlainTextComponentSerializer.plainText().serialize(result), "文字內容應該正確");
    }

    @Test
    void testMiniMessage() {
        String key = "test.minimessage";
        String value = "<red>Red Text</red>"; // MiniMessage 標籤應閉合
        setLanguageKey(key, value);

        Component result = Language.tl(key);

        // 驗證顏色是否正確解析為紅色
        assertEquals(NamedTextColor.RED, result.color(), "應該解析為紅色");
        assertEquals("Red Text", PlainTextComponentSerializer.plainText().serialize(result), "文字內容應該正確");
    }

    @Test
    void testMixedContent() {
        // & 碼會被預處理為 MiniMessage 標籤，兩種格式可共存
        String key = "test.mixed";
        String value = "<green>Green &cText</green>";
        setLanguageKey(key, value);

        Component result = Language.tl(key);

        assertEquals(NamedTextColor.GREEN, result.color(), "開頭應該是綠色");
        String plainText = PlainTextComponentSerializer.plainText().serialize(result);
        assertEquals("Green Text", plainText, "&c 應該被轉換為顏色而非純文字");
        // 子組件應該包含紅色
        assertTrue(result.children().stream()
            .anyMatch(child -> NamedTextColor.RED.equals(child.color())), "子組件應該包含紅色");
    }

    @Test
    void testNoColor() {
        String key = "test.plain";
        String value = "Plain Text";
        setLanguageKey(key, value);

        Component result = Language.tl(key);

        assertEquals("Plain Text", PlainTextComponentSerializer.plainText().serialize(result));
    }

    @Test
    void testComplexMiniMessage() {
        String key = "test.complex_mm";
        String value = "<gradient:red:blue>Hello <yellow>World</yellow>!</gradient>";
        setLanguageKey(key, value);

        Component result = Language.tl(key);
        // 驗證是否包含漸層和黃色 (MiniMessage 特性)
        String plainText = PlainTextComponentSerializer.plainText().serialize(result);
        assertEquals("Hello World!", plainText); // 漸層和顏色在純文本中不顯示
        // 要精確測試漸層需要更深入的 Adventure API 檢查
        // 這裡只簡單驗證主體文字和部分顏色
        // 檢查子組件是否有黃色
        assertTrue(result.children().stream()
            .anyMatch(child -> NamedTextColor.YELLOW.equals(child.color())), "子組件應該包含黃色");
    }

    @Test
    void testComplexLegacy() {
        String key = "test.complex_legacy";
        String value = "&cHello &bWorld &a!"; // 多個 Legacy 顏色
        setLanguageKey(key, value);

        Component result = Language.tl(key);

        // 驗證純文字內容
        assertEquals("Hello World !", PlainTextComponentSerializer.plainText().serialize(result));

        // 檢查 Component 再次序列化回 Legacy 格式後的表現
        String serializedLegacy = LegacyComponentSerializer.legacyAmpersand().serialize(result);
        assertTrue(serializedLegacy.startsWith("&cHello"), "Legacy 序列化後應該以紅色 Hello 開頭");
        assertTrue(serializedLegacy.contains("&bWorld"), "Legacy 序列化後應該包含水藍色 World");
        assertTrue(serializedLegacy.contains("&a!"), "Legacy 序列化後應該包含綠色 !");
    }

    @Test
    void testLegacyWithMiniMessageInside() {
        // & 碼和 MiniMessage 標籤共存，& 碼會被預處理為 MiniMessage 標籤
        String key = "test.legacy_with_mm";
        String value = "&cHello <red>World</red>";
        setLanguageKey(key, value);

        Component result = Language.tl(key);

        // &c 會被預處理為 <red>，所以 Hello 也是紅色
        assertEquals(NamedTextColor.RED, result.color(), "開頭應該是紅色");
        String plainText = PlainTextComponentSerializer.plainText().serialize(result);
        assertEquals("Hello World", plainText, "&c 應該被轉換為顏色");
    }

    @Test
    void testMiniMessageWithLegacyInside() {
        // MiniMessage 格式 <red>Hello &bWorld</red>
        // &b 會被預處理為 <aqua>
        String key = "test.mm_with_legacy";
        String value = "<red>Hello &bWorld</red>";
        setLanguageKey(key, value);

        Component result = Language.tl(key);

        assertEquals(NamedTextColor.RED, result.color(), "開頭應該是紅色");
        String plainText = PlainTextComponentSerializer.plainText().serialize(result);
        assertEquals("Hello World", plainText, "&b 應該被轉換為顏色而非純文字");
        // 子組件應該包含水藍色
        assertTrue(result.children().stream()
            .anyMatch(child -> NamedTextColor.AQUA.equals(child.color())), "子組件應該包含水藍色");
    }

    @Test
    void testTagResolver() {
        String key = "test.tag_resolver";
        String value = "Hello <name>!";
        setLanguageKey(key, value);

        Component result = Language.tl(key, TagUtil.tag("name", "World"));

        String plainText = PlainTextComponentSerializer.plainText().serialize(result);
        assertEquals("Hello World!", plainText, "命名佔位符應該被正確替換");
    }

    @Test
    void testTagResolverWithLegacyColor() {
        // 模擬語言檔中的實際格式，如 get-banner: 取得旗幟 &r#<name>
        String key = "test.tag_resolver_legacy";
        String value = "&aGet banner &r#<name>";
        setLanguageKey(key, value);

        Component result = Language.tl(key, TagUtil.tag("name", "TestBanner"));

        // 驗證純文字內容：& 碼被處理為顏色、佔位符被替換
        String plainText = PlainTextComponentSerializer.plainText().serialize(result);
        assertEquals("Get banner #TestBanner", plainText, "& 碼和命名佔位符應該共存");
        // 驗證 &a 被正確轉換為綠色（透過 Legacy 反向序列化確認）
        String legacySerialized = LegacyComponentSerializer.legacyAmpersand().serialize(result);
        assertTrue(legacySerialized.contains("&aGet banner"), "應該包含綠色的 Get banner");
    }
}

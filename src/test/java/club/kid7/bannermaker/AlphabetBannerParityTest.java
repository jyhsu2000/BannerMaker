package club.kid7.bannermaker;

import club.kid7.bannermaker.configuration.ConfigManager;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 一次性 parity 驗證：對所有字元、顏色、bordered 組合比對 Phase 丁資料化前後
 * AlphabetBanner.toItemStack() 的輸出（banner material + patterns 序列）完全相等。
 * <p>
 * 通過後即刪除 {@link LegacyAlphabetBanner} 與本測試。
 */
class AlphabetBannerParityTest {

    private static final String LETTERS;

    static {
        StringBuilder sb = new StringBuilder();
        for (char c = 'A'; c <= 'Z'; c++) sb.append(c);
        for (char c = '0'; c <= '9'; c++) sb.append(c);
        sb.append("?!.");
        // 額外丟一個未定義字元，確認兩邊都 fallback 為空 patterns
        sb.append("@");
        LETTERS = sb.toString();
    }

    private static final DyeColor[][] COLOR_PAIRS = {
        {DyeColor.WHITE, DyeColor.BLACK},
        {DyeColor.RED, DyeColor.YELLOW},
    };

    private ServerMock server;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        MockBukkit.load(BannerMaker.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
        ConfigManager.reset();
    }

    @Test
    void allLettersMatchLegacyOutput() {
        List<String> mismatches = new ArrayList<>();

        for (DyeColor[] pair : COLOR_PAIRS) {
            DyeColor base = pair[0];
            DyeColor dye = pair[1];
            for (boolean bordered : new boolean[]{true, false}) {
                for (int i = 0; i < LETTERS.length(); i++) {
                    String letter = LETTERS.substring(i, i + 1);

                    ItemStack actual = AlphabetBanner.get(letter, base, dye, bordered);
                    ItemStack expected = LegacyAlphabetBanner.get(letter, base, dye, bordered);

                    if (actual.getType() != expected.getType()) {
                        mismatches.add(String.format(
                            "letter=%s bordered=%s base=%s dye=%s: material %s != %s",
                            letter, bordered, base, dye, actual.getType(), expected.getType()));
                        continue;
                    }

                    BannerMeta actualMeta = (BannerMeta) actual.getItemMeta();
                    BannerMeta expectedMeta = (BannerMeta) expected.getItemMeta();
                    assertNotNull(actualMeta);
                    assertNotNull(expectedMeta);

                    List<Pattern> actualPatterns = actualMeta.getPatterns();
                    List<Pattern> expectedPatterns = expectedMeta.getPatterns();
                    if (!actualPatterns.equals(expectedPatterns)) {
                        mismatches.add(String.format(
                            "letter=%s bordered=%s base=%s dye=%s:%n  actual:   %s%n  expected: %s",
                            letter, bordered, base, dye, actualPatterns, expectedPatterns));
                    }
                }
            }
        }

        assertEquals(List.of(), mismatches,
            () -> "下列組合與 Phase 丁前不一致：\n" + String.join("\n", mismatches));
    }
}

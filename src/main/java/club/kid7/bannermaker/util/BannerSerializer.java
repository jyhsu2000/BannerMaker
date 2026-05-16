package club.kid7.bannermaker.util;

import club.kid7.bannermaker.registry.DyeColorRegistry;
import org.bukkit.DyeColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * 旗幟序列化／反序列化工具：將 banner（底色 + 所有 pattern）與可分享字串互轉。
 * 用於 {@code /bm view} 指令、share command、未來 Web 介面匯入匯出等需要把 banner
 * 表達為文字的場合。
 *
 * <h2>Wire format v2（現行產出格式）</h2>
 * <pre>
 *   base64 解碼後的 byte layout：
 *   ┌──────┬──────┬─────────┬──────────────────────────────┐
 *   │ 0x42 │ 0x4D │  0x02   │  UTF-8 payload bytes         │
 *   └──────┴──────┴─────────┴──────────────────────────────┘
 *     'B'    'M'   version
 *
 *   Payload：&lt;base_color&gt;(;&lt;pattern_key&gt;:&lt;color&gt;)*
 *     base_color / color：{@link DyeColor#name()} lowercase（red, white, ...）
 *     pattern_key       ：{@link Keyed#getKey()}.getKey()（cross, border, ...）
 * </pre>
 *
 * <h2>Wire format v1（僅保留解析、不再產出）</h2>
 * 由 {@link SerializationUtil#objectToBase64} 包裝的 Java Object Stream，內容為
 * {@code "<colorCode>;<patternId>:<colorCode>;..."} 字串，色碼為 pre-1.13 metadata
 * 數字（0=BLACK ... 15=WHITE），pattern 用 {@link PatternType#getByIdentifier(String)}
 * 的縮寫。base64 字串以 {@code rO0} 開頭。
 *
 * <h2>Deserialize 分流</h2>
 * base64 decode 後看第 1 個 byte：
 * <ul>
 *   <li>{@code 0xAC} → v1 路徑（Java OOS unwrap）</li>
 *   <li>{@code 0x42}（且第 2 byte {@code 0x4D}）→ v2 路徑，第 3 byte 為版本號</li>
 * </ul>
 *
 * <p>所有解析失敗皆拋 {@link BannerDeserializationException} 的子類別，呼叫端可依
 * 例外型別給予結構化錯誤回應；本插件 {@code /bm view} 對玩家仍只給通用提示。
 *
 * @see BannerDeserializationException
 */
public class BannerSerializer {

    private static final byte V2_MAGIC_B = 0x42; // 'B'
    private static final byte V2_MAGIC_M = 0x4D; // 'M'
    private static final byte V2_VERSION = 0x02;

    private static final byte V1_OOS_MAGIC = (byte) 0xAC;

    private BannerSerializer() {
        // Utility class
    }

    /**
     * 將 banner 編碼為 v2 base64 字串。
     *
     * @param banner 欲序列化的旗幟
     * @return Base64 編碼後的 v2 字串；若 banner 不是合法 banner，回傳 {@code null}
     * @see #deserialize(String)
     */
    public static String serialize(ItemStack banner) {
        if (!BannerUtil.isBanner(banner)) {
            return null;
        }
        DyeColor baseColor = Objects.requireNonNull(DyeColorRegistry.getDyeColor(banner.getType()));
        BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());

        StringBuilder payload = new StringBuilder(baseColor.name().toLowerCase());
        for (Pattern pattern : bm.getPatterns()) {
            // 透過 Keyed 介面呼叫 getKey() 以避開 PatternType class↔interface 演化的
            // binary compat 陷阱（編譯期看到的若為 enum，invokevirtual 在新版 interface 會炸）
            Keyed patternType = (Keyed) pattern.getPattern();
            String patternKey = patternType.getKey().getKey();
            payload.append(';')
                .append(patternKey)
                .append(':')
                .append(pattern.getColor().name().toLowerCase());
        }

        byte[] payloadBytes = payload.toString().getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[3 + payloadBytes.length];
        result[0] = V2_MAGIC_B;
        result[1] = V2_MAGIC_M;
        result[2] = V2_VERSION;
        System.arraycopy(payloadBytes, 0, result, 3, payloadBytes.length);

        return Base64.getEncoder().encodeToString(result);
    }

    /**
     * 將 wire 字串還原為 banner。自動偵測 v1 / v2 並走對應解析路徑。
     *
     * @param bannerString {@code /bm view} 指令傳入的字串
     * @return 還原後的 banner ItemStack
     * @throws InvalidBannerFormatException 字串無法 base64 解碼、缺欄、版本不支援等
     * @throws UnknownBannerPatternException 字串引用了當前 server 不認得的 pattern key
     * @throws UnknownBannerColorException   字串引用了不存在的 DyeColor
     */
    public static ItemStack deserialize(String bannerString) {
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(bannerString);
        } catch (IllegalArgumentException e) {
            throw new InvalidBannerFormatException("Banner string is not valid Base64", e);
        }
        if (bytes.length == 0) {
            throw new InvalidBannerFormatException("Banner string decoded to empty bytes");
        }

        if (bytes[0] == V1_OOS_MAGIC) {
            return deserializeV1(bannerString);
        }
        if (bytes.length >= 3 && bytes[0] == V2_MAGIC_B && bytes[1] == V2_MAGIC_M) {
            return deserializeV2(bytes);
        }
        throw new InvalidBannerFormatException("Unrecognized banner string format");
    }

    private static ItemStack deserializeV1(String bannerString) {
        String dataString;
        try {
            dataString = SerializationUtil.objectFromBase64(bannerString);
        } catch (IOException e) {
            throw new InvalidBannerFormatException("Failed to decode v1 Object Stream wrapper", e);
        }
        if (dataString == null || dataString.isEmpty()) {
            throw new InvalidBannerFormatException("v1 payload is empty");
        }

        String[] dataArray = dataString.split(";");
        int baseColorCode;
        try {
            baseColorCode = Integer.parseInt(dataArray[0]);
        } catch (NumberFormatException e) {
            throw new InvalidBannerFormatException("v1 base color is not numeric: " + dataArray[0], e);
        }
        Material bannerMaterial;
        try {
            bannerMaterial = DyeColorRegistry.getBannerMaterial(baseColorCode);
        } catch (NullPointerException e) {
            throw new UnknownBannerColorException("Unknown v1 base color code: " + baseColorCode, e);
        }

        ItemStack banner = new ItemStack(bannerMaterial);
        BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());

        for (int i = 1; i < dataArray.length; i++) {
            String[] patternData = dataArray[i].split(":");
            if (patternData.length != 2) {
                throw new InvalidBannerFormatException("v1 pattern entry malformed: " + dataArray[i]);
            }
            PatternType patternType = PatternType.getByIdentifier(patternData[0]);
            if (patternType == null) {
                throw new UnknownBannerPatternException("Unknown v1 pattern identifier: " + patternData[0]);
            }
            int patternColorCode;
            try {
                patternColorCode = Integer.parseInt(patternData[1]);
            } catch (NumberFormatException e) {
                throw new InvalidBannerFormatException("v1 pattern color is not numeric: " + patternData[1], e);
            }
            DyeColor patternColor;
            try {
                patternColor = DyeColorRegistry.getDyeColor(patternColorCode);
            } catch (NullPointerException e) {
                throw new UnknownBannerColorException("Unknown v1 pattern color code: " + patternColorCode, e);
            }
            bm.addPattern(new Pattern(patternColor, patternType));
        }
        banner.setItemMeta(bm);
        return banner;
    }

    private static ItemStack deserializeV2(byte[] bytes) {
        byte version = bytes[2];
        if (version != V2_VERSION) {
            throw new InvalidBannerFormatException(
                "Unsupported BannerMaker wire format version: " + (version & 0xFF));
        }
        String payload = new String(bytes, 3, bytes.length - 3, StandardCharsets.UTF_8);
        if (payload.isEmpty()) {
            throw new InvalidBannerFormatException("v2 payload is empty");
        }

        String[] parts = payload.split(";");
        DyeColor baseColor;
        try {
            baseColor = DyeColor.valueOf(parts[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnknownBannerColorException("Unknown v2 base color: " + parts[0], e);
        }

        ItemStack banner = new ItemStack(DyeColorRegistry.getBannerMaterial(baseColor));
        BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());

        for (int i = 1; i < parts.length; i++) {
            String[] patternData = parts[i].split(":");
            if (patternData.length != 2) {
                throw new InvalidBannerFormatException("v2 pattern entry malformed: " + parts[i]);
            }
            NamespacedKey key = new NamespacedKey(NamespacedKey.MINECRAFT, patternData[0].toLowerCase());
            PatternType patternType = Registry.BANNER_PATTERN.get(key);
            if (patternType == null) {
                throw new UnknownBannerPatternException("Unknown v2 pattern key: " + patternData[0]);
            }
            DyeColor patternColor;
            try {
                patternColor = DyeColor.valueOf(patternData[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new UnknownBannerColorException("Unknown v2 pattern color: " + patternData[1], e);
            }
            bm.addPattern(new Pattern(patternColor, patternType));
        }
        banner.setItemMeta(bm);
        return banner;
    }
}

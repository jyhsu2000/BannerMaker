package club.kid7.bannermaker.util;

import club.kid7.bannermaker.registry.DyeColorRegistry;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.io.IOException;
import java.util.Objects;

/**
 * 旗幟序列化／反序列化工具：將 banner（底色 + 所有 pattern）與可分享字串互轉。
 * 用於 {@code /bm view} 指令、share command 等需要把 banner 表達為文字的場合。
 * <p>
 * 從 {@link BannerUtil} 抽出以遵循單一職責原則。
 */
public class BannerSerializer {

    private BannerSerializer() {
        // Utility class
    }

    /**
     * 將 banner（底色 + 所有 pattern）編碼為 Base64 字串，供 {@code /bm view} 分享指令或網路傳輸使用。
     * 格式：{@code <colorCode>;<patternId>:<colorCode>;...} 之後再以 Base64 編碼。
     *
     * @param banner 欲序列化的旗幟
     * @return Base64 編碼後的字串；若 banner 不是合法 banner，回傳 {@code null}
     * @see #deserialize(String)
     */
    public static String serialize(ItemStack banner) {
        //只檢查旗幟
        if (!BannerUtil.isBanner(banner)) {
            return null;
        }
        DyeColor color = Objects.requireNonNull(DyeColorRegistry.getDyeColor(banner.getType()));
        int colorCode = DyeColorRegistry.getValue(color);
        StringBuilder dataStringBuilder = new StringBuilder(String.valueOf(colorCode));

        BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());

        for (Pattern pattern : bm.getPatterns()) {
            dataStringBuilder
                .append(";")
                .append(pattern.getPattern().getIdentifier())
                .append(":")
                .append(DyeColorRegistry.getValue(pattern.getColor()));
        }
        String dataString = dataStringBuilder.toString();

        return SerializationUtil.objectToBase64(dataString);
    }

    /**
     * 由 {@link #serialize(ItemStack)} 產生的 Base64 字串還原為 ItemStack。
     * 解析失敗時拋出 {@link RuntimeException}，呼叫端應自行 try/catch 處理（例如顯示「無效的旗幟字串」訊息）。
     *
     * @param bannerString {@code /bm view} 指令傳入的 Base64 字串
     * @return 還原後的 banner ItemStack
     * @throws RuntimeException 若字串無法解碼或結構不符
     * @see #serialize(ItemStack)
     */
    public static ItemStack deserialize(String bannerString) {
        try {
            String dataString = SerializationUtil.objectFromBase64(bannerString);
            String[] dataArray = dataString.split(";");

            ItemStack banner = new ItemStack(DyeColorRegistry.getBannerMaterial(Integer.parseInt(dataArray[0])));

            BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());

            for (int i = 1; i < dataArray.length; i++) {
                String[] patternData = dataArray[i].split(":");
                PatternType patternType = PatternType.getByIdentifier(patternData[0]);
                DyeColor patternColor = DyeColorRegistry.getDyeColor(Integer.parseInt(patternData[1]));
                Pattern pattern = new Pattern(patternColor, Objects.requireNonNull(patternType));
                bm.addPattern(pattern);
            }
            banner.setItemMeta(bm);
            return banner;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

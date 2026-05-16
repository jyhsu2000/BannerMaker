package club.kid7.bannermaker.util;

import club.kid7.bannermaker.BannerMaker;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XTag;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

/**
 * 旗幟基本判斷與識別工具。
 * <p>
 * 這個類在 v3.0 重整時切割為四個專責類別：
 * <ul>
 *     <li>{@link BannerSerializer} — banner ↔ 字串的序列化／反序列化</li>
 *     <li>{@link BannerCost} — 合成材料計算與「玩家是否有足夠材料」判斷</li>
 *     <li>{@link BannerPatternLayout} — 3x3 配方視覺化與 PatternType 清單</li>
 *     <li>{@code BannerUtil}（本類）— banner 是否是 banner、是否可合成、key/名稱</li>
 * </ul>
 */
public class BannerUtil {

    private BannerUtil() {
        // Utility class
    }

    /**
     * 檢查 ItemStack 是否為旗幟
     *
     * @param itemStack 要檢查的物品
     * @return 是否為旗幟
     */
    public static boolean isBanner(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        return isBanner(itemStack.getType());
    }

    /**
     * 檢查 Material 是否為旗幟
     *
     * @param material 要檢查的材質
     * @return 是否為旗幟
     */
    public static boolean isBanner(Material material) {
        if (material == null) {
            return false;
        }
        return XTag.BANNERS.isTagged(XMaterial.matchXMaterial(material));
    }

    /**
     * 判斷是否為旗幟圖形物品
     *
     * @param itemStack 欲檢查的物品
     * @return 是否為旗幟圖形物品
     */
    public static boolean isBannerPatternItemStack(ItemStack itemStack) {
        return itemStack.getType().toString().endsWith("_BANNER_PATTERN");
    }

    /**
     * 是否可以在生存模式合成（不超過 6 個 pattern）
     *
     * @param banner 旗幟
     * @return 是否可以合成
     */
    public static boolean isCraftableInSurvival(ItemStack banner) {
        //只檢查旗幟
        if (!isBanner(banner)) {
            return false;
        }
        int patternCount = ((BannerMeta) Objects.requireNonNull(banner.getItemMeta())).numberOfPatterns();
        return patternCount <= 6;
    }

    /**
     * 是否可以合成
     *
     * @param player 玩家
     * @param banner 旗幟
     * @return 是否可以合成
     */
    public static boolean isCraftable(Player player, ItemStack banner) {
        //只檢查旗幟
        if (!isBanner(banner)) {
            return false;
        }
        // 若啟用複雜合成功能，則額外檢查玩家是否擁有對應權限
        if (BannerMaker.getInstance().isEnableComplexBannerCraft()) {
            if (player.hasPermission("BannerMaker.getBanner.complex-craft")) {
                return true;
            }
        }
        return isCraftableInSurvival(banner);
    }

    /**
     * 取得旗幟在玩家存檔中的 key
     *
     * @param banner 欲檢查之旗幟
     * @return banner 在 PersistentData 中儲存的 key；非旗幟回傳 {@code null}
     */
    public static String getKey(ItemStack banner) {
        //只處理旗幟
        if (!isBanner(banner)) {
            return null;
        }
        ItemMeta itemMeta = Objects.requireNonNull(banner.getItemMeta());
        return PersistentDataUtil.get(itemMeta, "banner-key");
    }

    /**
     * 取得旗幟名稱，若無自訂名稱則嘗試回傳 key。
     *
     * @param banner 欲檢查之旗幟
     * @return 自訂名稱 / key / 空字串；非旗幟回傳 {@code null}
     */
    public static String getName(ItemStack banner) {
        //只處理旗幟
        if (!isBanner(banner)) {
            return null;
        }
        //先試著取得自訂名稱
        if (banner.hasItemMeta() && Objects.requireNonNull(banner.getItemMeta()).hasDisplayName()) {
            return banner.getItemMeta().getDisplayName();
        }
        //嘗試取得key
        String key = getKey(banner);
        if (key != null) {
            return key;
        }
        //若都沒有，回傳空字串
        return "";
    }
}

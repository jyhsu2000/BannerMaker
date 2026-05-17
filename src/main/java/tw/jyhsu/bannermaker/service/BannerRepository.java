package tw.jyhsu.bannermaker.service;

import tw.jyhsu.bannermaker.configuration.ConfigManager;
import tw.jyhsu.bannermaker.registry.DyeColorRegistry;
import tw.jyhsu.bannermaker.banner.BannerUtil;
import tw.jyhsu.bannermaker.util.PersistentDataUtil;
import org.bukkit.DyeColor;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BannerRepository {

    /**
     * 將旗幟存入該玩家的個人收藏檔（{@code banner/<uuid>.yml}）。
     * 索引值為儲存當下的時間戳記，故同玩家短時間連續儲存不會衝突。
     *
     * @param player 收藏該旗幟的玩家
     * @param banner 欲儲存的旗幟物品
     * @return 是否成功儲存；若 banner 不是合法旗幟回傳 {@code false}
     */
    public boolean saveBanner(Player player, ItemStack banner) {
        //只處理旗幟
        if (!BannerUtil.isBanner(banner)) {
            return false;
        }
        //設定檔
        String fileName = getFileName(player);
        FileConfiguration config = ConfigManager.get(fileName);
        //索引值（時間戳記，不會重複）
        String key = String.valueOf(System.currentTimeMillis());
        //旗幟資訊
        BannerMeta bm = (BannerMeta) Objects.requireNonNull(banner.getItemMeta());
        //儲存（pattern 用 namespace key 後綴；色用 DyeColor.name() 明確意圖）
        config.set(key + ".color", Objects.requireNonNull(DyeColorRegistry.getDyeColor(banner.getType())).name());
        List<String> patternList = new ArrayList<>();
        for (Pattern pattern : bm.getPatterns()) {
            // 透過 Keyed interface 呼叫 getKey() 避開 PatternType class↔interface binary compat 雷區
            Keyed patternType = (Keyed) pattern.getPattern();
            patternList.add(patternType.getKey().getKey() + ":" + pattern.getColor().name());
        }
        if (!patternList.isEmpty()) {
            config.set(key + ".patterns", patternList);
        }
        ConfigManager.save(fileName);
        return true;
    }

    /**
     * 讀取玩家儲存的全部旗幟（不分頁）。
     *
     * @param player 旗幟收藏的擁有者
     * @return 該玩家收藏的所有 banner ItemStack；無收藏時回傳空清單
     */
    public List<ItemStack> loadBannerList(Player player) {
        return loadBannerList(player, 0);
    }

    /**
     * 讀取玩家儲存的旗幟（依頁分批）。每頁 45 筆。
     * 強制重新讀取檔案以避免使用者於遊戲中異動後 GUI 內容沒更新。
     *
     * @param player 旗幟收藏的擁有者
     * @param page   頁碼，1-based。傳 0 表示載入全部、不分頁
     * @return 該頁的 banner 清單；無對應資料時為空清單
     */
    public List<ItemStack> loadBannerList(Player player, int page) {
        List<ItemStack> bannerList = new ArrayList<>();
        //設定檔
        String fileName = getFileName(player);
        ConfigManager.load(fileName);
        //強制重新讀取，以避免選單內容未即時更新
        ConfigManager.reload(fileName);
        FileConfiguration config = ConfigManager.get(fileName);
        //起始索引值
        int startIndex = Math.max(0, (page - 1) * 45);
        //旗幟
        Set<String> keySet = config.getKeys(false);
        List<String> keyList = new ArrayList<>(keySet);
        //載入該頁旗幟，若無指定頁碼，則載入全部
        for (int i = startIndex; i < keyList.size() && (i < startIndex + 45 || page == 0); i++) {
            String key = keyList.get(i);
            //嘗試讀取旗幟
            ItemStack banner = loadBanner(player, key);
            if (banner == null) {
                continue;
            }
            bannerList.add(banner);
        }
        return bannerList;
    }

    //讀取旗幟
    private ItemStack loadBanner(Player player, String key) {
        //設定檔
        String fileName = getFileName(player);
        FileConfiguration config = ConfigManager.get(fileName);
        //檢查是否為正確格式
        if (!config.isString(key + ".color")
            || (config.contains(key + ".patterns") && !config.isList(key + ".patterns"))) {
            return null;
        }
        ItemStack banner;
        try {
            banner = new ItemStack(DyeColorRegistry.getBannerMaterial(DyeColor.valueOf(config.getString(key + ".color"))));
            BannerMeta bm = Objects.requireNonNull((BannerMeta) banner.getItemMeta());
            //新增 Patterns
            if (config.contains(key + ".patterns")) {
                List<String> patternsList = config.getStringList(key + ".patterns");
                for (String str : patternsList) {
                    String strPattern = str.split(":")[0];
                    String strColor = str.split(":")[1];
                    Pattern pattern = new Pattern(DyeColor.valueOf(strColor), Objects.requireNonNull(resolvePatternType(strPattern)));
                    bm.addPattern(pattern);
                }
            }
            //將 key 藏於 PersistentData
            PersistentDataUtil.set(bm, "banner-key", key);
            banner.setItemMeta(bm);
        } catch (Exception e) {
            banner = null;
        }

        return banner;
    }

    /**
     * 解析 YAML 內 pattern 識別字串為 {@link PatternType}。
     * 先嘗試新格式（namespace key 後綴，如 {@code stripe_left}），若 server 不認得才 fallback
     * 試舊縮寫格式（如 {@code ls}）— 為了讀回 v2.5.x 以前儲存的玩家收藏。
     *
     * @param id YAML 內存的 pattern 識別字串
     * @return 對應的 PatternType；若兩種格式皆無法解出回傳 {@code null}
     */
    private static PatternType resolvePatternType(String id) {
        try {
            NamespacedKey key = new NamespacedKey(NamespacedKey.MINECRAFT, id.toLowerCase());
            PatternType modern = Registry.BANNER_PATTERN.get(key);
            if (modern != null) {
                return modern;
            }
        } catch (IllegalArgumentException ignored) {
            // id 含 NamespacedKey 不接受的字元（如大寫），改試 legacy
        }
        return resolvePatternTypeLegacy(id);
    }

    @SuppressWarnings({"deprecation", "removal"})
    private static PatternType resolvePatternTypeLegacy(String id) {
        // PatternType.getByIdentifier 是 v1 YAML（與 v1 wire format）使用的舊縮寫對映表，
        // 已 deprecated and marked for removal；保留作為 backward compat、隔離到單獨 method 標明意圖。
        return PatternType.getByIdentifier(id);
    }

    /**
     * 從玩家的收藏中移除指定 key 的旗幟。
     *
     * @param player 旗幟收藏的擁有者
     * @param key    旗幟的索引值（儲存時的時間戳記）
     * @return 永遠回傳 {@code true}（即使 key 不存在亦不視為錯誤）
     */
    public boolean removeBanner(Player player, String key) {
        //設定檔
        String fileName = getFileName(player);
        FileConfiguration config = ConfigManager.get(fileName);
        //移除
        config.set(key, null);
        //儲存
        ConfigManager.save(fileName);
        return true;
    }

    //取得旗幟總數
    public int getBannerCount(Player player) {
        List<ItemStack> bannerList = loadBannerList(player);
        return bannerList.size();
    }

    //旗幟檔案路徑
    private String getFileName(Player player) {
        return getFileName(player.getUniqueId().toString());
    }

    private String getFileName(String configFileName) {
        return "banner" + File.separator + configFileName + ".yml";
    }
}

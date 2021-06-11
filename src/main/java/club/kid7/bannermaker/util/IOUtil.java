package club.kid7.bannermaker.util;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.pluginutilities.configuration.KConfigManager;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static club.kid7.bannermaker.configuration.Language.tl;

public class IOUtil {

    //儲存旗幟
    static public void saveBanner(Player player, ItemStack banner) {
        //只處理旗幟
        if (!BannerUtil.isBanner(banner)) {
            player.sendMessage(MessageUtil.format(true, "&c" + tl("io.save-failed")));
            return;
        }
        //設定檔
        String fileName = getFileName(player);
        FileConfiguration config = KConfigManager.get(fileName);
        //索引值（時間戳記，不會重複）
        String key = String.valueOf(System.currentTimeMillis());
        //旗幟資訊
        BannerMeta bm = (BannerMeta) banner.getItemMeta();
        //儲存
        config.set(key + ".color", DyeColorUtil.of(banner.getType()).toString());
        List<String> patternList = new ArrayList<>();
        for (Pattern pattern : bm.getPatterns()) {
            patternList.add(pattern.getPattern().getIdentifier() + ":" + pattern.getColor().toString());
        }
        if (patternList.size() > 0) {
            config.set(key + ".patterns", patternList);
        }
        KConfigManager.save(fileName);
        //訊息
        player.sendMessage(MessageUtil.format(true, "&a" + tl("io.save-success")));
    }

    //讀取旗幟清單
    static public List<ItemStack> loadBannerList(Player player) {
        return loadBannerList(player, 0);
    }

    static public List<ItemStack> loadBannerList(Player player, int page) {
        List<ItemStack> bannerList = new ArrayList<>();
        //設定檔
        String fileName = getFileName(player);
        KConfigManager.load(fileName);
        //強制重新讀取，以避免選單內容未即時更新
        KConfigManager.reload(fileName);
        FileConfiguration config = KConfigManager.get(fileName);
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
    private static ItemStack loadBanner(Player player, String key) {
        //設定檔
        String fileName = getFileName(player);
        FileConfiguration config = KConfigManager.get(fileName);
        //檢查是否為物品
        ItemStack banner = null;
        //檢查是否為正確格式
        if ((config.isInt(key + ".color") || config.isString(key + ".color"))
            && (!config.contains(key + ".patterns") || config.isList(key + ".patterns"))) {
            //嘗試以新格式讀取
            try {
                //建立旗幟
                if (config.isInt(key + ".color")) {
                    // FIXME: 維持舊版相容性
                    banner = new ItemStack(DyeColorUtil.toBannerMaterial(DyeColorUtil.of(config.getInt(key + ".color"))));
                } else {
                    banner = new ItemStack(DyeColorUtil.toBannerMaterial(DyeColor.valueOf(config.getString(key + ".color"))));
                }
                BannerMeta bm = (BannerMeta) banner.getItemMeta();
                //新增Patterns
                if (config.contains(key + ".patterns")) {
                    List<String> patternsList = config.getStringList(key + ".patterns");
                    for (String str : patternsList) {
                        String strPattern = str.split(":")[0];
                        String strColor = str.split(":")[1];
                        Pattern pattern = new Pattern(DyeColor.valueOf(strColor), PatternType.getByIdentifier(strPattern));
                        bm.addPattern(pattern);
                    }
                    banner.setItemMeta(bm);
                }
                //將 key 藏於 PersistentData
                NamespacedKey namespacedKey = new NamespacedKey(BannerMaker.getInstance(), "banner-key");
                bm.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, key);
                banner.setItemMeta(bm);
            } catch (Exception e) {
                banner = null;
            }
        }
        //只處理旗幟
        if (!BannerUtil.isBanner(banner)) {
            return null;
        }
        return banner;
    }

    //刪除旗幟
    static public void removeBanner(Player player, String key) {
        //設定檔
        String fileName = getFileName(player);
        FileConfiguration config = KConfigManager.get(fileName);
        //移除
        config.set(key, null);
        //儲存
        KConfigManager.save(fileName);
        //顯示訊息
        player.sendMessage(MessageUtil.format(true, "&a" + tl("io.remove-banner", key)));
    }

    //取得旗幟總數
    static public int getBannerCount(Player player) {
        List<ItemStack> bannerList = loadBannerList(player, 0);
        return bannerList.size();
    }

    //旗幟檔案路徑
    private static String getFileName(Player player) {
        return getFileName(player.getUniqueId().toString());
    }

    private static String getFileName(String configFileName) {
        return "banner" + File.separator + configFileName + ".yml";
    }
}

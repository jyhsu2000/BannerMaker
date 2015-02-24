package tw.kid7.BannerMaker.util;

import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.configuration.ConfigManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IOUtil {

    //儲存旗幟
    static public void saveBanner(Player player, ItemStack banner) {
        //只處理旗幟
        if (banner == null || !banner.getType().equals(Material.BANNER)) {
            player.sendMessage(MessageUtil.format(true, "&cSave failed."));
            return;
        }
        //設定檔
        String fileName = player.getName() + ".yml";
        ConfigManager.load(fileName);
        FileConfiguration config = ConfigManager.get(fileName);
        //索引值（時間戳記，不會重複）
        long index = System.currentTimeMillis();
        //儲存
        config.set(String.valueOf(index), banner);
        ConfigManager.save(fileName);
        //訊息
        player.sendMessage(MessageUtil.format(true, "&aSave success."));
    }

    //讀取旗幟
    static public List<ItemStack> loadBanner(Player player) {
        List<ItemStack> bannerList = new ArrayList<>();
        //設定檔
        String fileName = player.getName() + ".yml";
        ConfigManager.load(fileName);
        FileConfiguration config = ConfigManager.get(fileName);
        //當前頁數
        int currentBannerPage = 1;
        if (BannerMaker.getInstance().currentBannerPage.containsKey(player.getName())) {
            currentBannerPage = BannerMaker.getInstance().currentBannerPage.get(player.getName());
        } else {
            BannerMaker.getInstance().currentBannerPage.put(player.getName(), 1);
        }
        //起始索引值
        int startIndex = Math.max(0, (currentBannerPage - 1) * 45);
        //旗幟
        Set<String> keySet = config.getKeys(false);
        List<String> keyList = new ArrayList<>();
        keyList.addAll(keySet);
        for (int i = startIndex; i < keyList.size(); i++) {
            String key = keyList.get(i);
            //檢查是否為物品
            if (!config.isItemStack(key)) {
                continue;
            }
            ItemStack banner = config.getItemStack(key);
            //只處理旗幟
            if (banner == null || !banner.getType().equals(Material.BANNER)) {
                continue;
            }
            bannerList.add(banner);
        }
        return bannerList;
    }

    //刪除旗幟
    static public void removeBanner(Player player, int index) {
        //設定檔
        String fileName = player.getName() + ".yml";
        FileConfiguration config = ConfigManager.get(fileName);
        Set<String> keySet = config.getKeys(false);
        List<String> keyList = new ArrayList<>();
        keyList.addAll(keySet);
        //當前頁數
        int currentBannerPage = 1;
        if (BannerMaker.getInstance().currentBannerPage.containsKey(player.getName())) {
            currentBannerPage = BannerMaker.getInstance().currentBannerPage.get(player.getName());
        } else {
            BannerMaker.getInstance().currentBannerPage.put(player.getName(), 1);
        }
        //索引值調整
        index += Math.max(0, (currentBannerPage - 1) * 45);
        //檢查索引值
        if (index >= keySet.size()) {
            return;
        }
        //設定檔路徑
        String path = keyList.get(index);
        //移除
        config.set(path, null);
        //儲存
        ConfigManager.save(fileName);
        //顯示訊息
        player.sendMessage(MessageUtil.format(true, "&aRemove banner &r#" + index));
    }
}

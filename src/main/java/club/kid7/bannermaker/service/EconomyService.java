package club.kid7.bannermaker.service;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.configuration.ConfigManager;
import club.kid7.bannermaker.registry.DyeColorRegistry;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.MaterialUtil;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EconomyService {

    /**
     * 檢查經濟系統是否可用
     *
     * @return 是否可用
     */
    public boolean isAvailable() {
        return BannerMaker.getInstance().getEconomy() != null;
    }

    /**
     * 取得旗幟的價格
     *
     * @param banner 旗幟
     * @return 價格
     */
    public double getPrice(ItemStack banner) {
        if (!BannerUtil.isBanner(banner)) {
            return 0;
        }
        if (!isAvailable()) {
            return 0;
        }
        FileConfiguration config = ConfigManager.get("config");
        if (config == null) {
            return 0;
        }
        double price = config.getDouble("Economy.Price", 0);

        List<ItemStack> materials = BannerUtil.getMaterials(banner);
        for (ItemStack material : materials) {
            price += getMaterialPrice(material) * material.getAmount();
        }

        return price;
    }

    /**
     * 格式化金額
     *
     * @param amount 金額
     * @return 格式化後的字串
     */
    public String format(double amount) {
        return BannerMaker.getInstance().getEconomy().format(amount);
    }

    /**
     * 檢查玩家是否有足夠的錢
     *
     * @param player 玩家
     * @param amount 金額
     * @return 是否足夠
     */
    public boolean has(Player player, double amount) {
        return BannerMaker.getInstance().getEconomy().has(player, amount);
    }

    /**
     * 從玩家扣款
     *
     * @param player 玩家
     * @param amount 金額
     * @return 交易回應
     */
    public EconomyResponse withdraw(Player player, double amount) {
        return BannerMaker.getInstance().getEconomy().withdrawPlayer(player, amount);
    }

    private double getMaterialPrice(ItemStack itemStack) {
        String priceConfigFileName = "price";
        FileConfiguration priceConfig = ConfigManager.get(priceConfigFileName);
        if (priceConfig == null) {
            return 0;
        }
        //物品資料
        Material type = itemStack.getType();
        //預設路徑
        String configPath = type.toString();
        //特殊路徑
        if (MaterialUtil.isWool(type)) {
            DyeColor woolColor = DyeColorRegistry.getDyeColor(type);
            if (woolColor != null) {
                configPath = "WOOL." + woolColor.name();
            }
        } else if (MaterialUtil.isDye(type)) {
            DyeColor dyeColor = DyeColorRegistry.getDyeColor(type);
            if (dyeColor != null) {
                configPath = "DYE." + dyeColor.name();
            }
        }
        //檢查設定
        if (!priceConfig.contains(configPath)) {
            priceConfig.set(configPath, 0);
            ConfigManager.save(priceConfigFileName);
        }

        //取得金額
        return priceConfig.getDouble(configPath, 0);
    }
}

package club.kid7.bannermaker.service;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.util.BannerUtil;
import club.kid7.bannermaker.util.EconUtil;
import club.kid7.bannermaker.util.InventoryUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

import static club.kid7.bannermaker.configuration.Language.tl;

public class BannerService {

    /**
     * 使用材料合成旗幟
     *
     * @param player 要給予物品的玩家
     * @param banner 要給予的旗幟
     * @return 是否成功給予
     */
    public boolean craft(Player player, ItemStack banner) {
        //檢查材料
        if (!BannerUtil.hasEnoughMaterials(player.getInventory(), banner)) {
            return false;
        }
        //移除材料
        if (!removeMaterials(player, banner)) {
            return false;
        }

        InventoryUtil.give(player, banner);
        return true;
    }

    /**
     * 購買旗幟
     *
     * @param player 要給予物品的玩家
     * @param banner 要給予的旗幟
     * @return 是否成功給予
     */
    public boolean buy(Player player, ItemStack banner) {
        MessageService messageService = BannerMaker.getInstance().getMessageService();
        //檢查是否啟用經濟
        if (BannerMaker.getInstance().getEconomy() == null) {
            //未啟用經濟，強制失敗
            messageService.send(player, Component.text("Error: Economy not supported", NamedTextColor.RED));
            return false;
        }
        //價格
        double price = EconUtil.getPrice(banner);
        //檢查財產是否足夠
        if (!BannerMaker.getInstance().getEconomy().has(player, price)) {
            //財產不足
            messageService.send(player, tl(NamedTextColor.RED, "general.no-money"));
            return false;
        }
        //扣款
        EconomyResponse response = BannerMaker.getInstance().getEconomy().withdrawPlayer(player, price);
        //檢查交易是否成功
        if (!response.transactionSuccess()) {
            //交易失敗
            messageService.send(player, Component.text("Error: " + response.errorMessage, NamedTextColor.RED));
            return false;
        }
        InventoryUtil.give(player, banner);
        messageService.send(player, tl(NamedTextColor.GREEN, "general.money-transaction",
            BannerMaker.getInstance().getEconomy().format(response.amount),
            BannerMaker.getInstance().getEconomy().format(response.balance)));
        return true;
    }

    /**
     * 從物品欄移除材料
     */
    private boolean removeMaterials(Player player, ItemStack banner) {
        if (!BannerUtil.isBanner(banner)) {
            return false;
        }
        if (!BannerUtil.hasEnoughMaterials(player.getInventory(), banner)) {
            return false;
        }
        List<ItemStack> materials = BannerUtil.getMaterials(banner);
        //過濾材料，不須消耗旗幟圖形
        materials.removeIf(BannerUtil::isBannerPatternItemStack);
        HashMap<Integer, ItemStack> itemCannotRemoved = player.getInventory().removeItem(materials.toArray(new ItemStack[0]));
        return itemCannotRemoved.isEmpty();
    }
}

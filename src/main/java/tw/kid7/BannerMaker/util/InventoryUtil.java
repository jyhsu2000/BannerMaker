package tw.kid7.BannerMaker.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class InventoryUtil {
    /**
     * 給予玩家單一物品堆疊
     *
     * @param player    要給予物品的玩家
     * @param itemStack 要給予的物品
     */
    public static void give(Player player, ItemStack itemStack) {
        //放進玩家的物品欄
        HashMap<Integer, ItemStack> itemsCanNotAddToInv = player.getInventory().addItem(itemStack);
        //若有放不進去的部分，直接噴在地上
        if (!itemsCanNotAddToInv.isEmpty()) {
            player.getWorld().dropItem(player.getLocation(), itemsCanNotAddToInv.get(0));
        }
    }
}

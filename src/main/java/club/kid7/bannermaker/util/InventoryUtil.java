package club.kid7.bannermaker.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class InventoryUtil {
    /**
     * 給予玩家單一物品堆疊
     *
     * @param player    要給予物品的玩家
     * @param itemStack 要給予的物品
     */
    public static void give(Player player, ItemStack itemStack) {
        //移除所有 PersistentData
        ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
        PersistentDataUtil.removeAll(itemMeta);
        itemStack.setItemMeta(itemMeta);
        //放進玩家的物品欄
        HashMap<Integer, ItemStack> itemsCanNotAddToInv = player.getInventory().addItem(itemStack);
        //若有放不進去的部分，直接噴在地上
        if (!itemsCanNotAddToInv.isEmpty()) {
            player.getWorld().dropItem(player.getLocation(), itemsCanNotAddToInv.get(0));
        }
    }

    public static void sort(List<ItemStack> itemStacks) {
        //移除空值
        itemStacks.removeAll(Collections.singletonList(null));
        //重新排序
        itemStacks.sort((itemStack1, itemStack2) -> {
            int c = Integer.compare(itemStack1.getType().ordinal(), itemStack2.getType().ordinal());
            if (c == 0) {
                c = -Integer.compare(itemStack1.getAmount(), itemStack2.getAmount());
            }
            return c;
        });
        //合併
        ItemStack previous = null;
        final Iterator<ItemStack> iterator = itemStacks.iterator();
        while (iterator.hasNext()) {
            final ItemStack item = iterator.next();
            if (previous != null && previous.isSimilar(item) && previous.getAmount() < previous.getMaxStackSize()) {
                int count = Math.min(item.getAmount(), previous.getMaxStackSize() - previous.getAmount());
                if (count > 0) {
                    previous.setAmount(previous.getAmount() + count);
                    item.setAmount(item.getAmount() - count);
                    if (item.getAmount() <= 0) {
                        iterator.remove();
                        continue;
                    }
                }
            }
            previous = item;
        }
    }
}

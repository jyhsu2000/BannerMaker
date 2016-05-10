package tw.kid7.BannerMaker.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BannerUtil {
    /**
     * 檢查ItemStack是否為旗幟
     *
     * @param itemStack 欲檢查的物品
     * @return boolean
     */
    static public boolean isBanner(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        if (itemStack.getType().equals(Material.BANNER)) {
            return true;
        }
        return false;
    }

    /**
     * 取得旗幟材料清單
     *
     * @param itemStack 欲取得材料清單之旗幟
     * @return List<ItemStack>
     */
    static public List<ItemStack> getMaterials(ItemStack itemStack) {
        List<ItemStack> materialList = new ArrayList<ItemStack>();
        //只檢查旗幟
        if (!isBanner(itemStack)) {
            return materialList;
        }
        //基本材料
        //木棒
        ItemStack stick = new ItemStack(Material.STICK, 1);
        materialList.add(stick);
        //羊毛
        //顏色
        int color = 15 - itemStack.getDurability();
        //羊毛
        ItemStack wool = new ItemStack(Material.WOOL, 6, (short) color);
        materialList.add(wool);

        return materialList;
    }
}

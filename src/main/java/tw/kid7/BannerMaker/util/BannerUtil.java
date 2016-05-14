package tw.kid7.BannerMaker.util;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.material.Dye;

import java.util.*;

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
        //Pattern材料
        Inventory materialInventory = Bukkit.createInventory(null, 54);
        BannerMeta bm = (BannerMeta) itemStack.getItemMeta();
        //逐Pattern計算
        for (Pattern pattern : bm.getPatterns()) {
            //所需染料
            Dye dye = new Dye();
            dye.setColor(pattern.getColor());
            switch (pattern.getPattern()) {
                case SQUARE_BOTTOM_LEFT:
                case SQUARE_BOTTOM_RIGHT:
                case SQUARE_TOP_LEFT:
                case SQUARE_TOP_RIGHT:
                case CIRCLE_MIDDLE:
                    materialInventory.addItem(dye.toItemStack(1));
                    break;
                case STRIPE_BOTTOM:
                case STRIPE_TOP:
                case STRIPE_LEFT:
                case STRIPE_RIGHT:
                case STRIPE_CENTER:
                case STRIPE_MIDDLE:
                case STRIPE_DOWNRIGHT:
                case STRIPE_DOWNLEFT:
                case TRIANGLE_BOTTOM:
                case TRIANGLE_TOP:
                case TRIANGLES_BOTTOM:
                case TRIANGLES_TOP:
                case DIAGONAL_LEFT:
                case DIAGONAL_RIGHT:
                case DIAGONAL_LEFT_MIRROR:
                case DIAGONAL_RIGHT_MIRROR:
                    materialInventory.addItem(dye.toItemStack(3));
                    break;
                case STRIPE_SMALL:
                case RHOMBUS_MIDDLE:
                case GRADIENT:
                case GRADIENT_UP:
                    materialInventory.addItem(dye.toItemStack(4));
                    break;
                case CROSS:
                case STRAIGHT_CROSS:
                    materialInventory.addItem(dye.toItemStack(5));
                    break;
                case HALF_VERTICAL:
                case HALF_HORIZONTAL:
                case HALF_VERTICAL_MIRROR:
                case HALF_HORIZONTAL_MIRROR:
                    materialInventory.addItem(dye.toItemStack(6));
                    break;
                case BORDER:
                    materialInventory.addItem(dye.toItemStack(8));
                    break;
                case CURLY_BORDER:
                    materialInventory.addItem(new ItemStack(Material.VINE));
                    if (!pattern.getColor().equals(DyeColor.BLACK)) {
                        materialInventory.addItem(dye.toItemStack(1));
                    }
                    break;
                case CREEPER:
                    materialInventory.addItem(new ItemStack(Material.SKULL_ITEM, 1, (short) 4));
                    if (!pattern.getColor().equals(DyeColor.BLACK)) {
                        materialInventory.addItem(dye.toItemStack(1));
                    }
                    break;
                case BRICKS:
                    materialInventory.addItem(new ItemStack(Material.BRICK));
                    if (!pattern.getColor().equals(DyeColor.BLACK)) {
                        materialInventory.addItem(dye.toItemStack(1));
                    }
                    break;
                case SKULL:
                    materialInventory.addItem(new ItemStack(Material.SKULL_ITEM, 1, (short) 1));
                    if (!pattern.getColor().equals(DyeColor.BLACK)) {
                        materialInventory.addItem(dye.toItemStack(1));
                    }
                    break;
                case FLOWER:
                    materialInventory.addItem(new ItemStack(Material.RED_ROSE, 1, (short) 8));
                    if (!pattern.getColor().equals(DyeColor.BLACK)) {
                        materialInventory.addItem(dye.toItemStack(1));
                    }
                    break;
                case MOJANG:
                    materialInventory.addItem(new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1));
                    if (!pattern.getColor().equals(DyeColor.BLACK)) {
                        materialInventory.addItem(dye.toItemStack(1));
                    }
                    break;
            }
        }
        //加到暫存清單
        List<ItemStack> patternMaterials = new ArrayList<ItemStack>();
        Collections.addAll(patternMaterials, materialInventory.getContents());
        //移除空值
        patternMaterials.removeAll(Collections.singletonList(null));
        //重新排序
        Collections.sort(patternMaterials, new Comparator<ItemStack>() {
            public int compare(ItemStack itemStack1, ItemStack itemStack2) {
                if (itemStack1.getTypeId() != itemStack2.getTypeId()) {
                    return itemStack1.getTypeId() - itemStack2.getTypeId();
                }
                return itemStack1.getDurability() - itemStack2.getDurability();
            }
        });
        //將材料加到清單中
        materialList.addAll(patternMaterials);

        return materialList;
    }

    /**
     * 取得旗幟在玩家存檔中的Key
     *
     * @param itemStack 欲檢查之旗幟
     * @return String
     */
    static public String getKey(ItemStack itemStack) {
        //只檢查旗幟
        if (!isBanner(itemStack)) {
            return null;
        }
        String key = null;
        //嘗試取出key
        try {
            key = HiddenStringUtil.extractHiddenString(itemStack.getItemMeta().getLore().get(0));
        } catch (Exception exception) {
            return null;
        }
        return key;
    }
}

package tw.kid7.util.customGUI;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CustomGUIManager {
    private static HashMap<UUID, Class<? extends CustomGUIMenu>> lastOpenedMenuClassMap = Maps.newHashMap();

    /**
     * 對特定玩家開啟前一次開啟的自定義選單（{@link CustomGUIInventory}）
     *
     * @param player 玩家
     */
    public static void openPrevious(Player player) {
        openPrevious(player, null);
    }

    /**
     * 對特定玩家開啟前一次開啟的自定義選單（{@link CustomGUIInventory}）
     *
     * @param player           玩家
     * @param defaultMenuClass 預設選單類別
     */
    public static void openPrevious(Player player, Class<? extends CustomGUIMenu> defaultMenuClass) {
        try {
            Class<? extends CustomGUIMenu> lastOpenedMenuClass = lastOpenedMenuClassMap.get(player.getUniqueId());
            if (lastOpenedMenuClass == null) {
                lastOpenedMenuClass = defaultMenuClass;
            }
            CustomGUIMenu menu = lastOpenedMenuClass.newInstance();
            CustomGUIInventory inventory = menu.build(player);
            if (inventory == null) {
                return;
            }
            inventory.open(player);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 對特定玩家開啟自定義選單（{@link CustomGUIInventory}）
     *
     * @param player    玩家
     * @param menuClass 選單類別
     */
    public static void open(Player player, Class<? extends CustomGUIMenu> menuClass) {
        try {
            CustomGUIMenu menu = menuClass.newInstance();
            lastOpenedMenuClassMap.put(player.getUniqueId(), menuClass);
            CustomGUIInventory inventory = menu.build(player);
            if (inventory == null) {
                return;
            }
            inventory.open(player);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

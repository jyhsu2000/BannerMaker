package tw.kid7.BannerMaker;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import tw.kid7.BannerMaker.inventoryMenu.*;

import java.util.HashMap;

public enum State {
    MAIN_MENU,
    CREATE_BANNER,
    CREATE_ALPHABET,
    BANNER_INFO;

    private static HashMap<String, State> stateMap = Maps.newHashMap();

    /**
     * 設定玩家狀態
     *
     * @param player 玩家
     * @param state  狀態
     */
    public static void set(Player player, State state) {
        stateMap.put(player.getName(), state);
    }

    /**
     * 取得玩家狀態（預設：MAIN_MENU）
     *
     * @param player 玩家
     * @return 狀態
     */
    public static State get(Player player) {
        return get(player, State.MAIN_MENU);
    }

    /**
     * 取得玩家狀態
     * 並指定無法取得時的預設值
     *
     * @param player 玩家
     * @return 狀態
     */
    public static State get(Player player, State defaultState) {
        if (!stateMap.containsKey(player.getName())) {
            return defaultState;
        }
        return stateMap.get(player.getName());
    }

    /**
     * 取得此狀態的GUI選單介面
     *
     * @return GUI選單介面
     */
    public AbstractInventoryMenu getInventoryMenu() {
        AbstractInventoryMenu menu;
        switch (this) {
            case CREATE_BANNER:
                menu = CreateBannerInventoryMenu.getInstance();
                break;
            case CREATE_ALPHABET:
                menu = CreateAlphabetInventoryMenu.getInstance();
                break;
            case BANNER_INFO:
                menu = BannerInfoInventoryMenu.getInstance();
                break;
            case MAIN_MENU:
            default:
                menu = MainInventoryMenu.getInstance();
        }
        return menu;
    }
}

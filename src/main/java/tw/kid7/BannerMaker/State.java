package tw.kid7.BannerMaker;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

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
}

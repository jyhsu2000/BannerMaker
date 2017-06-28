package tw.kid7.BannerMaker;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerDataMap {
    /**
     * 所有玩家資料實例
     */
    private static final HashMap<String, PlayerData> playerDataMap = Maps.newHashMap();

    /**
     * 取得玩家資料實例
     *
     * @param player 玩家
     * @return 玩家資料
     */
    public static PlayerData get(Player player) {
        String uuidString = player.getUniqueId().toString();
        PlayerData playerData = playerDataMap.get(uuidString);
        if (playerData == null) {
            playerData = new PlayerData();
            playerDataMap.put(uuidString, playerData);
        }
        return playerData;
    }
}

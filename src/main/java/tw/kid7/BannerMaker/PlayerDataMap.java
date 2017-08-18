package tw.kid7.BannerMaker;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerDataMap {
    /**
     * 所有玩家資料實例
     */
    private final HashMap<UUID, PlayerData> playerDataMap = Maps.newHashMap();

    /**
     * 取得玩家資料實例
     *
     * @param player 玩家
     * @return 玩家資料
     */
    public PlayerData get(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData playerData = playerDataMap.get(uuid);
        if (playerData == null) {
            playerData = new PlayerData();
            playerDataMap.put(uuid, playerData);
        }
        return playerData;
    }
}

package club.kid7.bannermaker;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerDataMap {
    /**
     * 所有玩家資料實例
     */
    private final HashMap<UUID, PlayerData> allPlayerData = Maps.newHashMap();

    /**
     * 取得玩家資料實例
     *
     * @param player 玩家
     * @return 玩家資料
     */
    public PlayerData get(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData playerData = allPlayerData.get(uuid);
        if (playerData == null) {
            playerData = new PlayerData();
            allPlayerData.put(uuid, playerData);
        }
        return playerData;
    }
}

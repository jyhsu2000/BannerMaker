package tw.jyhsu.bannermaker;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataMap {

    private final Map<UUID, PlayerData> allPlayerData = new HashMap<>();

    /**
     * 取得玩家的 {@link PlayerData}；尚未存在則 lazily 建立。
     *
     * @param player 玩家
     * @return 該玩家的資料；同一玩家多次呼叫回傳同一物件
     */
    public PlayerData get(Player player) {
        return allPlayerData.computeIfAbsent(player.getUniqueId(), k -> new PlayerData());
    }
}

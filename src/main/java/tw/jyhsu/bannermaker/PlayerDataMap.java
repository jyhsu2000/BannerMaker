package tw.jyhsu.bannermaker;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 將每個玩家對應到一份 {@link PlayerData}（per-player GUI 編輯狀態）。
 * 玩家斷線時自動移除該玩家的 entry，避免長時間運行的伺服器累積離線玩家的 stale 資料。
 */
public class PlayerDataMap implements Listener {

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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        allPlayerData.remove(event.getPlayer().getUniqueId());
    }
}

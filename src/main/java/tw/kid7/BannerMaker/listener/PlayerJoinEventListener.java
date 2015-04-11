package tw.kid7.BannerMaker.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tw.kid7.BannerMaker.util.IOUtil;

public class PlayerJoinEventListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        //登入時檢查更新設定檔檔名
        IOUtil.updateFileNameToUUID(player);
    }
}

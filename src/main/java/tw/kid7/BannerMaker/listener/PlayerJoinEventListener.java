package tw.kid7.BannerMaker.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tw.kid7.BannerMaker.util.IOUtil;

public class PlayerJoinEventListener implements Listener{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        //更新資料
        IOUtil.update(player.getName());
    }
}

package tw.kid7.BannerMaker.version;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VersionHandler {
    public ItemStack getItemInMainHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }
}

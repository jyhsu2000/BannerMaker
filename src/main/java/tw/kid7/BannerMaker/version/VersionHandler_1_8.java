package tw.kid7.BannerMaker.version;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VersionHandler_1_8 extends VersionHandler {
    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getItemInMainHand(Player player) {
        return player.getItemInHand();
    }
}

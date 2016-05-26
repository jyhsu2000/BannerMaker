package tw.kid7.BannerMaker.util;

import org.bukkit.entity.Player;
import tw.kid7.BannerMaker.State;
import tw.kid7.BannerMaker.inventoryMenu.AbstractInventoryMenu;

public class InventoryMenuUtil {

    static public void openMenu(Player player) {
        //取得玩家狀態
        State state = State.get(player);
        //取得該狀態的GUI選單
        AbstractInventoryMenu menu = state.getInventoryMenu();
        //開啟選單
        menu.open(player);
    }

}

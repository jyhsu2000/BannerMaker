package tw.kid7.BannerMaker.customMenu;

import com.google.common.collect.Maps;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;

public class CustomMenuItem {
    /**
     * 各點擊類型的動作
     */
    private HashMap<ClickType, CustomMenuAction> customMenuActionMap = Maps.newHashMap();

    /**
     * 建構子
     */
    CustomMenuItem() {
    }

    /**
     * 設定特定點擊類型的動作
     *
     * @param clickType 點擊類型
     * @param customMenuAction 動作
     * @return this
     */
    public CustomMenuItem set(ClickType clickType, CustomMenuAction customMenuAction) {
        customMenuActionMap.put(clickType, customMenuAction);
        return this;
    }

    /**
     * 取得特定點擊類型的動作
     *
     * @param clickType 點擊類型
     * @return 動作
     */
    public CustomMenuAction get(ClickType clickType) {
        return customMenuActionMap.get(clickType);
    }

    /**
     * 執行特定點擊類型的動作
     *
     * @param clickType 點擊類型
     */
    public void action(ClickType clickType) {
        CustomMenuAction customMenuAction = get(clickType);
        if (customMenuAction == null) {
            return;
        }
        customMenuAction.action();
    }
}

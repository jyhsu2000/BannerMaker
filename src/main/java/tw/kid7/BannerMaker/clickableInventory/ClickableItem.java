package tw.kid7.BannerMaker.clickableInventory;

import com.google.common.collect.Maps;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;

public class ClickableItem {
    /**
     * 各點擊類型的動作
     */
    private HashMap<ClickType, Clickable> clickableHashMap = Maps.newHashMap();

    /**
     * 建構子
     */
    ClickableItem() {
    }

    /**
     * 設定特定點擊類型的動作
     *
     * @param clickType 點擊類型
     * @param clickable 動作
     * @return this
     */
    public ClickableItem set(ClickType clickType, Clickable clickable) {
        clickableHashMap.put(clickType, clickable);
        return this;
    }

    /**
     * 取得特定點擊類型的動作
     *
     * @param clickType 點擊類型
     * @return 動作
     */
    public Clickable get(ClickType clickType) {
        return clickableHashMap.get(clickType);
    }

    /**
     * 執行特定點擊類型的動作
     *
     * @param clickType 點擊類型
     */
    public void action(ClickType clickType) {
        Clickable clickable = get(clickType);
        if (clickable == null) {
            return;
        }
        clickable.action();
    }
}

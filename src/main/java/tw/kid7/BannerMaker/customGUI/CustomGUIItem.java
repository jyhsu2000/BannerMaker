package tw.kid7.BannerMaker.customGUI;

import com.google.common.collect.Maps;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;

public class CustomGUIItem {
    /**
     * 各點擊類型的動作
     */
    private HashMap<ClickType, CustomGUIItemListener> customGUIItemListenerMap = Maps.newHashMap();

    /**
     * 建構子
     */
    CustomGUIItem() {
    }

    /**
     * 設定特定點擊類型的動作
     *
     * @param clickType 點擊類型
     * @param customGUIItemListener 動作
     * @return this
     */
    public CustomGUIItem set(ClickType clickType, CustomGUIItemListener customGUIItemListener) {
        customGUIItemListenerMap.put(clickType, customGUIItemListener);
        return this;
    }

    /**
     * 取得特定點擊類型的動作
     *
     * @param clickType 點擊類型
     * @return 動作
     */
    public CustomGUIItemListener get(ClickType clickType) {
        return customGUIItemListenerMap.get(clickType);
    }

    /**
     * 執行特定點擊類型的動作
     *
     * @param clickType 點擊類型
     */
    public void action(ClickType clickType) {
        CustomGUIItemListener customGUIItemListener = get(clickType);
        if (customGUIItemListener == null) {
            return;
        }
        customGUIItemListener.action();
    }
}

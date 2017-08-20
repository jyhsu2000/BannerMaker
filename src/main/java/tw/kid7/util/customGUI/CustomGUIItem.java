package tw.kid7.util.customGUI;

import com.google.common.collect.Maps;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;

public class CustomGUIItem {
    /**
     * 各點擊類型的動作
     */
    private HashMap<ClickType, CustomGUIItemHandler> customGUIItemHandlerMap = Maps.newHashMap();

    /**
     * 建構子
     */
    CustomGUIItem() {
    }

    /**
     * 設定特定點擊類型的動作
     *
     * @param clickType 點擊類型
     * @param customGUIItemHandler 動作
     * @return this
     */
    public CustomGUIItem set(ClickType clickType, CustomGUIItemHandler customGUIItemHandler) {
        customGUIItemHandlerMap.put(clickType, customGUIItemHandler);
        return this;
    }

    /**
     * 取得特定點擊類型的動作
     *
     * @param clickType 點擊類型
     * @return 動作
     */
    private CustomGUIItemHandler get(ClickType clickType) {
        return customGUIItemHandlerMap.get(clickType);
    }

    /**
     * 執行特定點擊類型的動作
     *
     * @param clickType 點擊類型
     */
    void action(ClickType clickType) {
        CustomGUIItemHandler customGUIItemHandler = get(clickType);
        if (customGUIItemHandler == null) {
            return;
        }
        customGUIItemHandler.action();
    }
}

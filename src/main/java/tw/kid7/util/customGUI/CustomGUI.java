package tw.kid7.util.customGUI;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomGUI {
    private static boolean enabled = false;

    /**
     * 啟用流程
     */
    public static void enable(JavaPlugin plugin) {
        //避免重複啟用
        if (enabled) {
            return;
        }
        //監聽事件
        Bukkit.getPluginManager().registerEvents(new CustomGUIMenuListener(), plugin);
        enabled = true;
    }

    /**
     * 禁用流程
     */
    public static void disable() {
        enabled = false;
        //關閉所有自定義選單
        CustomGUIMenu.closeAll();
    }

    /**
     * 此機制是否已經啟用
     *
     * @return 是否已經啟用
     */
    static boolean isEnabled() {
        return enabled;
    }
}
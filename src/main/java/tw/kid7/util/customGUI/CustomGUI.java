package tw.kid7.util.customGUI;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomGUI {
    private static boolean enabled = false;

    /**
     * 啟用流程
     */
    public static void enable(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new CustomGUIMenuListener(), plugin);
        enabled = true;
    }

    /**
     * 禁用流程
     */
    public static void disable() {
        enabled = false;
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

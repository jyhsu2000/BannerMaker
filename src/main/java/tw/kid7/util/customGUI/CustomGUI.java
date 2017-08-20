package tw.kid7.util.customGUI;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomGUI {
    private JavaPlugin plugin;

    /**
     * 建構子
     *
     * @param plugin 使用此機制的插件
     */
    public CustomGUI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 啟用流程
     */
    public void enable() {
        Bukkit.getPluginManager().registerEvents(new CustomGUIMenuListener(), plugin);
    }

    /**
     * 禁用流程
     */
    public void disable() {
        CustomGUIMenu.closeAll();
    }
}

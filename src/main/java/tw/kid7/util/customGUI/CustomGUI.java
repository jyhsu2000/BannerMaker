package tw.kid7.util.customGUI;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomGUI {
    private JavaPlugin plugin;

    public CustomGUI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        Bukkit.getPluginManager().registerEvents(new CustomGUIMenuListener(), plugin);
    }

    public void disable() {
        CustomGUIMenu.closeAll();
    }
}

package tw.kid7.BannerMaker;

import org.bukkit.plugin.java.JavaPlugin;

public class BannerMaker extends JavaPlugin {
    private static BannerMaker instance = null;

    @Override
    public void onEnable() {
        //指令
        this.getCommand("BannerMaker").setExecutor(new BannerMakerCommandExecutor());
    }

    @Override
    public void onDisable() {

    }

    public static BannerMaker getInstance() {
        return instance;
    }
}

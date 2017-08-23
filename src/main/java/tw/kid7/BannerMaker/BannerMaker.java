package tw.kid7.BannerMaker;

import net.milkbowl.vault.economy.Economy;
import org.bstats.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import tw.kid7.BannerMaker.command.CommandManager;
import tw.kid7.BannerMaker.configuration.ConfigManager;
import tw.kid7.BannerMaker.configuration.DefaultConfig;
import tw.kid7.BannerMaker.configuration.Language;
import tw.kid7.BannerMaker.version.VersionHandler;
import tw.kid7.BannerMaker.version.VersionHandler_1_8;
import tw.kid7.util.customGUI.CustomGUI;

import java.util.Arrays;
import java.util.List;

public class BannerMaker extends JavaPlugin {
    private static BannerMaker instance = null;
    public Economy econ = null;
    public boolean enableAlphabetAndNumber = true;
    private VersionHandler versionHandler = null;
    public CommandManager commandManager = null;
    public PlayerDataMap playerDataMap = null;

    public VersionHandler getVersionHandler() {
        return versionHandler;
    }

    @Override
    public void onEnable() {
        instance = this;
        //根據不同版本選擇Handler
        String version = getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        switch (version) {
            case "v1_8_R1":
            case "v1_8_R2":
            case "v1_8_R3":
                versionHandler = new VersionHandler_1_8();
                break;
            default:
                versionHandler = new VersionHandler();
                break;
        }

        //指令
        commandManager = new CommandManager(this);
        this.getCommand("BannerMaker").setExecutor(commandManager);
        this.getCommand("BannerMaker").setTabCompleter(commandManager);
        //CustomGUI
        CustomGUI.enable();
        //Config
        List<String> configList = Arrays.asList("config", "price");
        for (String config : configList) {
            String configFileName = config + ".yml";
            ConfigManager.load(configFileName);
            ConfigManager.save(configFileName);
        }
        //Reload
        reload();
        //bStats
        Metrics metrics = new Metrics(this);
    }

    @Override
    public void onDisable() {
        //CustomGUI
        CustomGUI.disable();
    }

    public static BannerMaker getInstance() {
        return instance;
    }

    public void reload() {
        //Reload Config
        ConfigManager.reloadAll();
        //載入語言包
        new Language(this).loadLanguage();
        //Check Default Config
        new DefaultConfig(this).checkConfig();
        //經濟
        if (setupEconomy()) {
            getLogger().info("Vault dependency found! Enable economy supported");
        } else {
            getLogger().info("Disable economy supported");
        }
        //設定檔
        String configFileName = "config.yml";
        FileConfiguration config = ConfigManager.get(configFileName);
        if (config != null) {
            //字母與數字
            enableAlphabetAndNumber = config.getBoolean("AlphabetAndNumberBanner.Enable", true);
        }
        //玩家資料
        playerDataMap = new PlayerDataMap();
    }

    private boolean setupEconomy() {
        econ = null;
        //檢查設定
        String configFileName = "config.yml";
        FileConfiguration config = ConfigManager.get(configFileName);
        assert config != null;
        //若無啟用經濟
        if (!config.getBoolean("Economy.Enable", false)) {
            return false;
        }

        //檢查Vault
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        //檢查經濟支援
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}

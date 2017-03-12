package tw.kid7.BannerMaker;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import tw.kid7.BannerMaker.configuration.ConfigManager;
import tw.kid7.BannerMaker.configuration.DefaultConfig;
import tw.kid7.BannerMaker.configuration.Language;
import tw.kid7.BannerMaker.listener.InventoryClickEventListener;
import tw.kid7.BannerMaker.listener.PlayerJoinEventListener;
import tw.kid7.BannerMaker.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BannerMaker extends JavaPlugin {
    private static BannerMaker instance = null;
    public static Economy econ = null;
    public static boolean enableAlphabetAndNumber = true;

    @Override
    public void onEnable() {
        instance = this;
        //指令
        this.getCommand("BannerMaker").setExecutor(new BannerMakerCommandExecutor());
        //Listener
        this.getServer().getPluginManager().registerEvents(new InventoryClickEventListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(), this);
        //check and update banner data
        File folder = new File(getDataFolder() + "/banner");
        File[] fileList = folder.listFiles();
        if (fileList != null && fileList.length > 0) {
            for (File file : fileList) {
                if (file.isFile()) {
                    String fileName = file.getName().replace(".yml", "");
                    IOUtil.update(fileName);
                }
            }
        }
        //Config
        List<String> configList = Arrays.asList("config");
        for (String config : configList) {
            String configFileName = config + ".yml";
            ConfigManager.load(configFileName);
            ConfigManager.save(configFileName);
        }
        //Reload
        reload();
        //Plugin Metrics
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }
    }

    @Override
    public void onDisable() {

    }

    public static BannerMaker getInstance() {
        return instance;
    }

    public static void reload() {
        //載入語言包
        Language.loadLanguage();
        //Reload Config
        ConfigManager.reloadAll();
        //Check Default Config
        new DefaultConfig().checkConfig();
        //經濟
        if (getInstance().setupEconomy()) {
            getInstance().getLogger().info("Vault dependency found! Enable economy supported");
        } else {
            getInstance().getLogger().info("Disable economy supported");
        }
        //設定檔
        String configFileName = "config.yml";
        FileConfiguration config = ConfigManager.get(configFileName);
        if (config != null) {
            //字母與數字
            enableAlphabetAndNumber = config.getBoolean("AlphabetAndNumberBanner.Enable", true);
        }
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
        //若價格設定非正數
        if (config.getDouble("Economy.Price", 0) <= 0) {
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

package tw.kid7.BannerMaker;

import com.google.common.collect.Maps;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import tw.kid7.BannerMaker.configuration.ConfigManager;
import tw.kid7.BannerMaker.configuration.DefaultConfig;
import tw.kid7.BannerMaker.configuration.Language;
import tw.kid7.BannerMaker.listener.InventoryClickEventListener;
import tw.kid7.BannerMaker.listener.PlayerJoinEventListener;
import tw.kid7.BannerMaker.util.IOUtil;
import tw.kid7.BannerMaker.util.MessageUtil;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BannerMaker extends JavaPlugin {
    private static BannerMaker instance = null;
    public static Economy econ = null;
    public HashMap<String, State> stateMap = Maps.newHashMap();
    public HashMap<String, Integer> selectedColor = Maps.newHashMap();
    public HashMap<String, ItemStack> currentBanner = Maps.newHashMap();
    public HashMap<String, Boolean> morePatterns = Maps.newHashMap();
    public HashMap<String, Integer> selectedIndex = Maps.newHashMap();
    public HashMap<String, Integer> currentRecipePage = Maps.newHashMap();
    public HashMap<String, Integer> currentBannerPage = Maps.newHashMap();
    List<String> configList;

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
        configList = Arrays.asList("config");
        for (String config : configList) {
            String configFileName = config + ".yml";
            ConfigManager.load(configFileName);
            ConfigManager.save(configFileName);
        }
        //Reload
        reload();
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
            getInstance().getServer().getConsoleSender().sendMessage(MessageUtil.format(true, "&aVault dependency found! Enable economy supported"));
        } else {
            getInstance().getServer().getConsoleSender().sendMessage(MessageUtil.format(true, "&cDisable economy supported"));
        }
    }

    private boolean setupEconomy() {
        econ = null;
        //檢查設定
        String configFileName = "config.yml";
        FileConfiguration config = ConfigManager.get(configFileName);
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

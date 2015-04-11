package tw.kid7.BannerMaker;

import com.google.common.collect.Maps;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import tw.kid7.BannerMaker.configuration.ConfigManager;
import tw.kid7.BannerMaker.configuration.DefaultConfig;
import tw.kid7.BannerMaker.configuration.Language;
import tw.kid7.BannerMaker.listener.InventoryClickEventListener;
import tw.kid7.BannerMaker.listener.PlayerJoinEventListener;
import tw.kid7.BannerMaker.util.IOUtil;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BannerMaker extends JavaPlugin {
    private static BannerMaker instance = null;
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
        //Reload Config
        ConfigManager.reloadAll();
        //Check Default Config
        new DefaultConfig().checkConfig();
        //載入語言包
        Language.loadLanguage();
    }
}

package club.kid7.bannermaker;

import club.kid7.bannermaker.configuration.ConfigManager;
import club.kid7.bannermaker.configuration.DefaultConfig;
import club.kid7.bannermaker.configuration.Language;
import club.kid7.bannermaker.service.BannerRepository;
import club.kid7.bannermaker.service.BannerService;
import club.kid7.bannermaker.service.EconomyService;
import club.kid7.bannermaker.service.MessageService;
import co.aikar.commands.PaperCommandManager;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

import static club.kid7.bannermaker.configuration.Language.tl;

public class BannerMaker extends JavaPlugin {
    private static BannerMaker instance = null;
    private Economy econ = null;
    private boolean enableAlphabetAndNumber = true;
    private boolean enableComplexBannerCraft = false;
    private PlayerDataMap playerDataMap = null;
    private MessageService messageService;
    private EconomyService economyService;
    private BannerService bannerService;
    private BannerRepository bannerRepository;
    private PaperCommandManager commandManager;

    public static BannerMaker getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // 初始化服務
        messageService = new MessageService(this);
        economyService = new EconomyService();
        bannerService = new BannerService();
        bannerRepository = new BannerRepository();

        // 初始化 ACF Command Manager
        commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");

        //Commands
        registerCommands();

        //Config
        List<String> configList = Arrays.asList("config", "price");
        for (String config : configList) {
            ConfigManager.load(config);
        }
        //Reload
        reload();
        //bStats
        if (!isUnitTest()) {
            int pluginId = 383;
            new Metrics(this, pluginId);
        }
    }

    private boolean isUnitTest() {
        try {
            Class.forName("org.mockbukkit.mockbukkit.MockBukkit");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onDisable() {
        // 關閉 MessageService 的 Audiences
        if (messageService != null) {
            messageService.closeAudiences();
        }
    }

    // 提供 MessageService 的 getter
    public MessageService getMessageService() {
        return messageService;
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

    public BannerService getBannerService() {
        return bannerService;
    }

    public EconomyService getEconomyService() {
        return economyService;
    }

    public BannerRepository getBannerRepository() {
        return bannerRepository;
    }

    public Economy getEconomy() {
        return econ;
    }

    public void setEconomy(Economy econ) {
        this.econ = econ;
    }

    public boolean isEnableAlphabetAndNumber() {
        return enableAlphabetAndNumber;
    }

    public boolean isEnableComplexBannerCraft() {
        return enableComplexBannerCraft;
    }

    public PlayerDataMap getPlayerDataMap() {
        return playerDataMap;
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
            messageService.send(getServer().getConsoleSender(), tl("general.economy-enabled"));
        } else {
            messageService.send(getServer().getConsoleSender(), tl("general.economy-disabled"));
        }
        //設定檔
        FileConfiguration config = ConfigManager.get("config");
        if (config != null) {
            //字母與數字
            enableAlphabetAndNumber = config.getBoolean("AlphabetAndNumberBanner.Enable", true);
            //複雜旗幟合成
            enableComplexBannerCraft = config.getBoolean("ComplexBannerCraft.Enable", false);
        }
        //玩家資料
        playerDataMap = new PlayerDataMap();
    }

    private boolean setupEconomy() {
        econ = null;
        //檢查設定
        FileConfiguration config = ConfigManager.get("config");
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

    private void registerCommands() {
        // Old Command Registration (Disabled)
        // CommandComponent bmCommand = new BannerMakerCommand(this);
        // getCommand("BannerMaker").setExecutor(bmCommand);
        // getCommand("BannerMaker").setTabCompleter(bmCommand);

        // New ACF Command Registration
        commandManager.registerCommand(new club.kid7.bannermaker.command.acf.BannerMakerCommand(this));
    }
}

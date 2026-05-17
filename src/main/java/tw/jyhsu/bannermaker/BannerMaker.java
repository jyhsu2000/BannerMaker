package tw.jyhsu.bannermaker;

import tw.jyhsu.bannermaker.configuration.ConfigManager;
import tw.jyhsu.bannermaker.configuration.DefaultConfig;
import tw.jyhsu.bannermaker.configuration.Language;
import tw.jyhsu.bannermaker.gui.GuiUtil;
import tw.jyhsu.bannermaker.service.BannerRepository;
import tw.jyhsu.bannermaker.state.PlayerDataMap;
import tw.jyhsu.bannermaker.service.BannerService;
import tw.jyhsu.bannermaker.service.EconomyService;
import tw.jyhsu.bannermaker.service.MessageService;
import co.aikar.commands.PaperCommandManager;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

import static tw.jyhsu.bannermaker.configuration.Language.tl;

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
    private Language language;

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
        playerDataMap = new PlayerDataMap();
        getServer().getPluginManager().registerEvents(playerDataMap, this);

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

    public Language getLanguage() {
        return language;
    }

    /**
     * 重新建立並載入語言系統。供 {@link #reload()} 與測試使用。
     * 每次呼叫都會 new 一個 Language instance 並寫入 {@link #language}，跟舊版每次 {@code reload()}
     * 內 {@code new Language(this).loadLanguage()} 行為等價，只是 reference 現在有被保留。
     */
    public void reloadLanguage() {
        language = new Language(this);
        language.loadLanguage();
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
        reloadLanguage();
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
        // playerDataMap 在 onEnable() 時建立、不在 reload() 重建：玩家正在 GUI 編輯時若 admin
        // 跑 /bm reload，他的 currentEditBanner / 字母旗幟編輯等狀態不該被清空。但強制關掉所有
        // 開著的本插件 GUI，避免玩家看到 stale 譯文 / 價格 / 已關閉功能的按鈕；編輯狀態保留在
        // PlayerData，重開 GUI 後可從上次繼續。onEnable 內第一次 reload() 時 onlinePlayers
        // 為空，closeAllOurGuis() 是 no-op，不需特判。
        GuiUtil.closeAllOurGuis();
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
        commandManager.registerCommand(new tw.jyhsu.bannermaker.command.BannerMakerCommand(this));
    }
}

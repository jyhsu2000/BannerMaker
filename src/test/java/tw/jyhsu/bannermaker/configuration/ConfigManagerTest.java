package tw.jyhsu.bannermaker.configuration;

import tw.jyhsu.bannermaker.BannerMaker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigManagerTest {

    @AfterEach
    void tearDown() {
        ConfigManager.reset();
    }

    /**
     * 確認 ConfigManager.load() 在 BannerMaker plugin 不存在時 fail-fast。
     * <p>
     * 過去這個情境（onEnable 之前 / JVM 剛起、static instance 仍為 null）會在
     * {@code plugin.getDataFolder()} 觸發模糊的 NPE；改為拋 IllegalStateException
     * 並標示出被呼叫的方法名，方便診斷。
     */
    @Test
    void load_FailsFast_WhenPluginNotEnabled() {
        try (MockedStatic<BannerMaker> mocked = Mockito.mockStatic(BannerMaker.class)) {
            mocked.when(BannerMaker::getInstance).thenReturn(null);

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> ConfigManager.load("nonexistent.yml"));

            assertTrue(ex.getMessage().contains("load"),
                "例外訊息應指出是哪個方法被呼叫，目前訊息：" + ex.getMessage());
            assertTrue(ex.getMessage().contains("lifecycle"),
                "例外訊息應明確指出是生命週期問題，目前訊息：" + ex.getMessage());
        }
    }

    /**
     * 卡死 load() 的執行順序：fresh install（磁碟上無檔案）時必須「先解壓資源檔、後載入記憶體」。
     * 若順序顛倒，記憶體會快取到空設定，導致預設值遺失、DefaultConfig 誤判所有 key 缺漏。
     */
    @Test
    void load_ExtractsResourceBeforeCaching_OnFreshInstall() {
        MockBukkit.mock();
        try {
            BannerMaker plugin = MockBukkit.load(BannerMaker.class);
            // 模擬 fresh install：清空快取並刪除 onEnable 時已解壓的 config.yml
            ConfigManager.reset();
            File file = new File(plugin.getDataFolder(), "config.yml");
            assertTrue(!file.exists() || file.delete(), "前置：應能刪除已解壓的 config.yml");

            ConfigManager.load("config");

            assertTrue(ConfigManager.get("config").contains("Language"),
                "fresh install 時記憶體應含 jar 內預設值，不應是空設定");
        } finally {
            MockBukkit.unmock();
        }
    }

    /**
     * 資料檔（resource=false）缺檔屬正常狀態：load() 不建檔、save() 才建檔、
     * 檔案被外部刪除後 reload() 應將記憶體同步為空而非噴 SEVERE（issue #35 多伺服器共用資料夾情境）。
     */
    @Test
    void dataFile_LoadDoesNotCreateFile_AndReloadSyncsToEmptyAfterExternalDelete() {
        MockBukkit.mock();
        try {
            BannerMaker plugin = MockBukkit.load(BannerMaker.class);
            String fileName = "banner/test-user.yml";
            File file = new File(plugin.getDataFolder(), fileName);

            ConfigManager.load(fileName, false);
            assertFalse(file.exists(), "load() 對資料檔不應在磁碟建立檔案");

            ConfigManager.set(fileName, "1700000000000.color", "RED");
            ConfigManager.save(fileName);
            assertTrue(file.exists(), "save() 應建立資料檔");

            assertTrue(file.delete(), "前置：應能刪除資料檔");
            ConfigManager.reload(fileName);
            assertFalse(ConfigManager.get(fileName).contains("1700000000000.color"),
                "檔案被外部刪除後 reload 應將記憶體同步為空");
        } finally {
            MockBukkit.unmock();
        }
    }
}

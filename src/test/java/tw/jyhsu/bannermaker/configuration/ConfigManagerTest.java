package tw.jyhsu.bannermaker.configuration;

import tw.jyhsu.bannermaker.BannerMaker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

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
}

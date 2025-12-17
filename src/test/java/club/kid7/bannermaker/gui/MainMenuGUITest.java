package club.kid7.bannermaker.gui;

import club.kid7.bannermaker.BannerMaker;
import club.kid7.bannermaker.configuration.ConfigManager;
import club.kid7.bannermaker.configuration.Language;
import club.kid7.bannermaker.service.MessageService;
import club.kid7.bannermaker.util.IOUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockito.MockedStatic;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class MainMenuGUITest {

    private ServerMock server;
    private BannerMaker plugin;
    private PlayerMock player;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        // Load the plugin to ensure ConfigManager and MessageService are initialized
        plugin = MockBukkit.load(BannerMaker.class);

        // Ensure language files are loaded for testing
        new Language(plugin).loadLanguage();

        player = server.addPlayer("TestPlayer");
    }

    @AfterEach
    void tearDown() {
        // Reset ConfigManager's static state to prevent cross-test pollution
        ConfigManager.reset();
        MockBukkit.unmock();
    }

    @Test
    void testShowMainMenuGUI() {
        // Mock JavaPlugin.getProvidingPlugin to return our mock plugin instance
        // This is necessary because InventoryFramework uses getProvidingPlugin which fails in test environment
        // Also Mock IOUtil to avoid file I/O errors
        try (MockedStatic<JavaPlugin> mockedJavaPlugin = mockStatic(JavaPlugin.class);
             MockedStatic<IOUtil> mockedIOUtil = mockStatic(IOUtil.class)) {

            mockedJavaPlugin.when(() -> JavaPlugin.getProvidingPlugin(any(Class.class)))
                .thenReturn(plugin);

            mockedIOUtil.when(() -> IOUtil.loadBannerList(player))
                .thenReturn(Collections.emptyList());

            // Given
            MessageService messageService = BannerMaker.getInstance().getMessageService();
            Component expectedTitleComponent = Language.tl("gui.prefix").append(Language.tl("gui.main-menu"));
            String expectedTitle = LegacyComponentSerializer.legacySection().serialize(expectedTitleComponent);

            // When
            MainMenuGUI.show(player);

            // Then
            assertNotNull(player.getOpenInventory(), "玩家應該開啟了一個物品欄");
            assertNotNull(player.getOpenInventory().getTopInventory(), "頂部物品欄不應該為 null");
            assertTrue(player.getOpenInventory().getTitle().contains(expectedTitle),
                "物品欄標題應該包含 MainMenuGUI 的標題: " + expectedTitle);
        }
    }
}

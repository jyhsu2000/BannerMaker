package tw.jyhsu.bannermaker.gui;

import tw.jyhsu.bannermaker.BannerMaker;
import tw.jyhsu.bannermaker.util.ItemBuilder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;

import static tw.jyhsu.bannermaker.configuration.Language.tl;

/**
 * GUI 共用 helper：ChestGui 樣板、工具列灰玻璃填充、安全 add（先 removeItem 再 addItem）、
 * 跨行 slot 計算等。
 * 主要服務於採「位置固定 + 灰玻璃填空」設計的 GUI。
 */
public class GuiUtil {

    /**
     * 追蹤本插件透過 {@link #createChestGui(String)} 建立的所有 ChestGui，用於 {@code /bm reload}
     * 時辨識「哪些開著的 inventory 是我們的、可以安全關閉」。
     * <p>
     * 使用 weak reference set：ChestGui 被 IF 釋放後自動 evict，不需要 listener 手動清，
     * 也不會誤傷其他同樣用 IF 的插件 ── 它們的 ChestGui 不會被加進這個 set。
     */
    private static final Set<ChestGui> openedGuis =
        Collections.newSetFromMap(new WeakHashMap<>());

    private GuiUtil() {
        // Utility class
    }

    /**
     * 建立一個 6 row 高的 ChestGui，標題為 {@code gui.title.prefix + tl(titleKey)}，
     * 並預設 onGlobalClick → cancel 防止物品被搬出。pane 的設定由 caller 自行決定
     * （多數 GUI 一張 9x6 StaticPane，MainMenuGUI 則用 PaginatedPane + 9x1 StaticPane）。
     * <p>
     * 同時把建出的 ChestGui 加入 {@link #openedGuis} 以便 reload 時識別並強制關閉。
     */
    static ChestGui createChestGui(String titleKey) {
        Component titleComponent = tl("gui.title.prefix").append(tl(titleKey));
        String title = LegacyComponentSerializer.legacySection().serialize(titleComponent);
        ChestGui gui = new ChestGui(6, title);
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        openedGuis.add(gui);
        return gui;
    }

    /**
     * 強制關閉所有目前開著的本插件 GUI，並通知玩家原因。
     * <p>
     * 用於 {@code /bm reload} 結尾，避免玩家在 reload 後的 GUI 內看到 stale 譯文 / 價格 /
     * 已關閉功能的按鈕。PlayerData 內的編輯狀態（{@code currentEditBanner} 等）不動，
     * 玩家重開 GUI 後從上次的狀態繼續。
     * <p>
     * 只關 {@link #openedGuis} 內紀錄過的 ChestGui，不會誤傷其他插件的 GUI 或 vanilla 工作台。
     */
    public static void closeAllOurGuis() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
            if (holder instanceof ChestGui chestGui && openedGuis.contains(chestGui)) {
                player.closeInventory();
                BannerMaker.getInstance().getMessageService()
                    .send(player, tl(NamedTextColor.YELLOW, "general.gui-closed-by-reload"));
            }
        }
    }

    /**
     * 操作列上「無動作」位置的視覺填充：純灰玻璃，displayName 設為單一空格以隱藏 vanilla
     * 預設名（跟合成表 brown glass 邊框同手法）。玩家 hover 看到空白 tooltip、無從推測
     * 該位置潛在有功能。
     */
    static GuiItem grayPaneFiller() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        return new GuiItem(pane);
    }

    /**
     * 將指定 ItemStack 與 click handler 放到 pane 的某個位置，先 remove 既有 item 再 add
     * 以確保覆蓋預先填好的灰玻璃或其他既有 item。
     */
    static void putAt(StaticPane pane, int x, int y, ItemStack item,
                      Consumer<InventoryClickEvent> handler) {
        pane.removeItem(x, y);
        pane.addItem(new GuiItem(item, handler::accept), x, y);
    }

    /**
     * 將 StaticPane 的指定列整列填上灰玻璃，作為「工具列」風格的視覺底盤。
     * 之後再用 {@link #putAt} 條件性覆蓋有功能的 slot。
     * 每個 slot 先 removeItem 再 addItem，可安全覆蓋既有內容。
     */
    static void fillToolbarRow(StaticPane pane, int y) {
        for (int x = 0; x < 9; x++) {
            pane.removeItem(x, y);
            pane.addItem(grayPaneFiller(), x, y);
        }
    }

    /**
     * 算 16 / 24 個 item 在 9 寬網格中跨 row 的 slot：每填滿 8 格就跳過 row 末尾那一格
     * （slot 8、17、26、35），讓兩側留出邊界對齊欄。
     * <p>
     * 例：{@code gridSlot(1, 0..15)} → 1, 2, ..., 8, 10, 11, ..., 17（16 格、跳過 slot 9）。
     *
     * @param startSlot 第一個 item 的 slot；應是某 row 起始 + 1（如 1、19）
     * @param index     從 0 起算的 item index
     */
    static int gridSlot(int startSlot, int index) {
        return startSlot + index + (index / 8);
    }
}

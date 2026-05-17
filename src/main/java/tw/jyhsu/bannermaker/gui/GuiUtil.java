package tw.jyhsu.bannermaker.gui;

import tw.jyhsu.bannermaker.util.ItemBuilder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * GUI 共用 helper：工具列灰玻璃填充、安全 add（先 removeItem 再 addItem）等。
 * 主要服務於採「位置固定 + 灰玻璃填空」設計的 GUI 工具列。
 */
class GuiUtil {

    private GuiUtil() {
        // Utility class
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
}

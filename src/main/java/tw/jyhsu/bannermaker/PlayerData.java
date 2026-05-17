package tw.jyhsu.bannermaker;

import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

/**
 * Per-player 的 GUI 暫時編輯狀態。玩家在 CreateBannerGUI、CreateAlphabetGUI 等選單內的選擇
 * （正在編輯哪支旗幟、目前選的染料色、是否切到「更多圖案」頁等）都暫存在這裡，並由
 * {@link PlayerDataMap} 透過 UUID 對應到玩家。
 * <p>
 * 兩組獨立的編輯狀態（一般旗幟 / 字母旗幟）共置於一個 class，互不影響；以下方註解段落區分。
 */
public class PlayerData {

    // === 一般旗幟編輯 ===

    private ItemStack currentEditBanner = null;
    private DyeColor selectedColor = DyeColor.BLACK;
    private boolean inSimplePreviewMode = false;
    private boolean showMorePatterns = false;

    // === 字母旗幟編輯 ===

    private AlphabetBanner currentAlphabetBanner = null;
    private boolean alphabetBannerBordered = true;

    /**
     * 回傳當前正在編輯的旗幟 ItemStack。
     * <p>
     * <strong>注意</strong>：回傳的是 live reference。GUI handler 直接對其 mutate
     * （例如 {@code setItemMeta(...)} 加 pattern）就能改動內部儲存的旗幟，不需再呼叫
     * {@link #setCurrentEditBanner(ItemStack)} 把同一個 reference 寫回。
     */
    public ItemStack getCurrentEditBanner() {
        return currentEditBanner;
    }

    public void setCurrentEditBanner(ItemStack currentEditBanner) {
        this.currentEditBanner = currentEditBanner;
    }

    public DyeColor getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(DyeColor selectedColor) {
        this.selectedColor = selectedColor;
    }

    public boolean isInSimplePreviewMode() {
        return inSimplePreviewMode;
    }

    public void setInSimplePreviewMode(boolean inSimplePreviewMode) {
        this.inSimplePreviewMode = inSimplePreviewMode;
    }

    public boolean isShowMorePatterns() {
        return showMorePatterns;
    }

    public void setShowMorePatterns(boolean showMorePatterns) {
        this.showMorePatterns = showMorePatterns;
    }

    /**
     * 回傳當前正在編輯的字母旗幟。
     * <p>
     * <strong>注意</strong>：回傳的是 live reference。對其 setter 的呼叫（如
     * {@code setBaseColor / setDyeColor / setBordered}）會直接改動內部儲存的物件，
     * 不需再呼叫 {@link #setCurrentAlphabetBanner(AlphabetBanner)} 把同一個 reference 寫回。
     */
    public AlphabetBanner getCurrentAlphabetBanner() {
        return currentAlphabetBanner;
    }

    public void setCurrentAlphabetBanner(AlphabetBanner currentAlphabetBanner) {
        this.currentAlphabetBanner = currentAlphabetBanner;
    }

    public boolean isAlphabetBannerBordered() {
        return alphabetBannerBordered;
    }

    public void setAlphabetBannerBordered(boolean alphabetBannerBordered) {
        this.alphabetBannerBordered = alphabetBannerBordered;
    }
}

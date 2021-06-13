package club.kid7.bannermaker;

import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

public class PlayerData {
    /**
     * 選單狀態
     */

    private Integer currentPage = 1;
    private ItemStack viewInfoBanner = null;
    private Integer currentRecipePage = 1;
    private AlphabetBanner currentAlphabetBanner = null;
    private boolean alphabetBannerBordered = true;
    private ItemStack currentEditBanner = null;
    private boolean showMorePatterns = false;
    private DyeColor selectedColor = DyeColor.BLACK;
    private boolean inSimplePreviewMode = false;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public ItemStack getViewInfoBanner() {
        return viewInfoBanner;
    }

    public void setViewInfoBanner(ItemStack viewInfoBanner) {
        this.viewInfoBanner = viewInfoBanner;
    }

    public Integer getCurrentRecipePage() {
        return currentRecipePage;
    }

    public void setCurrentRecipePage(Integer currentRecipePage) {
        this.currentRecipePage = currentRecipePage;
    }

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

    public ItemStack getCurrentEditBanner() {
        return currentEditBanner;
    }

    public void setCurrentEditBanner(ItemStack currentEditBanner) {
        this.currentEditBanner = currentEditBanner;
    }

    public boolean isShowMorePatterns() {
        return showMorePatterns;
    }

    public void setShowMorePatterns(boolean showMorePatterns) {
        this.showMorePatterns = showMorePatterns;
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
}

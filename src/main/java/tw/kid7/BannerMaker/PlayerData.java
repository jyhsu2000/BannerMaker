package tw.kid7.BannerMaker;

import com.google.common.collect.Maps;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tw.kid7.BannerMaker.inventoryMenu.AbstractInventoryMenu;
import tw.kid7.BannerMaker.util.AlphabetBanner;

import java.util.HashMap;

public class PlayerData {
    /**
     * 所有玩家資料實例
     */
    private static HashMap<String, PlayerData> playerDataMap = Maps.newHashMap();

    /**
     * 選單狀態
     */
    private InventoryMenuState inventoryMenuState = InventoryMenuState.MAIN_MENU;

    private Integer currentPage = 1;
    private ItemStack viewInfoBanner = null;
    private Integer currentRecipePage = 1;
    private AlphabetBanner currentAlphabetBanner = null;
    private boolean alphabetBannerBordered = true;
    private ItemStack currentEditBanner = null;
    private boolean showMorePatterns = false;
    private DyeColor selectedColor = DyeColor.BLACK;

    /**
     * 取得玩家資料實例
     *
     * @param player 玩家
     * @return 玩家資料
     */
    public static PlayerData get(Player player) {
        String uuidString = player.getUniqueId().toString();
        PlayerData playerData = playerDataMap.get(uuidString);
        if (playerData == null) {
            playerData = new PlayerData();
            playerDataMap.put(uuidString, playerData);
        }
        return playerData;
    }

    public InventoryMenuState getInventoryMenuState() {
        return inventoryMenuState;
    }

    public void setInventoryMenuState(InventoryMenuState inventoryMenuState) {
        this.inventoryMenuState = inventoryMenuState;
    }

    /**
     * 取得選單狀態對應的GUI選單介面
     *
     * @return GUI選單介面
     */
    public AbstractInventoryMenu getInventoryMenu() {
        return inventoryMenuState.getInventoryMenu();
    }

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
}

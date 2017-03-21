package tw.kid7.BannerMaker;

import tw.kid7.BannerMaker.inventoryMenu.*;

public enum InventoryMenuState {
    MAIN_MENU,
    CREATE_BANNER,
    CREATE_ALPHABET,
    BANNER_INFO;

    /**
     * 取得此狀態的GUI選單介面
     *
     * @return GUI選單介面
     */
    public AbstractInventoryMenu getInventoryMenu() {
        AbstractInventoryMenu menu;
        switch (this) {
            case CREATE_BANNER:
                menu = CreateBannerInventoryMenu.getInstance();
                break;
            case CREATE_ALPHABET:
                menu = CreateAlphabetInventoryMenu.getInstance();
                break;
            case BANNER_INFO:
                menu = BannerInfoInventoryMenu.getInstance();
                break;
            case MAIN_MENU:
            default:
                menu = MainInventoryMenu.getInstance();
        }
        return menu;
    }
}

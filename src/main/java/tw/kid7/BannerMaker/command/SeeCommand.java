package tw.kid7.BannerMaker.command;

import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import tw.kid7.BannerMaker.InventoryMenuState;
import tw.kid7.BannerMaker.PlayerData;
import tw.kid7.BannerMaker.util.DyeColorUtil;
import tw.kid7.BannerMaker.util.InventoryMenuUtil;
import tw.kid7.BannerMaker.util.MessageUtil;

import java.util.Set;

class SeeCommand extends AbstractCommand {
    //名稱
    private static final String name = "See";
    //介紹
    private static final String description = "Show banner info of the banner you're looking at";
    //權限
    private static final String permission = "BannerMaker.see";
    //使用方法
    private static final String usage = "/bm see";
    //僅能由玩家執行
    private static final boolean onlyFromPlayer = true;

    SeeCommand() {
        super(name, description, permission, usage, onlyFromPlayer);
    }

    @Override
    boolean handle(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Block block = player.getTargetBlock((Set<Material>) null, 20);
        if (block.getType() != Material.STANDING_BANNER && block.getType() != Material.WALL_BANNER) {
            //TODO: 加到語言包
            player.sendMessage(MessageUtil.format(true, "&cItem you're looking at is not a banner."));
            return true;
        }
        //根據方塊建立旗幟
        Banner blockState = (Banner) block.getState();
        ItemStack itemStack = new ItemStack(Material.BANNER, 1, DyeColorUtil.toShort(blockState.getBaseColor()));
        BannerMeta bannerMeta = (BannerMeta) itemStack.getItemMeta();
        bannerMeta.setPatterns(blockState.getPatterns());
        itemStack.setItemMeta(bannerMeta);

        PlayerData playerData = PlayerData.get(player);
        //設定查看旗幟
        playerData.setViewInfoBanner(itemStack);
        //重置頁數
        playerData.setCurrentRecipePage(1);
        //設定畫面
        playerData.setInventoryMenuState(InventoryMenuState.BANNER_INFO);
        //開啟選單
        InventoryMenuUtil.openMenu(player);

        return true;
    }
}

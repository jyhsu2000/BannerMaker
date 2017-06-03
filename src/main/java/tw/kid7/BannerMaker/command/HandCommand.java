package tw.kid7.BannerMaker.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.util.BannerUtil;
import tw.kid7.BannerMaker.util.InventoryMenuUtil;
import tw.kid7.BannerMaker.util.MessageUtil;

import static tw.kid7.BannerMaker.configuration.Language.tl;

class HandCommand extends AbstractCommand {
    //名稱
    private static final String name = "Hand";
    //介紹
    private static final String description = "Show banner info of the banner in hand";
    //權限
    private static final String permission = "BannerMaker.hand";
    //使用方法
    private static final String usage = "/bm hand";
    //僅能由玩家執行
    private static final boolean onlyFromPlayer = true;

    HandCommand() {
        super(name, description, permission, usage, onlyFromPlayer);
    }

    @Override
    boolean handle(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        ItemStack itemStack = BannerMaker.getInstance().getVersionHandler().getItemInMainHand(player);
        if (!BannerUtil.isBanner(itemStack)) {
            player.sendMessage(MessageUtil.format(true, "&c" + tl("command.not-banner-hand")));
            return true;
        }
        //複製旗幟，僅保留底色與樣式
        BannerMeta originalBannerMeta = (BannerMeta) itemStack.getItemMeta();
        ItemStack banner = new ItemStack(Material.BANNER, 1, itemStack.getDurability());
        BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();
        bannerMeta.setPatterns(originalBannerMeta.getPatterns());
        banner.setItemMeta(bannerMeta);
        //顯示旗幟
        InventoryMenuUtil.showBannerInfo(player, banner);
        return true;
    }
}

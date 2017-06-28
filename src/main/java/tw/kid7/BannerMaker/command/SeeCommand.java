package tw.kid7.BannerMaker.command;

import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.util.DyeColorUtil;
import tw.kid7.BannerMaker.util.InventoryMenuUtil;
import tw.kid7.BannerMaker.util.MessageUtil;

import java.util.Set;

import static tw.kid7.BannerMaker.configuration.Language.tl;

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

    SeeCommand(BannerMaker bannerMaker) {
        super(bannerMaker, name, description, permission, usage, onlyFromPlayer);
    }

    @Override
    boolean handle(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Block block = player.getTargetBlock((Set<Material>) null, 20);
        if (block.getType() != Material.STANDING_BANNER && block.getType() != Material.WALL_BANNER) {
            player.sendMessage(MessageUtil.format(true, "&c" + tl("command.not-banner-see")));
            return true;
        }
        //根據方塊建立旗幟
        Banner blockState = (Banner) block.getState();
        ItemStack banner = new ItemStack(Material.BANNER, 1, DyeColorUtil.toShort(blockState.getBaseColor()));
        BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();
        bannerMeta.setPatterns(blockState.getPatterns());
        banner.setItemMeta(bannerMeta);
        //顯示旗幟
        InventoryMenuUtil.showBannerInfo(player, banner);
        return true;
    }
}

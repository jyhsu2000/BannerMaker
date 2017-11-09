package tw.kid7.BannerMaker.command;

import club.kid7.pluginutilities.kitemstack.KItemStack;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tw.kid7.BannerMaker.util.DyeColorUtil;
import tw.kid7.BannerMaker.util.InventoryMenuUtil;
import tw.kid7.BannerMaker.util.MessageUtil;

import java.util.Set;

import static tw.kid7.BannerMaker.configuration.Language.tl;

public class SeeCommand extends CommandComponent {
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

    public SeeCommand() {
        super(name, description, permission, usage, onlyFromPlayer);
    }

    @Override
    public boolean executeCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Block block = player.getTargetBlock((Set<Material>) null, 20);
        if (block.getType() != Material.STANDING_BANNER && block.getType() != Material.WALL_BANNER) {
            player.sendMessage(MessageUtil.format(true, "&c" + tl("command.not-banner-see")));
            return true;
        }
        //根據方塊建立旗幟
        Banner blockState = (Banner) block.getState();
        KItemStack banner = new KItemStack(Material.BANNER)
            .durability(DyeColorUtil.toShort(blockState.getBaseColor()))
            .setPatterns(blockState.getPatterns());
        //顯示旗幟
        InventoryMenuUtil.showBannerInfo(player, banner);
        return true;
    }
}

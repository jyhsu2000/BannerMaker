package tw.kid7.BannerMaker.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tw.kid7.BannerMaker.configuration.Language;
import tw.kid7.BannerMaker.util.InventoryMenuUtil;
import tw.kid7.BannerMaker.util.MessageUtil;

public class BannerMakerCommand extends AbstractCommand {
    //指令名稱
    private static final String NAME = "BannerMaker";
    //指令說明
    private static final String DESCRIPTION = "Show menu of BannerMaker";
    //指令權限
    private static final String PERMISSION = "";
    //指令用途
    private static final String USAGE = "/BannerMaker";
    //子指令權限
    private static final String[] SUB_PERMISSIONS = {""};

    /**
     * Construct out object.
     *
     * @param sender the command sender
     */
    public BannerMakerCommand(CommandSender sender) {
        super(sender, NAME, DESCRIPTION, PERMISSION, SUB_PERMISSIONS, USAGE);
    }

    /**
     * Execute the command.
     *
     * @param sender  the sender of the command
     * @param command the command being done
     * @param label   the name of the command
     * @param args    the arguments supplied
     */
    @Override
    public void execute(CommandSender sender, Command command, String label, String[] args) {
        //只能由玩家使用
        if (!isSenderPlayer()) {
            sender.sendMessage(MessageUtil.format(true, "&c" + Language.get("command.player-only")));
            return;
        }
        Player player = (Player) sender;
        //開啟選單
        InventoryMenuUtil.openMenu(player);
    }
}

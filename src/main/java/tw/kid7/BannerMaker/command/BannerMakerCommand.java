package tw.kid7.BannerMaker.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import tw.kid7.BannerMaker.util.MessageUtil;

public class BannerMakerCommand extends AbstractCommand {
    //指令名稱
    public static final String NAME = "BannerMaker";
    //指令說明
    public static final String DESCRIPTION = "Show menu of BannerMaker";
    //指令權限
    public static final String PERMISSION = "";
    //指令用途
    public static final String USAGE = "/BannerMaker";
    //子指令權限
    public static final String[] SUB_PERMISSIONS = {""};

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
        if (!isSenderPlayer()) {
            sender.sendMessage(MessageUtil.format(true, "&cThis command can only be used by players in game"));
            return;
        }
    }
}

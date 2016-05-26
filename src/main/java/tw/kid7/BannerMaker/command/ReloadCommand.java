package tw.kid7.BannerMaker.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.configuration.Language;
import tw.kid7.BannerMaker.util.MessageUtil;

public class ReloadCommand extends AbstractCommand {
    //指令名稱
    private static final String NAME = "Reload";
    //指令說明
    private static final String DESCRIPTION = "Reload all config";
    //指令權限
    private static final String PERMISSION = "BannerMaker.reload";
    //指令用途
    private static final String USAGE = "/bm reload";
    //子指令權限
    private static final String[] SUB_PERMISSIONS = {""};

    /**
     * Construct out object.
     *
     * @param sender the command sender
     */
    public ReloadCommand(CommandSender sender) {
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
        if (!hasPermission()) {
            sender.sendMessage(MessageUtil.format(true, Language.get("general.no-permission")));
            return;
        }
        BannerMaker.reload();
        sender.sendMessage(MessageUtil.format(true, Language.get("general.reload")));
    }
}

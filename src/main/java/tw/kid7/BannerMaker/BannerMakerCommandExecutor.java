package tw.kid7.BannerMaker;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import tw.kid7.BannerMaker.command.BannerMakerCommand;
import tw.kid7.BannerMaker.command.AbstractCommand;
import tw.kid7.BannerMaker.command.ReloadCommand;

class BannerMakerCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        AbstractCommand cmd = new BannerMakerCommand(sender);

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "reload":
                    cmd = new ReloadCommand(sender);
                    break;
            }
        }

        cmd.execute(sender, command, label, args);
        return true;
    }
}

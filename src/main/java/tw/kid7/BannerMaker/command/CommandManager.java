package tw.kid7.BannerMaker.command;

import com.google.common.collect.Maps;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager implements CommandExecutor, TabCompleter {
    private static CommandManager instance;
    final HashMap<String, AbstractCommand> subCommandMap = Maps.newHashMap();

    private CommandManager() {
        addSubCommand("help", new HelpCommand());
        addSubCommand("reload", new ReloadCommand());
    }

    public static CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        AbstractCommand defaultCommand = new BannerMakerCommand();
        //子指令
        if (args.length > 0) {
            String subCommandLabel = args[0].toLowerCase();
            AbstractCommand subCommand = subCommandMap.get(subCommandLabel);
            if (subCommand == null) {
                defaultCommand.sendParameterWarning(sender);
                return true;
            }
            return subCommand.execute(sender, command, label, args);
        }
        //預設指令
        defaultCommand.execute(sender, command, label, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            //取得部分指令
            String partialCommand = args[0];
            //根據權限產生指令清單
            List<String> commands = new ArrayList<>();
            for (Map.Entry<String, AbstractCommand> subCommandEntry : subCommandMap.entrySet()) {
                if (subCommandEntry.getValue().hasPermission(sender)) {
                    commands.add(subCommandEntry.getKey());
                }
            }
            //匹配部分指令
            StringUtil.copyPartialMatches(partialCommand, commands, completions);
        }
        return completions;
    }

    private void addSubCommand(String label, AbstractCommand command) {
        subCommandMap.put(label, command);
    }
}

package tw.kid7.BannerMaker.cmd;

import com.sk89q.intake.CommandException;
import com.sk89q.intake.Intake;
import com.sk89q.intake.InvalidUsageException;
import com.sk89q.intake.InvocationCommandException;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.dispatcher.NoSubcommandsException;
import com.sk89q.intake.dispatcher.SubcommandRequiredException;
import com.sk89q.intake.fluent.CommandGraph;
import com.sk89q.intake.parametric.Injector;
import com.sk89q.intake.parametric.ParametricBuilder;
import com.sk89q.intake.util.auth.AuthorizationException;
import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import tw.kid7.BannerMaker.BannerMaker;
import tw.kid7.BannerMaker.cmd.module.CommandSenderModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {
    private final Dispatcher dispatcher;
    private BannerMaker bm;

    public CommandHandler(BannerMaker bm) {
        this.bm = bm;
        Injector injector = Intake.createInjector();
        injector.install(new CommandSenderModule());
        ParametricBuilder builder = new ParametricBuilder(injector);
        builder.setAuthorizer(new PermissionAuthorizer());
        dispatcher = new CommandGraph()
            .builder(builder)
            .commands()
            .group("bannermaker", "bm").registerMethods(new BannerMakerCommands(bm)).parent()
            .graph()
            .getDispatcher();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // create the command string
        StrBuilder builder = new StrBuilder();
        builder.append(label);
        for (String argument : args) {
            builder.appendSeparator(' ');
            builder.append(argument);
        }
        this.callCommand(builder.toString(), sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // create the command string
        StrBuilder builder = new StrBuilder();
        builder.append(alias);
        for (String argument : args) {
            builder.appendSeparator(' ');
            builder.append(argument);
        }
        return getSuggestions(builder.toString(), sender);
    }

    private List<String> getSuggestions(String arguments, CommandSender sender) {
        try {
            return dispatcher.getSuggestions(arguments, createNamespace(sender));
        } catch (CommandException e) {
            sender.sendMessage(e.getLocalizedMessage());
        }
        return Collections.emptyList();
    }

    private Namespace createNamespace(CommandSender forWhom) {
        Namespace namespace = new Namespace();
        namespace.put(CommandSender.class, forWhom);
        return namespace;
    }

    private void callCommand(String command, CommandSender sender) {
        //call the command
        try {
            dispatcher.call(command, createNamespace(sender), new ArrayList<String>());
            //handle errors
            //TODO: 完善錯誤訊息
        } catch (InvocationCommandException e) {
            sender.sendMessage("InvocationCommandException");
            sender.sendMessage(ChatColor.RED + e.getLocalizedMessage());
        } catch (SubcommandRequiredException e) {
            sender.sendMessage("SubcommandRequiredException");
            sender.sendMessage(ChatColor.RED + e.getLocalizedMessage());
        } catch (NoSubcommandsException e) {
            sender.sendMessage("NoSubcommandsException");
            sender.sendMessage(ChatColor.RED + e.getLocalizedMessage());
        } catch (InvalidUsageException e) {
            sender.sendMessage("InvalidUsageException");
            sender.sendMessage(ChatColor.RED + e.getLocalizedMessage());
        } catch (CommandException e) {
            sender.sendMessage("CommandException");
            sender.sendMessage(ChatColor.RED + e.getLocalizedMessage());
        } catch (AuthorizationException e) {
            sender.sendMessage("AuthorizationException");
            //FIXME: 顯示具體缺少的權限或原因
            sender.sendMessage(ChatColor.RED + e.getLocalizedMessage());
        }
    }
}

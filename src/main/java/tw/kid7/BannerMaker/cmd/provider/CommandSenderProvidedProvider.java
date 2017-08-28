package tw.kid7.BannerMaker.cmd.provider;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.ProvisionException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

public class CommandSenderProvidedProvider implements Provider<CommandSender> {

    public CommandSenderProvidedProvider() {
    }

    @Override
    public boolean isProvided() {
        return true;
    }

    @Nullable
    @Override
    public CommandSender get(CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException {
        return arguments.getNamespace().get(CommandSender.class);
    }

    @Override
    public List<String> getSuggestions(String prefix, Namespace locals) {
        List<String> suggestions = Lists.newArrayList();
        suggestions.add("console");
        for (Player p : Bukkit.getOnlinePlayers()) {
            suggestions.add(p.getName());
        }
        return ImmutableList.copyOf(suggestions);
    }
}

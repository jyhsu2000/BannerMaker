package tw.kid7.BannerMaker.cmd.module;

import com.sk89q.intake.parametric.Module;
import com.sk89q.intake.parametric.binder.Binder;
import org.bukkit.command.CommandSender;
import tw.kid7.BannerMaker.cmd.annotation.Sender;
import tw.kid7.BannerMaker.cmd.provider.CommandSenderProvidedProvider;
import tw.kid7.BannerMaker.cmd.provider.CommandSenderProvider;

public class CommandSenderModule implements Module {
    @Override
    public void configure(Binder binder) {
        // 參考 https://github.com/EngineHub/Intake/issues/7
        binder.bind(CommandSender.class).annotatedWith(Sender.class).toProvider(new CommandSenderProvidedProvider());
        binder.bind(CommandSender.class).toProvider(new CommandSenderProvider());
    }
}

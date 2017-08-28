package tw.kid7.BannerMaker.cmd;

import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.util.auth.Authorizer;
import org.bukkit.command.CommandSender;

import static com.google.common.base.Preconditions.checkNotNull;

public class PermissionAuthorizer implements Authorizer {
    @Override
    public boolean testPermission(Namespace namespace, String permission) {
        return checkNotNull(namespace.get(CommandSender.class), "Current user not available")
            .hasPermission(permission);
    }
}

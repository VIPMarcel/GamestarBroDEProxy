package vip.marcel.gamestarbro.proxy.utils.managers;

import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import vip.marcel.gamestarbro.proxy.Proxy;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record PermissionsManager(Proxy plugin) {

    public User getUser(UUID uuid) {
        return this.plugin.getLuckPerms().getUserManager().getUser(uuid);
    }

    public User getUser(ProxiedPlayer player) {
        return this.plugin.getLuckPerms().getPlayerAdapter(ProxiedPlayer.class).getUser(player);
    }

    public User getUserAsync(UUID uuid) {
        return this.plugin.getLuckPerms().getUserManager().loadUser(uuid).join();
    }

    public CompletableFuture<Boolean> hasPermissionAsync(UUID uuid, String permission) {
        return this.plugin.getLuckPerms().getUserManager().loadUser(uuid)
                .thenApplyAsync(user -> user.getCachedData().getPermissionData().checkPermission(permission).asBoolean());
    }

    public boolean hasPermission(UUID uuid, String permission) {
        return this.plugin.getLuckPerms().getUserManager().loadUser(uuid).join().getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

}

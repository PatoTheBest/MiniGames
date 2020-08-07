package me.patothebest.gamecore.permission;

import com.google.inject.Singleton;
import me.patothebest.gamecore.modules.Module;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class PermissionGroupManager implements Module {

    private final Map<String, PermissionGroup> permissionGroups;
    private final PermissionGroup defaultPermissionGroup;

    public PermissionGroupManager() {
        this.permissionGroups = new HashMap<>();
        this.defaultPermissionGroup = createGroup("default");
    }

    public PermissionGroup createGroup(String name) {
        if(getPermissionGroup(name) != null) {
            return null;
        }

        PermissionGroup permissionGroup = new PermissionGroup(name);
        permissionGroups.put(name, permissionGroup);
        return permissionGroup;
    }

    public PermissionGroup getPermissionGroup(String name) {
        return permissionGroups.get(name);
    }

    public PermissionGroup getOrCreatePermissionGroup(String name) {
        if(getPermissionGroup(name) != null) {
           return permissionGroups.get(name);
        }

        return createGroup(name);
    }

    public PermissionGroup getDefaultPermissionGroup() {
        return defaultPermissionGroup;
    }

    public Map<String, PermissionGroup> getPermissionGroups() {
        return permissionGroups;
    }
}

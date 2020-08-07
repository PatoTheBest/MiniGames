package me.patothebest.thetowers.file;

import me.patothebest.gamecore.file.FlatFile;
import me.patothebest.thetowers.TheTowersRemastered;
import org.bukkit.ChatColor;

import java.io.IOException;

public class PermissionGroupsFile extends FlatFile {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    private final TheTowersRemastered plugin;

    // -------------------------------------------- //
    // CONSTRUCTOR
    // -------------------------------------------- //

    PermissionGroupsFile(TheTowersRemastered plugin) {
        super("permission-groups");
        this.header = "Permission Groups File";
        this.plugin = plugin;

        load();
    }

    // -------------------------------------------- //
    // CLASS METHODS
    // -------------------------------------------- //

    public void loadGroups() {
        plugin.log(ChatColor.YELLOW + "Loading permission groups...");

        if(get("groups") == null) {
            return;
        }

        try {
            // get the list of groups as a string list and
            // create the groups
            getStringList("groups").forEach(s -> plugin.getPermissionGroupManager().createGroup(s));
            plugin.log("Loaded " + plugin.getPermissionGroupManager().getPermissionGroups().size() + " group(s)");
        } catch(Throwable t) {
            plugin.log(ChatColor.RED + "Could not load permission groups");
            t.printStackTrace();
        }
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public void save() {
        set("groups", plugin.getPermissionGroupManager().getPermissionGroups().keySet().toArray());

        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

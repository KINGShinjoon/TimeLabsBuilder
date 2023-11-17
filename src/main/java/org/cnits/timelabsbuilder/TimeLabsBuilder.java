package org.cnits.timelabsbuilder;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.cnits.timelabsbuilder.Command.TimeLabsCommand;
import org.cnits.timelabsbuilder.API.StartBuilding;
public final class TimeLabsBuilder extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("TimeLabsBuilder has been enabled");
        getLogger().info("Creator : SJ (sj@cnitsstudio.com)");

        if(checkDependencies()){
            StartBuilding.setPlugin(this);
            this.getCommand("timelabsbuilder").setExecutor(new TimeLabsCommand());
        }else{
            getLogger().severe("Dependencies not found, disabling this plugin");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("TimeLabsBuilder has been disabled");
    }

    private boolean checkDependencies(){
        PluginManager pluginManager = getServer().getPluginManager();
        Plugin citizens = pluginManager.getPlugin("Citizens");
        Plugin worldEdit = pluginManager.getPlugin("WorldEdit");

        if (citizens == null || worldEdit == null){
            getLogger().severe("Cound not find required plugins");
            return false;
        }
        getLogger().info ("Found required plugins");
        return true;
    }
}

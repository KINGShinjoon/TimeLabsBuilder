package org.cnits.timelabsbuilder;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.cnits.timelabsbuilder.API.BreakBuilding;
import org.cnits.timelabsbuilder.API.GetList;
import org.cnits.timelabsbuilder.API.LoadSchematic;
import org.cnits.timelabsbuilder.Command.TimeLabsCommand;
import org.cnits.timelabsbuilder.API.StartBuilding;

import java.io.File;

public final class TimeLabsBuilder extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("TimeLabsBuilder has been enabled");
        getLogger().info("Creator : SJ (sj@cnitsstudio.com)");
        getLogger().info("ver : 0.2.0");
        if(checkDependencies()){
            StartBuilding.setPlugin(this);
            GetList.setPlugin(this);
            LoadSchematic.setPlugin(this);
            BreakBuilding.setPlugin(this);
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

    public File getWorldEditSchematicsFolder() {
        Plugin worldEditPlugin = getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEditPlugin == null) {
            // 월드에딧 플러그인이 설치되어 있지 않은 경우
            return null;
        }

        // 월드에딧 플러그인의 데이터 폴더 경로를 얻습니다.
        File worldEditDataFolder = worldEditPlugin.getDataFolder();
        // 'schematics' 폴더 경로를 생성합니다.
        return new File(worldEditDataFolder, "schematics");
    }
}

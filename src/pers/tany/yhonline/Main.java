package pers.tany.yhonline;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pers.tany.yhonline.command.Commands;
import pers.tany.yhonline.listenevent.Events;
import pers.tany.yukinoaapi.interfacepart.configuration.IConfig;
import pers.tany.yukinoaapi.interfacepart.other.ITime;
import pers.tany.yukinoaapi.interfacepart.register.IRegister;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin {
    public static Plugin plugin;
    public static YamlConfiguration config;
    public static YamlConfiguration data;

    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getConsoleSender().sendMessage("§7[§fOnlineReward§7]§a插件已加载");

        IConfig.createResource(this, "", "config.yml", false);
        IConfig.createResource(this, "", "data.yml", false);

        config = IConfig.loadConfig(this, "", "config");
        data = IConfig.loadConfig(this, "", "data");

        IRegister.registerCommands(this, "yhonline", new Commands());
        IRegister.registerEvents(this, new Events());

        new BukkitRunnable() {

            @Override
            public void run() {
                for (String playerNumber : Main.config.getConfigurationSection("PlayerOnlineNumber").getKeys(false)) {
                    if (Bukkit.getOnlinePlayers().size() >= Integer.parseInt(playerNumber) && !Main.config.getStringList("Number").contains(playerNumber)) {
                        for (String command : Main.config.getStringList("PlayerOnlineNumber." + playerNumber)) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                        }
                        List<String> list = Main.data.getStringList("Number");
                        list.add(playerNumber);
                        Main.data.set("Number", list);
                        IConfig.saveConfig(Main.plugin, Main.data, "", "data");
                    }
                }
                for (String playerNumber : Main.config.getConfigurationSection("ForeverPlayerOnlineNumber").getKeys(false)) {
                    if (Bukkit.getOnlinePlayers().size() >= Integer.parseInt(playerNumber) && !Main.config.getStringList("ForeverNumber").contains(playerNumber)) {
                        for (String command : Main.config.getStringList("ForeverPlayerOnlineNumber." + playerNumber)) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                        }
                        List<String> list = Main.data.getStringList("ForeverNumber");
                        list.add(playerNumber);
                        Main.data.set("ForeverNumber", list);
                        IConfig.saveConfig(Main.plugin, Main.data, "", "data");
                    }
                }
            }

        }.runTaskTimer(Main.plugin, 100, 100);
        new BukkitRunnable() {

            @Override
            public void run() {
                if (Integer.parseInt(ITime.getNowString().split(":")[3]) == Main.config.getInt("Hour") && Integer.parseInt(ITime.getNowString().split(":")[4]) == Main.config.getInt("Minute")) {
                    Main.data.set("Number", new ArrayList<>());
                    IConfig.saveConfig(Main.plugin, Main.data, "", "data");
                }
            }

        }.runTaskTimer(Main.plugin, 1200, 1200);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§7[§fOnlineReward§7]§c插件已卸载");
    }
}

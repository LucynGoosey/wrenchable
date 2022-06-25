package io.github.rypofalem.wrenchable;

import org.bukkit.Material;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Rotatable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class WrenchableCommand implements CommandExecutor {

    private final Wrenchable plugin;

    public WrenchableCommand(Wrenchable plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) return false;
        switch (args[0]){

            // dump a list of wrenchable blocks
            case "dump":
                List<String> names = validBlocks().map(Enum::name).toList();

                File dumpFile = new File(plugin.getDataFolder(), "dump.yml");
                if (!dumpFile.exists()) {
                    dumpFile.getParentFile().mkdirs();
                    plugin.saveResource("dump.yml", true);
                }

                FileConfiguration dump = new YamlConfiguration();
                try {
                    dump.load(dumpFile);
                } catch (IOException | InvalidConfigurationException e) {
                    e.printStackTrace();
                    return true;
                }

                dump.set("dump", names);
                try {
                    dump.save(dumpFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            default: return false;
        }
    }

    private Stream<Material> validBlocks(){
        return Arrays.stream(Material.values()).filter(
                m -> !m.name().contains("LEGACY") && m.isBlock()
                && (m.createBlockData() instanceof Directional || m.createBlockData() instanceof Orientable || m.createBlockData() instanceof Rotatable)
        );
    }
}

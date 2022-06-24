package io.github.rypofalem.wrenchable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
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


            // create a platform of wrenchable blocks
            case "debugplatform":
                if(!(sender instanceof Player)){
                    sender.sendMessage("Only players can execute this command.");
                    return true;
                }
                if(args.length < 2 || !args[1].equals("isweariamnotatotaldumbass")) {
                    sender.sendMessage("This command is destructive. It will replace hundreds of blocks.)");
                    sendMessageDelay(sender, "It may even place invalid/corrupted blocks.", 3);
                    sendMessageDelay(sender, "Do NOT use this on server that is not disposable.", 6);
                    sendMessageDelay(sender, "Type \"/wrenchable debugplatform isweariamnotatotaldumbass\" to confirm", 9);
                    return true;
                }
                Location feet = ((Player)sender).getLocation();
                Iterator<Material> blocks = validBlocks().iterator();
                for(int x = feet.getBlockX()-50; x < feet.getBlockX() + 50; x++){
                    for(int z = feet.getBlockZ()-50; z < feet.getBlockZ() + 50; z++){
                        feet.getWorld().getBlockAt(x, feet.getBlockY(), z).setType(Material.STONE);
                        for(int yOffset = 0; yOffset < 5; yOffset++){
                            feet.getWorld().getBlockAt(x, 1 + yOffset + feet.getBlockY(), z).setType(Material.AIR);
                        }
                        if((x%5) == 0 && z % 5 == 0 && blocks.hasNext())
                            feet.getWorld().getBlockAt(x, 1 + feet.getBlockY(), z).setType(blocks.next());

                    }
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

    private void sendMessageDelay(CommandSender reciever, String message, long seconds){
        Bukkit.getScheduler().runTaskLater(plugin, () -> reciever.sendMessage(message), seconds * 20);
    }
}

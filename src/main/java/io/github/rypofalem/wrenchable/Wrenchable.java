package io.github.rypofalem.wrenchable;

import io.github.rypofalem.wrenchable.cyclable.CyclableDirectional;
import io.github.rypofalem.wrenchable.cyclable.CyclableOrientable;
import io.github.rypofalem.wrenchable.cyclable.CyclableRotatable;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Piston;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Wrenchable extends JavaPlugin implements Listener {
    private final Set<Material> whitelist = new HashSet<>();
    private final NamespacedKey wrenchKey = new NamespacedKey(this, "wrench");

    @Override
    public void onEnable() {
        // set wrench properties
        final ItemStack wrench = new ItemStack(Material.CARROT_ON_A_STICK);
        if (wrench.getItemMeta() == null || !(wrench.getItemMeta() instanceof Damageable damageableMeta)) {
            // normally unreachable, shut down the plugin if the api changed so significantly that the wrench can't
            // be setup properly
            getLogger().warning("Something fundamental about the wrench item seems to have changed and the Wrenchable plugin will not function correctly. Disabling Wrenchable.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        damageableMeta.setDisplayName("Wrench");
        damageableMeta.setDamage(1);
        damageableMeta.setUnbreakable(true);
        wrench.setItemMeta(damageableMeta);

        // setup crafting recipe
        final ShapedRecipe wrenchRecipe = new ShapedRecipe(wrenchKey, wrench);
        wrenchRecipe.shape(" G ", " GG", "I  ");
        wrenchRecipe.setIngredient('G', Material.GOLD_INGOT);
        wrenchRecipe.setIngredient('I', Material.IRON_INGOT);

        // register our stuff
        getServer().getPluginManager().registerEvents(this, this);
        getServer().addRecipe(wrenchRecipe);
        PluginCommand cmd = getCommand("wrenchable");
        if (cmd != null) cmd.setExecutor(new WrenchableCommand(this));
        else getLogger().warning("The command /wrenchable doesn't seem to be registered properly and won't work.");

        // load whitelist
        whitelist.clear();
        saveDefaultConfig();
        List<String> materialStrings = getConfig().getStringList("whitelist");
        for (String str : materialStrings) {
            Material mat = Material.matchMaterial(str);
            if (mat == null)
                getLogger().warning("could not add '%s' to whitelist because it doesn't match a known material".formatted(str));
            else if (!(mat.createBlockData() instanceof Directional || mat.createBlockData() instanceof Rotatable || mat.createBlockData() instanceof Orientable))
                getLogger().warning(
                        "Whitelisted material '%s' doesn't implement Directional, Rotatable or Orientable so cannot be wrenched".formatted(str));
            else whitelist.add(mat);
        }
    }

    public boolean isWrench(ItemStack item) {
        return item != null &&
                item.getType() == Material.CARROT_ON_A_STICK &&
                item.getItemMeta() != null &&
                item.getItemMeta().isUnbreakable() &&
                item.getItemMeta() instanceof Damageable &&
                ((Damageable) item.getItemMeta()).getDamage() == 1;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e) {
        // reasons to ignore the event
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK ||
                e.getClickedBlock() == null ||
                e.getHand() != EquipmentSlot.HAND ||
                !isWrench(e.getItem()) ||
                !whitelist.contains(e.getClickedBlock().getType())
        ) return;
        // don't mess with double chest
        if (e.getClickedBlock().getState() instanceof Chest &&
                ((Chest) e.getClickedBlock().getState()).getInventory().getHolder() instanceof DoubleChest) return;
        // don't mess with extended pistons
        if (e.getClickedBlock().getBlockData() instanceof Piston &&
                ((Piston) e.getClickedBlock().getBlockData()).isExtended()) return;


        // cycle block data if it implements the correct interfaces
        BlockData blockData = e.getClickedBlock().getBlockData();
        if (blockData instanceof Directional directional) new CyclableDirectional(directional).cycle();
        else if (blockData instanceof Rotatable rotatable) new CyclableRotatable(rotatable).cycle();
        else if (blockData instanceof Orientable orientable) new CyclableOrientable(orientable).cycle();
        else {
            // normally should be unreachable because we already check if whitelisted blocks implement the interface
            // before they are added to the whitelist.  Including this just in case.
            getLogger().warning("Whitelisted material '%s' doesn't implement Directional, Rotatable or Orientable so cannot be wrenched".formatted(blockData.getMaterial().toString()));
            return;
        }

        // apply the operation to the world
        e.getClickedBlock().setBlockData(blockData);
        e.setUseInteractedBlock(Event.Result.DENY); // Don't interact with the block
        e.setCancelled(true);
        e.getClickedBlock().getWorld().playSound(e.getClickedBlock().getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.BLOCKS, 1, .5f);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.getPlayer().discoverRecipe(wrenchKey);
    }
}

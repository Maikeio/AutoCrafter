package net.ddns.maikeio.autocrafter;

import org.bukkit.Material;
import org.bukkit.block.Dropper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;

public class AutoCraftListener implements Listener {

	AutoCraftHandler handler;

	public AutoCraftListener() {
		handler = new AutoCraftHandler();
	}

	@EventHandler
	public void PluginDisableEvent(org.bukkit.event.server.PluginDisableEvent event) {
		// saves all AutoCrafters to File
		handler.check();
		handler.save();
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void InventoryEvent(InventoryClickEvent event) {

		// Inventory of deInvstination
		Inventory deInv = event.getInventory();
		Player player = (Player) event.getWhoClicked();

		// Test for Upgrade Item was put into upgrable Dropper
		if (deInv.getHolder() instanceof Dropper && deInv.isEmpty()
				&& event.getCursor().isSimilar(AutoCraftHandler.AUTOCRAFTER_UPGRADE()) && event.getRawSlot() < 9) {

			int amount = event.getCursor().getAmount();
			event.setCursor(new ItemStack(Material.AIR));
			handler.addToEdditedAutoCrafter((Player) event.getWhoClicked(), deInv.getLocation(), amount);

		}

		// Test for take out an Item from Edit-Menu
		if (deInv.getType() == InventoryType.WORKBENCH && event.getRawSlot() == 0) {
			handler.register(player, deInv, event.getCurrentItem());
		}
	}

	@EventHandler
	public void InventoryCloseEvent(InventoryCloseEvent event) {

		// if player fails to set recipe, give him item back
		handler.failEditingAutoCrafter((Player) event.getPlayer());
	}

	@EventHandler
	public void InventoryMoveItemEvent​(InventoryMoveItemEvent event) {

		// test if a Item comes out of a Dropper
		if (event.getSource().getHolder() instanceof Dropper) {
			event.setCancelled(!handler.passItemFromAutoCraft(event.getSource().getLocation(), event.getItem()));
		}

		if (event.getDestination().getHolder() instanceof Dropper) {
			if (handler.itemToAutoCrafter(event.getDestination().getLocation(), event.getItem()))
				event.setItem(new ItemStack(Material.AIR));
			;
		}
	}

	@EventHandler
	public void BlockBreakEvent(org.bukkit.event.block.BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.DROPPER) {
			event.setDropItems(handler.breakAutoCrafter(event.getBlock().getLocation()));
		}
	}
}

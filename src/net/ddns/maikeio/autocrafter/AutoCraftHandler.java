package net.ddns.maikeio.autocrafter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class AutoCraftHandler {
	final File saveFile;
	HashMap<Location, AutoCrafter> aCrafter;
	HashMap<Player, AutoCrafter> openAutoCrafter;

	public AutoCraftHandler() {
		this.saveFile = new File(Main.SafeFolder + "AutoCrafter.save");
		openAutoCrafter = new HashMap<Player, AutoCrafter>();

		// loads every AutoCrafter from File an set AutoCrafter
		load(this.saveFile);
	}

	@SuppressWarnings("unchecked")
	private void load(File file) {
		if (saveFile.exists()) {
			// read File
			try {

				FileInputStream fileIn = new FileInputStream(file);
				BukkitObjectInputStream objectIn = new BukkitObjectInputStream(fileIn);
				Object obj = objectIn.readObject();
				this.aCrafter = (HashMap<Location, AutoCrafter>) obj;
				objectIn.close();
				fileIn.close();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			// Initialisiere AutoCrafterMap
		} else
			this.aCrafter = new HashMap<Location, AutoCrafter>();
	}

	public void check() {
		// tests every AutoCrafter if it is still there
		this.aCrafter.keySet().removeIf(location -> (location.getBlock().getType() != Material.DROPPER));
	}

	public void save() {
		try {
			// saves aCrafter Map to File

			FileOutputStream fileOut = new FileOutputStream(this.saveFile);
			BukkitObjectOutputStream objectOut = new BukkitObjectOutputStream(fileOut);
			objectOut.writeObject(this.aCrafter);
			objectOut.close();
			fileOut.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// item Stack von AUTOCRAFTER_UPGRADE
	public static ItemStack AUTOCRAFTER_UPGRADE() {
		ItemStack item = new ItemStack(Material.COMMAND_BLOCK);
		ItemMeta meta = item.getItemMeta();

		meta.setCustomModelData(1);
		meta.setDisplayName(ChatColor.WHITE + "Upgrade Module");

		// Discription of the Item
		List<String> discription = new ArrayList<String>();
		discription.add(ChatColor.DARK_AQUA + "this module is used");
		discription.add(ChatColor.DARK_AQUA + "to upgrade a dropper");
		discription.add(ChatColor.DARK_AQUA + "to an autocrafter");
		meta.setLore(discription);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack AUTOCRAFTER_UPGRADE(int amount) {
		ItemStack item = new ItemStack(Material.COMMAND_BLOCK, amount);
		ItemMeta meta = item.getItemMeta();

		meta.setCustomModelData(1);
		meta.setDisplayName(ChatColor.WHITE + "Upgrade Module");

		// Discription of the Item
		List<String> discription = new ArrayList<String>();
		discription.add(ChatColor.DARK_AQUA + "this module is used");
		discription.add(ChatColor.DARK_AQUA + "to upgrade a dropper");
		discription.add(ChatColor.DARK_AQUA + "to an autocrafter");
		meta.setLore(discription);
		item.setItemMeta(meta);
		return item;
	}

	// adds the AutoCrafter to the List of AutoCrafters
	public void register(Player player, Inventory deInv, ItemStack result) {

		if (!this.openAutoCrafter.containsKey(player))
			return;

		// Create recipe and get AutoCrafter from List
		AutoRecipe recipe = new AutoRecipe();
		AutoCrafter ac = this.openAutoCrafter.get(player);

		if (this.aCrafter.containsKey(ac.getLocation()))
			this.aCrafter.remove(ac.getLocation());
		// add every item in Crafting field to Recipe
		for (int i = 1; i < 10; i++) {
			if (deInv.getItem(i) instanceof ItemStack) {
				recipe.addRequired(deInv.getItem(i).getType());
			}
		}
		// set the result of the recipe
		recipe.setResult(result.getType(), result.getAmount());
		// give the AutoCrafter the recipe
		ac.setRecipe(recipe);
		ac.getInventory().clear();
		ac.setCustomName("AutoCrafter");
		// adds the AutoCrafter to List of AutoCrafter so it can be tested for and be
		// saved
		aCrafter.put(ac.getLocation(), ac);
		// remove player from editor Menu List
		this.openAutoCrafter.remove(player);
		player.closeInventory();

	}

	public void addToEdditedAutoCrafter(Player player, Location location, int amount) {

		AutoCrafter ac;
		// Test if it is allready upgraded
		if (aCrafter.containsKey(location)) {
			ac = aCrafter.get(location);
			player.getInventory().addItem(AUTOCRAFTER_UPGRADE());
		} else
			ac = new AutoCrafter(location);

		// return the Rest of Current Items Back to Player
		if (amount > 1)
			player.getInventory().addItem(AUTOCRAFTER_UPGRADE(amount - 1));

		//opens Crafting menu to test for crafting
		player.closeInventory();
		player.openWorkbench(player.getLocation(), true);
		
		this.openAutoCrafter.put(player, ac);

	}

// when close Inventory without setting an recipe, give Player Item back
	public void failEditingAutoCrafter(Player player) {
		if (this.openAutoCrafter.containsKey(player)) {
			if (!this.aCrafter.containsKey(this.openAutoCrafter.get(player).getLocation()))
				player.getInventory().addItem(AUTOCRAFTER_UPGRADE());
			this.openAutoCrafter.remove(player);
		}
	}

	public boolean passItemFromAutoCraft(Location location, ItemStack item) {

		// Test if its a AutoCrafter
		if (!aCrafter.containsKey(location))
			return true;

		AutoCrafter ac = aCrafter.get(location);

		// if the Item is not the Result of the recipe, cancel the event
		if (item.getType() == ac.getRecipe().getResult().getType())
			return true;
		return false;
	}

	public boolean itemToAutoCrafter(Location location, ItemStack item) {
		if (!aCrafter.containsKey(location))
			return false;

		AutoCrafter ac = aCrafter.get(location);

		ac.getInventory().addItem(item);

		boolean enough = ac.containsRequiredItems();

		while (enough) {
			ac.craft();
			enough = ac.containsRequiredItems();
		}
		return true;
	}

	public boolean breakAutoCrafter(Location location) {
		if (!aCrafter.containsKey(location))
			return true;
		aCrafter.remove(location);
		location.getWorld().dropItemNaturally(location, new ItemStack(Material.DROPPER));
		location.getWorld().dropItemNaturally(location, AutoCraftHandler.AUTOCRAFTER_UPGRADE());
		return false;
	}
}

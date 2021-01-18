package net.ddns.maikeio.autocrafter;

import java.io.Serializable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Dropper;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AutoCrafter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 356324531553881380L;
	private AutoRecipe recipe;
	private Location location;

	public AutoCrafter(Location location) {
		this.location = location;
		this.recipe = new AutoRecipe();

	}

	public void setRecipe(AutoRecipe recipe) {
		this.recipe = recipe;
	}

	public AutoRecipe getRecipe() {
		return this.recipe;
	}

	public Inventory getInventory() {
		Dropper dp = (Dropper) location.getBlock().getState();
		return (dp.getInventory());
	}

	public void setCustomName(String name) {
		Dropper dp = (Dropper) location.getBlock().getState();
		dp.setCustomName(name);
		dp.update(true);
	}

	public String getCustomName() {
		return ((Dropper) location.getBlock().getState()).getCustomName();
	}

	public Location getLocation() {
		return this.location;
	}

	public boolean containsRequiredItems() {
		for (Material material : this.getRecipe().getRequiements().keySet()) {
			if (!this.getInventory().contains(material, this.getRecipe().getRequiements().get(material)))
				return false;
		}
		return true;
	}

	public void craft() {

		//loops through every Item is needed
		for (Material material : this.getRecipe().getRequiements().keySet()) {
			//get amount of the needed Item
			int amount = this.getRecipe().getRequiements().get(material);
			//get size of the Inventory and Loope trough every slot
			int size = this.getInventory().getSize();
	        for (int slot = 0; slot < size; slot++) {
	        	//test if in the slot is needed Item
	            ItemStack is = this.getInventory().getItem(slot);
	            if (is == null) continue;
	            if (material == is.getType()) {
	            	//calculate amount after take needed amount
	                int newAmount = is.getAmount() - amount;
	                if (newAmount > 0) {
	                    is.setAmount(newAmount);
	                    break;
	                } else {
	                	this.getInventory().clear(slot);
	                    amount = -newAmount;
	                    if (amount == 0) break;
	                }
	            }
	        }
		}

		this.getInventory().addItem(this.getRecipe().getResult());
	}

}

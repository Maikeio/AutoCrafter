package net.ddns.maikeio.autocrafter;

import java.io.Serializable;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AutoRecipe implements Serializable {


	private static final long serialVersionUID = -634517811273458418L;
	Material resultMaterial;
	int resultAmount;
	HashMap<Material, Integer> Requiement;

	public AutoRecipe() {
		//set result to air so it is initialized, but not to any meaningfull item
		this.resultMaterial = Material.AIR;
		this.resultAmount = 1;
		this.Requiement = new HashMap<Material, Integer>();
	}

	//tests if the Requiement is already there and if so, add the count of reqiert items
	public void addRequired(Material material) {
		if (!this.Requiement.containsKey(material))
			this.Requiement.put(material, 0);
		this.Requiement.put(material, this.Requiement.get(material) + 1);
	}

	public void setResult(Material type, int amount) {
		this.resultMaterial = type;
		this.resultAmount = amount;

	}

	public ItemStack getResult() {
		return new ItemStack(resultMaterial, resultAmount);
	}

	//Loops throug the requiert items and checks if in the given Inventory are enough items
	public HashMap<Material, Integer> getRequiements(){
		return this.Requiement;
	}
}

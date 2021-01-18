package net.ddns.maikeio.autocrafter;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public static String SafeFolder = "plugins/AutoCrafter/";

	@Override
	public void onEnable() {

		// adds Listener for AutoCrafter
		getServer().getPluginManager().registerEvents(new AutoCraftListener(), this);

		// adds the Custom Recpies
		recipes();

		// test for save Folder
		if (!new File("plugins/AutoCrafter").exists())
			new File("plugins/AutoCrafter").mkdirs();
	}

	@Override
	public void onDisable() {

	}

	private void recipes() {

		// AUTOCRAFTER_UPGRADE Recipe
		NamespacedKey AUTOCRAFTER_UPGRADE_key = new NamespacedKey(this, "Autocrafter_Upgrade");
		ShapedRecipe AUTOCRAFTER_UPGRADE_recipe = new ShapedRecipe(AUTOCRAFTER_UPGRADE_key,
				AutoCraftHandler.AUTOCRAFTER_UPGRADE());

		// Pattern
		AUTOCRAFTER_UPGRADE_recipe.shape("GRG", "HNH", "HCH");

		// Definition
		AUTOCRAFTER_UPGRADE_recipe.setIngredient('R', Material.REDSTONE);
		AUTOCRAFTER_UPGRADE_recipe.setIngredient('G', Material.GOLD_INGOT);
		AUTOCRAFTER_UPGRADE_recipe.setIngredient('H', Material.HOPPER);
		AUTOCRAFTER_UPGRADE_recipe.setIngredient('C', Material.CRAFTING_TABLE);
		AUTOCRAFTER_UPGRADE_recipe.setIngredient('N', Material.NETHERITE_SCRAP);
		Bukkit.addRecipe(AUTOCRAFTER_UPGRADE_recipe);
	}
}

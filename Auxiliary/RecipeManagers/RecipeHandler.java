package Reika.RotaryCraft.Auxiliary.RecipeManagers;

import java.util.Collection;
import java.util.HashMap;

import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.RotaryCraft.RotaryCraft;
import Reika.RotaryCraft.Registry.ConfigRegistry;
import Reika.RotaryCraft.Registry.MachineRegistry;

import com.google.common.collect.HashBiMap;

public abstract class RecipeHandler {

	private final MultiMap<RecipeLevel, String> recipesByLevel = new MultiMap(new MultiMap.HashSetFactory());
	private final HashMap<String, RecipeLevel> recipeLevels = new HashMap();

	private final HashBiMap<MachineRecipe, String> recipeKeys = HashBiMap.create();

	public final MachineRegistry machine;

	protected RecipeHandler(MachineRegistry m) {
		machine = m;
	}

	protected final void onAddRecipe(MachineRecipe recipe, RecipeLevel rl) {
		String s = recipeKeys.get(recipe);
		if (s == null) {
			this.generateKey(recipe);
		}
		recipesByLevel.addValue(rl, s);
		recipeLevels.put(s, rl);
	}

	private void generateKey(MachineRecipe recipe) {
		String s = /*machine.getDefaultName()+"$"+recipe.getClass().getName()+"#"+*/recipe.getUniqueID();
		recipeKeys.put(recipe, s);
	}

	protected final Collection getRecipes(RecipeLevel rl) {
		return recipesByLevel.get(rl);
	}

	public final RecipeLevel getRecipeLevel(String rec) {
		return recipeLevels.get(rec);
	}

	public final boolean removeRecipe(String rec) {
		RecipeLevel rl = this.getRecipeLevel(rec);
		RecipeModificationPower power = getModificationPower();
		if (power.canRemove(rl)) {
			MachineRecipe recipe = recipeKeys.inverse().get(rec);
			if (rec == null) {
				RotaryCraft.logger.log("Recipe removal of '"+rec+"' from "+machine+" not possible; No such recipe with that key.");
				return false;
			}
			try {
				if (this.removeRecipe(rec)) {
					recipesByLevel.remove(rl, rec);
					recipeLevels.remove(rec);
					return true;
				}
				else {
					RotaryCraft.logger.log("Recipe removal of '"+rec+"' from "+machine+" failed; Potential code error.");
					return false;
				}
			}
			catch (Exception e) {
				RotaryCraft.logger.log("Recipe removal of '"+rec+"' from "+machine+" failed and threw an exception; Potential code error.");
				e.printStackTrace();
				return false;
			}
		}
		else {
			RotaryCraft.logger.log("Recipe removal of '"+rec+"' from "+machine+" rejected; This is a '"+rl+"' recipe and cannot be modified with '"+power+"' modify power.");
			return false;
		}
	}

	public abstract void addPostLoadRecipes();

	protected abstract boolean removeRecipe(MachineRecipe recipe);

	public static enum RecipeLevel {

		CORE(), //Core native recipesByLevel
		PROTECTED(), //Non-core but native and fairly important
		PERIPHERAL(), //Non-core but native
		MODINTERACT(), //Ones for mod interaction; also native
		API(), //API-level
		CUSTOM(); //Minetweaker

		private static final RecipeLevel[] list = values();

	}

	private static enum RecipeModificationPower {
		FULL(RecipeLevel.CORE),
		STRONG(RecipeLevel.PROTECTED),
		NORMAL(RecipeLevel.PERIPHERAL),
		DEFAULT(RecipeLevel.CUSTOM);

		private final RecipeLevel maxLevel;

		private static final RecipeModificationPower[] list = values();

		private RecipeModificationPower(RecipeLevel rl) {
			maxLevel = rl;
		}

		public final boolean canRemove(RecipeLevel rl) {
			return rl.ordinal() >= maxLevel.ordinal();
		}
	}

	private static RecipeModificationPower getModificationPower() {
		int get = Math.min(RecipeModificationPower.DEFAULT.ordinal(), Math.max(0, ConfigRegistry.getRecipeModifyPower()));
		return RecipeModificationPower.list[RecipeModificationPower.DEFAULT.ordinal()-get];
	}

	protected static interface MachineRecipe {

		String getUniqueID();

	}


}

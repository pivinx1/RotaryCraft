/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.RotaryCraft.Items.Placers;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockDeco extends ItemBlock {

	// This is for your naming of the metablock
	private static final String subNames[] =
		{
		"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"
		};

	public ItemBlockDeco(int id) {
		super(id);
		this.setHasSubtypes(true);
		//setItemName("machine");
	}

	@Override
	public int getMetadata (int damageValue) {
		return damageValue;
	}

	@Override
	public String getUnlocalizedName(ItemStack is)
	{
		int d = is.getItemDamage();
		return this.getUnlocalizedName() + d;
	}

	public String getItemNameIS(ItemStack itemstack){
		//this returns a string with the block name
		//example: blockXXX
		return new StringBuilder().append("machineblock").append(subNames[itemstack.getItemDamage()]).toString();
	}

	public String getTextureFile() {
		return "/Reika/RotaryCraft/Textures/Terrain/textures.png"; //return the block texture where the block texture is saved in
	}
}

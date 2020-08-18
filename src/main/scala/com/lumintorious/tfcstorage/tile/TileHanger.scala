package com.lumintorious.tfcstorage.tile

import com.lumintorious.tfcstorage.TFCStorage
import com.lumintorious.tfcstorage.food.StoredTrait
import com.lumintorious.tfcstorage.registry.Initializable
import com.lumintorious.tfcstorage.tile.renderers.HangerRenderer
import net.dries007.tfc.objects.inventory.ingredient.IIngredient
import net.dries007.tfc.objects.items.food.ItemFoodTFC
import net.dries007.tfc.util.agriculture.Food
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.EnumSkyBlock

object TileHanger extends Initializable {
	TileEntity.register("hanger", classOf[TileHanger])
}

class TileHanger extends TileFoodHolder {
	var isPreserving: Boolean = false
	
	override def handleGivenStack(stack: ItemStack) = {
		StoredTrait.HUNG << stack
		StoredTrait.COOL << stack
		super.handleGivenStack(stack)
	}
	
	def canPreserve(stack: ItemStack): Boolean =
		!stack.isEmpty &&
		getPos().getY() < 130 &&
		world.getLightFor(EnumSkyBlock.SKY, getPos()) < 2
	
	override def fits(stack: ItemStack) =
		IIngredient.of("categoryCookedMeat").test(stack) ||
		IIngredient.of("categoryDairt").test(stack) ||
		stack.getItem() == ItemFoodTFC.get(Food.GARLIC)
	
	override def updatePreservation(): Unit = {
		if(stack == null) {
			isPreserving = false
			return
		}
		StoredTrait.HUNG >> stack
		if (canPreserve(stack)) {
			StoredTrait.COOL >> stack
			isPreserving = true
		}
		else{
			StoredTrait.COOL << stack
			isPreserving = false
		}
		
	}
}

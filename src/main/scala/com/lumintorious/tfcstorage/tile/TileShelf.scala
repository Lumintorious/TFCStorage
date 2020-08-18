package com.lumintorious.tfcstorage.tile

import com.lumintorious.tfcstorage.TFCStorage
import com.lumintorious.tfcstorage.food.StoredTrait
import com.lumintorious.tfcstorage.registry.Initializable
import com.lumintorious.tfcstorage.tile.renderers.ShelfRenderer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.EnumSkyBlock

object TileShelf extends Initializable{
	TileEntity.register("food_shelf", classOf[TileShelf])
}

class TileShelf extends TileFoodHolder {
	var isPreserving: Boolean = false
	
	override def handleGivenStack(stack: ItemStack) = {
		StoredTrait.COOL << stack
		StoredTrait.HUNG << stack
		super.handleGivenStack(stack)
	}
	
	def canPreserve(stack: ItemStack): Boolean =
		!stack.isEmpty &&
		getPos().getY() < 130 &&
		world.getLightFor(EnumSkyBlock.SKY, getPos()) < 2
	
	override def updatePreservation(): Unit = {
		if(stack == null) {
			isPreserving = false
			return
		}
		StoredTrait.SHELVED >> stack
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
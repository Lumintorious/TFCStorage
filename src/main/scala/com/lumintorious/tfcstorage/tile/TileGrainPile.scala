package com.lumintorious.tfcstorage.tile

import com.lumintorious.tfcstorage.block.BlockGrainPile
import com.lumintorious.tfcstorage.food.StoredTrait
import com.lumintorious.tfcstorage.registry.Initializable
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.EnumSkyBlock

object TileGrainPile extends Initializable
{
	TileEntity.register("grain_pile", classOf[TileGrainPile])
}

class TileGrainPile extends TileFoodHolder
{
	var isPreserving = true
	
	override def fits(stack: ItemStack) = true
	
	override def giveStack(player: EntityPlayer, amount: Int) =
	{
		super.giveStack(player, amount)
		if(stack.isEmpty)
		{
			world.setBlockToAir(pos)
		}
		true
	}
	
	override def takeStack(player: EntityPlayer) = takeStack(player, 16)
	
	override def handleGivenStack(stack: ItemStack) = {
		StoredTrait.SHELTERED << stack
		super.handleGivenStack(stack)
	}
	
	def canPreserve(stack: ItemStack): Boolean =
		!stack.isEmpty &&
		world.getLightFor(EnumSkyBlock.SKY, pos.add(0, 1, 0)) < 15
	
	override def updatePreservation(): Unit = {
		if(stack == null) {
			isPreserving = false
			return
		}
		if (canPreserve(stack)) {
			StoredTrait.SHELTERED >> stack
			isPreserving = true
		}
		else{
			StoredTrait.SHELTERED << stack
			isPreserving = false
		}
	}
}

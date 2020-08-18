package com.lumintorious.tfcstorage.block

import com.lumintorious.tfcstorage.compat.waila.GrainPileProvider
import com.lumintorious.tfcstorage.registry.RegistryHandler
import com.lumintorious.tfcstorage.tile._
import mcjty.theoneprobe.api._
import net.dries007.tfc.api.capability.size._
import net.dries007.tfc.util.agriculture.Food
import net.minecraft.block._
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util._
import net.minecraft.util.math.BlockPos
import net.minecraft.world._
import net.minecraftforge.registries.IForgeRegistry

import scala.collection.mutable.HashMap

object BlockGrainPile extends RegistryHandler[Block] {
	var all = new HashMap[String, BlockGrainPile]()
	
	override def >>(registry: IForgeRegistry[Block]) =
	{
		Food.values().foreach(
			food =>
			{
				if(food.toString().toLowerCase().contains("grain") & food.getCategory() == Food.Category.GRAIN)
				{
					registry.register(new BlockGrainPile(food.toString().toLowerCase()))
				}
			}
		)
	}
}

class BlockGrainPile(val food: String)
extends BlockBase("grain_pile/" + food, Material.SAND)
with ITileEntityProvider
with IProbeInfoAccessor
with IItemSize
{
	this.setSoundType(SoundType.WOOD)
	this.setHarvestLevel("axe", 0)
	this.setHardness(-1f)
	this.setCreativeTab(CreativeTabs.DECORATIONS)
	
	BlockGrainPile.all.put(food, this)
	
	override def onBlockActivated(
		worldIn: World,
		pos: BlockPos,
		state: IBlockState,
		playerIn: EntityPlayer,
		hand: EnumHand,
		facing: EnumFacing,
		hitX: Float,
		hitY: Float,
		hitZ: Float
	): Boolean =
		worldIn.getTileEntity(pos) match {
		case tile: TileFoodHolder => tile.onRightClick(playerIn)
		case _                    => true
	}
	
	override def onBlockClicked(
		worldIn: World,
		pos: BlockPos,
		playerIn: EntityPlayer
	) = {
		worldIn.getTileEntity(pos) match {
		case tile: TileFoodHolder => tile.onLeftClick(playerIn)
		}
	}
	
	override def breakBlock(
		worldIn: World,
		pos: BlockPos,
		state: IBlockState
	) = {}
		
	override def createNewTileEntity(worldIn: World, meta: Int) = new TileGrainPile()
	
	override def addProbeInfo(
		probeMode: ProbeMode,
		iProbeInfo: IProbeInfo,
		entityPlayer: EntityPlayer,
		world: World, iBlockState: IBlockState,
		iProbeHitData: IProbeHitData
	) = {
		import scala.collection.JavaConversions._
		val list = GrainPileProvider.getTooltip(world, iProbeHitData.getPos(), null)
		for(text <- list) {
			iProbeInfo.text(text)
		}
	}
	
	override def getSize(itemStack: ItemStack) = Size.HUGE
	
	override def getWeight(itemStack: ItemStack) = Weight.VERY_HEAVY
}

package com.lumintorious.tfcstorage.block

import com.lumintorious.tfcstorage.compat.waila.HangerProvider
import com.lumintorious.tfcstorage.registry.RegistryHandler
import com.lumintorious.tfcstorage.tile._
import mcjty.theoneprobe.api._
import net.dries007.tfc.api.capability.size._
import net.dries007.tfc.api.registries.TFCRegistries
import net.dries007.tfc.api.types.Tree
import net.minecraft.block._
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util._
import net.minecraft.util.math._
import net.minecraft.world._
import net.minecraftforge.registries.IForgeRegistry

import scala.collection.JavaConversions._
import scala.collection.mutable.HashMap

object BlockHanger extends RegistryHandler[Block] {
	val all: HashMap[Tree, BlockHanger] = new HashMap[Tree, BlockHanger]
	
	override def >>(registry: IForgeRegistry[Block]) = {
		for(tree <- TFCRegistries.TREES.getValuesCollection) {
			val block = new BlockHanger(tree)
			registry.register(block)
		}
	}
	
	implicit class AABB(val aabb: AxisAlignedBB) {
		def +(aabb2: AxisAlignedBB) = aabb.intersect(aabb2)
	}
	
	val BOUNDING_BOX: AxisAlignedBB = new AxisAlignedBB(0, 0.75, 0, 1, 1, 1)
}

class BlockHanger(wood: Tree)
extends BlockBase("wood/hanger/" + wood.getRegistryName.getPath, Material.WOOD)
with ITileEntityProvider
with IProbeInfoAccessor
with IItemSize
{
	this.setSoundType(SoundType.WOOD)
	this.setHarvestLevel("axe", 0)
	this.setHardness(0.5f)
	this.setCreativeTab(CreativeTabs.DECORATIONS)
	
	BlockHanger.all.put(wood, this)
	
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
	
	override def isPassable(worldIn: IBlockAccess, pos: BlockPos) = true
	
	override def getBoundingBox(
		state: IBlockState,
		source: IBlockAccess, pos: BlockPos
	) = {
		BlockHanger.BOUNDING_BOX
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
	
	override def createNewTileEntity(worldIn: World, meta: Int) = new TileHanger()
	
	override def addProbeInfo(
	     probeMode: ProbeMode,
	     iProbeInfo: IProbeInfo,
	     entityPlayer: EntityPlayer,
	     world: World, iBlockState: IBlockState,
	     iProbeHitData: IProbeHitData
	) = {
		val list = HangerProvider.getTooltip(world, iProbeHitData.getPos(), null)
		for(text <- list) {
			iProbeInfo.text(text)
		}
	}
	
	override def isOpaqueCube(state: IBlockState) = false
	
	override def getRenderLayer = BlockRenderLayer.CUTOUT_MIPPED
	override def isTranslucent(state: IBlockState) = true
	override def isFullCube(state: IBlockState) = false
	override def isFullBlock(state: IBlockState) = false
	
	override def getSize(itemStack: ItemStack) = Size.NORMAL
	override def getWeight(itemStack: ItemStack) = Weight.MEDIUM
}

package com.lumintorious.tfcstorage.block

import java.util.Arrays

import com.lumintorious.tfcstorage.TFCStorage
import com.lumintorious.tfcstorage.tile.TileFoodHolder
import net.dries007.tfc.objects.items.itemblock.ItemBlockTFC
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.{IBlockAccess, World}

import scala.collection.mutable.MutableList

object BlockBase {
	val itemBlocks = new MutableList[Item]
}

abstract class BlockBase private(material: Material) extends Block(material) {
	def this(registryName: String, material: Material) {
		this(material)
		this.setRegistryName(registryName)
		this.setTranslationKey(registryName)
		this.initializeItemBlock()
	}
	
	def initializeItemBlock(): BlockBase = {
		this.itemBlock = new ItemBlockTFC(this).setRegistryName(this.getRegistryName)
		BlockBase.itemBlocks += this.itemBlock
		this
	}
	
	override
	def observedNeighborChange
	(
		observerState: IBlockState,
		world: World,
		observerPos: BlockPos,
		changedBlock: Block,
		changedBlockPos: BlockPos
	): Unit = {
		world.getTileEntity(observerPos) match {
		case tile: TileFoodHolder => tile.updatePreservation()
		}
	}
	
	override def breakBlock
	(
		worldIn: World,
		pos: BlockPos,
		state: IBlockState
	): Unit = {
		val drops = this.getDrops(worldIn, pos, state)
		drops.foreach(InventoryHelper.spawnItemStack(worldIn, pos.getX, pos.getY, pos.getZ, _))
	}
	
	def getDrops(
		world: World,
		pos: BlockPos,
		state: IBlockState
	): MutableList[ItemStack] =
	{
		val list = new MutableList[ItemStack]
		world.getTileEntity(pos) match {
			case tile: TileFoodHolder =>
				if(tile.getDrops(list))
				{
					list += new ItemStack(this.itemBlock)
				}
			case _ => list += new ItemStack(this.itemBlock)
		}
		list
	}
	
	override def getDrops(drops: NonNullList[ItemStack],
		world: IBlockAccess, pos: BlockPos,
		state: IBlockState, fortune: Int
	) = {}
	
	override def getDrops(
		world: IBlockAccess,
		pos: BlockPos,
		state: IBlockState,
		fortune: Int
	) = Arrays.asList()
	
	protected var itemBlock: Item = _
	
	override def getCreativeTab = CreativeTabs.DECORATIONS
	
	def getItemBlock(): Item = itemBlock
	
	def registerItemModel() = {
		TFCStorage.proxy.registerItemModel(getItemBlock(), 0, "normal")
	}
}

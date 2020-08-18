package com.lumintorious.tfcstorage.block

import com.lumintorious.tfcstorage.compat.waila.{JarProvider, ShelfProvider}
import com.lumintorious.tfcstorage.registry._
import com.lumintorious.tfcstorage.tile.{TileFoodHolder, TileJar, TileShelf}
import mcjty.theoneprobe.api.{IProbeHitData, IProbeInfo, IProbeInfoAccessor, ProbeMode}
import net.dries007.tfc.api.capability.size._
import net.dries007.tfc.api.registries.TFCRegistries
import net.dries007.tfc.api.types.Tree
import net.minecraft.block.BlockHorizontal.FACING
import net.minecraft.block.material.Material
import net.minecraft.block.state._
import net.minecraft.block._
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.util._
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.registries.IForgeRegistry

import scala.collection.JavaConversions._
import scala.collection.mutable.HashMap

object BlockShelf extends Initializable with RegistryHandler[Block]{
	val all: HashMap[Tree, BlockShelf] = new HashMap[Tree, BlockShelf]
	
	override def >>(registry: IForgeRegistry[Block]) = {
		for(tree <- TFCRegistries.TREES.getValuesCollection) {
			val block = new BlockShelf(tree)
			registry.register(block)
		}
	}
	
	val i = 1D / 16D
	
	val BOUNDING_BOX_E: AxisAlignedBB = new AxisAlignedBB(0, 0, 0, 9 * i, 1, 1)
	val BOUNDING_BOX_S: AxisAlignedBB = new AxisAlignedBB(0, 0, 0, 1, 1, 9 * i)
	val BOUNDING_BOX_W: AxisAlignedBB = new AxisAlignedBB(7 * i, 0, 0, 1, 1, 1)
	val BOUNDING_BOX_N: AxisAlignedBB = new AxisAlignedBB(0, 0, 7 * i, 1, 1, 1)
	
}

class BlockShelf(wood: Tree)
extends BlockBase("wood/food_shelf/" + wood.getRegistryName.getPath, Material.WOOD)
with IItemSize
with ITileEntityProvider
with IProbeInfoAccessor
{
	this.setSoundType(SoundType.WOOD)
	this.setHarvestLevel("axe", 0)
	this.setHardness(0.5f)
	this.setCreativeTab(CreativeTabs.DECORATIONS)
	this.setDefaultState(this.blockState.getBaseState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH))
	
	BlockShelf.all.put(wood, this)
	
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
	
	override def getStateFromMeta(meta: Int) =
		this
			.getDefaultState
			.withProperty(FACING, EnumFacing.byHorizontalIndex(meta))
	
	override def getMetaFromState(state: IBlockState) =
		state
			.getValue(FACING)
			.getHorizontalIndex
	
	override def createBlockState() = new BlockStateContainer(this, FACING)
	
	import net.minecraft.block.state.IBlockState
	import net.minecraft.entity.EntityLivingBase
	import net.minecraft.util.EnumFacing
	import net.minecraft.util.math.BlockPos
	import net.minecraft.world.World
	
	override def getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos) = {
	state.getValue(FACING) match {
		case EnumFacing.NORTH => BlockShelf.BOUNDING_BOX_N
		case EnumFacing.WEST => BlockShelf.BOUNDING_BOX_W
		case EnumFacing.EAST => BlockShelf.BOUNDING_BOX_E
		case EnumFacing.SOUTH => BlockShelf.BOUNDING_BOX_S
		}
	}
	
	override def getStateForPlacement(
        worldIn: World,
        pos: BlockPos,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        meta: Int,
        placer: EntityLivingBase
    ) = {
		var newFacing = facing
		if (facing.getAxis eq EnumFacing.Axis.Y) {
			newFacing = placer.getHorizontalFacing.getOpposite
		}
		getDefaultState.withProperty(FACING, newFacing)
	}
	
	
	override def hasTileEntity = true
	override def createTileEntity(world: World, state: IBlockState) = new TileShelf()
	override def createNewTileEntity(worldIn: World, meta: Int) = new TileShelf()
	
	override def isOpaqueCube(state: IBlockState) = false
	override def isFullCube(state: IBlockState) = false
	override def isFullBlock(state: IBlockState) = false
	
	override def getSize(itemStack: ItemStack) = Size.LARGE
	override def getWeight(itemStack: ItemStack) = Weight.HEAVY
	
	override def addProbeInfo(
		probeMode: ProbeMode,
		iProbeInfo: IProbeInfo,
		entityPlayer: EntityPlayer,
		world: World, iBlockState: IBlockState,
		iProbeHitData: IProbeHitData
    ) = {
		val list = ShelfProvider.getTooltip(world, iProbeHitData.getPos(), null)
		for(text <- list) {
			iProbeInfo.text(text)
		}
	}
}
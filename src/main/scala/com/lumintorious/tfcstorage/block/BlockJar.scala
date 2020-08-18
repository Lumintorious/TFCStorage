package com.lumintorious.tfcstorage.block

import com.lumintorious.tfcstorage.compat.waila.JarProvider
import com.lumintorious.tfcstorage.itemblock.ItemBlockJar
import com.lumintorious.tfcstorage.registry.RegistryHandler
import com.lumintorious.tfcstorage.tile._
import mcjty.theoneprobe.api._
import net.dries007.tfc.api.capability.size._
import net.dries007.tfc.objects.blocks.wood.BlockBarrel
import net.minecraft.block.BlockHorizontal.FACING
import net.minecraft.block.material.Material
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.block._
import net.minecraft.block.properties.{IProperty, PropertyBool}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math._
import net.minecraft.util.{registry, _}
import net.minecraft.world._
import net.minecraftforge.registries.IForgeRegistry

import scala.collection.JavaConverters._

object BlockJar
extends BlockJar("jar")
with RegistryHandler[Block] {
	override def >>(registry: IForgeRegistry[Block]) =
	{
		registry.register(this)
		registry.register(FILLED)
	}
	
	var FILLED: Block = new BlockJar("jar_filled")
	
	val BOUNDING_BOX: AxisAlignedBB = {
		val increment = 1D / 16D
		new AxisAlignedBB(4 * increment, 0, 4 * increment, 12 * increment, 10 * increment, 12 * increment)
	}
}

class BlockJar(name: String)
extends BlockBase(name, Material.GLASS)
with ITileEntityProvider
with IItemSize
with IProbeInfoAccessor {
	
	this.setSoundType(SoundType.GLASS)
	this.setHardness(0.5f)
	this.setCreativeTab(CreativeTabs.DECORATIONS)
	this.setDefaultState(this.blockState.getBaseState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH))
	
	override def initializeItemBlock() = {
		this.itemBlock = new ItemBlockJar(this).setRegistryName(this.getRegistryName())
		BlockBase.itemBlocks += this.itemBlock
		this
	}
	
	override def getStateFromMeta(meta: Int) = this.getDefaultState().withProperty(FACING, if(meta == 0) EnumFacing.NORTH else EnumFacing.SOUTH)
	
	override def getMetaFromState(state: IBlockState) = if(state.getValue(FACING) == EnumFacing.NORTH) 0 else 1
	
	override def createBlockState() = new BlockStateContainer(this, FACING)
	
	override def onBlockPlacedBy(
		worldIn: World,
		pos: BlockPos,
		state: IBlockState,
		placer: EntityLivingBase,
		stack: ItemStack
	) =
	{
		worldIn.getTileEntity(pos) match {
			case tile: TileJar => tile.loadFromStack(stack)
		}
	}
	
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
    ): Boolean = {
		worldIn.getTileEntity(pos) match {
			case tile: TileFoodHolder => tile.onRightClick(playerIn)
			case _ => true
		}
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
	
//	override def getBlockHardness
//	(
//		blockState: IBlockState,
//		worldIn: World,
//		pos: BlockPos
//	): Float =
//	{
//		val tile = TileFoodHolder.find[TileJar](worldIn, pos).getOrElse(return blockHardness)
//		if(tile.fluidTank.getFluidAmount() > 0) -1f else blockHardness
//	}
	
	override def getBoundingBox(
		state: IBlockState,
		source: IBlockAccess, pos: BlockPos
	) = BlockJar.BOUNDING_BOX
	
	override def hasTileEntity = true
	override def createTileEntity(world: World, state: IBlockState) = new TileJar()
	override def createNewTileEntity(worldIn: World, meta: Int) = new TileJar()
	
	override def isOpaqueCube(state: IBlockState) = false
	
	override def getRenderLayer = BlockRenderLayer.CUTOUT_MIPPED
	override def isTranslucent(state: IBlockState) = true
	override def isFullCube(state: IBlockState) = false
	override def isFullBlock(state: IBlockState) = false
	
	override def getSize(itemStack: ItemStack) = Size.HUGE
	override def getWeight(itemStack: ItemStack) = Option(itemStack.getTagCompound()) match {
		case Some(_) => Weight.MEDIUM
		case None => Weight.VERY_HEAVY
	}
	
	override def addProbeInfo(
		probeMode: ProbeMode,
		iProbeInfo: IProbeInfo,
		entityPlayer: EntityPlayer,
		world: World,
		iBlockState: IBlockState,
		iProbeHitData: IProbeHitData
    ) = {
		val list = JarProvider.getTooltip(world, iProbeHitData.getPos(), null)
		for(text <- list.asScala) {
			iProbeInfo.text(text)
		}
		
	}
}

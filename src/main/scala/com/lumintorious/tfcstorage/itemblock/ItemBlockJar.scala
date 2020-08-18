package com.lumintorious.tfcstorage.itemblock

import java.util

import com.lumintorious.tfcstorage.TFCStorage
import com.lumintorious.tfcstorage.block.BlockJar
import com.lumintorious.tfcstorage.registry.RegistryHandler
import net.dries007.tfc.TerraFirmaCraft
import net.dries007.tfc.api.capability.size._
import net.dries007.tfc.objects.items.itemblock.ItemBlockTFC
import net.minecraft.block.Block
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.relauncher._
import net.minecraftforge.registries.IForgeRegistry

object ItemBlockJar
extends RegistryHandler[Item]
{
	var INSTANCE: ItemBlockTFC = _
	var FILLED: ItemBlockTFC = _
	
	override def >>(registry: IForgeRegistry[Item]) =
	{
	}
}

class ItemBlockJar(block: Block)
extends ItemBlockTFC(block)
with IItemSize
{
	if(block.getRegistryName().toString().contains("_filled"))
	{
		ItemBlockJar.FILLED = this
	}
	else
	{
		ItemBlockJar.INSTANCE = this
	}
	
	@SideOnly(Side.CLIENT)
	override def getItemStackDisplayName(stack: ItemStack) =
	{
		var name: String = super.getItemStackDisplayName(stack)
		if(stack.hasTagCompound())
		{
			val tag = stack.getTagCompound()
			if(tag.hasKey("fluid"))
			{
				val tagFluid = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("fluid"))
				name += " (" + tagFluid.getLocalizedName() + ")"
			}
		}
		name
	}
	
	override def onItemUse(player: EntityPlayer,
		worldIn: World, pos: BlockPos,
		hand: EnumHand, facing: EnumFacing, hitX: Float,
		hitY: Float, hitZ: Float
	) =
	{
		if(this == ItemBlockJar.INSTANCE)
		{
			super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ)
		}
		else
		{
			ItemBlockJar.INSTANCE.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ)
		}
	}
	
	override def onItemUseFinish(
		stack: ItemStack,
		worldIn: World,
		entityLiving: EntityLivingBase
	) =
	{
		if(this == ItemBlockJar.INSTANCE)
		{
			super.onItemUseFinish(stack, worldIn, entityLiving)
		}
		else
		{
			ItemBlockJar.INSTANCE.onItemUseFinish(stack, worldIn, entityLiving)
		}
	}
	
	override def onItemUseFirst(
		player: EntityPlayer,
		world: World,
		pos: BlockPos,
		side: EnumFacing,
		hitX: Float,
		hitY: Float,
		hitZ: Float,
		hand: EnumHand
	) =
	{
		if(this == ItemBlockJar.INSTANCE)
		{
			super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand)
		}
		else
		{
			ItemBlockJar.INSTANCE.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand)
		}
	}
	
	
	@SideOnly(Side.CLIENT)
	override def addInformation(
		stack: ItemStack,
		worldIn: World,
		tooltip: util.List[String],
		flagIn: ITooltipFlag
	): Unit =
	{
		if(stack.hasTagCompound()) {
			val tag = stack.getTagCompound()
			if(tag.hasKey("fluid"))
			{
				val tagFluid = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("fluid"))
				tooltip.add(I18n.format(
					TerraFirmaCraft.MOD_ID + ".tooltip.barrel_fluid",
					tagFluid.amount.toString(),
					tagFluid.getLocalizedName()
				))
			}
		}
	}
	
	
	override def getSize(itemStack: ItemStack) = Option(itemStack.getTagCompound()) match {
	case Some(_) => Size.HUGE
	case None => Size.NORMAL
	}
	override def getWeight(itemStack: ItemStack) = Option(itemStack.getTagCompound()) match {
		case Some(_) => Weight.VERY_HEAVY
		case None => Weight.MEDIUM
	}
}

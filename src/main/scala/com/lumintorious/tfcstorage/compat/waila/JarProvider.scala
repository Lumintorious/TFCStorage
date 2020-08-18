package com.lumintorious.tfcstorage.compat.waila

import java.util.{ArrayList, Collections}

import com.lumintorious.tfcstorage.block.BlockJar
import com.lumintorious.tfcstorage.registry.Initializable
import com.lumintorious.tfcstorage.tile.TileJar
import mcp.mobius.waila.api.{IWailaPlugin, IWailaRegistrar, WailaPlugin}
import net.dries007.tfc.compat.waila.{HwylaPluginTFC, TOPPlugin}
import net.dries007.tfc.compat.waila.interfaces.{HwylaBlockInterface, IWailaBlock, TOPBlockInterface}
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._
import scala.collection.mutable.MutableList


object JarProvider extends IWailaBlock with Initializable{
	override def getTooltip(
		world: World,
		blockPos: BlockPos,
		nbtTagCompound: NBTTagCompound
	)= {
		var list = new MutableList[String]
		world.getTileEntity(blockPos) match {
			case tile: TileJar =>
				if(tile.stack != null && !tile.stack.isEmpty){
					list += tile.stack.getCount() + " x " + tile.stack.getDisplayName()
					Helper.computeDecayTooltip(tile.stack, list)
				}
				if(tile.fluidTank.getFluid() != null) {
					list += new TextComponentTranslation(
						"waila.tfc.barrel.contents",
						tile.fluidTank.getFluid().amount.toString(),
						tile.fluidTank.getFluid().getLocalizedName()
					)
				     .getFormattedText()
				}
		}
		list.asJava
		
	}
	
	override def getLookupClass() = Collections.singletonList(classOf[TileJar])
}

package com.lumintorious.tfcstorage.compat.waila

import java.util.Collections

import com.lumintorious.tfcstorage.registry.Initializable
import com.lumintorious.tfcstorage.tile.{TileGrainPile, TileJar}
import net.dries007.tfc.compat.waila.interfaces.IWailaBlock
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._
import scala.collection.mutable.MutableList

object GrainPileProvider extends IWailaBlock with Initializable{
	override def getTooltip(
		world: World,
		blockPos: BlockPos,
		nbtTagCompound: NBTTagCompound
	)= {
		var list = new MutableList[String]
		world.getTileEntity(blockPos) match {
		case tile: TileGrainPile =>
			if(tile.stack != null && !tile.stack.isEmpty){
				list += tile.stack.getCount() + " x " + tile.stack.getDisplayName()
				Helper.computeDecayTooltip(tile.stack, list)
			}
		}
		list.asJava
		
	}
	
	override def getLookupClass() = Collections.singletonList(classOf[TileGrainPile])
}

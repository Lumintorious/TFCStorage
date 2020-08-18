package com.lumintorious.tfcstorage.compat.waila

import java.util.Collections

import com.lumintorious.tfcstorage.food.StoredTrait
import com.lumintorious.tfcstorage.registry.Initializable
import com.lumintorious.tfcstorage.tile.{TileHanger, TileShelf}
import net.dries007.tfc.ConfigTFC
import net.dries007.tfc.api.capability.food.{CapabilityFood, FoodTrait, IFood}
import net.dries007.tfc.compat.waila.interfaces.IWailaBlock
import net.dries007.tfc.util.calendar.{CalendarTFC, ICalendar}
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.{TextComponentTranslation, TextFormatting}
import net.minecraft.world.World
import org.jline.utils.InfoCmp.Capability

import scala.collection.mutable.MutableList
import scala.collection.JavaConversions._

object HangerProvider extends IWailaBlock with Initializable{
	override def getTooltip(
		world: World,
		blockPos: BlockPos,
		nbtTagCompound: NBTTagCompound
	) = {
		var list = new MutableList[String]
		world.getTileEntity(blockPos) match {
		case tile: TileHanger =>
			if (tile.stack != null && !tile.stack.isEmpty) {
				list += tile.stack.getCount() + " x " + tile.stack.getDisplayName()
				Helper.computeDecayTooltip(tile.stack, list)
			}
			list
		}
	}
	
	override def getLookupClass() = Collections.singletonList(classOf[TileHanger])
}

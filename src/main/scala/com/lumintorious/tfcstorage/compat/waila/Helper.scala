package com.lumintorious.tfcstorage.compat.waila

import net.dries007.tfc.api.capability.food.{CapabilityFood, IFood}
import net.dries007.tfc.util.calendar.{CalendarTFC, ICalendar}
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextFormatting

import scala.collection.mutable.MutableList

object Helper {
	def computeDecayTooltip(stack: ItemStack, list: MutableList[String])= {
		stack.getCapability(CapabilityFood.CAPABILITY, null) match {
		case food: IFood =>
			if (food.isRotten()) {
				list += (I18n.format("tfc.tooltip.food_rotten"));
			}
			else {
				val rottenDate = food.getRottenDate();
				if (rottenDate == Long.MaxValue) {
					list += (I18n.format("tfc.tooltip.food_infinite_expiry"));
				}
				else {
					// Date food rots on.
					val rottenCalendarTime = rottenDate - CalendarTFC.PLAYER_TIME.getTicks() + CalendarTFC.CALENDAR_TIME.getTicks();
					// Days till food rots.
					val daysToRotInTicks   = rottenCalendarTime - CalendarTFC.CALENDAR_TIME.getTicks();
					list += (
					        I18n.format("tfc.tooltip.food_expiry_date.days",
						        String.valueOf(ICalendar.getTotalDays(daysToRotInTicks)))
					        );
				}
			}
		}
	}
}

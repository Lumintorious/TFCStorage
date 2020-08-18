package com.lumintorious.tfcstorage.tile

import com.lumintorious.tfcstorage.TFCStorage
import com.lumintorious.tfcstorage.block.BlockJar
import com.lumintorious.tfcstorage.food.StoredTrait
import com.lumintorious.tfcstorage.registry.Initializable
import com.lumintorious.tfcstorage.tile.renderers.JarRenderer
import net.dries007.tfc.api.capability.food._
import net.dries007.tfc.objects.fluids.FluidsTFC
import net.minecraft.block.BlockHorizontal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util._
import net.minecraftforge.common.capabilities._
import net.minecraftforge.fluids._
import net.minecraftforge.fluids.capability.CapabilityFluidHandler

import scala.collection.mutable.MutableList

object TileJar extends Initializable{
	TileEntity.register("food_jar", classOf[TileJar])
}

class TileJar extends TileFoodHolder with ICapabilityProvider{
	var fluidTank = new FluidTank(8000)
	
	
	override def handleTakenStack(stack: ItemStack) = {
		super.handleTakenStack(stack)
	}
	
	override def handleGivenStack(stack: ItemStack) = {
		StoredTrait.JAR << stack
		super.handleTakenStack(stack)
	}
	
	override def getDrops(list: MutableList[ItemStack]): Boolean = {
		val hasLiquid = this.fluidTank.getFluidAmount() > 0
		var stack = new ItemStack(BlockJar, 1)
		if(hasLiquid)
		{
			stack = new ItemStack(BlockJar.FILLED, 1)
		}
		val nbt = new NBTTagCompound()
		if(!this.stack.isEmpty)
		{
			list += this.stack
		}
		if(hasLiquid)
		{
			nbt.setTag("fluid", fluidTank.getFluid().writeToNBT(new NBTTagCompound()))
		}
		if(!nbt.isEmpty)
		{
			stack.setTagCompound(nbt)
		}
		list += stack
		false
	}
	
	def loadFromStack(stack: ItemStack): Unit = {
		val tag = Option(stack.getTagCompound).getOrElse(return)
		this.fluidTank.setFluid(FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("fluid")))
	}
	
	override def updatePreservation(): Unit = {
		if(stack == null) {
			isPreserving = false
			return
		}
		if (canPreserve(stack)) {
			StoredTrait.JAR >> stack
			isPreserving = true
		}
		else{
			StoredTrait.JAR << stack
			isPreserving = false
		}
	}
	
	def canPreserve(stack: ItemStack): Boolean = {
		if(fluidTank.getFluid() == null) return false
		val fluidStack = fluidTank.getFluid()
		if(fluidStack.getFluid() != FluidsTFC.VINEGAR.get() || fluidStack.amount < stack.getCount() * 125) {
			false
		}
		else {
			stack.getCapability(CapabilityFood.CAPABILITY, null) match {
				case cap: IFood => cap.getTraits.contains(FoodTrait.PICKLED)
				case _ => false
			}
		}
	}
	
	var isPreserving: Boolean = false
	
	override def onRightClick(player: EntityPlayer) = {
		val playerStack = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND)
		val c = CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY
		if(playerStack.hasCapability(c, null)) {
			FluidUtil.interactWithFluidHandler(player, EnumHand.MAIN_HAND, fluidTank)
		}
		else {
			super.onRightClick(player)
		}
	}
	
	override def hasCapability(capability: Capability[_], facing: EnumFacing) = {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			true
		}
		else{
			super.hasCapability(capability, facing)
		}
	}
	
	override def getCapability[T](capability: Capability[T], facing: EnumFacing) = {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			fluidTank.asInstanceOf[T]
		}
		else{
			super.getCapability(capability, facing)
		}
	}
	
	override def writeToNBT(nbt: NBTTagCompound) = {
		if(fluidTank.getFluidAmount() != 0){
			val fluidTag = new NBTTagCompound()
			fluidTank.getFluid().writeToNBT(fluidTag)
			nbt.setTag("fluid", fluidTag)
		}
		super.writeToNBT(nbt)
	}
	
	override def readFromNBT(nbt: NBTTagCompound) = {
		this.fluidTank.setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("fluid")))
		super.readFromNBT(nbt)
	}
}

package com.lumintorious.tfcstorage.tile

import net.dries007.tfc.api.capability.food.CapabilityFood
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ITickable

import scala.collection.mutable.MutableList

class TileFoodHolder extends TileEntity with ITickable{
	var stack: ItemStack = ItemStack.EMPTY
	
	def getDrops(list: MutableList[ItemStack]): Boolean = {
		val newStack = stack.copy()
		this.handleGivenStack(newStack)
		list += newStack
		true
	}
	
	def onLeftClick(player: EntityPlayer): Boolean = {
		if(!stack.isEmpty) {
			if(player.isSneaking) {
				giveStack(player)
			}
			else {
				giveStack(player, 1)
			}
		}
		true
	}
	
	def onRightClick(player: EntityPlayer): Boolean = {
		val playerStack = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND)
		if(stack.isEmpty && fits(playerStack)) {
			takeStack(player)
		}else{
			true
		}
	}
	
	def fits(stack: ItemStack): Boolean = stack.hasCapability(CapabilityFood.CAPABILITY, null) &&
                              (this.stack.isEmpty || (this.stack.getItem() == stack.getItem() && (!this.stack.getHasSubtypes() || this.stack.getMetadata() == stack.getMetadata()) && ItemStack.areItemStackTagsEqual(this.stack, stack)))
	
	def handleGivenStack(stack: ItemStack): Unit = {
		updatePreservation()
	}
	
	def giveStack(player: EntityPlayer): Boolean = giveStack(player, 64)
	
	def giveStack(player: EntityPlayer, amount: Int): Boolean = {
		
		val stackToGive = stack.splitStack(amount)
		handleGivenStack(stackToGive)
		val ok: Boolean = player.addItemStackToInventory(stackToGive)
		if(!ok){
			this.stack.setCount(this.stack.getCount + stackToGive.getCount)
		}
		true
	}
	
	def handleTakenStack(stack: ItemStack): Unit = {
		updatePreservation()
	}
	
	def takeStack(player: EntityPlayer): Boolean = takeStack(player, 64)
	
	def takeStack(player: EntityPlayer, amount: Int): Boolean = {
		val stackToTake = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).splitStack(amount)
		if(this.stack.isEmpty) {
			this.stack = stackToTake
		}else{
			this.stack.setCount(this.stack.getCount() + stackToTake.getCount())
		}
		handleTakenStack(stackToTake)
		true
	}
	
	private var timeLeft = 0
	override def update() = {
		if(timeLeft <= 0){
			timeLeft = 8000
			updatePreservation()
		}
		else{
			timeLeft += 1
		}
	}
	
	def updatePreservation() = {
	
	}
	
	override def readFromNBT(nbt: NBTTagCompound) {
		stack = new ItemStack(nbt.getCompoundTag("stack"))
		super.readFromNBT(nbt)
	}
	
	override def writeToNBT(nbt: NBTTagCompound) = {
		try {
			nbt.setTag("stack", stack.serializeNBT())
		}finally{}
		
		super.writeToNBT(nbt)
	}
	
	override def getUpdatePacket() = {
		val nbt = new NBTTagCompound
		try{
			this.writeToNBT(nbt)
		}
		finally{
		
		}
		val metadata = getBlockMetadata()
		new SPacketUpdateTileEntity(this.pos, metadata, nbt)
	}
	
	override def onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
		try {
			this.readFromNBT(pkt.getNbtCompound)
		}finally{
		
		}
	}
	
	override def getUpdateTag = {
		val nbt = new NBTTagCompound
		this.writeToNBT(nbt)
		nbt
	}
	
	override def handleUpdateTag(tag: NBTTagCompound) {
		this.readFromNBT(tag)
	}
	
	override def getTileData = {
		val nbt = new NBTTagCompound
		this.writeToNBT(nbt)
		nbt
	}
	
}

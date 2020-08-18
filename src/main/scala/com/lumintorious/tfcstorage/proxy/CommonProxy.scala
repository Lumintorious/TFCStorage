package com.lumintorious.tfcstorage.proxy

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity

class CommonProxy {
	def registerItemModel(item: Item, meta: Int, id: String): Unit = {}
	
	def registerTESR[T <: TileEntity](cls: Class[T], renderer: TileEntitySpecialRenderer[T]): Unit = {}
}

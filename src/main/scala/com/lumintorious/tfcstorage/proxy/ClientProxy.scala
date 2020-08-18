package com.lumintorious.tfcstorage.proxy

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.client.registry.ClientRegistry

class ClientProxy extends CommonProxy {
	override def registerItemModel(item: Item, meta: Int, id: String): Unit = {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName, id))
	}
	
	override def registerTESR[T <: TileEntity](cls: Class[T], renderer: TileEntitySpecialRenderer[T]): Unit = {
		ClientRegistry.bindTileEntitySpecialRenderer(cls, renderer)
	}
}

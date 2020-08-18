package com.lumintorious.tfcstorage.registry

import com.lumintorious.tfcstorage.TFCStorage
import com.lumintorious.tfcstorage.itemblock.ItemBlockJar
import com.lumintorious.tfcstorage.tile.renderers.{HangerRenderer, JarRenderer, ShelfRenderer}
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher._

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Array(Side.CLIENT), modid = TFCStorage.MODID)
object ClientEventHandler {
	
	@SubscribeEvent
	def registerModels(event: ModelRegistryEvent) {
		
		HangerRenderer.initialize()
		JarRenderer.initialize()
		ShelfRenderer.initialize()
	}
}

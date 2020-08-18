package com.lumintorious.tfcstorage

import com.lumintorious.tfcstorage.TFCStorage._
import com.lumintorious.tfcstorage.proxy.CommonProxy
import com.lumintorious.tfcstorage.registry.Initializable
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common._

@Mod(modid = MODID, version = VERSION, modLanguage = "scala", useMetadata = true)
object TFCStorage {
	final val MODID = "tfcstorage"
	final val VERSION = "1.0"
	
	@SidedProxy(
		clientSide = "com.lumintorious.tfcstorage.proxy.ClientProxy",
		serverSide = "com.lumintorious.tfcstorage.proxy.CommonProxy")
	var proxy: CommonProxy = _
	
	@EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		Initializable.initialize()
	}
}


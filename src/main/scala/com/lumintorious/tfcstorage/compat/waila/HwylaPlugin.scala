package com.lumintorious.tfcstorage.compat.waila

import mcp.mobius.waila.api.{IWailaPlugin, IWailaRegistrar, WailaPlugin}
import net.dries007.tfc.compat.waila.interfaces.HwylaBlockInterface

@WailaPlugin
class HwylaPlugin extends IWailaPlugin {
	override def register(iWailaRegistrar: IWailaRegistrar) = {
		new HwylaBlockInterface(JarProvider).register(iWailaRegistrar)
		new HwylaBlockInterface(ShelfProvider).register(iWailaRegistrar)
		new HwylaBlockInterface(HangerProvider).register(iWailaRegistrar)
		new HwylaBlockInterface(GrainPileProvider).register(iWailaRegistrar)
	}
}

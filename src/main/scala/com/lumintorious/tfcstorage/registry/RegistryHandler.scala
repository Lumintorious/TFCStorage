package com.lumintorious.tfcstorage.registry

import net.minecraftforge.registries._


trait RegistryHandler[Type <: IForgeRegistryEntry[Type]] {
	def >>(registry: IForgeRegistry[Type])
}


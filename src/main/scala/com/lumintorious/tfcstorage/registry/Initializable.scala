package com.lumintorious.tfcstorage.registry

import com.lumintorious.tfcstorage.tile._

trait Initializable {
	def initialize(): Unit = {}
}

object Initializable extends Initializable {
	TileShelf.initialize()
	TileJar.initialize()
	TileHanger.initialize()
	TileGrainPile.initialize()
}

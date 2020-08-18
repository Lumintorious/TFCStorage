package com.lumintorious.tfcstorage.registry

import com.lumintorious.tfcstorage.TFCStorage
import com.lumintorious.tfcstorage.block.{BlockBase, BlockGrainPile, BlockHanger, BlockJar, BlockShelf}
import com.lumintorious.tfcstorage.itemblock.ItemBlockJar
import com.lumintorious.tfcstorage.tile.{TileFoodHolder, TileGrainPile}
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.EnumFaceDirection
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Mod.EventBusSubscriber(modid = TFCStorage.MODID)
object EventHandler {
	
	@SubscribeEvent
	def onItemUse(event: LivingEntityUseItemEvent): Unit = {
		val stack = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.MAINHAND)
		val name = stack.getItem().getRegistryName().getPath().split("/").last.toLowerCase()
		if(event.getEntityLiving().isSneaking() && name.contains("_grain"))
		{
			val rayTrace = event.getEntity().rayTrace(4D, 1F)
			if(rayTrace.sideHit != EnumFacing.UP) return
			if(rayTrace.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				val pos = rayTrace.getBlockPos().add(0, 1, 0)
				val world = event.getEntity().getEntityWorld()
				if(world.getBlockState(pos) == Blocks.AIR.getDefaultState())
				{
					val block = BlockGrainPile.all.getOrElse(name, return)
					val state = block.getDefaultState()
					world.setBlockState(pos, state)
					val tile = world.getTileEntity(pos) match {
						case tile: TileFoodHolder => tile
						case _ => return
					}
					event.getEntity() match {
						case player: EntityPlayer => tile.takeStack(player, 16)
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	def registerBlocks(event: RegistryEvent.Register[Block]): Unit = {
		val registry = event.getRegistry
		BlockShelf >> registry
		BlockJar >> registry
		BlockHanger >> registry
		BlockGrainPile >> registry
	}
	
	@SubscribeEvent
	def registerItems(event: RegistryEvent.Register[Item]): Unit = {
		for (entry <- BlockBase.itemBlocks) {
			TFCStorage.proxy.registerItemModel(entry, 0, "normal")
			event.getRegistry.register(entry)
		}
		ItemBlockJar >> event.getRegistry()
	}
}

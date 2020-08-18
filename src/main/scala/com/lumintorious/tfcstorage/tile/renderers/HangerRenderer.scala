package com.lumintorious.tfcstorage.tile.renderers

import com.lumintorious.tfcstorage.TFCStorage
import com.lumintorious.tfcstorage.registry.Initializable
import com.lumintorious.tfcstorage.tile.{TileHanger, TileJar}
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager.{disableLighting, popAttrib, popMatrix, pushAttrib, pushMatrix, rotate, scale, translate}
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.util.control.Breaks

@SideOnly(Side.CLIENT)
object HangerRenderer
extends TileEntitySpecialRenderer[TileHanger]
with Initializable
{
	TFCStorage.proxy.registerTESR(classOf[TileHanger], this)
	
	override def render(
		te: TileHanger,
		x: Double,
		y: Double,
		z: Double,
		partialTicks: Float,
		destroyStage: Int,
		alpha: Float
	): Unit = {
		val renderItem = Minecraft.getMinecraft().getRenderItem()
		val stack = Option(te.stack).getOrElse(return)
		val world = Option(te.getWorld()).getOrElse(return)
		val state = Option(world.getBlockState(te.getPos())).getOrElse(return)
		val timeD = (360.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL).asInstanceOf[Float]
		
		val totalDraws = 4
		val maxStackSize = stack.getItem().getItemStackLimit(stack)
		val filled = stack.getCount().asInstanceOf[Float] / maxStackSize.asInstanceOf[Float]
		// 1: 1 = 16
		// 2: 2 = 16, 1 = 8
		// 64: 4 = 1, 16 = 4, 32 = 8, 64 = 16
		
		pushMatrix()
		pushAttrib()
		disableLighting()
		translate(x, y, z)
		pushMatrix()
		translate(0.5, 0.5, 0.5)
		scale(0.45f, 0.45f, 0.45f)
		RenderHelper.enableStandardItemLighting()
		var currentDraws = 0
		Breaks.breakable{
			rotate(timeD, 0, 1, 0)
			translate(0, 1f, 0)
			for (i <- 0 until 4) {
				translate(0, -1f, 0)
				if(currentDraws >= filled * totalDraws) {
					Breaks.break()
				} else {
					currentDraws += 1
				}
				renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED)
			}
		}
		
		popMatrix()
		
		RenderHelper.disableStandardItemLighting()
		popAttrib()
		popMatrix()
	}
}

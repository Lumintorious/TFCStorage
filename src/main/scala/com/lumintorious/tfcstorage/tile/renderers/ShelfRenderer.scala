package com.lumintorious.tfcstorage.tile.renderers

import com.lumintorious.tfcstorage.TFCStorage
import com.lumintorious.tfcstorage.registry.Initializable
import com.lumintorious.tfcstorage.tile.TileShelf
import net.minecraft.block.BlockHorizontal
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager.{disableLighting, popAttrib, popMatrix, pushAttrib, pushMatrix, rotate, scale, translate}
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.util.control.Breaks

@SideOnly(Side.CLIENT)
object ShelfRenderer
extends TileEntitySpecialRenderer[TileShelf]
with Initializable
{
	TFCStorage.proxy.registerTESR(classOf[TileShelf], this)
	
	override def render(
		te: TileShelf,
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
		var angle = state.getValue(BlockHorizontal.FACING).getHorizontalAngle
		angle = angle match {
		case 90 => 270
		case 270 => 90
		case _ => angle
		}
		
		val totalDraws = 16
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
		rotate(angle, 0, 1, 0)
		RenderHelper.enableStandardItemLighting()
		var currentDraws = 0
		Breaks.breakable{
			for (i <- 0 until 4) {
				if(currentDraws >= filled * totalDraws) {
					Breaks.break()
				}
				pushMatrix()
				translate(0, 0, -0.9f)
				translate(if (i % 2 == 0) 0.45f else -0.45f, if(i < 2) 0.45f else -0.45f, 0)
				for(j <- 0 until 4) {
					if(currentDraws >= filled * totalDraws) {
						popMatrix()
						Breaks.break()
					} else {
						currentDraws += 1
					}
					translate(0, 0, 0.175f)
					renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED)
				}
				popMatrix()
			}
		}
		
		popMatrix()
		
		RenderHelper.disableStandardItemLighting()
		popAttrib()
		popMatrix()
	}
}

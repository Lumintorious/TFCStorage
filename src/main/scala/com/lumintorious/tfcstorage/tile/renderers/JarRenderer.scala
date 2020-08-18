package com.lumintorious.tfcstorage.tile.renderers

import com.lumintorious.tfcstorage.TFCStorage
import com.lumintorious.tfcstorage.registry.Initializable
import com.lumintorious.tfcstorage.tile.{TileJar, TileShelf}
import javax.vecmath
import net.dries007.tfc.client.FluidSpriteCache
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.opengl.GL11

@SideOnly(Side.CLIENT)
object JarRenderer
extends TileEntitySpecialRenderer[TileJar]
with Initializable
{
	TFCStorage.proxy.registerTESR(classOf[TileJar], this)
	
	override def render(
		te: TileJar,
		x: Double,
		y: Double,
		z: Double,
		partialTicks: Float,
		destroyStage: Int,
		alpha: Float
	): Unit = {
		val renderItem = Minecraft.getMinecraft().getRenderItem()
		val stack = Option(te.stack).orNull
		val fluidStack = Option(te.fluidTank.getFluid).orNull
		val world = Option(te.getWorld()).getOrElse(return)
		val state = Option(world.getBlockState(te.getPos())).getOrElse(return)
		val timeD = (360.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL).asInstanceOf[Float]
		
		GlStateManager.pushMatrix()
		GlStateManager.translate(x, y, z)
		if (stack != null) {
			GlStateManager.pushMatrix()
			GlStateManager.translate(0.5, 0.3, 0.5)
			GlStateManager.scale(0.50, 0.50, 0.50)
			GlStateManager.rotate(timeD, 0, 1, 0)
			GlStateManager.translate(0f, MathHelper.sin(timeD / 10) / 15, 0f)
			renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED)
			GlStateManager.popMatrix()
		}
		
		if (fluidStack != null)
		{
			val fluid = fluidStack.getFluid()
			
			val sprite = FluidSpriteCache.getStillSprite(fluid)
			
			GlStateManager.enableAlpha()
			GlStateManager.enableBlend()
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			
			val color = fluid.getColor()
			
			val r = ((color >> 16) & 0xFF) / 255F
			val g = ((color >> 8) & 0xFF) / 255F
			val b = (color & 0xFF) / 255F
			val a = ((color >> 24) & 0xFF) / 255F
			
			GlStateManager.color(r, g, b, a)
			
			rendererDispatcher.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
			
			val buffer = Tessellator.getInstance().getBuffer()
			val filled = fluidStack.amount.asInstanceOf[Double] / te.fluidTank.getCapacity.asInstanceOf[Double]
			
			
			val inc = 1D / 16D
			
			val p0 = new vecmath.Vector3d(4.1 * inc, 0.1 * inc, 4.1 * inc)
			val p1 = new vecmath.Vector3d(11.9 * inc, 8.9 * inc * filled, 11.9 * inc)
			
			//Top
			buffer.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX_NORMAL)
			
			buffer.pos(p0.x, p1.y, p0.z).tex(sprite.getMinU(), sprite.getMinV()).normal(0, 0, 1).endVertex()
			buffer.pos(p0.x, p1.y, p1.z).tex(sprite.getMinU(), sprite.getMaxV()).normal(0, 0, 1).endVertex()
			buffer.pos(p1.x, p1.y, p1.z).tex(sprite.getMaxU(), sprite.getMaxV()).normal(0, 0, 1).endVertex()
			buffer.pos(p1.x, p1.y, p0.z).tex(sprite.getMaxU(), sprite.getMinV()).normal(0, 0, 1).endVertex()
			
			Tessellator.getInstance().draw()
			
			//bottom
			buffer.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX_NORMAL)
			
			buffer.pos(p1.x, p0.y, p0.z).tex(sprite.getMinU(), sprite.getMinV()).normal(0, 0, 1).endVertex()
			buffer.pos(p1.x, p0.y, p1.z).tex(sprite.getMinU(), sprite.getMaxV()).normal(0, 0, 1).endVertex()
			buffer.pos(p0.x, p0.y, p1.z).tex(sprite.getMaxU(), sprite.getMaxV()).normal(0, 0, 1).endVertex()
			buffer.pos(p0.x, p0.y, p0.z).tex(sprite.getMaxU(), sprite.getMinV()).normal(0, 0, 1).endVertex()
			
			Tessellator.getInstance().draw();
			
			// Side1
			buffer.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX_NORMAL)
			
			
			buffer.pos(p1.x, p0.y, p1.z).tex(sprite.getMinU(), sprite.getMinV()).normal(0, 0, 1).endVertex()
			buffer.pos(p1.x, p1.y, p1.z).tex(sprite.getMinU(), sprite.getMaxV()).normal(0, 0, 1).endVertex()
			buffer.pos(p0.x, p1.y, p1.z).tex(sprite.getMaxU(), sprite.getMaxV()).normal(0, 0, 1).endVertex()
			buffer.pos(p0.x, p0.y, p1.z).tex(sprite.getMaxU(), sprite.getMinV()).normal(0, 0, 1).endVertex()
			Tessellator.getInstance().draw()
			
			// Side1
			buffer.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX_NORMAL)
			
			
			buffer.pos(p0.x, p0.y, p0.z).tex(sprite.getMinU(), sprite.getMinV()).normal(0, 0, 1).endVertex()
			buffer.pos(p0.x, p1.y, p0.z).tex(sprite.getMinU(), sprite.getMaxV()).normal(0, 0, 1).endVertex()
			buffer.pos(p1.x, p1.y, p0.z).tex(sprite.getMaxU(), sprite.getMaxV()).normal(0, 0, 1).endVertex()
			buffer.pos(p1.x, p0.y, p0.z).tex(sprite.getMaxU(), sprite.getMinV()).normal(0, 0, 1).endVertex()
			Tessellator.getInstance().draw()
			
			
			// Side1
			buffer.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX_NORMAL)
			
			
			buffer.pos(p1.x, p0.y, p1.z).tex(sprite.getMinU(), sprite.getMaxV()).normal(0, 0, 1).endVertex()
			buffer.pos(p1.x, p0.y, p0.z).tex(sprite.getMaxU(), sprite.getMaxV()).normal(0, 0, 1).endVertex()
			buffer.pos(p1.x, p1.y, p0.z).tex(sprite.getMaxU(), sprite.getMinV()).normal(0, 0, 1).endVertex()
			buffer.pos(p1.x, p1.y, p1.z).tex(sprite.getMinU(), sprite.getMinV()).normal(0, 0, 1).endVertex()
			Tessellator.getInstance().draw()
			
			// Side1
			buffer.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX_NORMAL)
			
			buffer.pos(p0.x, p0.y, p0.z).tex(sprite.getMinU(), sprite.getMaxV()).normal(0, 0, 1).endVertex()
			buffer.pos(p0.x, p0.y, p1.z).tex(sprite.getMaxU(), sprite.getMaxV()).normal(0, 0, 1).endVertex()
			buffer.pos(p0.x, p1.y, p1.z).tex(sprite.getMaxU(), sprite.getMinV()).normal(0, 0, 1).endVertex()
			buffer.pos(p0.x, p1.y, p0.z).tex(sprite.getMinU(), sprite.getMinV()).normal(0, 0, 1).endVertex()
			Tessellator.getInstance().draw()
			
			
		}
		GlStateManager.popMatrix();
	}
}

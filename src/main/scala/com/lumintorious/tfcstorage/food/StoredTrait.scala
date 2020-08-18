package com.lumintorious.tfcstorage.food

import com.lumintorious.tfcstorage.registry.Initializable
import net.dries007.tfc.api.capability.food.{CapabilityFood, FoodTrait}
import net.minecraft.item.ItemStack

object StoredTrait extends Enumeration with Initializable{
	val COOL = new StoredTrait("cool", 0.50F)
	val SHELVED = new StoredTrait("shelved", 0.80F)
	val HUNG = new StoredTrait("hung", 0.60F)
	val JAR = new StoredTrait("jar", 0.09F)
	val SHELTERED = new StoredTrait("sheltered", 0.40F)
}

class StoredTrait(name: String, modifier: Float)
extends FoodTrait(name, modifier)
{
	FoodTrait.getTraits().put(name, this)
	def >> (stack: ItemStack) = CapabilityFood.applyTrait(stack, this)
	def << (stack: ItemStack) = CapabilityFood.removeTrait(stack, this)
}

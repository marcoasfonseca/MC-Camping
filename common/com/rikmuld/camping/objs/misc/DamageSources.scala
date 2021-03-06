package com.rikmuld.camping.objs.misc

import net.minecraft.util.ChatComponentText
import net.minecraft.util.DamageSource
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.IChatComponent
import net.minecraft.entity.player.EntityPlayer
import java.util.Random

class DamageSourceBleeding(name: String) extends DamageSource(name) {
  override def getDeathMessage(entity: EntityLivingBase): IChatComponent = {
    val random = new Random()
    val num = random.nextInt(5)
    if (entity.isInstanceOf[EntityPlayer]) {
      if (num == 0) new ChatComponentText(entity.asInstanceOf[EntityPlayer].getName + " bled away")
      else if (num == 1) new ChatComponentText(entity.asInstanceOf[EntityPlayer].getName + " has run out of blood")
      else if (num == 2) new ChatComponentText(entity.asInstanceOf[EntityPlayer].getName + " bled out")
      else if (num == 3) new ChatComponentText(entity.asInstanceOf[EntityPlayer].getName + " bled to death")
      else if (num == 4) new ChatComponentText(entity.asInstanceOf[EntityPlayer].getName + " fizzled")
      else null
    } else null
  }
}
package com.rikmuld.camping.objs.block

import net.minecraft.block.properties.PropertyBool
import com.rikmuld.corerm.objs.WithInstable
import com.rikmuld.corerm.objs.RMBlockContainer
import com.rikmuld.corerm.objs.WithModel
import com.rikmuld.corerm.objs.ObjInfo
import com.rikmuld.corerm.objs.RMBlock
import com.rikmuld.corerm.CoreUtils._
import com.rikmuld.corerm.misc.WorldBlock._
import net.minecraft.block.state.IBlockState
import com.rikmuld.camping.objs.block.Logseat._
import net.minecraft.block.state.BlockState
import net.minecraft.world.IBlockAccess
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraft.entity.EntityLiving
import net.minecraft.item.ItemStack
import net.minecraft.entity.EntityLivingBase
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.Block
import net.minecraft.util.AxisAlignedBB
import net.minecraft.entity.Entity
import com.rikmuld.corerm.objs.RMTile
import com.rikmuld.camping.objs.tile.TileLogseat
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.Vec3
import com.rikmuld.corerm.objs.WithProperties
import com.rikmuld.corerm.objs.RMProp
import com.rikmuld.corerm.objs.RMBoolProp
import com.rikmuld.corerm.objs.RMIntProp
import net.minecraft.block.properties.IProperty
import com.rikmuld.corerm.misc.Rotation

object Logseat {
  val IS_TURNED = PropertyBool.create("turned")
  val LONG = PropertyInteger.create("long", 0, 3)
}

class Logseat(modId:String, info:ObjInfo) extends RMBlockContainer(modId, info) with WithModel with WithProperties {
  setDefaultState(getStateFromMeta(0))
  Rotation.addRotationBlock(this, Right((-2, -1, 1)))
  
  override def getProps = Array(new RMBoolProp(IS_TURNED, 0), new RMIntProp(LONG, 2, 1))
  override def createNewTileEntity(world:World, meta:Int):RMTile = new TileLogseat
  override def isWood(world: IBlockAccess, pos:BlockPos): Boolean = true
  override def onBlockPlacedBy(world:World, pos:BlockPos, state:IBlockState, entity:EntityLivingBase, stack:ItemStack) = updateState((world, pos), (entity.facing.getHorizontalIndex & 1) == 0)
  override def onNeighborBlockChange(world:World, pos:BlockPos, state:IBlockState, block:Block) = updateState((world, pos), getTurn((world, pos).state))
  def getLong(state:IBlockState):Int = state.getValue(LONG).asInstanceOf[Int]
  def getTurn(state:IBlockState):Boolean = state.getValue(IS_TURNED).asInstanceOf[Boolean]
  def updateState(bd:BlockData, turn:Boolean){
    var long = 0
    long = long.bitPut(0, if((turn==false&&(bd.north.block==this&&bd.north.state.getValue(IS_TURNED)==turn))||(turn==true&&(bd.east.block==this&&bd.east.state.getValue(IS_TURNED)==turn))) 1 else 0) 
    long = long.bitPut(1, if((turn==false&&(bd.south.block==this&&bd.south.state.getValue(IS_TURNED)==turn))||(turn==true&&(bd.west.block==this&&bd.west.state.getValue(IS_TURNED)==turn))) 1 else 0)
    bd.setState(blockState.getBaseState.withProperty(IS_TURNED, turn).withProperty(LONG, long))
  }
  override def setBlockBoundsBasedOnState(world:IBlockAccess, pos:BlockPos) {
    val long = getLong(world.getBlockState(pos))
    if(getTurn(world.getBlockState(pos))) setBlockBounds((if(!((long & 2) > 0)) 2f/16f else 0), 0, 5f/16f, 1-(if(!((long & 1) > 0)) 2f/16f else 0), 6f/16f, 1-5f/16f)
    else setBlockBounds(5f/16f, 0, (if(!((long & 1) > 0)) 2f/16f else 0), 1-5f/16f, 6f/16f, 1-(if(!((long & 2) > 0)) 2f/16f else 0))
  }
  override def addCollisionBoxesToList(world: World, pos:BlockPos, state:IBlockState, axisAligned: AxisAlignedBB, list: java.util.List[_], entity: Entity) {
    setBlockBoundsBasedOnState(world, pos)
    super.addCollisionBoxesToList(world, pos, state, axisAligned, list, entity)
  }
  override def onBlockActivated(world: World, pos:BlockPos, state:IBlockState, player: EntityPlayer, side: EnumFacing, xHit: Float, yHit: Float, zHit: Float): Boolean = {
    if (!player.isRiding && (new Vec3(pos.getX + 0.5F, pos.getY + 0.5F, pos.getZ + 0.5F).distanceTo(new Vec3(player.posX, player.posY, player.posZ)) <= 2.5F)) {
      world.getTileEntity(pos).asInstanceOf[TileLogseat].mountable.interactFirst(player)
    }
    true
  }
}
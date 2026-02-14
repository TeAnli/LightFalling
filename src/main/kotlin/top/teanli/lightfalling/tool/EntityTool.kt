package top.teanli.lightfalling.tool

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3

/**
 * Utility object for entity-related operations
 */
object EntityTool {
    private val mc: Minecraft = Minecraft.getInstance()

    /**
     * Checks if an entity is a hostile mob
     */
    fun isHostileMob(entity: LivingEntity): Boolean {
        val className = entity.javaClass.simpleName
        return className.contains("Zombie") ||
               className.contains("Skeleton") ||
               className.contains("Creeper") ||
               className.contains("Spider") ||
               className.contains("Enderman") ||
               className.contains("Witch") ||
               className.contains("Blaze") ||
               className.contains("Ghast") ||
               className.contains("Slime") ||
               className.contains("Phantom") ||
               className.contains("Pillager") ||
               className.contains("Vindicator") ||
               className.contains("Evoker") ||
               className.contains("Ravager") ||
               className.contains("Wither") ||
               className.contains("Dragon")
    }

    /**
     * Checks if an entity is a passive mob
     */
    fun isPassiveMob(entity: LivingEntity): Boolean {
        val className = entity.javaClass.simpleName
        return className.contains("Cow") ||
               className.contains("Pig") ||
               className.contains("Sheep") ||
               className.contains("Chicken") ||
               className.contains("Horse") ||
               className.contains("Donkey") ||
               className.contains("Llama") ||
               className.contains("Cat") ||
               className.contains("Wolf") ||
               className.contains("Rabbit") ||
               className.contains("Villager") ||
               className.contains("IronGolem")
    }

    /**
     * Checks if a player can see an entity (line of sight check)
     */
    fun canSeeEntity(player: Player, entity: Entity): Boolean {
        val level = mc.level ?: return false
        val eyePos = player.getEyePosition(1.0f)
        val entityPos = entity.position().add(0.0, entity.bbHeight / 2.0, 0.0)
        
        val hitResult = level.clip(
            ClipContext(
                eyePos,
                entityPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
            )
        )
        
        return hitResult.type == HitResult.Type.MISS ||
               hitResult.location.distanceTo(eyePos) >= entityPos.distanceTo(eyePos)
    }

    /**
     * Gets entities within range of a position
     */
    fun getEntitiesInRange(centerPos: Vec3, range: Double): List<Entity> {
        val level = mc.level ?: return emptyList()
        return level.entitiesForRendering().filter { entity ->
            entity.position().distanceTo(centerPos) <= range
        }
    }
}


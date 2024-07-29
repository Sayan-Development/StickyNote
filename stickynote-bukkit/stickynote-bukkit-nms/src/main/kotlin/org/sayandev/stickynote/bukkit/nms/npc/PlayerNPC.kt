package org.sayandev.stickynote.bukkit.nms.npc

import com.mojang.authlib.GameProfile
import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer
import net.kyori.adventure.text.Component
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.sayandev.stickynote.bukkit.nms.NMSUtils
import org.sayandev.stickynote.bukkit.nms.NMSUtils.sendPacket
import org.sayandev.stickynote.bukkit.nms.PacketUtils
import org.sayandev.stickynote.bukkit.nms.enum.CollisionRule
import org.sayandev.stickynote.bukkit.nms.enum.ModelPart
import org.sayandev.stickynote.bukkit.nms.enum.NameTagVisibility
import org.sayandev.stickynote.bukkit.nms.enum.PlayerInfoAction
import org.sayandev.stickynote.bukkit.nms.skin.Skin
import org.sayandev.stickynote.bukkit.utils.ServerVersion
import org.sayandev.stickynote.bukkit.nms.accessors.*
import sun.reflect.ReflectionFactory
import java.lang.reflect.Constructor
import java.util.*

class PlayerNPC(
    val name: String,
    private val location: Location,
    skin: Skin? = null
): HumanEntityNPC(createServerPlayerObject(name, location.world), location, NPCType.PLAYER) {

    constructor(name: String, location: Location): this(name, location, null)

    init {
        skin?.apply(this)
    }

    var skin = skin; private set
    val tabName16 = "[NPC] " + UUID.randomUUID().toString().replace("-", "").substring(0, 10)

    var collision = true
        set(value) {
            field = value
            getViewers().sendPacket(createModifyPlayerTeamPacket())
        }

    var nameTagVisibility = NameTagVisibility.ALWAYS
        set(value) {
            field = value
            getViewers().sendPacket(createModifyPlayerTeamPacket())
        }

    /**
     * Sets the skin of the NPC and applies it to the NPC.
     * @param skin The skin to set.
     * @param modelParts The model parts to apply the skin to. Default is all model parts.
     * @see Skin
     * @see [org.sayandev.stickynote.bukkit.nms.skin.SkinUtils]
     */
    fun setSkin(skin: Skin, vararg modelParts: ModelPart = ModelPart.entries.toTypedArray()) {
        this.skin = skin
        skin.apply(this)
        setModelParts(*modelParts)
    }

    /**
     * Sets the model parts of the NPC.
     * @param modelParts The model parts to set.
     * @see ModelPart
     */
    fun setModelParts(vararg modelParts: ModelPart) {
        SynchedEntityDataAccessor.METHOD_SET!!.invoke(
            getEntityData(),
            PlayerAccessor.FIELD_DATA_PLAYER_MODE_CUSTOMISATION!!,
            ModelPart.getMasks(*modelParts)
        )
        sendEntityData()
    }

    fun setTabList(component: Component?) {
        getViewers().sendPacket(PacketUtils.getPlayerInfoPacket(entity, PlayerInfoAction.REMOVE_PLAYER))
        if (component != null) {
            listNameField[entity] = MinecraftComponentSerializer.get().serialize(component)
            getViewers().sendPacket(PacketUtils.getPlayerInfoPacket(entity, PlayerInfoAction.ADD_PLAYER))
        }
    }

    override fun addViewer(viewer: Player) {
        viewer.sendPacket(
            PacketUtils.getPlayerInfoPacket(entity, PlayerInfoAction.ADD_PLAYER),
            if ((ServerVersion.supports(21) || (ServerVersion.version() == 20 && ServerVersion.patchNumber() >= 2)))
                PacketUtils.getAddEntityPacket(entity)
            else
                PacketUtils.getAddPlayerPacket(entity),
            PacketUtils.getHeadRotatePacket(entity, location.yaw),
            createPlayerTeamPacket()
        )
    }

    override fun removeViewer(viewer: Player) {
        viewer.sendPacket(
            PacketUtils.getPlayerInfoPacket(entity, PlayerInfoAction.REMOVE_PLAYER),
            PacketUtils.getRemoveEntitiesPacket(entityId)
        )
    }

    private fun createPlayerTeamPacket(): Any {
        return PacketUtils.getTeamCreatePacket(
            tabName16,
            Component.empty(),
            Component.empty(),
            nameTagVisibility,
            if (collision) CollisionRule.ALWAYS else CollisionRule.NEVER,
            ChatColor.BLUE,
            listOf(name),
            false
        )
    }

    private fun createModifyPlayerTeamPacket(): Any {
        return PacketUtils.getTeamModifyPacket(
            tabName16,
            Component.empty(),
            Component.empty(),
            nameTagVisibility,
            if (collision) CollisionRule.ALWAYS else CollisionRule.NEVER,
            ChatColor.BLUE,
            false
        )
    }

    companion object {
        private val listNameField = ServerPlayerAccessor.TYPE!!.getField("listName")

        @JvmStatic
        fun createServerPlayerObject(name: String, world: World): Any {
            val serverLevel: Any = NMSUtils.getServerLevel(world)
            val profile = GameProfile(UUID.randomUUID(), name)
            val serverPlayer: Any
            if (ServerVersion.supports(21) || (ServerVersion.version() == 20 && ServerVersion.patchNumber() >= 2)) {
                serverPlayer = ServerPlayerAccessor.CONSTRUCTOR_3!!.newInstance(
                    NMSUtils.getDedicatedServer(),
                    serverLevel,
                    profile,
                    ClientInformationAccessor.METHOD_CREATE_DEFAULT!!.invoke(null)
                )

                val reflectionFactory = ReflectionFactory.getReflectionFactory()
                val objectConstructor: Constructor<*> = Any::class.java.getDeclaredConstructor()
                val constructor = reflectionFactory.newConstructorForSerialization(
                    ServerGamePacketListenerImplAccessor.TYPE!!,
                    objectConstructor
                )
                val instance: Any = ServerGamePacketListenerImplAccessor.TYPE!!.cast(constructor.newInstance())

                ServerPlayerAccessor.FIELD_CONNECTION!!.set(
                    serverPlayer, instance
                )
            } else if (ServerVersion.version() == 19 && ServerVersion.patchNumber() < 2) {
                serverPlayer = ServerPlayerAccessor.CONSTRUCTOR_2!!.newInstance(
                    NMSUtils.getDedicatedServer(),
                    serverLevel,
                    profile,
                    null
                )
            } else if (ServerVersion.supports(17)) {
                serverPlayer = ServerPlayerAccessor.CONSTRUCTOR_0!!.newInstance(
                    NMSUtils.getDedicatedServer(),
                    serverLevel,
                    profile
                )
            } else {
                serverPlayer = ServerPlayerAccessor.CONSTRUCTOR_1!!.newInstance(
                    NMSUtils.getDedicatedServer(),
                    serverLevel,
                    profile,
                    if (ServerVersion.supports(14)) ServerPlayerGameModeAccessor.CONSTRUCTOR_0!!
                        .newInstance(serverLevel) else ServerPlayerGameModeAccessor.CONSTRUCTOR_1!!
                        .newInstance(serverLevel)
                )
            }

            return serverPlayer
        }
    }

}
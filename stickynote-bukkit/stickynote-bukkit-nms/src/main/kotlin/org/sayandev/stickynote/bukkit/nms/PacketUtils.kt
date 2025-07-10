package org.sayandev.stickynote.bukkit.nms

import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer
import net.kyori.adventure.text.Component
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.sayandev.stickynote.bukkit.nms.accessors.*
import org.sayandev.stickynote.bukkit.nms.enum.*
import org.sayandev.stickynote.bukkit.utils.MathUtils
import org.sayandev.stickynote.bukkit.utils.ServerVersion
import org.sayandev.stickynote.bukkit.warn
import org.sayandev.stickynote.core.math.Vector3
import java.lang.reflect.Array
import java.util.*
import kotlin.reflect.full.memberProperties

object PacketUtils {

    @JvmStatic
    fun getOpenScreenPacket(containerId: Int, inventorySize: Int, component: Component): Any {
        return if (ServerVersion.supports(13)) {
            ClientboundOpenScreenPacketAccessor.CONSTRUCTOR_0!!.newInstance(
                containerId,
                MenuTypeAccessor::class.memberProperties.find { it.name == "GENERIC_9X" + (inventorySize / 9) }!!.getter.call(MenuTypeAccessor),
                MinecraftComponentSerializer.get().serialize(component)
            )
        } else {
            ClientboundOpenScreenPacketAccessor.CONSTRUCTOR_1!!
                .newInstance(containerId, "minecraft:chest", component, inventorySize)
        }
    }

    @JvmStatic
    fun getRespawnPacket(serverLevel: Any, newGameMode: GameMode, oldGameMode: GameMode, isFlat: Boolean): Any {
        val nmsNewGameMode = GameTypeAccessor::class.memberProperties.find { it.name == "FIELD_${newGameMode.name.uppercase()}" }!!.getter.call(GameTypeAccessor)!!
        val nmsOldGameMode = GameTypeAccessor::class.memberProperties.find { it.name == "FIELD_${oldGameMode.name.uppercase()}" }!!.getter.call(GameTypeAccessor)!!
        return if (ServerVersion.supports(19)) {
            ClientboundRespawnPacketAccessor.CONSTRUCTOR_1!!.newInstance(
                LevelAccessor.METHOD_DIMENSION_TYPE_ID!!.invoke(serverLevel),
                LevelAccessor.METHOD_DIMENSION!!.invoke(serverLevel),
                ServerLevelAccessor.METHOD_GET_SEED!!.invoke(serverLevel),
                nmsNewGameMode,
                nmsOldGameMode,
                false,
                isFlat,
                0.toByte(),
                Optional.empty<Any>()
            )
        } else {
            ClientboundRespawnPacketAccessor.CONSTRUCTOR_0!!.newInstance(
                LevelAccessor.METHOD_DIMENSION_TYPE_ID!!.invoke(serverLevel),
                LevelAccessor.METHOD_DIMENSION!!.invoke(serverLevel),
                ServerLevelAccessor.METHOD_GET_SEED!!.invoke(serverLevel),
                nmsNewGameMode,
                nmsOldGameMode,
                false,
                isFlat
            )
        }
    }

    @JvmStatic
    fun getPlayerInfoPacket(serverPlayer: Any, action: PlayerInfoAction): Any {
        if (ServerVersion.isAtLeast(19, 3)) {
            return if (action == PlayerInfoAction.REMOVE_PLAYER) {
                ClientboundPlayerInfoRemovePacketAccessor.CONSTRUCTOR_0!!
                    .newInstance(listOf(EntityAccessor.METHOD_GET_UUID!!.invoke(serverPlayer)))
            } else {
                ClientboundPlayerInfoUpdatePacketAccessor.CONSTRUCTOR_0!!.newInstance(
                    action.nmsObject!!,
                    serverPlayer
                )
            }
        } else {
            val serverPlayerArray = Array.newInstance(ServerPlayerAccessor.TYPE, 1)
            Array.set(serverPlayerArray, 0, serverPlayer)

            return ClientboundPlayerInfoUpdatePacketAccessor.CONSTRUCTOR_1!!
                .newInstance(action.nmsObject, serverPlayerArray)
        }
    }

    @JvmStatic
    fun getUpdateGameModePacket(serverPlayer: Any, gameMode: GameMode): Any {
        val profile: Any = PlayerAccessor.METHOD_GET_GAME_PROFILE!!.invoke(serverPlayer)
        val infoPacket = getPlayerInfoPacket(serverPlayer, PlayerInfoAction.UPDATE_GAME_MODE)

        val entries: MutableList<Any> = ArrayList<Any>(
            ClientboundPlayerInfoUpdatePacketAccessor.METHOD_ENTRIES!!.invoke(infoPacket) as List<*>
        )
        val ping = NMSUtils.getPing(serverPlayer)

        entries.add(
            if (ServerVersion.isAtLeast(21, 4)) {
                ClientboundPlayerInfoUpdatePacket_EntryAccessor.CONSTRUCTOR_2!!.newInstance(
                    EntityAccessor.METHOD_GET_UUID!!.invoke(serverPlayer),
                    profile,
                    true,
                    ping,
                    GameTypeAccessor.METHOD_BY_NAME!!.invoke(null, gameMode.name.lowercase()),
                    null,
                    false,
                    1,
                    null
                )
            } else if (ServerVersion.isAtLeast(21, 3)) {
                ClientboundPlayerInfoUpdatePacket_EntryAccessor.CONSTRUCTOR_1!!.newInstance(
                    EntityAccessor.METHOD_GET_UUID!!.invoke(serverPlayer),
                    profile,
                    true,
                    ping,
                    GameTypeAccessor.METHOD_BY_NAME!!.invoke(null, gameMode.name.lowercase()),
                    null,
                    1,
                    null
                )
            } else {
                ClientboundPlayerInfoUpdatePacket_EntryAccessor.CONSTRUCTOR_0!!.newInstance(
                    EntityAccessor.METHOD_GET_UUID!!.invoke(serverPlayer),
                    profile,
                    true,
                    ping,
                    GameTypeAccessor.METHOD_BY_NAME!!.invoke(null, gameMode.name.lowercase()),
                    null,
                    null
                )
            }
        )
        ClientboundPlayerInfoUpdatePacketAccessor.FIELD_ENTRIES!!.set(
            infoPacket,
            entries
        )

        return infoPacket
    }

    @JvmStatic
    fun getMobEffectPacket(effect: PotionEffect): Any {
        val mobEffect = getMobEffectByEffectType(effect.type)
        val effectConstructor =
            if (ServerVersion.isAtLeast(20, 5))
                MobEffectInstanceAccessor.CONSTRUCTOR_1!!
            else
                MobEffectInstanceAccessor.CONSTRUCTOR_0!!
        val effectPacket = effectConstructor.newInstance(
            mobEffect,
            effect.duration,
            effect.amplifier,
            effect.isAmbient,
            effect.hasParticles(),
            effect.hasIcon()
        )
        return effectPacket
    }

    @JvmStatic
    fun getMobEffectByEffectType(effect: PotionEffectType): Any {
        return if (ServerVersion.isAtLeast(20, 2)) {
            val memberProperty = if (ServerVersion.isAtLeast(20, 5)) {
                MobEffectsAccessor::class.memberProperties.find { it.name == "FIELD_${effect.name}_1" }!!
            } else {
                MobEffectsAccessor::class.memberProperties.find { it.name == "FIELD_${effect.name}" }!!
            }
            memberProperty.getter.call(MobEffectsAccessor)!!
        } else {
            //TODO: replace with key()
            @Suppress("removal")
            MobEffectAccessor.METHOD_BY_ID!!.invoke(null, effect.id)
        }
    }

    @JvmStatic
    fun getUpdateMobEffectPacket(player: Player, effect: PotionEffect): Any {
        val effectPacket = getMobEffectPacket(effect)
        return if ((ServerVersion.supports(20) && ServerVersion.patchNumber() >= 5) || ServerVersion.supports(21)) {
            ClientboundUpdateMobEffectPacketAccessor.CONSTRUCTOR_1!!.newInstance(player.entityId, effectPacket, true)
        } else {
            ClientboundUpdateMobEffectPacketAccessor.CONSTRUCTOR_0!!.newInstance(player.entityId, effectPacket)
        }
    }

    @JvmStatic
    fun getRemoveMobEffectPacket(player: Player, effect: PotionEffectType): Any {
        val effectConstructor = if (ServerVersion.isAtLeast(21, 5))
            ClientboundRemoveMobEffectPacketAccessor.CONSTRUCTOR_1!!
        else
            ClientboundRemoveMobEffectPacketAccessor.CONSTRUCTOR_0!!
        return effectConstructor.newInstance(player.entityId, getMobEffectByEffectType(effect))
    }

    @JvmStatic
    fun getAddPlayerPacket(player: Any): Any {
        return ClientboundAddPlayerPacketAccessor.CONSTRUCTOR_0!!.newInstance(player)
    }

    @JvmStatic
    fun getAddEntityPacket(entity: Any, data: Int): Any {
        return if (ServerVersion.supports(21)) {
            ClientboundAddEntityPacketAccessor.CONSTRUCTOR_4!!.newInstance(entity, NMSUtils.getServerEntityFromNmsEntity(entity), data)
        } else if (ServerVersion.supports(13)) {
            ClientboundAddEntityPacketAccessor.CONSTRUCTOR_1!!.newInstance(entity, data)
        } else {
            ClientboundAddMobPacketAccessor.CONSTRUCTOR_0!!.newInstance(entity)
        }
    }

    @JvmStatic
    fun getAddEntityPacket(entity: Any): Any {
        return getAddEntityPacket(entity, 0)
    }

    @JvmStatic
    fun getHeadRotatePacket(entity: Any, yaw: Float): Any {
        return ClientboundRotateHeadPacketAccessor.CONSTRUCTOR_0!!.newInstance(entity, MathUtils.getAngle(yaw))
    }

    @JvmStatic
    fun getRemoveEntitiesPacket(vararg ids: Int): Any {
        val idArray = Array.newInstance(Int::class.javaPrimitiveType, ids.size)
        for (i in ids.indices) {
            Array.set(idArray, i, ids[i])
        }

        return ClientboundRemoveEntitiesPacketAccessor.CONSTRUCTOR_0!!.newInstance(idArray)
    }

    @JvmStatic
    fun getEntityRotPacket(id: Int, yaw: Float, pitch: Float): Any {
        return ClientboundMoveEntityPacket_RotAccessor.CONSTRUCTOR_0!!
            .newInstance(id, MathUtils.getAngle(yaw), MathUtils.getAngle(pitch), true)
    }

    @JvmStatic
    fun getEntityPosPacket(id: Int, x: Double, y: Double, z: Double): Any {
        return if (ServerVersion.supports(13)) {
            ClientboundMoveEntityPacket_PosAccessor.CONSTRUCTOR_0!!.newInstance(
                id,
                (x * 4096).toInt().toShort(), (y * 4096).toInt().toShort(), (z * 4096).toInt().toShort(), true
            )
        } else {
            ClientboundMoveEntityPacket_PosAccessor.CONSTRUCTOR_1!!.newInstance(
                id,
                (x * 4096).toLong(), (y * 4096).toLong(), (z * 4096).toLong(), true
            )
        }
    }

    @JvmStatic
    fun getEntityPosRotPacket(
        id: Int,
        x: Double,
        y: Double,
        z: Double,
        yaw: Float,
        pitch: Float,
        onGround: Boolean
    ): Any {
        return if (ServerVersion.supports(13)) {
            ClientboundMoveEntityPacket_PosRotAccessor.CONSTRUCTOR_0!!.newInstance(
                id,
                (x * 4096).toInt().toShort(), (y * 4096).toInt().toShort(), (z * 4096).toInt().toShort(),
                MathUtils.getAngle(yaw), MathUtils.getAngle(pitch), onGround
            )
        } else {
            ClientboundMoveEntityPacket_PosRotAccessor.CONSTRUCTOR_1!!.newInstance(
                id,
                (x * 4096).toLong(), (y * 4096).toLong(), (z * 4096).toLong(),
                MathUtils.getAngle(yaw), MathUtils.getAngle(pitch), onGround
            )
        }
    }

    @JvmStatic
    fun getTeleportEntityPacket(entity: Any): Any {
        return ClientboundTeleportEntityPacketAccessor.CONSTRUCTOR_0!!.newInstance(entity)
    }

    @JvmStatic
    fun getEntityVelocityPacket(id: Int, x: Double, y: Double, z: Double): Any {
        return if (ServerVersion.supports(14)) {
            ClientboundSetEntityMotionPacketAccessor.CONSTRUCTOR_0!!
                .newInstance(id, Vec3Accessor.CONSTRUCTOR_0!!.newInstance(x, y, z))
        } else {
            ClientboundSetEntityMotionPacketAccessor.CONSTRUCTOR_1!!.newInstance(id, x, y, z)
        }
    }

    @JvmStatic
    fun getAnimatePacket(entity: Any, action: Int): Any {
        return ClientboundAnimatePacketAccessor.CONSTRUCTOR_0!!.newInstance(entity, action)
    }

    @JvmStatic
    fun getBlockDestructionPacket(location: Vector3, stage: Int): Any {
        return ClientboundBlockDestructionPacketAccessor.CONSTRUCTOR_0!!.newInstance(
            location.hashCode(),
            BlockPosAccessor.CONSTRUCTOR_0!!.newInstance(location.blockX, location.blockY, location.blockZ),
            stage
        )
    }

    @JvmStatic
    fun getEntityEquipmentPacket(id: Int, equipmentSlot: EquipmentSlot, nmsItem: Any?): Any {
        return if (ServerVersion.supports(13)) {
            val pair = Class.forName("com.mojang.datafixers.util.Pair").getConstructor(
                Any::class.java,
                Any::class.java
            ).newInstance(equipmentSlot.nmsSlot(), nmsItem)
            val pairList: MutableList<Any> = ArrayList()
            pairList.add(pair)

            ClientboundSetEquipmentPacketAccessor.CONSTRUCTOR_0!!.newInstance(id, pairList)
        } else if (ServerVersion.supports(9)) {
            ClientboundSetEquipmentPacketAccessor.CONSTRUCTOR_1!!
                .newInstance(id, equipmentSlot.nmsSlot(), nmsItem)
        } else {
            ClientboundSetEquipmentPacketAccessor.CONSTRUCTOR_2!!
                .newInstance(id, equipmentSlot.legacyNmsSlot(), nmsItem)
        }
    }

    @JvmStatic
    fun getCollectItemPacket(id: Int, collectorId: Int, itemAmount: Int): Any {
        return if (ServerVersion.supports(9)) {
            ClientboundTakeItemEntityPacketAccessor.CONSTRUCTOR_0!!.newInstance(id, collectorId, itemAmount)
        } else {
            ClientboundTakeItemEntityPacketAccessor.CONSTRUCTOR_1!!.newInstance(id, collectorId)
        }
    }

    @JvmStatic
    fun getBlockEventPacket(location: Vector3, blockMaterial: Material, actionId: Int, actionParam: Int): Any {
        return ClientboundBlockEventPacketAccessor.CONSTRUCTOR_0!!.newInstance(
            BlockPosAccessor.CONSTRUCTOR_0!!.newInstance(location.blockX, location.blockY, location.blockZ),
            BlocksAccessor.TYPE!!.javaClass.getField(blockMaterial.toString().uppercase(Locale.getDefault())).get(null),
            actionId, actionParam
        )
    }

    /**
     * @apiNote > 1.9
     */
    @JvmStatic
    fun getEntityPassengersPacket(entity: Any, vararg passengerIds: Int): Any {
        val packet: Any = ClientboundSetPassengersPacketAccessor.CONSTRUCTOR_0!!.newInstance(entity)
        ClientboundSetPassengersPacketAccessor.FIELD_PASSENGERS!!.set(packet, passengerIds)

        return packet
    }

    @JvmStatic
    fun getContainerSetContentPacket(
        containerId: Int,
        stateId: Int,
        items: List<ItemStack>,
        carriedItem: ItemStack
    ): Any {
        val nmsItems: MutableList<Any> = ArrayList()
        for (item in items) {
            nmsItems.add(NMSUtils.getNmsItemStack(item))
        }
        val nonNullList: Any =
            NonNullListAccessor.CONSTRUCTOR_0!!.newInstance(nmsItems, NMSUtils.getNmsEmptyItemStack())
        return ClientboundContainerSetContentPacketAccessor.CONSTRUCTOR_0!!.newInstance(
            containerId,
            stateId,
            nonNullList,
            NMSUtils.getNmsItemStack(carriedItem)
        )
    }

    @JvmStatic
    fun getChatPacket(message: Component, type: ChatType, sender: UUID): Any {
        val nmsComponent = MinecraftComponentSerializer.get().serialize(message)
        return if (ServerVersion.supports(16)) {
            ClientboundChatPacketAccessor.CONSTRUCTOR_0!!.newInstance(
                nmsComponent,
                type.nmsOject,
                sender
            )
        } else {
            if (ServerVersion.supports(12)) {
                ClientboundChatPacketAccessor.CONSTRUCTOR_1!!.newInstance(nmsComponent, type.nmsOject)
            } else {
                ClientboundChatPacketAccessor.CONSTRUCTOR_2!!.newInstance(nmsComponent, type.legacyId)
            }
        }
    }

    /**
     * @apiNote Chat preview was added in 1.19 and removed in 1.19.3
     */
    @JvmStatic
    fun getChatPreviewPacket(queryId: Int, message: Component): Any {
        return ClientboundChatPreviewPacketAccessor.CONSTRUCTOR_0!!.newInstance(
            queryId, MinecraftComponentSerializer.get().serialize(message)
        )
    }

    /**
     * @apiNote Chat preview was added in 1.19 and removed in 1.19.3
     */
    @JvmStatic
    fun getSetDisplayChatPreviewPacket(enabled: Boolean): Any {
        return ClientboundSetDisplayChatPreviewPacketAccessor.CONSTRUCTOR_0!!.newInstance(enabled)
    }

    @JvmStatic
    fun getPlayerTeamPacket(
        name: String,
        playerPrefix: Component,
        playerSuffix: Component,
        nameTagVisibility: NameTagVisibility,
        collisionRule: CollisionRule,
        color: ChatColor,
        players: Collection<String>,
        canSeeFriendlyInvisible: Boolean,
        method: Int
    ): Any {
        val packet: Any
        if (ServerVersion.supports(17)) {
            val parameters: Optional<Any>
            if (method == 0 || method == 2) {
                val playerTeam: Any = PlayerTeamAccessor.CONSTRUCTOR_0!!.newInstance(null, name)
                PlayerTeamAccessor.FIELD_PLAYER_PREFIX!!.set(playerTeam, MinecraftComponentSerializer.get().serialize(playerPrefix))
                PlayerTeamAccessor.FIELD_PLAYER_SUFFIX!!.set(playerTeam, MinecraftComponentSerializer.get().serialize(playerSuffix))
                PlayerTeamAccessor.FIELD_NAME_TAG_VISIBILITY!!.set(playerTeam, nameTagVisibility.modernNmsObject)
                PlayerTeamAccessor.FIELD_COLLISION_RULE!!.set(playerTeam, collisionRule.modernNmsObject)
                PlayerTeamAccessor.FIELD_COLOR!!.set(playerTeam,  ChatFormattingAccessor::class.memberProperties.find { it.name == "FIELD_${color.name.uppercase()}" }!!.getter.call(ChatFormattingAccessor)!!)
                (PlayerTeamAccessor.FIELD_PLAYERS!!.get(playerTeam) as MutableSet<String>).addAll(players)
                PlayerTeamAccessor.FIELD_SEE_FRIENDLY_INVISIBLES!!.set(playerTeam, canSeeFriendlyInvisible)

                parameters = Optional.of(ClientboundSetPlayerTeamPacket_ParametersAccessor.CONSTRUCTOR_0!!.newInstance(playerTeam))
            } else {
                parameters = Optional.empty()
            }

            packet = ClientboundSetPlayerTeamPacketAccessor.CONSTRUCTOR_0!!.newInstance(
                name,
                method,
                parameters,
                players
            )
        } else {
            packet = ClientboundSetPlayerTeamPacketAccessor.CONSTRUCTOR_1!!.newInstance()

            ClientboundSetPlayerTeamPacketAccessor.FIELD_NAME!!.set(packet, name)
            ClientboundSetPlayerTeamPacketAccessor.FIELD_NAMETAG_VISIBILITY!!.set(packet, nameTagVisibility.nmsName)
            if (ServerVersion.supports(13)) {
                ClientboundSetPlayerTeamPacketAccessor.FIELD_COLOR!!.set(packet, ChatFormattingAccessor::class.memberProperties.find { it.name == "FIELD_${color.name.uppercase()}" }!!.getter.call(ChatFormattingAccessor))
            }
            ClientboundSetPlayerTeamPacketAccessor.FIELD_PLAYERS!!.set(packet, players)
            ClientboundSetPlayerTeamPacketAccessor.FIELD_METHOD!!.set(packet, method)
            var options = 0
            if (canSeeFriendlyInvisible) {
                options = options or 2
            }
            ClientboundSetPlayerTeamPacketAccessor.FIELD_OPTIONS!!.set(packet, options)
            if (ServerVersion.supports(13)) {
                ClientboundSetPlayerTeamPacketAccessor.FIELD_DISPLAY_NAME!!.set(packet, MinecraftComponentSerializer.get().serialize(Component.empty()))
                ClientboundSetPlayerTeamPacketAccessor.FIELD_PLAYER_PREFIX!!.set(packet, MinecraftComponentSerializer.get().serialize(playerPrefix))
                ClientboundSetPlayerTeamPacketAccessor.FIELD_PLAYER_SUFFIX!!.set(packet, MinecraftComponentSerializer.get().serialize(playerSuffix))
            }
            if (ServerVersion.supports(9)) {
                ClientboundSetPlayerTeamPacketAccessor.FIELD_COLLISION_RULE!!.set(packet, collisionRule.nmsName)
            }
        }

        return packet
    }

    @JvmStatic
    fun getTeamCreatePacket(
        name: String,
        playerPrefix: Component,
        playerSuffix: Component,
        nameTagVisibility: NameTagVisibility,
        collisionRule: CollisionRule,
        color: ChatColor,
        players: Collection<String>,
        canSeeFriendlyInvisible: Boolean
    ): Any {
        return getPlayerTeamPacket(
            name,
            playerPrefix,
            playerSuffix,
            nameTagVisibility,
            collisionRule,
            color,
            players,
            canSeeFriendlyInvisible,
            0
        )
    }

    @JvmStatic
    fun getTeamRemovePacket(name: String): Any {
        return getPlayerTeamPacket(
            name,
            Component.empty(),
            Component.empty(),
            NameTagVisibility.ALWAYS,
            CollisionRule.ALWAYS,
            ChatColor.RESET,
            emptyList(),
            false,
            1
        )
    }

    @JvmStatic
    fun getTeamModifyPacket(
        name: String,
        playerPrefix: Component,
        playerSuffix: Component,
        nameTagVisibility: NameTagVisibility,
        collisionRule: CollisionRule,
        color: ChatColor,
        canSeeFriendlyInvisible: Boolean
    ): Any {
        return getPlayerTeamPacket(
            name,
            playerPrefix,
            playerSuffix,
            nameTagVisibility,
            collisionRule,
            color,
            emptyList(),
            canSeeFriendlyInvisible,
            2
        )
    }

    @JvmStatic
    fun getTeamAddPlayerPacket(name: String, players: Collection<String>): Any {
        return getPlayerTeamPacket(
            name,
            Component.empty(),
            Component.empty(),
            NameTagVisibility.ALWAYS,
            CollisionRule.ALWAYS,
            ChatColor.RESET,
            players,
            false,
            3
        )
    }

    @JvmStatic
    fun getTeamRemovePlayerPacket(name: String, players: Collection<String>): Any {
        return getPlayerTeamPacket(
            name,
            Component.empty(),
            Component.empty(),
            NameTagVisibility.ALWAYS,
            CollisionRule.ALWAYS,
            ChatColor.RESET,
            players,
            false,
            4
        )
    }

    @JvmStatic
    fun getEntityEventPacket(entity: Any, eventId: Byte): Any {
        return ClientboundEntityEventPacketAccessor.CONSTRUCTOR_0!!.newInstance(entity, eventId)
    }

    @JvmStatic
    fun getEntityCustomNameDataPacket(entity: Any): Any {
        val entityData: Any = EntityAccessor.METHOD_GET_ENTITY_DATA!!.invoke(entity)

        return ClientboundSetEntityDataPacketAccessor.CONSTRUCTOR_1!!.newInstance(
            EntityAccessor.METHOD_GET_ID!!.invoke(entity),
            listOf(
                SynchedEntityDataAccessor.METHOD_GET_ITEM!!.invoke(
                    entityData,
                    EntityAccessor.FIELD_DATA_CUSTOM_NAME!!
                ),
                SynchedEntityDataAccessor.METHOD_GET_ITEM!!.invoke(
                    entityData,
                    EntityAccessor.FIELD_DATA_CUSTOM_NAME_VISIBLE!!
                )
            )
        )
    }

    @JvmStatic
    fun getSetScorePacket(id: String, name: String, score: Int): Any {
        if (ServerVersion.isAtLeast(20, 5)) {
            return ClientboundSetScorePacketAccessor.CONSTRUCTOR_2!!.newInstance(
                name,
                id,
                score,
                Optional.empty<Any>(),
                Optional.of(BlankFormatAccessor.FIELD_INSTANCE!!)
            )
        } else if (ServerVersion.containsPatch(20, 3)) {
            return ClientboundSetScorePacketAccessor.CONSTRUCTOR_1!!.newInstance(
                name,
                id,
                score,
                null,
                BlankFormatAccessor.FIELD_INSTANCE!!
            )
        } else {
            return ClientboundSetScorePacketAccessor.CONSTRUCTOR_0!!.newInstance(
                ServerScoreboard_MethodAccessor.FIELD_CHANGE,
                id,
                name,
                score
            )
        }
    }

    /**
     * @param objective nms objective
     * @param method 0: Create, 1: Remove, 2: Change
     * @return packet
     */
    @JvmStatic
    fun getSetObjectivePacket(objective: Any, method: Int): Any {
        return ClientboundSetObjectivePacketAccessor.CONSTRUCTOR_0!!.newInstance(
            objective,
            method
        ).apply {
            ClientboundSetObjectivePacketAccessor.FIELD_NUMBER_FORMAT?.set(this, Optional.of(BlankFormatAccessor.FIELD_INSTANCE!!))
        }
    }

    @JvmStatic
    fun getSetDisplayObjectivePacket(objective: Any): Any {
        return if (ServerVersion.isAtLeast(20, 2)) {
            ClientboundSetDisplayObjectivePacketAccessor.CONSTRUCTOR_1!!.newInstance(
                DisplaySlotAccessor.FIELD_SIDEBAR!!,
                objective
            )
        } else {
            ClientboundSetDisplayObjectivePacketAccessor.CONSTRUCTOR_0!!.newInstance(
                1,
                objective
            )
        }
    }

    @JvmStatic
    fun getEntityDataPacket(entity: Any): Any {
        if (ServerVersion.isAtLeast(19, 3)) {
            val entityData = EntityAccessor.METHOD_GET_ENTITY_DATA!!.invoke(
                entity
            )
            val list: MutableList<Any> = mutableListOf()
            if (ServerVersion.isAtLeast(20, 5)) {
                @Suppress("UNCHECKED_CAST")
                list.addAll((SynchedEntityDataAccessor.FIELD_ITEMS_BY_ID_1!!.get(entityData)!! as kotlin.Array<Any>)
                    .map { SynchedEntityData_DataItemAccessor.METHOD_VALUE!!.invoke(it) })
            } else {
                val int2ObjectClass = Class.forName("it.unimi.dsi.fastutil.ints.Int2ObjectMap")
                val valuesMethod = int2ObjectClass.getMethod("values")
                val iteratorMethod = int2ObjectClass.getMethod("values").returnType.getMethod("iterator")
                
                val iterator = iteratorMethod.invoke(
                    valuesMethod.invoke(SynchedEntityDataAccessor.FIELD_ITEMS_BY_ID!!.get(entityData))
                ) as Iterator<*>

                while (iterator.hasNext()) {
                    list.add(SynchedEntityData_DataItemAccessor.METHOD_VALUE!!.invoke(iterator.next()))
                }
            }

            return ClientboundSetEntityDataPacketAccessor.CONSTRUCTOR_1!!.newInstance(
                EntityAccessor.METHOD_GET_ID!!.invoke(entity),
                list
            )
        } else {
            return ClientboundSetEntityDataPacketAccessor.CONSTRUCTOR_0!!.newInstance(
                EntityAccessor.METHOD_GET_ID!!.invoke(entity),
                EntityAccessor.METHOD_GET_ENTITY_DATA!!.invoke(entity),
                true
            )
        }
    }

    @JvmStatic
    fun getEntityDataPacket(id: Int, metadataId: Int, value: Any): Any {
        val synchedEntityData: Any = SynchedEntityDataAccessor.CONSTRUCTOR_0!!.newInstance(null)
        if (ServerVersion.supports(9)) {
            val entityDataSerializer: Any = NMSUtils.getEntityDataSerializer(value)!!
            SynchedEntityDataAccessor.METHOD_DEFINE!!.invoke(
                synchedEntityData,
                EntityDataSerializerAccessor.METHOD_CREATE_ACCESSOR!!.invoke(entityDataSerializer, metadataId),
                value
            )
        } else {
            SynchedEntityDataAccessor.METHOD_FUNC_75682_A!!.invoke(synchedEntityData, metadataId, value)
        }

        return ClientboundSetEntityDataPacketAccessor.CONSTRUCTOR_0!!.newInstance(id, synchedEntityData, true)
    }

    @JvmStatic
    fun getUpdateAttributesPacket(id: Int, attributeInstances: Collection<Any>): Any {
        return ClientboundUpdateAttributesPacketAccessor.CONSTRUCTOR_0!!.newInstance(id, attributeInstances)
    }

}
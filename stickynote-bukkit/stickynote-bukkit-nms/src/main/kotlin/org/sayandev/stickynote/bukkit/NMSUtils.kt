package org.sayandev.stickynote.bukkit

import com.cryptomorin.xseries.ReflectionUtils
import org.sayandev.stickynote.nms.accessors.ServerCommonPacketListenerImplAccessor
import org.sayandev.stickynote.nms.accessors.ServerGamePacketListenerImplAccessor
import org.sayandev.stickynote.nms.accessors.ServerPlayerAccessor
import org.bukkit.entity.Player
import org.sayandev.stickynote.bukkit.utils.ServerVersion
import java.lang.reflect.Method
import java.util.concurrent.Future

object NMSUtils {

    private var CRAFT_PLAYER: Class<*>? = null

    private var CRAFT_PLAYER_GET_HANDLE_METHOD: Method? = null

    init {
        runCatching { CRAFT_PLAYER = ReflectionUtils.getCraftClass("entity.CraftPlayer") }
        runCatching { CRAFT_PLAYER_GET_HANDLE_METHOD = CRAFT_PLAYER!!.getMethod("getHandle") }
    }

    fun getServerPlayer(player: Player): Any {
        return CRAFT_PLAYER_GET_HANDLE_METHOD!!.invoke(player)
    }

    fun getServerGamePacketListener(player: Player): Any {
        return ServerPlayerAccessor.FIELD_CONNECTION!![getServerPlayer(player)]
    }

    /**
     * Sends one or more packets to a player.
     * @param player The player that is going to receive the packet(s).
     * @param packets The packet(s) that are going to be sent to the player.
     */
    @JvmStatic
    fun sendPacketSync(player: Player, vararg packets: Any) {
        try {
            //ReflectionUtils.sendPacketSync(player, packets);
            val commonGameConnection = getServerGamePacketListener(player!!)
            for (packet in packets) {
                if ((ServerVersion.supports(20) && ServerVersion.patchNumber() >= 2) || ServerVersion.supports(21)) {
                    ServerCommonPacketListenerImplAccessor.METHOD_SEND!!.invoke(commonGameConnection, packet)
                } else {
                    ServerGamePacketListenerImplAccessor.METHOD_SEND!!.invoke(commonGameConnection, packet)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Error(e)
        }
    }

    /**
     * Sends one or more packets to a group of player.
     * @param players The players that are going to receive the packet(s).
     * @param packets The packet(s) that are going to be sent to the player(s).
     */
    @JvmStatic
    fun sendPacketSync(players: Collection<Player>, vararg packets: Any) {
        for (player in players) {
            sendPacketSync(player, *packets)
        }
    }

    /**
     * Sends one or more packets to a player asynchronously. Packets are thread safe.
     * @param player The player that is going to receive the packet(s).
     * @param packets The packet(s) that are going to be sent to the player.
     */
    @JvmStatic
    fun sendPacket(player: Player, vararg packets: Any): Future<*> {
        return runEAsync { sendPacketSync(player, *packets) }
    }

    /**
     * Sends one or more packets to a group of player asynchronously. Packets are thread safe.
     * @param players The players that are going to receive the packet(s).
     * @param packets The packet(s) that are going to be sent to the player(s).
     */
    @JvmStatic
    fun sendPacket(players: Collection<Player>, vararg packets: Any): Future<*> {
        return runEAsync { sendPacketSync(players, *packets) }
    }
}
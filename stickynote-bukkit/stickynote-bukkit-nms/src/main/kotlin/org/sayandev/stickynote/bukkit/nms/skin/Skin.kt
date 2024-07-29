package org.sayandev.stickynote.bukkit.nms.skin

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.skinsrestorer.api.property.SkinProperty
import org.bukkit.Bukkit.getOnlinePlayers
import org.bukkit.entity.Player
import org.sayandev.stickynote.bukkit.hasPlugin
import org.sayandev.stickynote.bukkit.nms.NMSUtils
import org.sayandev.stickynote.bukkit.nms.NMSUtils.sendPacket
import org.sayandev.stickynote.bukkit.nms.PacketUtils
import org.sayandev.stickynote.bukkit.nms.enum.PlayerInfoAction
import org.sayandev.stickynote.bukkit.nms.npc.PlayerNPC
import org.sayandev.stickynote.bukkit.nms.accessors.PlayerAccessor

class Skin(
    val texture: String,
    val signature: String?
) {

    /**
     * Applies the skin to a player
     * @param player The player to apply the skin to
     */
    fun apply(player: Player) {
        if (hasPlugin("SkinsRestorer") && signature != null) {
            SkinUtils.getSkinsRestorer().getSkinApplier(Player::class.java).applySkin(player, SkinProperty.of(texture, signature))
        } else {
            val serverPlayer: Any = NMSUtils.getServerPlayer(player)
            val gameProfile = PlayerAccessor.METHOD_GET_GAME_PROFILE!!.invoke(serverPlayer) as GameProfile

            gameProfile.properties.removeAll("textures")
            gameProfile.properties.put("textures", Property("textures", texture, signature))

            getOnlinePlayers().sendPacket(
                PacketUtils.getPlayerInfoPacket(serverPlayer, PlayerInfoAction.ADD_PLAYER),
                PacketUtils.getAddEntityPacket(serverPlayer)
            )
            player.sendPacket(
                PacketUtils.getRespawnPacket(
                    NMSUtils.getServerLevel(player.world),
                    player.gameMode,
                    player.gameMode,
                    false
                )
            )
        }
    }

    /**
     * Applies the skin to a player NPC
     * @param playerNPC The player NPC to apply the skin to
     */
    fun apply(playerNPC: PlayerNPC) {
        val gameProfile = PlayerAccessor.METHOD_GET_GAME_PROFILE!!.invoke(playerNPC.entity) as GameProfile
        gameProfile.properties.removeAll("textures")
        gameProfile.properties.put("textures", Property("textures", texture, signature))
    }

}
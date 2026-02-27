package org.sayandev.stickynote.paper.nms.skin

import com.cryptomorin.xseries.profiles.PlayerProfiles
import com.google.gson.JsonParser
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.skinsrestorer.api.SkinsRestorer
import net.skinsrestorer.api.SkinsRestorerProvider
import org.bukkit.entity.Player
import org.sayandev.stickynote.paper.hasPlugin
import org.sayandev.stickynote.paper.nms.NMSUtils
import org.sayandev.stickynote.paper.nms.accessors.PlayerAccessor
import org.sayandev.stickynote.paper.nms.skin.SkinUtils.getOnlineSkinAsync
import org.sayandev.stickynote.paper.runEAsync
import org.sayandev.stickynote.paper.utils.ServerVersion
import java.io.InputStreamReader
import java.net.URL
import java.util.UUID
import javax.net.ssl.HttpsURLConnection
import kotlin.jvm.optionals.getOrNull

object SkinUtils {

    private val cache: MutableMap<String, Skin> = mutableMapOf()

    /**
     * Gets the skin of a player from Mojang API. This method is slow and should be run asynchronously. Use [getOnlineSkinAsync] for asyncronous calls
     * @note using legacy JsonParser#parse to prevent problems in outdated 1.8 forks
     * @param name The name of the player
     * @return The skin of the player. null if the player is not found
     */
    fun getOnlineSkin(name: String): Skin? {
        if (cache.contains(name)) return cache[name]!!

        val profileUrl = URL("https://api.mojang.com/users/profiles/minecraft/$name").openConnection() as HttpsURLConnection
        val profileJson = JsonParser().parse(InputStreamReader(profileUrl.inputStream)).asJsonObject
        if (profileJson.has("errorMessage")) {
            return null
        }
        val uuid = profileJson["id"].asString

        val sessionUrl = URL("https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false")
        val sessionJson = JsonParser().parse(InputStreamReader(sessionUrl.openStream())).asJsonObject
        if (sessionJson.has("errorMessage")) {
            return null
        }
        val property = sessionJson["properties"].asJsonArray[0].asJsonObject

        val texture = property["value"].asString
        val signature = property["signature"].asString

        val skin = Skin(texture, signature)
        cache[name] = skin

        return skin
    }

    /**
     * Gets the skin of a player from Mojang API asynchronously
     * @param name The name of the player
     * @param callback The callback function that will be called when the skin is fetched
     */
    fun getOnlineSkinAsync(name: String, callback: (Skin?) -> Unit) {
        runEAsync {
            callback(getOnlineSkin(name))
        }
    }

    /**
     * Gets the skin of a player from SkinsRestorer
     * @param nameOrUuid The name or UUID of the player
     * @return The skin of the player. null if the player is not found or SkinsRestorer is not installed
     */
    fun getSkinsRestorerFromStorage(nameOrUuid: String): Skin? {
        if (!hasPlugin("SkinsRestorer")) return null
        val result = getSkinsRestorer().skinStorage.getPlayerSkin(nameOrUuid, true).getOrNull() ?: return null
        return Skin(result.skinProperty.value, result.skinProperty.signature)
    }

    fun getSkinsRestorerFromStorage(player: Player): Skin? {
        return getSkinsRestorerFromStorage(player, false)
    }

    fun getSkinsRestorerFromStorage(player: Player, fallback: Boolean): Skin? {
        return getSkinsRestorerFromStorage(player.uniqueId, player.name, fallback)
    }

    fun getSkinsRestorerFromStorage(uniqueId: UUID, username: String): Skin? {
        return getSkinsRestorerFromStorage(uniqueId, username, false)
    }

    fun getSkinsRestorerFromStorage(uniqueId: UUID, username: String, fallback: Boolean): Skin? {
        if (!hasPlugin("SkinsRestorer")) return null
        var result = getSkinsRestorer().playerStorage.getSkinOfPlayer(uniqueId).getOrNull()
        if (result == null && fallback) {
            result = getSkinsRestorer().skinStorage.getPlayerSkin(username, true).getOrNull()?.skinProperty
        }
        if (result == null) return null
        return Skin(result.value, result.signature)
    }

    /**
     * Gets the skin of a player that is currently online. This method is fast and can be run synchronously
     * @param player The player
     * @return The skin of the player. null if the player does not have any skin
     */
    fun getOnlineSkin(player: Player): Skin? {
        val gameProfile = PlayerAccessor.METHOD_GET_GAME_PROFILE!!.invoke(NMSUtils.getServerPlayer(player)) as GameProfile
        val property = gameProfile.properties["textures"].firstOrNull() ?: return null

        var signature: String? = null
        if (property.hasSignature()) {
            val signatureMethod = if (ServerVersion.isAtLeast(20, 3)) {
                Property::class.java.getMethod("signature")
            } else {
                Property::class.java.getMethod("getSignature")
            }
            signature = signatureMethod.invoke(property) as String
        }

        return Skin(PlayerProfiles.getTextureValue(gameProfile)!!, signature)
    }

    /**
     * Gets the SkinsRestorer instance
     * @return The SkinsRestorer instance
     */
    fun getSkinsRestorer(): SkinsRestorer = SkinsRestorerProvider.get()

}
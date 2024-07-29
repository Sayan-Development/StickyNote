package org.sayandev.stickynote.bukkit.nms.skin

import com.cryptomorin.xseries.profiles.PlayerProfiles
import com.google.gson.JsonParser
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.skinsrestorer.api.SkinsRestorer
import net.skinsrestorer.api.SkinsRestorerProvider
import org.bukkit.entity.Player
import org.sayandev.stickynote.bukkit.hasPlugin
import org.sayandev.stickynote.bukkit.nms.NMSUtils
import org.sayandev.stickynote.bukkit.runEAsync
import org.sayandev.stickynote.bukkit.utils.ServerVersion
import org.sayandev.stickynote.bukkit.nms.accessors.PlayerAccessor
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.jvm.optionals.getOrNull

object SkinUtils {

    private val cache: MutableMap<String, Skin> = mutableMapOf()

    /**
     * Gets the skin of a player from Mojang API. This method is slow and should be run asynchronously. Use [getSkinAsync] for asyncronous calls
     * @param name The name of the player
     * @return The skin of the player. null if the player is not found
     */
    fun getSkin(name: String): Skin? {
        if (cache.contains(name)) return cache[name]!!

        val profileUrl = URL("https://api.mojang.com/users/profiles/minecraft/$name").openConnection() as HttpsURLConnection
        val profileJson = JsonParser.parseReader(InputStreamReader(profileUrl.inputStream)).asJsonObject
        if (profileJson.has("errorMessage")) {
            return null
        }
        val uuid = profileJson["id"].asString

        val sessionUrl = URL("https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false")
        val sessionJson = JsonParser.parseReader(InputStreamReader(sessionUrl.openStream())).asJsonObject
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
    fun getSkinAsync(name: String, callback: (Skin?) -> Unit) {
        runEAsync {
            callback(getSkin(name))
        }
    }

    /**
     * Gets the skin of a player from SkinsRestorer
     * @param nameOrUuid The name or UUID of the player
     * @return The skin of the player. null if the player is not found or SkinsRestorer is not installed
     */
    fun getSkinFromSkinsRestorer(nameOrUuid: String): Skin? {
        if (!hasPlugin("SkinsRestorer")) return null

        val result = getSkinsRestorer().skinStorage.getPlayerSkin(nameOrUuid, true).getOrNull() ?: return null

        return Skin(result.skinProperty.value, result.skinProperty.signature)
    }

    /**
     * Gets the skin of a player that is currently online. This method is fast and can be run synchronously
     * @param player The player
     * @return The skin of the player. null if the player does not have any skin
     */
    fun getSkin(player: Player): Skin? {
        val gameProfile = PlayerAccessor.METHOD_GET_GAME_PROFILE!!.invoke(NMSUtils.getServerPlayer(player)) as GameProfile
        val property = gameProfile.properties["textures"].firstOrNull() ?: return null

        var signature: String? = null
        if (property.hasSignature()) {
            val signatureMethod = if ((ServerVersion.version() == 20 && ServerVersion.patchNumber() >= 3) || ServerVersion.supports(21)) {
                Property::class.java.getMethod("signature")
            } else {
                Property::class.java.getMethod("getSignature")
            }
            signature = signatureMethod.invoke(gameProfile) as String
        }

        return Skin(PlayerProfiles.getSkinValue(gameProfile)!!, signature)
    }

    /**
     * Gets the SkinsRestorer instance
     * @return The SkinsRestorer instance
     */
    fun getSkinsRestorer(): SkinsRestorer = SkinsRestorerProvider.get()

}
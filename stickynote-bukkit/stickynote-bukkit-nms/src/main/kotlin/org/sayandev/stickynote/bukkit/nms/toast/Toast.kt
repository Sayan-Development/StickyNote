package org.sayandev.stickynote.bukkit.nms.toast

import com.cryptomorin.xseries.XMaterial
import com.github.shynixn.mccoroutine.bukkit.ticks
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.entity.Player
import org.sayandev.stickynote.bukkit.async
import org.sayandev.stickynote.bukkit.launch
import org.sayandev.stickynote.bukkit.nms.NMSUtils
import org.sayandev.stickynote.bukkit.nms.NMSUtils.sendPacketSync
import org.sayandev.stickynote.bukkit.nms.accessors.*
import org.sayandev.stickynote.bukkit.utils.AdventureUtils.component
import org.sayandev.stickynote.bukkit.utils.ServerVersion
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class Toast(
    val title: Component,
    icon: XMaterial,
    frameType: FrameType
) {

    @Deprecated("Trimming characters may not work as expected. Trim it yourself to your needs and use the other constructor")
    constructor(
        title: String,
        icon: XMaterial,
        frameType: FrameType,
        trimCharacters: Boolean = true,
        vararg placeholder: TagResolver = emptyArray()
    ): this(parseTitle(title, trimCharacters, *placeholder), icon, frameType)

    private val addPacket: Any
    private val removePacket: Any
    private val advancementProgress: Any

    init {
        val jsonAdvancement = JsonObject()

        val displayJson = JsonObject()
        val iconJson = JsonObject()
        val descJson = JsonObject()

        val criteriaJson = JsonObject()
        val elytraJson = JsonObject()

        val requirementsArray = JsonArray()
        val elytraRequirementArray = JsonArray()


        //In 1.20.5 and above, "item" was changed to "id"
        iconJson.addProperty(
            if (ServerVersion.isAtLeast(20, 5)) "id" else "item",
            "minecraft:" + icon.parseMaterial().toString().lowercase()
        )
        if (!ServerVersion.supports(13)) iconJson.addProperty("data", icon.data)

        descJson.addProperty("text", "")

        displayJson.add("title", JsonParser.parseString(GsonComponentSerializer.gson().serialize(title)))
        displayJson.add("icon", iconJson)
        displayJson.add("description", descJson)
        displayJson.addProperty("frame", frameType.toString().lowercase())
        displayJson.addProperty("show_toast", true)
        displayJson.addProperty("announce_to_chat", false)
        displayJson.addProperty("hidden", false)

        elytraJson.addProperty("trigger", "minecraft:impossible")
        criteriaJson.add("elytra", elytraJson)

        elytraRequirementArray.add("elytra")
        requirementsArray.add(elytraRequirementArray)

        jsonAdvancement.add("display", displayJson)
        jsonAdvancement.add("criteria", criteriaJson)
        jsonAdvancement.add("requirements", requirementsArray)

        val advancementResourceLocation: Any = NMSUtils.createResourceLocation("stickynote_toasts_" + UUID.randomUUID())
        var advancementBuilder: Any? = null
        var advancement: Any? = null
        if (ServerVersion.supports(13)) {
            var deserializationContext: Any? = null
            if (ServerVersion.supports(20)) {
                if (ServerVersion.containsPatch(20, 3)) {
                    //Extracted this method from bukkit's UnsafeValues
                    val jsonOps = Class.forName("com.mojang.serialization.JsonOps").getField("INSTANCE")[null]
                    val decoderParse = Class.forName("com.mojang.serialization.Decoder").getMethod(
                        "parse",
                        Class.forName("com.mojang.serialization.DynamicOps"),
                        Any::class.java
                    )
                    val dataResultGet = Class.forName("com.mojang.serialization.DataResult").getMethod("result")
                    advancement = (dataResultGet.invoke(
                        decoderParse.invoke(
                            AdvancementAccessor.FIELD_CODEC!!,
                            jsonOps,
                            jsonAdvancement
                        )
                    ) as Optional<*>).orElseThrow {
                        IllegalStateException("Failed to parse advancement json")
                    }
                } else {
                    deserializationContext = DeserializationContextAccessor.CONSTRUCTOR_1!!.newInstance(
                        advancementResourceLocation,
                        LootDataManagerAccessor.CONSTRUCTOR_0!!.newInstance()
                    )
                }
            } else {
                deserializationContext = DeserializationContextAccessor.CONSTRUCTOR_0!!.newInstance(
                    advancementResourceLocation,
                    PredicateManagerAccessor.CONSTRUCTOR_0!!.newInstance()
                )
            }
            //In 1.20.2 fromJson method was moved to "Advancement" class (previously was in "Advancement$Builder")
            //In 1.20.3 and above, advancement is getting initialized above.
            if (ServerVersion.equals(20, 2)) {
                //1.20.2
                advancement = AdvancementAccessor.METHOD_FROM_JSON!!.invoke(null, jsonAdvancement, deserializationContext)
            } else if (!ServerVersion.isAtLeast(20, 2)) {
                //1.20.1 and lower
                advancementBuilder = Advancement_BuilderAccessor.METHOD_FROM_JSON!!.invoke(
                    null,
                    jsonAdvancement,
                    deserializationContext
                )
            }
        } else {
            advancementBuilder = GsonHelperAccessor.METHOD_FROM_JSON!!.invoke(
                null,
                ServerAdvancementManagerAccessor.FIELD_GSON!!,
                Gson().toJson(jsonAdvancement),
                Advancement_BuilderAccessor.TYPE
            )
        }
        if (advancement == null) {
            advancement = Advancement_BuilderAccessor.METHOD_BUILD!!.invoke(advancementBuilder, advancementResourceLocation)
        }

        this.advancementProgress = AdvancementProgressAccessor.CONSTRUCTOR_0!!.newInstance()
        val toAddSet: MutableCollection<Any> = HashSet()

        //In 1.20.2 and above, "AdvancementRequirements" was added. In older versions, requirements was just a 2d string array (String[][])
        if ((ServerVersion.supports(20) && ServerVersion.patchNumber() >= 2) || ServerVersion.supports(21)) {
            AdvancementProgressAccessor.METHOD_UPDATE_1!!.invoke(
                advancementProgress,
                AdvancementAccessor.METHOD_REQUIREMENTS!!.invoke(advancement) //returns AdvancementRequirements
            )

            toAddSet.add(
                AdvancementHolderAccessor.CONSTRUCTOR_0!!.newInstance(
                    advancementResourceLocation,
                    advancement
                )
            )
        } else {
            AdvancementProgressAccessor.METHOD_UPDATE!!.invoke(
                advancementProgress,
                AdvancementAccessor.METHOD_GET_CRITERIA!!.invoke(advancement),
                AdvancementAccessor.METHOD_GET_REQUIREMENTS!!.invoke(advancement) //returns String[][]
            )

            toAddSet.add(advancement!!)
        }

        val progressMap: MutableMap<Any, Any> = HashMap()
        progressMap[advancementResourceLocation] = advancementProgress

        val toRemoveSet: MutableSet<Any> = HashSet()
        toRemoveSet.add(advancementResourceLocation)

        addPacket = ClientboundUpdateAdvancementsPacketAccessor.CONSTRUCTOR_0!!.newInstance(
            false,
            toAddSet,
            emptySet<Any>(),
            progressMap)
        removePacket = ClientboundUpdateAdvancementsPacketAccessor.CONSTRUCTOR_0!!.newInstance(
            false,
            emptySet<Any>(),
            toRemoveSet,
            emptyMap<Any, Any>()
        )
    }

    fun send(player: Player, vararg otherPlayers: Player) {
        val players = listOf(player, *otherPlayers)
        launch {
            async {
                awardCriteria()
                players.sendPacketSync(addPacket)
                delay(5.ticks)
                revokeCriteria()
                players.sendPacketSync(removePacket)
            }
        }
    }

    private fun awardCriteria() {
        AdvancementProgressAccessor.METHOD_GRANT_PROGRESS!!.invoke(advancementProgress, "elytra")
    }

    private fun revokeCriteria() {
        AdvancementProgressAccessor.METHOD_REVOKE_PROGRESS!!.invoke(advancementProgress, "elytra")
    }

    companion object {
        private const val IGNORE_CHAR = 'ˑ'
        private val ESCAPE_TOKEN_PATTERN = Pattern.compile("((?<start><)(?<token>[^<>]+(:(?<inner>['\"]?([^'\"](\\\\['\"])?)+['\"]?))*)(?<end>>))+?")

        fun parseTitle(rawTitle: String, trimCharacters: Boolean, vararg placeholder: TagResolver): Component {
            val title = if (trimCharacters) trimCharacters(rawTitle) else rawTitle

            return title.component(*placeholder)
        }

        private fun trimCharacters(string: String): String {
            var input = string
            val characterLimit = 45

            input = input.replace(IGNORE_CHAR.toString(), ".")
            val modifiedInput = replaceTokensWithIgnoreChar(input)

            var i = 0
            var j = 0
            for (character in modifiedInput.toCharArray()) {
                j++
                if (character != IGNORE_CHAR) i++
                if (i > characterLimit) break
            }
            input = input.substring(0, j)
            if (i > characterLimit) input = "$input..."

            return input
        }

        private fun replaceTokensWithIgnoreChar(richMessage: String): String {
            val sb = StringBuilder()
            val matcher: Matcher = ESCAPE_TOKEN_PATTERN.matcher(richMessage)
            var lastEnd = 0
            var i = 0
            while (matcher.find()) {
                i++
                if (i > 20) {
                    break
                }
                val startIndex = matcher.start()
                val endIndex = matcher.end()

                if (startIndex > lastEnd) {
                    sb.append(richMessage, lastEnd, startIndex)
                }
                lastEnd = endIndex

                var token = matcher.group("token")
                val inner = matcher.group("inner")

                // also escape inner
                if (inner != null) {
                    token = token.replace(inner, replaceTokensWithIgnoreChar(inner))
                }

                sb.append(IGNORE_CHAR).append(token.replace(".", IGNORE_CHAR.toString())).append(IGNORE_CHAR)
            }

            if (richMessage.length > lastEnd) {
                sb.append(richMessage.substring(lastEnd))
            }

            return sb.toString()
        }
    }
}
/*
package org.sayandev.bukkit.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.CraftingRecipe
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice.ExactChoice
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import org.sayandev.bukkit.item.extension.ItemExtension


open class SayanItem(
    val id: String,
    val features: MutableList<ItemExtension> = mutableListOf(),
    var convertFromVanilla: Boolean = false,
) */
/*: ContainerItemEvent()*//*
 {

    var name = id

    val baseItem = ItemStack(Material.STONE)

    val headerLore = mutableListOf<Component>()
    val lore = mutableListOf<Component>()
    val footerLore = mutableListOf<Component>()
    val rarityLore = mutableListOf<Component>()
    val enchants = mutableListOf<EnchantmentInfo>()
    val events = mutableListOf<Listener>()

    init {
        baseItem.editMeta { meta ->
            meta.persistentDataContainer[sayanItemNamespace, PersistentDataType.STRING] = id
        }
    }

    fun material(type: Material): SayanItem {
        this.baseItem.type = type
        return this
    }

    fun material(): Material {
        return this.baseItem.type
    }

    fun texture(texture: String) {
        baseItem.itemMeta = SkullUtils.applySkin(baseItem.itemMeta as SkullMeta, texture)
        update()
    }

    inline fun buildMaterial(crossinline material: SayanItem.() -> Material): SayanItem {
        this.material(material(this))
        return this
    }


    fun name(name: String): SayanItem {
        this.name = name
        this.baseItem.editMeta { meta -> meta.displayName(name.cleanComponent()) }
        return this
    }

    fun name(): Component? {
        return this.baseItem.itemMeta.displayName()
    }

    inline fun buildName(crossinline name: SayanItem.() -> String): SayanItem {
        this.name(name(this))
        return this
    }

    inline fun name(crossinline name: SayanItem.() -> String): SayanItem {
        this.name(name(this))
        return this
    }

    fun lore(lore: List<String>) {
        this.lore.clear()
        this.lore.addAll(lore.map { line -> line.cleanComponent() })
    }

    inline fun buildLore(crossinline lore: SayanItem.() -> List<String>): SayanItem {
        this.lore(lore(this))
        return this
    }

    fun headerLore(lore: List<String>) {
        this.headerLore.clear()
        this.headerLore.addAll(lore.map { line -> line.cleanComponent() })
    }

    inline fun buildHeaderLore(crossinline lore: SayanItem.() -> List<String>): SayanItem {
        this.headerLore(lore(this))
        return this
    }

    fun footerLore(lore: List<String>) {
        this.footerLore.clear()
        this.footerLore.addAll(lore.map { line -> line.cleanComponent() })
    }

    inline fun buildFooterLore(crossinline lore: SayanItem.() -> List<String>): SayanItem {
        this.footerLore(lore(this))
        return this
    }

    inline fun buildEnchants(enchants: SayanItem.() -> List<EnchantmentInfo>): SayanItem {
        enchants(this).forEach { enchant -> this.buildEnchant { enchant } }
        return this
    }

    inline fun buildEnchant(crossinline enchant: SayanItem.() -> EnchantmentInfo): SayanItem {
        val enchantmentInfo = enchant(this)
        enchants.add(enchantmentInfo)
        this.baseItem.editMeta { meta -> meta.addEnchant(enchantmentInfo.enchantment, enchantmentInfo.level, enchantmentInfo.ignoreLevelRestriction) }
        return this
    }

    fun hasEnchant(enchantment: Enchantment, level: Int): Boolean {
        return enchants.find { previousEnchants -> previousEnchants.enchantment == enchantment && previousEnchants.level == level } != null
    }

    fun convertFromVanilla(placeable: Boolean) {
        this.convertFromVanilla = placeable
    }

    fun convertFromVanilla() {
        convertFromVanilla(true)
    }

    fun enchanted(enchanted: Boolean): SayanItem {
        this.baseItem.editMeta { meta ->
            if (enchanted) {
                meta.addEnchant(Enchantment.WATER_WORKER, 100, true)
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            } else {
                meta.removeEnchant(Enchantment.WATER_WORKER)
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS)
            }
        }
        return this
    }

    fun enchanted(): SayanItem {
        return enchanted(true)
    }

    fun smeltTicks(ticks: Int) {
        this.smeltTicks = ticks
    }

    fun smeltTicks(): Int {
        return this.smeltTicks
    }

    val alphabet = ('A'..'Z').toList()
    inline fun <reified R: CraftingRecipe> buildRecipe(crossinline recipe: SayanItem.() -> List<String>): SayanItem {
        */
/*val keyedRecipe = recipe(this).mapIndexed { index, line -> RecipeData.fromString(alphabet[index], line, this) }
        recipeMatrix.addAll(keyedRecipe)


        val craftingRecipe = if (R::class == ShapedRecipe::class) {
            require(keyedRecipe.size == 9) { "ShapedRecipe must have 9 keys" }

            val shapedRecipe = ShapedRecipe(NamespacedKey(Ruom.getPlugin(), id), display())

            val shape = keyedRecipe
                .joinToString("") { it.char.toString().ifEmpty { " " } }
                .chunked(3)
                .toTypedArray()
            shapedRecipe.shape(*shape)

            for (key in keyedRecipe) {
                if (key.recipeChoice == null) continue
                Ruom.warn("choice: ${key.recipeChoice}")
                shapedRecipe.setIngredient(key.char, key.recipeChoice)
            }
            shapedRecipe
        } else if (R::class == ShapelessRecipe::class) {
            val shapelessRecipe = ShapelessRecipe(NamespacedKey(Ruom.getPlugin(), id), display())
            for (key in keyedRecipe) {
                if (key.recipeChoice == null) continue
                shapelessRecipe.addIngredient(key.recipeChoice)
            }
            shapelessRecipe
        } else {
            throw IllegalArgumentException("Recipe type not supported (${R::class})")
        }

        Bukkit.addRecipe(craftingRecipe)
        for (player in Ruom.getOnlinePlayers()) {
            player.discoverRecipe(craftingRecipe.key)
        }
        this.recipe = craftingRecipe*//*


        return this
    }

    fun give(player: Player, amount: Int = 1, vararg additionalData: Pair<PersistentDataType<*, *>, Any>) {
        val item = this.baseItem.clone()
        additionalData.forEach { data ->
            item.editMeta { meta -> meta.persistentDataContainer.set(NamespacedKey(Ruom.getPlugin(), data.toString()), PersistentDataType.STRING, data.toString()) }
        }
        player.inventory.addItem(item.apply { this.amount = amount })
    }

    fun setup() {
        SayanItems.addItem(this)
    }

    open fun generateLore(item: ItemStack, player: Player? = null): MutableList<Component> {
        val set = baseItem.sayanItemSet()
        return mutableListOf(*headerLore.toTypedArray(), *lore.toTypedArray(), *footerLore.toTypedArray(), *(item.lore()?.toTypedArray() ?: emptyArray())).apply {
            if (set != null) {
                this.addAll(0, set.lore)
            }

            for (feature in features) {
                this.addAll(feature.lore(item, player))
            }

            this.addAll(rarityLore)
        }
    }

    fun display(): ItemStack {
        return baseItem.clone().apply {
            this.editMeta { meta ->
                meta.lore(generateLore(baseItem))
            }
        }
    }

    override fun onItemUpdate(player: Player, item: ItemStack): ItemStack {
        if (player.gameMode == GameMode.CREATIVE) return item
        if (item.sayanItem()?.isSimilar(this) != true) return item
        return item.apply {
            this.editMeta { meta ->
                meta.lore(generateLore(item))
            }
        }
    }

    inline fun <reified T: Event> registerEvent(
        priority: EventPriority = EventPriority.HIGH,
        ignoreCancelled: Boolean = false,
        crossinline run: T.() -> Unit
    ): SayanItem {
        val event = object : ObjectiveEvent<T>(T::class.java) {
            override fun execute(event: T) {
                if (event.item()?.isSimilar(this@SayanItem) != true) return
                Unit.run { run(event) }
            }
        }
        Bukkit.getPluginManager().registerEvent(
            T::class.java,
            event,
            priority,
            event,
            Ruom.getPlugin(),
            ignoreCancelled
        )
        return this@SayanItem
    }

    fun isSimilar(item: SayanItem): Boolean {
        return this.id == item.id
    }

    fun update(): SayanItem {
        return this
    }

    companion object {
        val sayanItemNamespace = NamespacedKey(Ruom.getPlugin(), "sayanitem")

        inline fun item(
            id: String,
            features: MutableList<ItemFeature> = mutableListOf(),
            placeable: Boolean = false,
            body: SayanItem.() -> SayanItem
        ): SayanItem {
            return body(SayanItem(id, features = features, convertFromVanilla = placeable))
        }

        fun ItemStack.sayanItem(): SayanItem? {
            this.itemMeta?.persistentDataContainer?.get(sayanItemNamespace, PersistentDataType.STRING)?.let {
                return SayanItems.getItem(it)
            } ?: return null
        }

        fun String.cleanComponent(): Component {
            return Component.text().decoration(TextDecoration.ITALIC, false).append(this.toComponent()).asComponent()
        }

        fun Event.item(): SayanItem? {
            return when (this::class) {
                BlockBreakEvent::class -> {
                    (this as BlockBreakEvent).player.inventory.itemInMainHand.sayanItem()
                }
                PlayerInteractEvent::class -> {
                    (this as PlayerInteractEvent).item?.sayanItem()
                }
                EntityDamageByEntityEvent::class -> {
                    (this as EntityDamageByEntityEvent).damager.let { damager ->
                        when (damager) {
                            is Projectile -> {
                                (damager.shooter as? LivingEntity)?.equipment?.getItem(EquipmentSlot.HAND)?.sayanItem()
                            }
                            is LivingEntity -> {
                                damager.equipment?.getItem(EquipmentSlot.HAND)?.sayanItem()
                            }
                            else -> null
                        }
                    }
                }
                PlayerFishEvent::class -> {
                    (this as PlayerFishEvent).let { event ->
                        if (event.state != PlayerFishEvent.State.CAUGHT_FISH) null
                        else event.player.equipment.getItem(EquipmentSlot.HAND).sayanItem()
                    }
                }
                else -> {
                    throw IllegalArgumentException("This event is not yet implemented! (class: `${this::class.java}`)")
                }
            }
        }

        fun Event.player(): Player {
            return when (this::class) {
                BlockBreakEvent::class -> {
                    (this as BlockBreakEvent).player
                }
                else -> {
                    throw IllegalArgumentException("This event is not yet implemented! (class: `${this::class.java}`)")
                }
            }
        }
    }

    data class EnchantmentInfo(
        val enchantment: Enchantment,
        val level: Int,
        val ignoreLevelRestriction: Boolean,
    )

    data class RecipeData(
        val char: Char,
        val recipeChoice: ExactChoice?,
        val result: SayanItem,
    ) {
        companion object {

            fun fromString(char: Char, input: String, result: SayanItem): RecipeData {
                val regex = """(\w+)\s?(\d+)?(?:\s?\|\|\s?(\w+)\s?(\d+)?)?""".toRegex()
                val matches = regex.findAll(input)

                val items = matches.map { matchResult ->
                    val (item, amount) = matchResult.destructured
                    (SayanItems.getItem(item) ?: SayanItems.emptyItem).baseItem.clone().apply {
                        this.amount = amount.toIntOrNull() ?: 0
                    }
                }.toList()
                return if (items.isEmpty()) {
                    RecipeData(char, null, result)
                } else {
                    RecipeData(char, ExactChoice(items), result)
                }
            }
        }
    }

}*/

import me.kcra.takenaka.generator.accessor.AccessorType
import me.kcra.takenaka.generator.accessor.CodeLanguage
import me.kcra.takenaka.generator.accessor.plugin.accessorRuntime
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.time.Instant
import java.util.*
import java.util.Optional
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.UnaryOperator

plugins {
    alias(libs.plugins.takenaka)
}

repositories {
    // Takenaka
    maven("https://repo.screamingsandals.org/public")
    // Takenaka - SNAPSHOT
    maven("https://repo.screamingsandals.org/snapshots")
}

dependencies {
//    api(libs.packetevents.spigot)

    compileOnly(libs.paper)

    compileOnly(project(":stickynote-core"))
    compileOnly(project(":stickynote-bukkit"))

    mappingBundle("me.kcra.takenaka:mappings:1.8.8+1.21.11")
    implementation(accessorRuntime())
}

tasks {
    sourcesJar {
        dependsOn(generateAccessors)
    }
}

@Suppress("LocalVariableName")
accessors {
    basePackage("org.sayandev.stickynote.bukkit.nms.accessors")
    namespaces("spigot", "mojang")
    accessorType(AccessorType.REFLECTION)
    codeLanguage(CodeLanguage.KOTLIN)
    versionRange("1.8.8", "1.21.11")
    mappingWebsite("https://mappings.dev/")

    val ClientboundPlayerInfoUpdatePacket = "net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket" // 1.19.3 and above
    val ClientboundPlayerInfoUpdatePacketEntry = "net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket\$Entry"
    val ClientboundPlayerInfoRemovePacket = "net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket" // 1.19.3 and above
    val ClientboundPlayerInfoUpdatePacketAction = "net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket\$Action" // 1.19.3 and above
    val ClientboundAddEntityPacket = "net.minecraft.network.protocol.game.ClientboundAddEntityPacket"
    val ClientboundAddMobPacket = "net.minecraft.network.protocol.game.ClientboundAddMobPacket" // 1.8 - 1.18.2
    val ClientboundAddPlayerPacket = "net.minecraft.network.protocol.game.ClientboundAddPlayerPacket"
    val ClientboundRotateHeadPacket = "net.minecraft.network.protocol.game.ClientboundRotateHeadPacket"
    val ClientboundRemoveEntitiesPacket = "net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket"
    val ClientboundMoveEntityPacketRot = "net.minecraft.network.protocol.game.ClientboundMoveEntityPacket\$Rot"
    val ClientboundMoveEntityPacketPos = "net.minecraft.network.protocol.game.ClientboundMoveEntityPacket\$Pos"
    val ClientboundMoveEntityPacketPosRot = "net.minecraft.network.protocol.game.ClientboundMoveEntityPacket\$PosRot"
    val ClientboundAnimatePacket = "net.minecraft.network.protocol.game.ClientboundAnimatePacket"
    val ClientboundBlockChangedAckPacket = "net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket"
    val ClientboundSetEntityDataPacket = "net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket"
    val ClientboundSetEquipmentPacket = "net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket"
    val PacketPlayOutEntityEquipment = "net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment" // 1.8 version of ClientboundSetEquipmentPacket
    val ClientboundTeleportEntityPacket = "net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket"
    val ClientboundSetEntityMotionPacket = "net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket"
    val ClientboundTakeItemEntityPacket = "net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket"
    val ClientboundBlockEventPacket = "net.minecraft.network.protocol.game.ClientboundBlockEventPacket"
    val ClientboundSetPassengersPacket = "net.minecraft.network.protocol.game.ClientboundSetPassengersPacket"
    val ClientboundBlockDestructionPacket = "net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket"
    val ClientboundUpdateAdvancementsPacket = "net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket"
    val ClientboundLevelChunkPacketData = "net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData" // 1.18 and above
    val ClientboundLevelChunkWithLightPacket = "net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket" // 1.18 and above
    val ClientboundLevelChunkPacket = "net.minecraft.network.protocol.game.ClientboundLevelChunkPacket" // 1.17 and below
    val ClientboundLightUpdatePacket = "net.minecraft.network.protocol.game.ClientboundLightUpdatePacket"
    val ClientboundOpenScreenPacket = "net.minecraft.network.protocol.game.ClientboundOpenScreenPacket"
    val ClientboundRespawnPacket = "net.minecraft.network.protocol.game.ClientboundRespawnPacket"
    val ClientboundEntityEventPacket = "net.minecraft.network.protocol.game.ClientboundEntityEventPacket"
    val ClientboundChatPacket = "net.minecraft.network.protocol.game.ClientboundChatPacket"
    val ClientboundSetPlayerTeamPacket = "net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket"
    val ClientboundSetPlayerTeamPacketParameters = "net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket\$Parameters"
    val ClientboundSetDisplayChatPreviewPacket = "net.minecraft.network.protocol.game.ClientboundSetDisplayChatPreviewPacket"
    val ClientboundChatPreviewPacket = "net.minecraft.network.protocol.game.ClientboundChatPreviewPacket"
    val ClientboundPlayerChatPacket = "net.minecraft.network.protocol.game.ClientboundPlayerChatPacket"
    val ClientboundSystemChatPacket = "net.minecraft.network.protocol.game.ClientboundSystemChatPacket"
    val ClientboundKeepAlivePacket = "net.minecraft.network.protocol.game.ClientboundKeepAlivePacket"
    val ClientboundSetCameraPacket = "net.minecraft.network.protocol.game.ClientboundSetCameraPacket"
    val ClientboundContainerSetContentPacket = "net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket"
    val ClientboundContainerSetSlotPacket = "net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket"
    val ClientboundLevelParticlesPacket = "net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket"
    val ClientboundSetDisplayObjectivePacket = "net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket"
    val ClientboundSetObjectivePacket = "net.minecraft.network.protocol.game.ClientboundSetObjectivePacket"
    val ClientboundSetScorePacket = "net.minecraft.network.protocol.game.ClientboundSetScorePacket"
    val ClientboundUpdateMobEffectPacket = "net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket"
    val ClientboundRemoveMobEffectPacket = "net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket"
    val ClientboundUpdateAttributesPacket = "net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket"
    val ClientboundBlockUpdatePacket = "net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket"
    val ServerboundPlayerActionPacket = "net.minecraft.network.protocol.game.ServerboundPlayerActionPacket"
    val ServerboundPlayerActionPacketAction = "net.minecraft.network.protocol.game.ServerboundPlayerActionPacket\$Action"
    val ServerboundInteractPacket = "net.minecraft.network.protocol.game.ServerboundInteractPacket"
    val ServerboundInteractPacketAction = "net.minecraft.network.protocol.game.ServerboundInteractPacket\$Action"
    val ServerboundInteractPacketActionType = "net.minecraft.network.protocol.game.ServerboundInteractPacket\$ActionType"
    val ServerboundInteractPacketActionInteractAt = "net.minecraft.network.protocol.game.ServerboundInteractPacket\$InteractionAtLocationAction"
    val ServerboundInteractPacketActionInteract = "net.minecraft.network.protocol.game.ServerboundInteractPacket\$InteractionAction"
    val ServerboundKeepAlivePacket = "net.minecraft.network.protocol.game.ServerboundKeepAlivePacket"
    val ServerboundClientInformationPacket = "net.minecraft.network.protocol.game.ServerboundClientInformationPacket"
    val ServerboundUseItemOnPacket = "net.minecraft.network.protocol.game.ServerboundUseItemOnPacket"
    val ServerPlayer = "net.minecraft.server.level.ServerPlayer"
    val Player = "net.minecraft.world.entity.player.Player"
    val ServerLevel = "net.minecraft.server.level.ServerLevel"
    val ServerLevelAccessor = "net.minecraft.world.level.ServerLevelAccessor"
    val ServerPlayerGameMode = "net.minecraft.server.level.ServerPlayerGameMode"
    val Level = "net.minecraft.world.level.Level"
    val LevelSpigot = "net.minecraft.server.VVV.World"
    val LevelWriter = "net.minecraft.world.level.LevelWriter"
    val LevelChunk = "net.minecraft.world.level.chunk.LevelChunk"
    val ChunkAccess = "net.minecraft.world.level.chunk.ChunkAccess"
    val ChunkStatus = "net.minecraft.world.level.chunk.status.ChunkStatus"
    val LevelLightEngine = "net.minecraft.world.level.lighting.LevelLightEngine"
    val Packet = "net.minecraft.network.protocol.Packet"
    val ServerGamePacketListenerImpl = "net.minecraft.server.network.ServerGamePacketListenerImpl"
    val ServerCommonPacketListenerImpl = "net.minecraft.server.network.ServerCommonPacketListenerImpl" // 1.20.2 and above
    val Connection = "net.minecraft.network.Connection"
    val MinecraftServer = "net.minecraft.server.MinecraftServer"
    val GameType = "net.minecraft.world.level.GameType"
//    val MobSpawnType = "net.minecraft.world.entity.MobSpawnType"
    val EntitySpawnReason = "net.minecraft.world.entity.EntitySpawnReason"
    val Pose = "net.minecraft.world.entity.Pose"
    val Vec3 = "net.minecraft.world.phys.Vec3"
    val Vec3i = "net.minecraft.core.Vec3i"
    val Rotations = "net.minecraft.core.Rotations"
    val Mob = "net.minecraft.world.entity.Mob"
    val Entity = "net.minecraft.world.entity.Entity"
    val ServerEntity = "net.minecraft.server.level.ServerEntity"
    val LivingEntity = "net.minecraft.world.entity.LivingEntity"
    val BlockEntity = "net.minecraft.world.level.block.entity.BlockEntity"
    val SpawnerBlockEntity = "net.minecraft.world.level.block.entity.SpawnerBlockEntity"
    val BaseSpawner = "net.minecraft.world.level.BaseSpawner"
    val SpawnData = "net.minecraft.world.level.SpawnData"
    val EntityType = "net.minecraft.world.entity.EntityType"
    val EquipmentSlot = "net.minecraft.world.entity.EquipmentSlot"
    val InteractionHand = "net.minecraft.world.InteractionHand"
    val BlockPos = "net.minecraft.core.BlockPos"
    val ChunkPos = "net.minecraft.world.level.ChunkPos"
    val Direction = "net.minecraft.core.Direction"
    val BlockState = "net.minecraft.world.level.block.state.BlockState"
    val BlockBehaviour = "net.minecraft.world.level.block.state.BlockBehaviour"
    val BlockStateBase = "net.minecraft.world.level.block.state.BlockBehaviour\$BlockStateBase"
    val Blocks = "net.minecraft.world.level.block.Blocks"
    val Block = "net.minecraft.world.level.block.Block"
    val Component = "net.minecraft.network.chat.Component"
    val IChatBaseComponent = "net.minecraft.server.VVV.IChatBaseComponent"
    val ComponentSerializer = "net.minecraft.network.chat.Component\$Serializer"
    val Item = "net.minecraft.world.item.Item"
    val ItemStack = "net.minecraft.world.item.ItemStack"
    val LegacyItemStack = "net.minecraft.item.ItemStack" // Searge // 1.8
    val Potion = "net.minecraft.world.item.alchemy.Potion"
    val Potions = "net.minecraft.world.item.alchemy.Potions"
    val PotionContents = "net.minecraft.world.item.alchemy.PotionContents"
    val SynchedEntityData = "net.minecraft.network.syncher.SynchedEntityData"
    val DataItem = "net.minecraft.network.syncher.SynchedEntityData\$DataItem"
    val Tag = "net.minecraft.nbt.Tag"
    val CompoundTag = "net.minecraft.nbt.CompoundTag"
    val NBTTagCompound = "net.minecraft.server.VVV.NBTTagCompound"
    val ListTag = "net.minecraft.nbt.ListTag"
    val StringTag = "net.minecraft.nbt.StringTag"
    val TagParser = "net.minecraft.nbt.TagParser"
    val EntityDataSerializer = "net.minecraft.network.syncher.EntityDataSerializer"
    val EntityDataSerializers = "net.minecraft.network.syncher.EntityDataSerializers"
    val EntityDataAccessor = "net.minecraft.network.syncher.EntityDataAccessor"
    val ResourceLocation = "net.minecraft.resources.ResourceLocation"
    val Identifier = "net.minecraft.resources.Identifier" // 1.21.11
    val ResourceKey = "net.minecraft.resources.ResourceKey"
    val Advancement = "net.minecraft.advancements.Advancement"
    val AdvancementHolder = "net.minecraft.advancements.AdvancementHolder" // 1.20.2 and above
    val AdvancementBuilder = "net.minecraft.advancements.Advancement\$Builder"
    val AdvancementProgress = "net.minecraft.advancements.AdvancementProgress"
    val AdvancementRequirements = "net.minecraft.advancements.AdvancementRequirements"
    val ServerAdvancementManager = "net.minecraft.server.ServerAdvancementManager"
    val FrameType = "net.minecraft.advancements.FrameType"
    val DeserializationContext = "net.minecraft.advancements.critereon.DeserializationContext"
    val PredicateManager = "net.minecraft.world.level.storage.loot.PredicateManager"
    val LootDataManager = "net.minecraft.world.level.storage.loot.LootDataManager" // 1.20.1 and above
    val GsonHelper = "net.minecraft.util.GsonHelper"
    val CreativeModeTab = "net.minecraft.world.item.CreativeModeTab"
    val AbstractContainerMenu = "net.minecraft.world.inventory.AbstractContainerMenu"
    val MenuType = "net.minecraft.world.inventory.MenuType"
    val DimensionType = "net.minecraft.world.level.dimension.DimensionType"
    val ParticleOptions = "net.minecraft.core.particles.ParticleOptions"
    val DifficultyInstance = "net.minecraft.world.DifficultyInstance"
    val SpawnGroupData = "net.minecraft.world.entity.SpawnGroupData"
    val ChatType = "net.minecraft.network.chat.ChatType"
    val ChatTypeBound = "net.minecraft.network.chat.ChatType\$Bound"
    val VillagerData = "net.minecraft.world.entity.npc.villager.VillagerData" // 1.21.11
    val VillagerType = "net.minecraft.world.entity.npc.villager.VillagerType" // 1.21.11
    val VillagerProfession = "net.minecraft.world.entity.npc.villager.VillagerProfession" // 1.21.11
    val ChatFormatting = "net.minecraft.ChatFormatting"
    val BoatType = "net.minecraft.world.entity.vehicle.Boat\$Type"
    val Registry = "net.minecraft.core.Registry"
    val BuiltInRegistries = "net.minecraft.core.registries.BuiltInRegistries"
    val MappedRegistry = "net.minecraft.core.MappedRegistry"
    val WritableRegistry = "net.minecraft.core.WritableRegistry"
    val RegistryAccess = "net.minecraft.core.RegistryAccess"
    val BuiltinRegistries = "net.minecraft.data.BuiltinRegistries"
    val CoreBuiltInRegistries = "net.minecraft.core.registries.BuiltInRegistries"
    val Holder = "net.minecraft.core.Holder"
    val Biome = "net.minecraft.world.level.biome.Biome"
    val BiomeBuilder = "net.minecraft.world.level.biome.Biome\$BiomeBuilder"
    val BiomeCategory = "net.minecraft.world.level.biome.Biome\$BiomeCategory"
    val BiomePrecipitation = "net.minecraft.world.level.biome.Biome\$Precipitation"
    val TemperatureModifier = "net.minecraft.world.level.biome.Biome\$TemperatureModifier"
    val BiomeGenerationSettings = "net.minecraft.world.level.biome.BiomeGenerationSettings"
    val BiomeSpecialEffects = "net.minecraft.world.level.biome.BiomeSpecialEffects"
    val BiomeSpecialEffectsBuilder = "net.minecraft.world.level.biome.BiomeSpecialEffects\$Builder"
    val BiomeSpecialEffectsGrassColorModifier = "net.minecraft.world.level.biome.BiomeSpecialEffects\$GrassColorModifier"
    val MobSpawnSettings = "net.minecraft.world.level.biome.MobSpawnSettings"
    val SoundEvent = "net.minecraft.sounds.SoundEvent"
    val SoundType = "net.minecraft.world.level.block.SoundType"
    val ChatSender = "net.minecraft.network.chat.ChatSender"
    val CryptSaltSignaturePair = "net.minecraft.util.Crypt\$SaltSignaturePair"
    val PlayerChatMessage = "net.minecraft.network.chat.PlayerChatMessage"
    val ProfilePublicKey = "net.minecraft.world.entity.player.ProfilePublicKey"
    val NonNullList = "net.minecraft.core.NonNullList"
    val MobEffectInstance = "net.minecraft.world.effect.MobEffectInstance"
    val MobEffect = "net.minecraft.world.effect.MobEffect"
    val MobEffects = "net.minecraft.world.effect.MobEffects"
    val Scoreboard = "net.minecraft.world.scores.Scoreboard"
    val PlayerTeam = "net.minecraft.world.scores.PlayerTeam"
    val Team = "net.minecraft.world.scores.Team"
    val CollisionRule = "net.minecraft.world.scores.Team\$CollisionRule"
    val Visibility = "net.minecraft.world.scores.Team\$Visibility"
    val DyeColor = "net.minecraft.world.item.DyeColor"
    val SignText = "net.minecraft.world.level.block.entity.SignText"
    val EnumParticle = "net.minecraft.server.VVV.EnumParticle"
    val ParticleType = "net.minecraft.core.particles.ParticleType"
    val PositionSource = "net.minecraft.world.level.gameevent.PositionSource"
    val BlockPositionSource = "net.minecraft.world.level.gameevent.BlockPositionSource"
    val EntityPositionSource = "net.minecraft.world.level.gameevent.EntityPositionSource"
    val VibrationPath = "net.minecraft.world.level.gameevent.vibrations.VibrationPath"
    val DustParticleOptions = "net.minecraft.core.particles.DustParticleOptions"
    val DustColorTransitionOptions = "net.minecraft.core.particles.DustColorTransitionOptions"
    val BlockParticleOption = "net.minecraft.core.particles.BlockParticleOption"
    val ItemParticleOption = "net.minecraft.core.particles.ItemParticleOption"
    val VibrationParticleOption = "net.minecraft.core.particles.VibrationParticleOption"
    val ShriekParticleOption = "net.minecraft.core.particles.ShriekParticleOption"
    val SculkChargeParticleOptions = "net.minecraft.core.particles.SculkChargeParticleOptions"
    val Vector3f = "com.mojang.math.Vector3f"
    val Objective = "net.minecraft.world.scores.Objective"
    val ObjectiveCriteria = "net.minecraft.world.scores.criteria.ObjectiveCriteria"
    val ObjectiveCriteriaRenderType = "net.minecraft.world.scores.criteria.ObjectiveCriteria\$RenderType"
    val ServerScoreboardMethod = "net.minecraft.server.ServerScoreboard\$Method"
    val RemoteChatSessionData = "net.minecraft.network.chat.RemoteChatSession\$Data"
    val ClientInformation = "net.minecraft.server.level.ClientInformation"
    val ClientBrandRetriever = "net.minecraft.client.ClientBrandRetriever"
    val PacketFlow = "net.minecraft.network.protocol.PacketFlow"
    val CommonListenerCookie = "net.minecraft.server.network.CommonListenerCookie"
    val GameProfile = "com.mojang.authlib.GameProfile"
    val Lifecycle = "com.mojang.serialization.Lifecycle"
    val NumberFormat = "net.minecraft.network.chat.numbers.NumberFormat"
    val BlankFormat = "net.minecraft.network.chat.numbers.BlankFormat"
    val DisplaySlot = "net.minecraft.world.scores.DisplaySlot"
    val Display = "net.minecraft.world.entity.Display"
    val BlockDisplay = "net.minecraft.world.entity.Display\$BlockDisplay"
    val ItemDisplay = "net.minecraft.world.entity.Display\$ItemDisplay"
    val TextDisplay = "net.minecraft.world.entity.Display\$TextDisplay"
    val BillboardConstraints = "net.minecraft.world.entity.Display\$BillboardConstraints"
    val ItemDisplayContext = "net.minecraft.world.item.ItemDisplayContext"
    val AttributeInstance = "net.minecraft.world.entity.ai.attributes.AttributeInstance"
    val Attributes = "net.minecraft.world.entity.ai.attributes.Attributes"
    val MessageSignature = "net.minecraft.network.chat.MessageSignature"
    val SignedMessageBodyPacked = "net.minecraft.network.chat.SignedMessageBody\$Packed"
    val LastSeenMessagesPacked = "net.minecraft.network.chat.LastSeenMessages\$Packed"
    val FilterMask = "net.minecraft.network.chat.FilterMask"
    val BlockHitResult = "net.minecraft.world.phys.BlockHitResult"
    val HitResultType = "net.minecraft.world.phys.HitResult\$Type"

    val CrossbowItem = "net.minecraft.world.item.CrossbowItem"
    val ArmorStand = "net.minecraft.world.entity.decoration.ArmorStand"
    val Arrow = "net.minecraft.world.entity.projectile.arrow.Arrow" // 1.21.11
    val AbstractThrownPotion = "net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion" // 1.21.11
    val ThrownTrident = "net.minecraft.world.entity.projectile.arrow.ThrownTrident" // 1.21.11
    val ThrowableItemProjectile = "net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile" // 1.21.11
    val ItemEntity = "net.minecraft.world.entity.item.ItemEntity"
    val FallingBlockEntity = "net.minecraft.world.entity.item.FallingBlockEntity"
    val AreaEffectCloud = "net.minecraft.world.entity.AreaEffectCloud"
    val FishingHook = "net.minecraft.world.entity.projectile.FishingHook"
    val LegacyFishingHook = "spigot:EntityFishingHook"
    val FireworkRocketEntity = "net.minecraft.world.entity.projectile.FireworkRocketEntity"
    val LightningBolt = "net.minecraft.world.entity.LightningBolt"
    val SignBlockEntity = "net.minecraft.world.level.block.entity.SignBlockEntity"
    val Villager = "net.minecraft.world.entity.npc.villager.Villager" // 1.21.11
    val Boat = "net.minecraft.world.entity.vehicle.boat.Boat" // 1.21.11
    val Creeper = "net.minecraft.world.entity.monster.Creeper"
    val Zombie = "net.minecraft.world.entity.monster.zombie.Zombie" // 1.21.11
    // 1.8
    val EntityPlayer = "net.minecraft.entity.player.EntityPlayerMP"
    val World = "net.minecraft.world.World"

    mapClass(ClientboundPlayerInfoUpdatePacket) {
        constructor(ClientboundPlayerInfoUpdatePacketAction, ServerPlayer)
        constructor("net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket\$Action", "$ServerPlayer[]")
        constructor(EnumSet::class, Collection::class)
        fieldInferred("entries", "1.20.4")
        methodInferred("createPlayerInitializing", "1.20.4", Collection::class)
        methodInferred("entries", "1.20.4")
    }
    mapClass(ClientboundPlayerInfoUpdatePacketEntry) {
        constructor(UUID::class, GameProfile, Boolean::class, Int::class, GameType, Component, RemoteChatSessionData)
        constructor(UUID::class, GameProfile, Boolean::class, Int::class, GameType, Component, Int::class, RemoteChatSessionData)
        constructor(UUID::class, GameProfile, Boolean::class, Int::class, GameType, Component, Boolean::class, Int::class, RemoteChatSessionData)
    }
    mapClass(ClientboundPlayerInfoRemovePacket) {
        constructor(List::class)
        fieldInferred("profileIds", "1.20.4")
    }
    mapClass(ClientboundPlayerInfoUpdatePacketAction) {
        enumConstant(
            "ADD_PLAYER",
            "INITIALIZE_CHAT",
            "UPDATE_GAME_MODE",
            "UPDATE_LISTED",
            "UPDATE_LATENCY",
            "UPDATE_DISPLAY_NAME"
        )
        fieldInferred("REMOVE_PLAYER", "1.19")
    }
    mapClass(ClientboundAddMobPacket) {
        constructor(LivingEntity)
    }
    mapClass(ClientboundAddEntityPacket) {
        constructor(Int::class, UUID::class, Double::class, Double::class, Double::class, Float::class, Float::class, EntityType, Int::class, Vec3)
        constructor(Entity, Int::class)
        constructor(Entity)
        // 1.21 - start
        constructor(Entity, ServerEntity)
        constructor(Entity, ServerEntity, Int::class)
        constructor(Int::class, UUID::class, Double::class, Double::class, Double::class, Float::class, Float::class, EntityType, Int::class, Vec3, Double::class)
        // 1.21 - end
        methodInferred("getId", "1.20.4")
    }
    mapClass(ClientboundAddPlayerPacket) {
        constructor(Player)
        methodInferred("getEntityId", "1.20.1")
        methodInferred("getPlayerId", "1.20.1")
        methodInferred("getX", "1.20.1")
        methodInferred("getY", "1.20.1")
        methodInferred("getZ", "1.20.1")
        methodInferred("getyRot", "1.20.1")
        methodInferred("getxRot", "1.20.1")
    }
    mapClass(ClientboundRotateHeadPacket) {
        constructor(Entity, Byte::class)
        methodInferred("getEntity", "1.20.4", Level)
        methodInferred("getYHeadRot", "1.20.4")
    }
    mapClass(ClientboundRemoveEntitiesPacket) {
        constructor(IntArray::class)
    }
    mapClass(ClientboundMoveEntityPacketRot) {
        constructor(Int::class, Byte::class, Byte::class, Boolean::class)
    }
    mapClass(ClientboundMoveEntityPacketPos) {
        constructor(Int::class, Short::class, Short::class, Short::class, Boolean::class)
        constructor(Int::class, Long::class, Long::class, Long::class, Boolean::class)
        constructor(Int::class, Byte::class, Byte::class, Byte::class, Boolean::class)
    }
    mapClass(ClientboundMoveEntityPacketPosRot) {
        constructor(Int::class, Short::class, Short::class, Short::class, Byte::class, Byte::class, Boolean::class)
        constructor(Int::class, Long::class, Long::class, Long::class, Byte::class, Byte::class, Boolean::class)
        constructor(Int::class, Byte::class, Byte::class, Byte::class, Byte::class, Byte::class, Boolean::class)
    }
    mapClass(ClientboundAnimatePacket) {
        constructor(Entity, Int::class)
        methodInferred("getId", "1.20.4")
        methodInferred("getAction", "1.20.4")
    }
    mapClass(ClientboundBlockChangedAckPacket) {
        constructor(BlockPos, BlockState, ServerboundPlayerActionPacketAction, Boolean::class)
    }
    mapClass(ClientboundSetEntityDataPacket) {
        constructor(Int::class, SynchedEntityData, Boolean::class)
        constructor(Int::class, List::class) //1.19.3 and higher
        methodInferred("id", "1.20.4")
    }
    mapClass(ClientboundSetEquipmentPacket) {
        constructor(Int::class, List::class)
        constructor(Int::class, EquipmentSlot, ItemStack)
        constructor(Int::class, Int::class, LegacyItemStack)
    }
    mapClass(ClientboundTeleportEntityPacket) {
        constructor(Entity)
        fieldInferred("id", "1.20.4")
        fieldInferred("x", "1.20.4")
        fieldInferred("y", "1.20.4")
        fieldInferred("z", "1.20.4")
        fieldInferred("yRot", "1.20.4")
        fieldInferred("xRot", "1.20.4")
        fieldInferred("onGround", "1.20.4")
    }
    mapClass(ClientboundSetEntityMotionPacket) {
        constructor(Int::class, Vec3)
        constructor(Int::class, Double::class, Double::class, Double::class)
        methodInferred("getId", "1.20.4")
        methodInferred("getXa", "1.20.4")
        methodInferred("getYa", "1.20.4")
        methodInferred("getZa", "1.20.4")
    }
    mapClass(ClientboundTakeItemEntityPacket) {
        constructor(Int::class, Int::class, Int::class)
        constructor(Int::class, Int::class)
        methodInferred("getItemId", "1.20.4")
        methodInferred("getPlayerId", "1.20.4")
        methodInferred("getAmount", "1.20.4")
    }
    mapClass(ClientboundBlockEventPacket) {
        constructor(BlockPos, Block, Int::class, Int::class)
        methodInferred("getPos", "1.20.4")
        methodInferred("getB0", "1.20.4")
        methodInferred("getB1", "1.20.4")
        methodInferred("getBlock", "1.20.4")
    }
    mapClass(ClientboundSetPassengersPacket) {
        constructor(Entity)
        methodInferred("getPassengers", "1.20.4")
        methodInferred("getVehicle", "1.20.4")
        fieldInferred("vehicle", "1.20.4")
        fieldInferred("passengers", "1.20.4")
    }
    mapClass(ClientboundBlockDestructionPacket) {
        constructor(Int::class, BlockPos, Int::class)
        methodInferred("getId", "1.20.4")
        methodInferred("getPos", "1.20.4")
        methodInferred("getProgress", "1.20.4")
    }
    mapClass(ClientboundUpdateAdvancementsPacket) {
        constructor(Boolean::class, Collection::class, Set::class, Map::class)
        methodInferred("getAdded", "1.20.4")
        methodInferred("getRemoved", "1.20.4")
        methodInferred("getProgress", "1.20.4")
        methodInferred("shouldReset", "1.20.4")
    }
    mapClass(ClientboundLevelChunkPacketData) {
        constructor(LevelChunk)
    }
    mapClass(ClientboundLevelChunkWithLightPacket) {
        constructor(LevelChunk, LevelLightEngine, BitSet::class, BitSet::class, Boolean::class)
    }
    mapClass(ClientboundLightUpdatePacket) {
        constructor(ChunkPos, LevelLightEngine, BitSet::class, BitSet::class, Boolean::class)
        constructor(ChunkPos, LevelLightEngine, Boolean::class)
    }
    mapClass(ClientboundOpenScreenPacket) {
        constructor(Int::class, MenuType, Component)
        constructor(Int::class, String::class, IChatBaseComponent, Int::class)
    }
    mapClass(ClientboundRespawnPacket) {
        constructor(DimensionType, ResourceKey, Long::class, GameType, GameType, Boolean::class, Boolean::class, Boolean::class)
        constructor(ResourceKey, ResourceKey, Long::class, GameType, GameType, Boolean::class, Boolean::class, Byte::class, Optional::class)
    }
    mapClass(ClientboundEntityEventPacket) {
        constructor(Entity, Byte::class)
    }
    mapClass(ClientboundChatPacket) {
        constructor(Component, ChatType, UUID::class)
        constructor(Component, ChatType)
        constructor(IChatBaseComponent, Byte::class)
        methodInferred("getMessage", "1.16.5")
        methodInferred("getType", "1.16.5")
        methodInferred("getSender", "1.16.5")
        method(Byte::class, "func_179841_c")
        field(Byte::class, "field_179842_b")
        fieldInferred("message", "1.16.5")
    }
    mapClass(ClientboundSetPlayerTeamPacket) {
        constructor(String::class, Int::class, Optional::class, Collection::class)
        constructor()
        fieldInferred("name", "1.20.4")
        fieldInferred("displayName", "1.16.5")
        fieldInferred("playerPrefix", "1.16.5")
        fieldInferred("playerSuffix", "1.16.5")
        fieldInferred("nametagVisibility", "1.16.5")
        fieldInferred("collisionRule", "1.16.5")
        fieldInferred("color", "1.16.5")
        fieldInferred("players", "1.16.5")
        fieldInferred("method", "1.16.5")
        fieldInferred("options", "1.16.5")
    }
    mapClass(ClientboundSetPlayerTeamPacketParameters) {
        constructor(PlayerTeam)
    }
    mapClass(ClientboundSetDisplayChatPreviewPacket) {
        constructor(Boolean::class)
        //TODO (removed) methodInferred("enabled", "1.16.5")
    }
    mapClass(ClientboundChatPreviewPacket) {
        constructor(Int::class, Component)
        //TODO (removed) methodInferred("queryId", "1.20.4")
        //TODO (removed) methodInferred("preview", "1.20.4")
    }
    mapClass(ClientboundPlayerChatPacket) {
        constructor(Component, Optional::class, Int::class, ChatSender, Instant::class, CryptSaltSignaturePair)
        constructor(UUID::class, Int::class, MessageSignature, SignedMessageBodyPacked, Component, FilterMask, ChatTypeBound)
        field(UUID::class, "sender")
        field(Int::class, "index")
        field(MessageSignature, "signature")
        field(SignedMessageBodyPacked, "body")
        field(Component, "unsignedContent")
        field(FilterMask, "filterMask")
        field(ChatTypeBound, "chatType")
    }
    mapClass(ClientboundSystemChatPacket) {
        constructor(Component, Boolean::class)
        field(Component, "content")
        method(Component, "content")
        field(Boolean::class, "overlay")
    }
    mapClass(ClientboundSetCameraPacket) {
        fieldInferred("cameraId", "1.20.4")
    }
    mapClass(ClientboundContainerSetContentPacket) {
        constructor(Int::class, Int::class, NonNullList, ItemStack)
        fieldInferred("containerId", "1.20.4")
        fieldInferred("items", "1.20.4")
    }
    mapClass(ClientboundContainerSetSlotPacket) {
        constructor(Int::class, Int::class, Int::class, ItemStack)
        fieldInferred("slot", "1.20.4")
        fieldInferred("itemStack", "1.20.4")
    }
    mapClass(ClientboundLevelParticlesPacket) {
        constructor(EnumParticle, Boolean::class, Float::class, Float::class, Float::class, Float::class, Float::class, Float::class, Float::class, Int::class, IntArray::class) //1.8.8 - 1.12.2
        constructor(ParticleOptions, Boolean::class, Float::class, Float::class, Float::class, Float::class, Float::class, Float::class, Float::class, Int::class) //1.13 - 1.14.4
        constructor(ParticleOptions, Boolean::class, Double::class, Double::class, Double::class, Float::class, Float::class, Float::class, Float::class, Int::class) //1.15 and above
    }
    mapClass(ClientboundSetDisplayObjectivePacket) {
        constructor(Int::class, Objective)
        constructor(DisplaySlot, Objective)
        methodInferred("getObjectiveName", "1.20.4")
    }
    mapClass(ClientboundSetObjectivePacket) {
        constructor(Objective, Int::class)
        fieldInferred("numberFormat", "1.20.6")
    }
    mapClass(ClientboundSetScorePacket) {
        constructor(ServerScoreboardMethod, String::class, String::class, Int::class)
        constructor(String::class, String::class, Int::class, Component, NumberFormat) //1.20.2 - 1.20.3
        constructor(String::class, String::class, Int::class, Optional::class, Optional::class) //1.20.4 and above
    }
    mapClass(ClientboundUpdateMobEffectPacket) {
        constructor(Int::class, MobEffectInstance)
        constructor(Int::class, MobEffectInstance, Boolean::class)
    }
    mapClass(ClientboundRemoveMobEffectPacket) {
        constructor(Int::class, MobEffect)
        constructor(Int::class, Holder)
    }
    mapClass(ClientboundUpdateAttributesPacket) {
        constructor(Int::class, Collection::class)
        constructor(Int::class, List::class)
    }
    mapClass(ClientboundBlockUpdatePacket) {
        method(BlockState, "getBlockState")
        method(BlockPos, "getPos")
    }
    mapClass(ServerboundPlayerActionPacket) {
        method(BlockPos, "getPos")
        method(Direction, "getDirection")
        method(ServerboundPlayerActionPacketAction, "getAction")
        method(Int::class, "getSequence")
    }
    mapClass(ServerboundPlayerActionPacketAction) {
        enumConstant(
            "START_DESTROY_BLOCK",
            "ABORT_DESTROY_BLOCK",
            "STOP_DESTROY_BLOCK",
            "DROP_ALL_ITEMS",
            "DROP_ITEM",
            "RELEASE_USE_ITEM",
            "SWAP_ITEM_WITH_OFFHAND",
        )
    }
    mapClass(ServerboundInteractPacket) {
        fieldInferred("entityId", "1.20.4")
        fieldInferred("action", "1.20.4")
        fieldInferred("usingSecondaryAction", "1.20.4")
        fieldInferred("location", "1.16.5")
        fieldInferred("hand", "1.16.5")
    }
    mapClass(ServerboundInteractPacketAction) {
        methodInferred("getType", "1.20.4")
        fieldInferred("INTERACT", "1.16.5")
        fieldInferred("ATTACK", "1.16.5")
        fieldInferred("INTERACT_AT", "1.16.5")
    }
    mapClass(ServerboundInteractPacketActionType) {
        enumConstant(
            "INTERACT",
            "ATTACK",
            "INTERACT_AT"
        )
    }
    mapClass(ServerboundInteractPacketActionInteractAt) {
        fieldInferred("hand", "1.20.4")
        fieldInferred("location", "1.20.4")
    }
    mapClass(ServerboundInteractPacketActionInteract) {
        fieldInferred("hand", "1.20.4")
        method(ServerboundInteractPacketActionType, "getType")
    }
    mapClass(ServerboundUseItemOnPacket) {
        method(InteractionHand, "getHand")
        method(BlockHitResult, "getHitResult")
        method(Int::class, "getSequence")
    }
    mapClass(ServerPlayer) {
        constructor(MinecraftServer, ServerLevel, GameProfile)
        constructor(MinecraftServer, ServerLevel, GameProfile, ServerPlayerGameMode)
        constructor(MinecraftServer, ServerLevel, GameProfile, ProfilePublicKey)
        constructor(MinecraftServer, ServerLevel, GameProfile, ClientInformation) //1.20.2 and above
        methodInferred("setCamera", "1.20.4", Entity)
        methodInferred("refreshContainer", "1.16.5", AbstractContainerMenu)
        fieldInferred("connection", "1.20.4")
        fieldInferred("latency", "1.16.5")
    }
    mapClass(Player) {
        methodInferred("setEntityOnShoulder", "1.20.4", CompoundTag)
        methodInferred("setShoulderEntityRight", "1.20.4", CompoundTag)
        methodInferred("setShoulderEntityLeft", "1.20.4", CompoundTag)
        methodInferred("getGameProfile", "1.20.4")
        methodInferred("playSound", "1.20.4", SoundEvent, Float::class, Float::class)
        fieldInferred("containerMenu", "1.20.4")
        fieldInferred("DATA_PLAYER_MODE_CUSTOMISATION", "1.20.4")
    }
    mapClass(ServerLevel) {
        methodInferred("getSeed", "1.20.4")
        methodInferred("addFreshEntity", "1.20.4", Entity)
    }
    mapClass(ServerPlayerGameMode) {
        constructor(ServerLevel)
        constructor(LevelSpigot)
    }
    mapClass(Level) {
        methodInferred("getChunkAt", "1.20.4", BlockPos)
        methodInferred("getChunk", "1.20.4", Int::class, Int::class)
        methodInferred("func_175703_c", "1.8.8", "net.minecraft.util.BlockPos")
        methodInferred("getBlockState", "1.20.4", BlockPos)
        methodInferred("getLightEngine", "1.20.4")
        methodInferred("dimension", "1.20.4")
        methodInferred("dimensionType", "1.20.4")
        methodInferred("dimensionTypeId", "1.20.4")
        methodInferred("getBlockEntity", "1.20.4", BlockPos)
        methodInferred("getCurrentDifficultyAt", "1.20.4", BlockPos)
    }
    mapClass(LevelChunk) {
        methodInferred("getBlockState", "1.18.2", BlockPos)
        methodInferred("getFluidState", "1.18.2", BlockPos)
        methodInferred("setBlockState", "1.18.2", BlockPos, BlockState, Boolean::class)
        methodInferred("getBlockEntityNbtForSaving", "1.18.2", BlockPos)
        methodInferred("getLevel", "1.18.2")
    }
    mapClass(ChunkAccess) {
        methodInferred("getPos", "1.20.4")
    }
    mapClass(ChunkStatus) {
        fieldInferred("EMPTY", "1.18.2")
        fieldInferred("FULL", "1.18.2")
        fieldInferred("BIOMES", "1.18.2")
        methodInferred("byName", "1.18.2", String::class)
    }
    mapClass(LevelLightEngine) {
        methodInferred("checkBlock", "1.20.4", BlockPos)
    }
    mapClass(Player) {
        methodInferred("setEntityOnShoulder", "1.20.4", CompoundTag)
        methodInferred("setShoulderEntityRight", "1.20.4", CompoundTag)
        methodInferred("setShoulderEntityLeft", "1.20.4", CompoundTag)
        methodInferred("getGameProfile", "1.20.4")
        methodInferred("playSound", "1.20.4", SoundEvent, Float::class, Float::class)
        fieldInferred("containerMenu", "1.20.4")
        fieldInferred("DATA_PLAYER_MODE_CUSTOMISATION", "1.20.4")
    }
    mapClass(ServerLevel) {
        methodInferred("getSeed", "1.20.4")
        methodInferred("addFreshEntity", "1.20.4", Entity)
    }
    mapClass(ServerGamePacketListenerImpl) {
        constructor(MinecraftServer, Connection, ServerPlayer, CommonListenerCookie)
        methodInferred("send", "1.16.5", Packet)
        fieldInferred("connection", "1.16.5")
    }
    mapClass(ServerCommonPacketListenerImpl) {
        fieldInferred("connection", "1.20.4")
        method(Void.TYPE, "send", Packet)
        methodInferred("latency", "1.20.4")
    }
    mapClass(Connection) {
        constructor(PacketFlow)
        methodInferred("disconnect", "1.20.4", Component)
        methodInferred("connectToServer", "1.19.2", InetSocketAddress::class, Boolean::class)
        methodInferred("connectToLocalServer", "1.20.4", SocketAddress::class)
        methodInferred("getAverageReceivedPackets", "1.20.4")
        methodInferred("getAverageSentPackets", "1.20.4")
        fieldInferred("channel", "1.20.4")
        fieldInferred("address", "1.20.4")
    }
    mapClass(MinecraftServer) {
        methodInferred("registryAccess", "1.20.4")
    }
    mapClass(GameType) {
        enumConstant(
            "SURVIVAL",
            "CREATIVE",
            "SPECTATOR",
            "ADVENTURE"
        )
        methodInferred("byName", "1.20.4", String::class)
    }
    mapClass(EntitySpawnReason) {
        enumConstant(
            "NATURAL",
            "CHUNK_GENERATION",
            "SPAWNER",
            "STRUCTURE",
            "BREEDING",
            "MOB_SUMMONED",
            "JOCKEY",
            "EVENT",
            "CONVERSION",
            "REINFORCEMENT",
            "TRIGGERED",
            "BUCKET",
//            "SPAWN_EGG",
            "SPAWN_ITEM_USE",
            "COMMAND",
            "DISPENSER",
            "PATROL",
        )
    }
    mapClass(Pose) {
        enumConstant(
            "STANDING",
            "FALL_FLYING",
            "SLEEPING",
            "SWIMMING",
            "SPIN_ATTACK",
            "CROUCHING",
            "LONG_JUMPING",
            "DYING",
        )
    }
    mapClass(Vec3) {
        constructor(Double::class, Double::class, Double::class)
        methodInferred("x", "1.20.4")
        methodInferred("y", "1.20.4")
        methodInferred("z", "1.20.4")
        field(Double::class, "x") // below 1.14
        field(Double::class, "y") // below 1.14
        field(Double::class, "z") // below 1.14
    }
    mapClass(Vec3i) {
        methodInferred("getX", "1.20.4")
        methodInferred("getY", "1.20.4")
        methodInferred("getZ", "1.20.4")
    }
    mapClass(Mob) {
        methodInferred("finalizeSpawn", "1.21.3", ServerLevelAccessor, DifficultyInstance, EntitySpawnReason, SpawnGroupData)
    }
    mapClass(Entity) {
        constructor(EntityType, Level)
        methodInferred("getType", "1.20.4")
        methodInferred("getId", "1.20.4")
        methodInferred("setId", "1.20.4", Int::class)
        methodInferred("setPose", "1.20.4", Pose)
        methodInferred("hasPose", "1.20.4", Pose)
        methodInferred("isCrouching", "1.20.4")
        methodInferred("setPos", "1.20.4", Double::class, Double::class, Double::class)
        methodInferred("setRot", "1.20.4", Float::class, Float::class)
        methodInferred("setGlowingTag", "1.20.4", Boolean::class) //1.17 and higher
        methodInferred("hasGlowingTag", "1.20.4")
        methodInferred("setGlowing", "1.16.5", Boolean::class) //1.16.5 and below
        methodInferred("isGlowing", "1.16.5")
        methodInferred("setCustomName", "1.20.4", Component)
        methodInferred("setCustomName", "1.12.2", String::class) // 1.12.2 and below
        methodInferred("getCustomName", "1.20.4")
        methodInferred("setCustomNameVisible", "1.20.4", Boolean::class)
        methodInferred("isCustomNameVisible", "1.20.4")
        methodInferred("setInvisible", "1.20.4", Boolean::class)
        methodInferred("isInvisible", "1.20.4")
        methodInferred("setInvulnerable", "1.20.4", Boolean::class)
        methodInferred("setIsInPowderSnow", "1.20.4", Boolean::class)
        methodInferred("setItemSlot", "1.20.4", EquipmentSlot, ItemStack)
        methodInferred("setNoGravity", "1.20.4", Boolean::class)
        methodInferred("isNoGravity", "1.20.4")
        methodInferred("setOnGround", "1.20.4", Boolean::class)
        methodInferred("isOnGround", "1.16.5")
        methodInferred("setSprinting", "1.20.4", Boolean::class)
        methodInferred("isSprinting", "1.20.4")
        methodInferred("setSwimming", "1.20.4", Boolean::class)
        methodInferred("isSwimming", "1.20.4")
        methodInferred("setTicksFrozen", "1.20.4", Int::class) //1.17 and higher
        methodInferred("getTicksFrozen", "1.20.4") //1.17 and higher
        methodInferred("setUUID", "1.20.4", UUID::class)
        methodInferred("getUUID", "1.20.4")
        methodInferred("getEntityData", "1.20.4")
        methodInferred("setSharedFlag", "1.20.4", Int::class, Boolean::class)
        methodInferred("getSharedFlag", "1.20.4", Int::class)
        methodInferred("moveTo", "1.20.4", Double::class, Double::class, Double::class)
        methodInferred("level", "1.21.4")
        fieldInferred("position", "1.20.4")
        field(Float::class, "yRot")
        field(Float::class, "xRot")
        fieldInferred("locX", "1.8.8")
        fieldInferred("locY", "1.8.8")
        fieldInferred("locZ", "1.8.8")
        fieldInferred("DATA_CUSTOM_NAME", "1.20.4")
        fieldInferred("DATA_CUSTOM_NAME_VISIBLE", "1.20.4")
        fieldInferred("DATA_SILENT", "1.20.4")
        fieldInferred("DATA_NO_GRAVITY", "1.20.4")
        fieldInferred("DATA_POSE", "1.20.4")
        fieldInferred("DATA_TICKS_FROZEN", "1.20.4")
    }
    mapClass(ServerEntity) {
        constructor(ServerLevel, Entity, Int::class, Boolean::class, Consumer::class)
    }
    mapClass(LivingEntity) {
        methodInferred("setArrowCount", "1.20.4", Int::class)
        methodInferred("getArrowCount", "1.20.4")
        methodInferred("setSleepingPos", "1.20.4", BlockPos)
        methodInferred("getSleepingPos", "1.20.4")
        methodInferred("removeEffectParticles", "1.20.4")
        methodInferred("setStingerCount", "1.20.4", Int::class)
        methodInferred("getStingerCount", "1.20.4")
        methodInferred("triggerItemUseEffects", "1.20.4", ItemStack, Int::class)
        methodInferred("startUsingItem", "1.20.4", InteractionHand)
        methodInferred("stopUsingItem", "1.20.4")
        methodInferred("getUseItem", "1.20.4")
        methodInferred("getUseItemRemainingTicks", "1.20.4")
        methodInferred("setLivingEntityFlag", "1.20.4", Int::class, Boolean::class)
        method(AttributeInstance, "getAttribute", Holder)
        fieldInferred("useItem", "1.20.4")
        fieldInferred("DATA_LIVING_ENTITY_FLAGS", "1.20.4")
        fieldInferred("DATA_HEALTH_ID", "1.20.4")
        fieldInferred("DATA_EFFECT_COLOR_ID", "1.20.4")
        fieldInferred("DATA_EFFECT_AMBIENCE_ID", "1.20.4")
        fieldInferred("DATA_ARROW_COUNT_ID", "1.20.4")
        fieldInferred("DATA_STINGER_COUNT_ID", "1.20.4")
        fieldInferred("SLEEPING_POS_ID", "1.20.4")
    }
    mapClass(SpawnerBlockEntity) {
        methodInferred("getSpawner", "1.20.4")
    }
    mapClass(BaseSpawner) {
        fieldInferred("nextSpawnData", "1.20.4")
    }
    mapClass(SpawnData) {
        methodInferred("getEntityToSpawn", "1.20.4")
    }
    mapClass(EntityType) {
        methodInferred("loadEntityRecursive", "1.20.4", CompoundTag, Level, Function::class)
        enumConstant(
            "ALLAY",
            "AREA_EFFECT_CLOUD",
            "ARMOR_STAND",
            "ARROW",
            "AXOLOTL",
            "BAT",
            "BEE",
            "BLAZE",
            "BLOCK_DISPLAY",
            "BOAT",
            "BREEZE",
            "CAMEL",
            "CAT",
            "CAVE_SPIDER",
            "CHEST_BOAT",
            "CHEST_MINECART",
            "CHICKEN",
            "COD",
            "COMMAND_BLOCK_MINECART",
            "COW",
            "CREEPER",
            "DOLPHIN",
            "DONKEY",
            "DRAGON_FIREBALL",
            "DROWNED",
            "EGG",
            "ELDER_GUARDIAN",
            "END_CRYSTAL",
            "ENDER_DRAGON",
            "ENDER_PEARL",
            "ENDERMAN",
            "ENDERMITE",
            "EVOKER",
            "EVOKER_FANGS",
            "EXPERIENCE_BOTTLE",
            "EXPERIENCE_ORB",
            "EYE_OF_ENDER",
            "FALLING_BLOCK",
            "FIREWORK_ROCKET",
            "FOX",
            "FROG",
            "FURNACE_MINECART",
            "GHAST",
            "GIANT",
            "GLOW_ITEM_FRAME",
            "GLOW_SQUID",
            "GOAT",
            "GUARDIAN",
            "HOGLIN",
            "HOPPER_MINECART",
            "HORSE",
            "HUSK",
            "ILLUSIONER",
            "INTERACTION",
            "IRON_GOLEM",
            "ITEM",
            "ITEM_DISPLAY",
            "ITEM_FRAME",
            "FIREBALL",
            "LEASH_KNOT",
            "LIGHTNING_BOLT",
            "LLAMA",
            "LLAMA_SPIT",
            "MAGMA_CUBE",
            "MARKER",
            "MINECART",
            "MOOSHROOM",
            "MULE",
            "OCELOT",
            "PAINTING",
            "PANDA",
            "PARROT",
            "PHANTOM",
            "PIG",
            "PIGLIN",
            "PIGLIN_BRUTE",
            "PILLAGER",
            "POLAR_BEAR",
            "POTION",
            "PUFFERFISH",
            "RABBIT",
            "RAVAGER",
            "SALMON",
            "SHEEP",
            "SHULKER",
            "SHULKER_BULLET",
            "SILVERFISH",
            "SKELETON",
            "SKELETON_HORSE",
            "SLIME",
            "SMALL_FIREBALL",
            "SNIFFER",
            "SNOW_GOLEM",
            "SNOWBALL",
            "SPAWNER_MINECART",
            "SPECTRAL_ARROW",
            "SPIDER",
            "SQUID",
            "STRAY",
            "STRIDER",
            "TADPOLE",
            "TEXT_DISPLAY",
            "TNT",
            "TNT_MINECART",
            "TRADER_LLAMA",
            "TRIDENT",
            "TROPICAL_FISH",
            "TURTLE",
            "VEX",
            "VILLAGER",
            "VINDICATOR",
            "WANDERING_TRADER",
            "WARDEN",
            "WIND_CHARGE",
            "WITCH",
            "WITHER",
            "WITHER_SKELETON",
            "WITHER_SKULL",
            "WOLF",
            "ZOGLIN",
            "ZOMBIE",
            "ZOMBIE_HORSE",
            "ZOMBIE_VILLAGER",
            "ZOMBIFIED_PIGLIN",
            "PLAYER",
            "FISHING_BOBBER",
            "BLOCK_DISPLAY",
            "ITEM_DISPLAY",
            "TEXT_DISPLAY"
        )
    }
    mapClass(EquipmentSlot) {
        enumConstant(
            "MAINHAND",
            "OFFHAND",
            "FEET",
            "LEGS",
            "CHEST",
            "HEAD",
        )
    }
    mapClass(InteractionHand) {
        enumConstant(
            "MAIN_HAND",
            "OFF_HAND"
        )
    }
    mapClass(BlockPos) {
        constructor(Int::class, Int::class, Int::class)
        constructor(Double::class, Double::class, Double::class)
        constructor(Vec3)
    }
    mapClass(ChunkPos) {
        constructor(Int::class, Int::class)
        methodInferred("getMiddleBlockX", "1.20.4")
        methodInferred("getMiddleBlockZ", "1.20.4")
        methodInferred("getMinBlockX", "1.20.4")
        methodInferred("getMinBlockZ", "1.20.4")
        methodInferred("getMaxBlockX", "1.20.4")
        methodInferred("getMaxBlockZ", "1.20.4")
        methodInferred("getBlockX", "1.20.4", Int::class)
        methodInferred("getBlockZ", "1.20.4", Int::class)
    }
    mapClass(Direction) {
        methodInferred("getName", "1.20.4")
        enumConstant(
            "DOWN",
            "UP",
            "NORTH",
            "SOUTH",
            "WEST",
            "EAST"
        )
    }
    mapClass(BlockState) {
        methodInferred("getBlock", "1.12.2")
    }
    mapClass(BlockStateBase) {
        methodInferred("getBlock", "1.20.4")
    }
    mapClass(Blocks)
    mapClass(Rotations) {
        constructor(Float::class, Float::class, Float::class)
        methodInferred("getX", "1.20.4")
        methodInferred("getY", "1.20.4")
        methodInferred("getZ", "1.20.4")
        methodInferred("getWrappedX", "1.20.4")
        methodInferred("getWrappedY", "1.20.4")
        methodInferred("getWrappedZ", "1.20.4")
    }
    mapClass(Block) {
        fieldInferred("stepSound", "1.8.8")
        methodInferred("byItem", "1.20.4", Item)
        methodInferred("getById", "1.8.8", Int::class)
        methodInferred("getSoundType", "1.20.4", BlockState)
        methodInferred("defaultBlockState", "1.20.4")
        methodInferred("getId", "1.20.4", BlockState)
    }
    mapClass(Component) {
        methodInferred("getStyle", "1.20.4")
        methodInferred("getContents", "1.20.4")
        methodInferred("getString", "1.20.4")
        methodInferred("getSiblings", "1.20.4")
        methodInferred("plainCopy", "1.20.4")
        methodInferred("copy", "1.20.4")
    }
    mapClass(ComponentSerializer) {
        methodInferred("fromJsonLenient", "1.20.4", String::class)
    }
    mapClass(Item) {
        methodInferred("getItemCategory", "1.18.2")
    }
    mapClass(ItemStack) {
        constructor(CompoundTag)
        fieldInferred("EMPTY", "1.20.4")
        fieldInferred("TAG_ENCH", "1.20.4")
        fieldInferred("TAG_DISPLAY", "1.20.4")
        fieldInferred("TAG_DISPLAY_NAME", "1.20.4")
        fieldInferred("TAG_LORE", "1.20.4")
        fieldInferred("TAG_DAMAGE", "1.20.4")
        fieldInferred("TAG_COLOR", "1.20.4")
        fieldInferred("TAG_UNBREAKABLE", "1.20.4")
        fieldInferred("TAG_REPAIR_COST", "1.20.4")
        fieldInferred("TAG_CAN_DESTROY_BLOCK_LIST", "1.20.4")
        fieldInferred("TAG_CAN_PLACE_ON_BLOCK_LIST", "1.20.4")
        fieldInferred("TAG_HIDE_FLAGS", "1.20.4")
        methodInferred("createStack", "1.10.2", NBTTagCompound)
        methodInferred("of", "1.20.4", CompoundTag)
        methodInferred("getTag", "1.20.4")
        methodInferred("getOrCreateTag", "1.20.4")
        methodInferred("setTag", "1.20.4", CompoundTag)
        methodInferred("getHoverName", "1.20.4")
        methodInferred("getDisplayName", "1.20.4")
        methodInferred("getItem", "1.20.4")
        methodInferred("save", "1.20.4", CompoundTag)
    }
    mapClass(Potion) {
        fieldInferred("name", "1.20.4")
    }
    mapClass(PotionContents) {
        methodInferred("getMobEffects", "1.20.4", ItemStack)
        methodInferred("getColor", "1.20.4", ItemStack)
        methodInferred("getPotion", "1.20.4", ItemStack)
        methodInferred("getPotion", "1.20.4", CompoundTag)
        methodInferred("setPotion", "1.20.4", ItemStack, Potion)
    }
    mapClass(SynchedEntityData) {
        constructor(Entity)
        fieldInferred("itemsById", "1.20.4")
        field("$DataItem[]", "itemsById")
        methodInferred("packDirty", "1.20.4") //1.19.3 and higher
        methodInferred("getNonDefaultValues", "1.20.4") //1.19.3 and higher
        methodInferred("define", "1.20.4", EntityDataAccessor, Object::class)
        methodInferred("defineId", "1.20.4", Class::class, EntityDataSerializer)
        methodInferred("set", "1.20.4", EntityDataAccessor, Object::class)
        methodInferred("get", "1.20.4", EntityDataAccessor)
        methodInferred("getItem", "1.20.4", EntityDataAccessor)
        // TODO
        methodInferred("func_75682_a", "1.8.8", Int::class, Object::class)
        methodInferred("add", "1.8.8", Int::class, Int::class)
        methodInferred("watch", "1.8.8", Int::class, Object::class)
    }
    mapClass(DataItem) {
        constructor(EntityDataAccessor, Object::class)
        fieldInferred("initialValue", "1.20.4")
        methodInferred("getAccessor", "1.20.4")
        methodInferred("setValue", "1.20.4", Object::class)
        methodInferred("getValue", "1.20.4")
        methodInferred("isSetToDefault", "1.20.4")
        methodInferred("value", "1.20.4")
    }
    mapClass(Tag) {
        fieldInferred("OBJECT_HEADER", "1.20.4")
        fieldInferred("ARRAY_HEADER", "1.20.4")
        fieldInferred("OBJECT_REFERENCE", "1.20.4")
        fieldInferred("STRING_SIZE", "1.20.4")
        fieldInferred("TAG_END", "1.20.4")
        fieldInferred("TAG_BYTE", "1.20.4")
        fieldInferred("TAG_SHORT", "1.20.4")
        fieldInferred("TAG_INT", "1.20.4")
        fieldInferred("TAG_LONG", "1.20.4")
        fieldInferred("TAG_FLOAT", "1.20.4")
        fieldInferred("TAG_DOUBLE", "1.20.4")
        fieldInferred("TAG_BYTE_ARRAY", "1.20.4")
        fieldInferred("TAG_STRING", "1.20.4")
        fieldInferred("TAG_LIST", "1.20.4")
        fieldInferred("TAG_COMPOUND", "1.20.4")
        fieldInferred("TAG_INT_ARRAY", "1.20.4")
        fieldInferred("TAG_LONG_ARRAY", "1.20.4")
        fieldInferred("TAG_ANY_NUMERIC", "1.20.4")
        fieldInferred("MAX_DEPTH", "1.20.4")
    }
    mapClass(CompoundTag) {
        constructor()
        methodInferred("getAllKeys", "1.20.4")
        methodInferred("size", "1.20.4")
        methodInferred("put", "1.20.4", String::class, Tag)
        methodInferred("putString", "1.20.4", String::class, String::class)
        methodInferred("get", "1.20.4", String::class)
        methodInferred("getList", "1.20.4", String::class, Int::class)
        methodInferred("getString", "1.20.4", String::class)
        methodInferred("getCompound", "1.20.4", String::class)
        methodInferred("remove", "1.20.4", String::class)
        methodInferred("copy", "1.20.4")
    }
    mapClass(ListTag) {
        constructor(List::class, Byte::class)
        constructor()
    }
    mapClass(StringTag) {
        constructor(String::class)
    }
    mapClass(TagParser) {
        methodInferred("parseTag", "1.20.4", String::class)
    }
    mapClass(EntityDataSerializer) {
        methodInferred("createAccessor", "1.20.4", Int::class)
    }
    mapClass(EntityDataSerializers) {
        fieldInferred("BYTE", "1.20.4")
        fieldInferred("INT", "1.20.4")
        fieldInferred("FLOAT", "1.20.4")
        fieldInferred("STRING", "1.20.4")
        fieldInferred("COMPONENT", "1.20.4")
        fieldInferred("OPTIONAL_COMPONENT", "1.20.4")
        fieldInferred("ITEM_STACK", "1.20.4")
        fieldInferred("BLOCK_STATE", "1.20.4")
        fieldInferred("BOOLEAN", "1.20.4")
        fieldInferred("PARTICLE", "1.20.4")
        fieldInferred("ROTATIONS", "1.20.4")
        fieldInferred("BLOCK_POS", "1.20.4")
        fieldInferred("OPTIONAL_BLOCK_POS", "1.20.4")
        fieldInferred("DIRECTION", "1.20.4")
        fieldInferred("OPTIONAL_UUID", "1.20.4")
        fieldInferred("COMPOUND_TAG", "1.20.4")
        fieldInferred("VILLAGER_DATA", "1.20.4")
        fieldInferred("OPTIONAL_UNSIGNED_INT", "1.20.4")
        fieldInferred("POSE", "1.20.4")
    }
    mapClass(EntityDataAccessor) {
        methodInferred("getId", "1.20.4")
    }
    mapClass(Identifier) {
        constructor(String::class)
        constructor(String::class, String::class)
        methodInferred("getPath", "1.20.4")
        methodInferred("getNamespace", "1.20.4")
    }
    mapClass(ResourceKey) {
        methodInferred("create", "1.21.11", ResourceKey, Identifier)
    }
    mapClass(Advancement) {
        methodInferred("getDisplay", "1.19.2")
        methodInferred("getRewards", "1.19.2")
        methodInferred("getCriteria", "1.19.2")
        methodInferred("criteria", "1.20.4")
        methodInferred("getId", "1.19.2")
        methodInferred("getRequirements", "1.19.2")
        methodInferred("requirements", "1.20.4")
        methodInferred("getChatComponent", "1.19.2")
        methodInferred("fromJson", "1.20.2", "com.google.gson.JsonObject", DeserializationContext)
        methodInferred("serializeToJson", "1.20.2")
        fieldInferred("CODEC", "1.20.4")
    }
    mapClass(AdvancementHolder) {
        constructor(Identifier, Advancement)
    }
    mapClass(AdvancementBuilder) {
        methodInferred("advancement", "1.19.2")
        methodInferred("parent", "1.19.2", Advancement)
        methodInferred("parent", "1.19.2", ResourceLocation)
        methodInferred("serializeToJson", "1.19.2")
        methodInferred("fromJson", "1.19.2", "com.google.gson.JsonObject", DeserializationContext)
        methodInferred("build", "1.20.1", ResourceLocation) //1.12 - 1.20.1
    }
    mapClass(AdvancementProgress) {
        constructor()
        methodInferred("update", "1.19.4", Map::class, "java.lang.String[][]")
        methodInferred("update", "1.20.2", AdvancementRequirements)
        methodInferred("isDone", "1.20.2")
        methodInferred("grantProgress", "1.20.2", String::class)
        methodInferred("revokeProgress", "1.20.2", String::class)
        methodInferred("getRemainingCriteria", "1.20.2")
    }
    mapClass(AdvancementRequirements) {
        constructor("java.lang.String[][]")
        methodInferred("requirements", "1.20.2")
    }
    mapClass(ServerAdvancementManager) {
        fieldInferred("GSON", "1.20.4")
    }
    mapClass(DeserializationContext) {
        constructor(ResourceLocation, PredicateManager)
        constructor(ResourceLocation, LootDataManager)
    }
    mapClass(PredicateManager) {
        constructor()
    }
    mapClass(LootDataManager) {
        constructor()
    }
    mapClass(GsonHelper) {
        methodInferred("fromJson", "1.18.2", "com.google.gson.Gson", String::class, Class::class)
    }
    mapClass(CreativeModeTab) {
        fieldInferred("langId", "1.18.2")
    }
    mapClass(AbstractContainerMenu) {
        methodInferred("sendAllDataToRemote", "1.20.4")
        fieldInferred("containerId", "1.20.4")
    }
    mapClass(MenuType) {
        enumConstant(
            "GENERIC_9x1",
            "GENERIC_9x2",
            "GENERIC_9x3",
            "GENERIC_9x4",
            "GENERIC_9x5",
            "GENERIC_9x6",
        )
    }
    mapClass(DimensionType) {
        fieldInferred("DEFAULT_OVERWORLD", "1.18.2")
        fieldInferred("DEFAULT_NETHER", "1.18.2")
        fieldInferred("DEFAULT_END", "1.18.2")
    }
    mapClass(ChatType) {
        enumConstant(
            "CHAT",
            "SYSTEM",
            "GAME_INFO"
        )
        field(ResourceKey, "CHAT")
        field(ResourceKey, "SAY_COMMAND")
        field(ResourceKey, "MSG_COMMAND_INCOMING")
        field(ResourceKey, "MSG_COMMAND_OUTGOING")
        field(ResourceKey, "TEAM_MSG_COMMAND_INCOMING")
        field(ResourceKey, "TEAM_MSG_COMMAND_OUTGOING")
        field(ResourceKey, "EMOTE_COMMAND")
    }
    mapClass(ChatTypeBound) {
        constructor(Holder, Component, Optional::class)
    }
    mapClass(VillagerData) {
        constructor(Holder, Holder, Int::class)
        methodInferred("getType", "1.19.2")
        methodInferred("getProfession", "1.19.2")
    }
    mapClass(VillagerData) {
        /*enumConstant(
            "DESERT",
            "JUNGLE",
            "PLAINS",
            "SAVANNA",
            "SNOW",
            "SWAMP",
            "TAIGA",
        ) Before 1.21.11 */

        field(ResourceKey, "DESERT")
        field(ResourceKey, "JUNGLE")
        field(ResourceKey, "PLAINS")
        field(ResourceKey, "SAVANNA")
        field(ResourceKey, "SNOW")
        field(ResourceKey, "SWAMP")
        field(ResourceKey, "TAIGA")
    }
    mapClass(VillagerProfession) {
        /*enumConstant(
            "NONE",
            "ARMORER",
            "BUTCHER",
            "CARTOGRAPHER",
            "CLERIC",
            "FARMER",
            "FISHERMAN",
            "FLETCHER",
            "LEATHERWORKER",
            "LIBRARIAN",
            "MASON",
            "NITWIT",
            "SHEPHERD",
            "TOOLSMITH",
            "WEAPONSMITH",
        ) Before 1.21.11 */
        field(ResourceKey, "NONE")
        field(ResourceKey, "ARMORER")
        field(ResourceKey, "BUTCHER")
        field(ResourceKey, "CARTOGRAPHER")
        field(ResourceKey, "CLERIC")
        field(ResourceKey, "FARMER")
        field(ResourceKey, "FISHERMAN")
        field(ResourceKey, "FLETCHER")
        field(ResourceKey, "LEATHERWORKER")
        field(ResourceKey, "LIBRARIAN")
        field(ResourceKey, "MASON")
        field(ResourceKey, "NITWIT")
        field(ResourceKey, "SHEPHERD")
        field(ResourceKey, "TOOLSMITH")
        field(ResourceKey, "WEAPONSMITH")
    }
    mapClass(ChatFormatting) {
        enumConstant(
            "BLACK",
            "DARK_BLUE",
            "DARK_GREEN",
            "DARK_AQUA",
            "DARK_RED",
            "DARK_PURPLE",
            "GOLD",
            "GRAY",
            "DARK_GRAY",
            "BLUE",
            "GREEN",
            "AQUA",
            "RED",
            "LIGHT_PURPLE",
            "YELLOW",
            "WHITE",
            "OBFUSCATED",
            "BOLD",
            "STRIKETHROUGH",
            "UNDERLINE",
            "ITALIC",
            "RESET",
        )
    }
    mapClass(BoatType) {
        enumConstant(
            "OAK",
            "SPRUCE",
            "BIRCH",
            "JUNGLE",
            "ACACIA",
            "DARK_OAK"
        )
    }
    mapClass(Registry) {
//        fieldInferred("BIOME_REGISTRY", "1.19")
//        fieldInferred("PARTICLE_TYPE", "1.18.2")
//        fieldInferred("BLOCK", "1.18.2")
//        methodInferred("getOrThrow", "1.18.2", ResourceKey)
//        methodInferred("get", "1.20.4", ResourceKey)
//        methodInferred("get", "1.20.4", ResourceLocation)
//        methodInferred("register", "1.18.2", Registry, ResourceLocation, Object::class)
//        methodInferred("register", "1.18.2", Registry, ResourceKey, Object::class)
    }
    mapClass(MappedRegistry) {
        fieldInferred("frozen", "1.18.2")
    }
    mapClass(WritableRegistry) {
        methodInferred("register", "1.18.2", ResourceKey, Object::class, Lifecycle)
    }
    mapClass(RegistryAccess) {
        methodInferred("ownedRegistryOrThrow", "1.18.2", ResourceKey)
    }
    /*mapClass(BuiltinRegistries) {
        fieldInferred("BIOME", "1.18.2")
        methodInferred("register", "1.18.2", Registry, ResourceKey, Object::class)
    }
    mapClass(CoreBuiltInRegistries) {
        fieldInferred("PARTICLE_TYPE", "1.20.4")
        fieldInferred("BLOCK", "1.20.4")
    }*/
    mapClass(Holder) {
        methodInferred("direct", "1.20.6", Object::class)
    }
    mapClass(Biome) {
        fieldInferred("generationSettings", "1.18.2")
        fieldInferred("mobSettings", "1.18.2")
        methodInferred("getPrecipitation", "1.18.2")
        methodInferred("getBiomeCategory", "1.18.2")
        methodInferred("getSpecialEffects", "1.18.2")
    }
    mapClass(BiomeBuilder) {
        constructor()
        methodInferred("from", "1.18.2", Biome)
        methodInferred("precipitation", "1.18.2", BiomePrecipitation)
        methodInferred("biomeCategory", "1.18.2", BiomeCategory)
        methodInferred("temperature", "1.18.2", Float::class)
        methodInferred("downfall", "1.18.2", Float::class)
        methodInferred("specialEffects", "1.18.2", BiomeSpecialEffects)
        methodInferred("mobSpawnSettings", "1.18.2", MobSpawnSettings)
        methodInferred("generationSettings", "1.18.2", BiomeGenerationSettings)
        methodInferred("temperatureAdjustment", "1.18.2", TemperatureModifier)
        methodInferred("build", "1.18.2")
    }
    mapClass(TemperatureModifier) {
        enumConstant(
            "NONE",
            "FROZEN"
        )
    }
    mapClass(BiomeSpecialEffects) {
        methodInferred("getFogColor", "1.18.2")
        methodInferred("getWaterColor", "1.18.2")
        methodInferred("getWaterFogColor", "1.18.2")
        methodInferred("getSkyColor", "1.18.2")
        methodInferred("getFoliageColorOverride", "1.18.2")
        methodInferred("getGrassColorOverride", "1.18.2")
        methodInferred("getGrassColorModifier", "1.18.2")
        methodInferred("getAmbientParticleSettings", "1.18.2")
        methodInferred("getAmbientLoopSoundEvent", "1.18.2")
        methodInferred("getAmbientMoodSettings", "1.18.2")
        methodInferred("getAmbientAdditionsSettings", "1.18.2")
        methodInferred("getBackgroundMusic", "1.18.2")
    }
    mapClass(BiomeSpecialEffectsBuilder) {
        constructor()
        methodInferred("fogColor", "1.18.2", Int::class)
        methodInferred("waterColor", "1.18.2", Int::class)
        methodInferred("waterFogColor", "1.18.2", Int::class)
        methodInferred("skyColor", "1.18.2", Int::class)
        methodInferred("foliageColorOverride", "1.18.2", Int::class)
        methodInferred("grassColorModifier", "1.18.2", BiomeSpecialEffectsGrassColorModifier)
        methodInferred("grassColorOverride", "1.18.2", Int::class)
        methodInferred("build", "1.18.2")
    }
    mapClass(BiomeSpecialEffectsGrassColorModifier) {
        enumConstant(
            "NONE",
            "DARK_FOREST",
            "SWAMP"
        )
    }
    mapClass(SoundEvent) {
        methodInferred("getLocation", "1.20.4")
    }
    mapClass(SoundType) {
        fieldInferred("breakSound", "1.20.4")
        fieldInferred("stepSound", "1.20.4")
        fieldInferred("placeSound", "1.20.4")
        fieldInferred("hitSound", "1.20.4")
        fieldInferred("fallSound", "1.20.4")
    }
    mapClass(PlayerChatMessage) {
        methodInferred("serverContent", "1.19.2")
        methodInferred("signedContent", "1.20.4")
        methodInferred("signature", "1.20.4")
        methodInferred("unsignedContent", "1.20.4")
    }
    mapClass(NonNullList) {
        constructor(List::class, Object::class)
        methodInferred("create",  "1.20.4")
        methodInferred("withSize",  "1.20.4", Int::class, Object::class)
        methodInferred("get",  "1.20.4", Int::class)
        methodInferred("set",  "1.20.4", Int::class, Object::class)
        methodInferred("add",  "1.20.4", Int::class, Object::class)
        methodInferred("remove",  "1.20.4", Int::class)
        methodInferred("size",  "1.20.4")
        methodInferred("clear",  "1.20.4")
    }
    mapClass(MobEffectInstance) {
        constructor(MobEffect, Int::class, Int::class, Boolean::class, Boolean::class, Boolean::class)
        constructor(Holder, Int::class, Int::class, Boolean::class, Boolean::class, Boolean::class)
        fieldInferred("effect", "1.20.4")
        methodInferred("getEffect", "1.20.4")
        methodInferred("getDuration", "1.20.4")
        methodInferred("getAmplifier", "1.20.4")
        methodInferred("isAmbient", "1.20.4")
        methodInferred("isVisible", "1.20.4")
        methodInferred("showIcon", "1.20.4")
    }
    mapClass(MobEffect) {
        methodInferred("getDescriptionId", "1.20.4")
        methodInferred("getDisplayName", "1.20.4")
        methodInferred("getCategory", "1.20.4")
        methodInferred("getColor", "1.20.4")
        methodInferred("byId", "1.16.5", Int::class)
    }
    mapClass(MobEffects) {
        fieldInferred("MOVEMENT_SPEED", "1.20.4")
        fieldInferred("MOVEMENT_SLOWDOWN", "1.20.4")
        fieldInferred("DIG_SPEED", "1.20.4")
        fieldInferred("DIG_SLOWDOWN", "1.20.4")
        fieldInferred("DAMAGE_BOOST", "1.20.4")
        fieldInferred("HEAL", "1.20.4")
        fieldInferred("HARM", "1.20.4")
        fieldInferred("JUMP", "1.20.4")
        fieldInferred("CONFUSION", "1.20.4")
        fieldInferred("REGENERATION", "1.20.4")
        fieldInferred("DAMAGE_RESISTANCE", "1.20.4")
        fieldInferred("FIRE_RESISTANCE", "1.20.4")
        fieldInferred("WATER_BREATHING", "1.20.4")
        fieldInferred("INVISIBILITY", "1.20.4")
        fieldInferred("BLINDNESS", "1.20.4")
        fieldInferred("NIGHT_VISION", "1.20.4")
        fieldInferred("HUNGER", "1.20.4")
        fieldInferred("WEAKNESS", "1.20.4")
        fieldInferred("POISON", "1.20.4")
        fieldInferred("WITHER", "1.20.4")
        fieldInferred("HEALTH_BOOST", "1.20.4")
        fieldInferred("ABSORPTION", "1.20.4")
        fieldInferred("SATURATION", "1.20.4")
        fieldInferred("GLOWING", "1.20.4")
        fieldInferred("LEVITATION", "1.20.4")
        fieldInferred("LUCK", "1.20.4")
        fieldInferred("UNLUCK", "1.20.4")
        fieldInferred("SLOW_FALLING", "1.20.4")
        fieldInferred("CONDUIT_POWER", "1.20.4")
        fieldInferred("DOLPHINS_GRACE", "1.20.4")
        fieldInferred("BAD_OMEN", "1.20.4")
        fieldInferred("HERO_OF_THE_VILLAGE", "1.20.4")
        fieldInferred("DARKNESS", "1.20.4")

        fieldInferred("MOVEMENT_SPEED", "1.20.6")
        fieldInferred("MOVEMENT_SLOWDOWN", "1.20.6")
        fieldInferred("DIG_SPEED", "1.20.6")
        fieldInferred("DIG_SLOWDOWN", "1.20.6")
        fieldInferred("DAMAGE_BOOST", "1.20.6")
        fieldInferred("HEAL", "1.20.6")
        fieldInferred("HARM", "1.20.6")
        fieldInferred("JUMP", "1.20.6")
        fieldInferred("CONFUSION", "1.20.6")
        fieldInferred("REGENERATION", "1.20.6")
        fieldInferred("DAMAGE_RESISTANCE", "1.20.6")
        fieldInferred("FIRE_RESISTANCE", "1.20.6")
        fieldInferred("WATER_BREATHING", "1.20.6")
        fieldInferred("INVISIBILITY", "1.20.6")
        fieldInferred("BLINDNESS", "1.20.6")
        fieldInferred("NIGHT_VISION", "1.20.6")
        fieldInferred("HUNGER", "1.20.6")
        fieldInferred("WEAKNESS", "1.20.6")
        fieldInferred("POISON", "1.20.6")
        fieldInferred("WITHER", "1.20.6")
        fieldInferred("HEALTH_BOOST", "1.20.6")
        fieldInferred("ABSORPTION", "1.20.6")
        fieldInferred("SATURATION", "1.20.6")
        fieldInferred("GLOWING", "1.20.6")
        fieldInferred("LEVITATION", "1.20.6")
        fieldInferred("LUCK", "1.20.6")
        fieldInferred("UNLUCK", "1.20.6")
        fieldInferred("SLOW_FALLING", "1.20.6")
        fieldInferred("CONDUIT_POWER", "1.20.6")
        fieldInferred("DOLPHINS_GRACE", "1.20.6")
        fieldInferred("BAD_OMEN", "1.20.6")
        fieldInferred("HERO_OF_THE_VILLAGE", "1.20.6")
        fieldInferred("DARKNESS", "1.20.6")
    }
    mapClass(Scoreboard) {
        constructor()
        methodInferred("hasObjective", "1.20.1", String::class)
        methodInferred("getOrCreateObjective", "1.16.5", String::class)
        methodInferred("getObjective", "1.16.5", String::class)
        methodInferred("addObjective", "1.16.5", String::class, ObjectiveCriteria, Component, ObjectiveCriteriaRenderType)
        method(Objective, "addObjective", String::class, ObjectiveCriteria, Component, ObjectiveCriteriaRenderType, Boolean::class, NumberFormat)
        methodInferred("getOrCreatePlayerScore", "1.16.5", String::class, Objective)
        methodInferred("getTrackedPlayers", "1.16.5")
        methodInferred("resetPlayerScore", "1.16.5", String::class, Objective)
        methodInferred("removeObjective", "1.16.5", Objective)
    }
    mapClass(PlayerTeam) {
        constructor(Scoreboard, String::class)
        fieldInferred("scoreboard", "1.16.5")
        fieldInferred("name", "1.16.5")
        fieldInferred("players", "1.16.5")
        fieldInferred("displayName", "1.16.5")
        fieldInferred("playerPrefix", "1.16.5")
        fieldInferred("playerSuffix", "1.16.5")
        fieldInferred("allowFriendlyFire", "1.16.5")
        fieldInferred("seeFriendlyInvisibles", "1.16.5")
        fieldInferred("nameTagVisibility", "1.16.5")
        fieldInferred("deathMessageVisibility", "1.16.5")
        fieldInferred("color", "1.16.5")
        fieldInferred("collisionRule", "1.16.5")
        fieldInferred("displayNameStyle", "1.16.5")
    }
    mapClass(Team) {
        // TODO: Wasn't in ruom
    }
    mapClass(CollisionRule) {
        enumConstant(
            "ALWAYS",
            "NEVER",
            "PUSH_OTHER_TEAMS",
            "PUSH_OWN_TEAM"
        )
    }
    mapClass(Visibility) {
        enumConstant(
            "ALWAYS",
            "NEVER",
            "HIDE_FOR_OTHER_TEAMS",
            "HIDE_FOR_OWN_TEAM"
        )
    }
    mapClass(DyeColor) {
        enumConstant(
            "WHITE",
            "ORANGE",
            "MAGENTA",
            "LIGHT_BLUE",
            "YELLOW",
            "LIME",
            "PINK",
            "GRAY",
            "LIGHT_GRAY",
            "CYAN",
            "PURPLE",
            "BLUE",
            "BROWN",
            "GREEN",
            "RED",
            "BLACK",
        )
        methodInferred("getName", "1.20.4")
        methodInferred("getFireworkColor", "1.20.4")
        methodInferred("byId", "1.20.4", Int::class)
    }
    mapClass(SignText) {
        constructor("${Component}[]", "${Component}[]", DyeColor, Boolean::class)
        methodInferred("emptyMessages", "1.20.4")
        methodInferred("hasGlowingText", "1.20.4")
        methodInferred("setHasGlowingText", "1.20.4", Boolean::class)
        methodInferred("getColor", "1.20.4")
        methodInferred("setColor", "1.20.4", DyeColor)
        methodInferred("getMessage", "1.20.4", Int::class, Boolean::class)
        methodInferred("setMessage", "1.20.4", Int::class, Component)
    }
    mapClass(ParticleType) {
        // TODO: Warn't in ruom
    }
    mapClass(PositionSource) {
        // TODO: Wasn't in ruom
    }
    mapClass(BlockPositionSource) {
        constructor(BlockPos)
    }
    mapClass(EntityPositionSource) {
        constructor(Int::class)
        constructor(Entity, Float::class)
    }
    mapClass(VibrationPath) {
        constructor(BlockPos, PositionSource, Int::class)
    }
    mapClass(DustParticleOptions) {
        constructor(Float::class, Float::class, Float::class, Float::class)
        constructor("org.joml.Vector3f", Float::class)
        constructor(Vector3f, Float::class)
    }
    mapClass(DustColorTransitionOptions) {
        constructor(Vector3f, Vector3f, Float::class)
        constructor("org.joml.Vector3f", "org.joml.Vector3f", Float::class)
    }
    mapClass(BlockParticleOption) {
        constructor(ParticleType, BlockState)
    }
    mapClass(ItemParticleOption) {
        constructor(ParticleType, ItemStack)
    }
    mapClass(VibrationParticleOption) {
        constructor(VibrationPath)
        constructor(PositionSource, Int::class)
    }
    mapClass(ShriekParticleOption) {
        constructor(Int::class)
    }
    mapClass(SculkChargeParticleOptions) {
        constructor(Float::class)
    }
    mapClass(Objective) {
        constructor(Scoreboard, String::class, ObjectiveCriteria, Component, ObjectiveCriteriaRenderType)
        constructor(Scoreboard, String::class, ObjectiveCriteria, Component, ObjectiveCriteriaRenderType, Boolean::class, NumberFormat)
    }
    mapClass(ObjectiveCriteria) {
        fieldInferred("TRIGGER", "1.20.4")
        fieldInferred("HEALTH", "1.20.4")
        fieldInferred("FOOD", "1.20.4")
        fieldInferred("AIR", "1.20.4")
        fieldInferred("ARMOR", "1.20.4")
        fieldInferred("EXPERIENCE", "1.20.4")
        fieldInferred("LEVEL", "1.20.4")
        fieldInferred("TEAM_KILL", "1.20.4")
        fieldInferred("KILLED_BY_TEAM", "1.20.4")
    }
    mapClass(ObjectiveCriteriaRenderType) {
        fieldInferred("INTEGER", "1.16.5")
        fieldInferred("HEARTS", "1.16.5")
    }
    mapClass(ServerScoreboardMethod) {
        enumConstant(
            "CHANGE",
            "REMOVE"
        )
    }
    mapClass(ClientInformation) {
        methodInferred("createDefault", "1.20.4")
    }
    mapClass(ClientBrandRetriever) {
        method(String::class, "getClientModName")
    }
    mapClass(PacketFlow) {
        enumConstant(
            "CLIENTBOUND",
            "SERVERBOUND"
        )
    }
    mapClass(CommonListenerCookie) {
        constructor(GameProfile, Int::class, ClientInformation)
    }
    mapClass(BlankFormat) {
        field(BlankFormat, "INSTANCE")
    }
    mapClass(DisplaySlot) {
        enumConstant("SIDEBAR", "BELOW_NAME")
    }
    mapClass(Display) {
        constructor(EntityType, Level)
        field(EntityDataAccessor, "DATA_TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS_ID")
        field(EntityDataAccessor, "DATA_TRANSFORMATION_INTERPOLATION_DURATION_ID")
        field(EntityDataAccessor, "DATA_POS_ROT_INTERPOLATION_DURATION_ID")
        field(EntityDataAccessor, "DATA_TRANSLATION_ID")
        field(EntityDataAccessor, "DATA_SCALE_ID")
        field(EntityDataAccessor, "DATA_LEFT_ROTATION_ID")
        field(EntityDataAccessor, "DATA_RIGHT_ROTATION_ID")
        field(EntityDataAccessor, "DATA_BILLBOARD_RENDER_CONSTRAINTS_ID")
        field(EntityDataAccessor, "DATA_BRIGHTNESS_OVERRIDE_ID")
        field(EntityDataAccessor, "DATA_VIEW_RANGE_ID")
        field(EntityDataAccessor, "DATA_SHADOW_RADIUS_ID")
        field(EntityDataAccessor, "DATA_SHADOW_STRENGTH_ID")
        field(EntityDataAccessor, "DATA_WIDTH_ID")
        field(EntityDataAccessor, "DATA_HEIGHT_ID")
        field(EntityDataAccessor, "DATA_GLOW_COLOR_OVERRIDE_ID")
        field(String::class, "TAG_POS_ROT_INTERPOLATION_DURATION")
        field(String::class, "TAG_TRANSFORMATION_INTERPOLATION_DURATION")
        field(String::class, "TAG_TRANSFORMATION_START_INTERPOLATION")
        field(String::class, "TAG_TRANSFORMATION")
        field(String::class, "TAG_BILLBOARD")
        field(String::class, "TAG_BRIGHTNESS")
        field(String::class, "TAG_VIEW_RANGE")
        field(String::class, "TAG_SHADOW_RADIUS")
        field(String::class, "TAG_SHADOW_STRENGTH")
        field(String::class, "TAG_WIDTH")
        field(String::class, "TAG_HEIGHT")
        field(String::class, "TAG_GLOW_COLOR_OVERRIDE")
    }
    mapClass(BlockDisplay) {
        constructor(EntityType, Level)
        field(EntityDataAccessor, "DATA_BLOCK_STATE_ID")
        method(Void.TYPE, "defineSynchedData")
    }
    mapClass(ItemDisplay) {
        constructor(EntityType, Level)
        field(EntityDataAccessor, "DATA_ITEM_STACK_ID")
        field(EntityDataAccessor, "DATA_ITEM_DISPLAY_ID")
        method(Void.TYPE, "defineSynchedData")
    }
    mapClass(TextDisplay) {
        constructor(EntityType, Level)
        field(EntityDataAccessor, "DATA_TEXT_ID")
        field(EntityDataAccessor, "DATA_LINE_WIDTH_ID")
        field(EntityDataAccessor, "DATA_BACKGROUND_COLOR_ID")
        field(EntityDataAccessor, "DATA_TEXT_OPACITY_ID")
        field(EntityDataAccessor, "DATA_STYLE_FLAGS_ID")
        method(Void.TYPE, "defineSynchedData")
    }
    mapClass(BillboardConstraints) {
        enumConstant(
            "FIXED",
            "VERTICAL",
            "HORIZONTAL",
            "CENTER"
        )
        method(Byte::class, "getId")
    }
    mapClass(ItemDisplayContext) {
        enumConstant(
            "NONE",
            "THIRD_PERSON_LEFT_HAND",
            "THIRD_PERSON_RIGHT_HAND",
            "FIRST_PERSON_LEFT_HAND",
            "FIRST_PERSON_RIGHT_HAND",
            "HEAD",
            "GUI",
            "GROUND",
            "FIXED"
        )
        method(Byte::class, "getId")
    }
    mapClass(AttributeInstance) {
        method(Double::class, "getBaseValue")
        method(Void.TYPE, "setBaseValue", Double::class)
    }
    mapClass(Attributes) {
        field(Holder, "ARMOR")
        field(Holder, "ARMOR_TOUGHNESS")
        field(Holder, "ATTACK_DAMAGE")
        field(Holder, "ATTACK_KNOCKBACK")
        field(Holder, "ATTACK_SPEED")
        field(Holder, "BLOCK_BREAK_SPEED")
        field(Holder, "BLOCK_INTERACTION_RANGE")
        field(Holder, "ENTITY_INTERACTION_RANGE")
        field(Holder, "FALL_DAMAGE_MULTIPLIER")
        field(Holder, "FLYING_SPEED")
        field(Holder, "FOLLOW_RANGE")
        field(Holder, "GRAVITY")
        field(Holder, "JUMP_STRENGTH")
        field(Holder, "KNOCKBACK_RESISTANCE")
        field(Holder, "LUCK")
        field(Holder, "MAX_ABSORPTION")
        field(Holder, "MAX_HEALTH")
        field(Holder, "MOVEMENT_SPEED")
        field(Holder, "SAFE_FALL_DISTANCE")
        field(Holder, "SCALE")
        field(Holder, "SPAWN_REINFORCEMENTS_CHANCE")
        field(Holder, "STEP_HEIGHT")
    }
    mapClass(MessageSignature) {
        constructor(ByteArray::class)
    }
    mapClass(SignedMessageBodyPacked) {
        constructor(String::class, Instant::class, Long::class, LastSeenMessagesPacked)
        field(String::class, "content")
        field(Instant::class, "timeStamp")
        field(Long::class, "salt")
        field(LastSeenMessagesPacked, "lastSeen")
    }
    mapClass(LastSeenMessagesPacked) {
        constructor(List::class)
        field(LastSeenMessagesPacked, "EMPTY")
    }
    mapClass(FilterMask) {
        field(FilterMask, "FULLY_FILTERED")
        field(FilterMask, "PASS_THROUGH")
    }
    mapClass(BlockHitResult) {
        method(BlockPos, "getBlockPos")
        method(Direction, "getDirection")
        method(HitResultType, "getType")
        method(Boolean::class, "isInside")
    }
    mapClass(HitResultType) {
        enumConstant(
            "MISS",
            "BLOCK",
            "ENTITY"
        )
    }

    mapClass(CrossbowItem) {
        methodInferred("isCharged", "1.16.5", ItemStack)
        methodInferred("setCharged", "1.16.5", ItemStack, Boolean::class)
        methodInferred("getChargedProjectiles", "1.16.5", ItemStack)
        methodInferred("clearChargedProjectiles", "1.16.5", ItemStack)
        methodInferred("getChargeDuration", "1.16.5", ItemStack)
        methodInferred("getStartSound", "1.16.5", Int::class)
        methodInferred("getPowerForTime", "1.16.5", Int::class, ItemStack)
    }
    mapClass(ArmorStand) {
        constructor(EntityType, Level)
        constructor(Level, Double::class, Double::class, Double::class) // 1.8
        methodInferred("setHeadPose", "1.16.5", Rotations)
        methodInferred("setBodyPose", "1.16.5", Rotations)
        methodInferred("setLeftArmPose", "1.16.5", Rotations)
        methodInferred("setRightArmPose", "1.16.5", Rotations)
        methodInferred("setLeftLegPose", "1.18", Rotations)
        methodInferred("setRightLegPose", "1.18", Rotations)
        methodInferred("setMarker", "1.16.5", Boolean::class)
        methodInferred("setNoBasePlate", "1.16.5", Boolean::class)
        methodInferred("setShowArms", "1.16.5", Boolean::class)
        methodInferred("setSmall", "1.16.5", Boolean::class)
        methodInferred("setYBodyRot", "1.16.5", Float::class)
        methodInferred("setYHeadRot", "1.16.5", Float::class)
        methodInferred("getHeadPose", "1.16.5")
        methodInferred("getBodyPose", "1.16.5")
        methodInferred("getLeftArmPose", "1.18")
        methodInferred("getRightArmPose", "1.18")
        methodInferred("getLeftLegPose", "1.18")
        methodInferred("getRightLegPose", "1.18")
        methodInferred("isMarker", "1.16.5")
        methodInferred("isNoBasePlate", "1.16.5")
        methodInferred("isShowArms", "1.16.5")
        methodInferred("isSmall", "1.16.5")
        method(Void.TYPE, "setGravity", Boolean::class)
        // TODO
//        methodInferred("spigot:setGravity:1.8.8", "1.16.5", Boolean::class)
//        methodInferred("spigot:hasGravity:1.8.8", "1.16.5")
    }
    mapClass(Arrow) {
        constructor(EntityType, Level)
        methodInferred("setEffectsFromItem", "1.20.4", ItemStack)
        methodInferred("makeParticle", "1.16.5", Int::class)
        methodInferred("getColor", "1.16.5")
        methodInferred("setFixedColor", "1.16.5", Int::class)
    }
    mapClass(AbstractThrownPotion) {
        constructor(EntityType, Level)
        constructor(LevelSpigot)
        fieldInferred("DATA_ITEM_STACK", "1.15.2")
    }
    mapClass(ThrownTrident) {
        constructor(EntityType, Level)
        constructor(Level, LivingEntity, ItemStack)
        fieldInferred("tridentItem", "1.16.5")
        fieldInferred("clientSideReturnTridentTickCount", "1.16.5")
        fieldInferred("ID_LOYALTY", "1.16.5")
        fieldInferred("ID_FOIL", "1.16.5")
    }
    mapClass(ThrowableItemProjectile) {
        methodInferred("setItem", "1.16.5", ItemStack)
        methodInferred("getItemRaw", "1.16.5")
        fieldInferred("DATA_ITEM_STACK", "1.16.5")
    }
    mapClass(ItemEntity) {
        constructor(Level, Double::class, Double::class, Double::class, ItemStack)
        methodInferred("setItem", "1.16.5", ItemStack)
        methodInferred("getItem", "1.16.5")
        fieldInferred("DATA_ITEM", "1.16.5")
    }
    mapClass(FallingBlockEntity) {
        constructor(Level, Double::class, Double::class, Double::class, BlockState)
        methodInferred("setStartPos", "1.16.5", BlockPos)
        methodInferred("getBlockState", "1.16.5")
        methodInferred("getAddEntityPacket", "1.16.5")
        fieldInferred("blockState", "1.16.5")
    }
    mapClass(AreaEffectCloud) {
        constructor(Level, Double::class, Double::class, Double::class)
        methodInferred("setRadius", "1.16.5", Float::class)
        methodInferred("getRadius", "1.16.5")
        methodInferred("getColor", "1.16.5")
        methodInferred("setFixedColor", "1.16.5", Int::class)
        methodInferred("getParticle", "1.16.5")
        methodInferred("setParticle", "1.16.5", ParticleOptions)
        methodInferred("setWaiting", "1.16.5", Boolean::class)
        methodInferred("getPotion", "1.20.4")
    }
    mapClass(FishingHook) {
        constructor(EntityType, Level)
        methodInferred("setOwner", "1.20.4", Entity)
        methodInferred("getAddEntityPacket", "1.16.5")
        fieldInferred("DATA_HOOKED_ENTITY", "1.16.5")
        fieldInferred("DATA_BITING", "1.16.5")
    }
    mapClass(FireworkRocketEntity) {
        constructor(EntityType, Level)
        methodInferred("hasExplosion", "1.16.5")
        methodInferred("isAttachedToEntity", "1.16.5")
        fieldInferred("DATA_ID_FIREWORKS_ITEM", "1.16.5")
        fieldInferred("DATA_ATTACHED_TO_TARGET", "1.16.5")
        fieldInferred("DATA_SHOT_AT_ANGLE", "1.16.5")
    }
    mapClass(LightningBolt) {
        constructor(EntityType, Level)
    }
    mapClass(SignBlockEntity) {
        methodInferred("updateText", "1.20.4", UnaryOperator::class, Boolean::class)
        methodInferred("getMessage", "1.19.2", Int::class, Boolean::class)
        method(Void.TYPE, "setMessage", Int::class, Component, Component)
        method(Void.TYPE, "setMessage", Int::class, Component)
        methodInferred("getUpdatePacket", "1.19.2")
        methodInferred("hasGlowingText", "1.19.2")
        methodInferred("setHasGlowingText", "1.19.2", Boolean::class)
        methodInferred("markUpdated", "1.19.2")
        fieldInferred("messages", "1.19.2")
    }
    mapClass(Villager) {
        constructor(EntityType, Level)
        fieldInferred("DATA_VILLAGER_DATA", "1.16.5")
    }
    mapClass(Boat) {
        constructor(EntityType, Level)
        methodInferred("setDamage", "1.16.5", Float::class)
        methodInferred("getDamage", "1.16.5")
        methodInferred("setHurtTime", "1.16.5", Int::class)
        methodInferred("getHurtTime", "1.16.5")
        methodInferred("setBubbleTime", "1.16.5", Int::class)
        methodInferred("getBubbleTime", "1.16.5")
        methodInferred("setHurtDir", "1.16.5", Int::class)
        methodInferred("getHurtDir", "1.16.5")
        methodInferred("setType", "1.16.5", BoatType)
        methodInferred("getBoatType", "1.16.5")
        fieldInferred("DATA_ID_PADDLE_LEFT", "1.16.5")
        fieldInferred("DATA_ID_PADDLE_RIGHT", "1.16.5")
    }
    mapClass(Creeper) {
        fieldInferred("DATA_SWELL_DIR", "1.16.5")
        fieldInferred("DATA_IS_POWERED", "1.16.5")
        fieldInferred("DATA_IS_IGNITED", "1.16.5")
    }
    mapClass(Zombie) {
        constructor(EntityType, Level)
        constructor(Level)
    }

}

import me.kcra.takenaka.generator.accessor.AccessorType
import me.kcra.takenaka.generator.accessor.CodeLanguage
import me.kcra.takenaka.generator.accessor.plugin.accessorRuntime
import java.time.Instant
import java.util.BitSet
import java.util.EnumSet
import java.util.Optional
import java.util.UUID

plugins {
    id("me.kcra.takenaka.accessor") version "1.1.3"
}

repositories {
    // Takenaka
    maven("https://repo.screamingsandals.org/public")
}

dependencies {
    compileOnly(project(":stickynote-bukkit"))

    mappingBundle("me.kcra.takenaka:mappings:1.8.8+1.20.6")
    implementation(accessorRuntime())
}

tasks {
    sourcesJar {
        dependsOn(generateAccessors)
    }
}

@Suppress("LocalVariableName")
accessors {
    basePackage("org.sayandev.stickynote.nms.accessors")
    accessedNamespaces("spigot")
    accessorType(AccessorType.REFLECTION)
    codeLanguage(CodeLanguage.KOTLIN)
    versionRange("1.8.8", "1.20.4")

    val ClientboundPlayerInfoUpdatePacket = "net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket" // 1.19.3 and above
    val ClientboundPlayerInfoUpdatePacketEntry = "net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket\$Entry"
    val ClientboundPlayerInfoRemovePacket = "net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket" // 1.19.3 and above
    val ClientboundPlayerInfoUpdatePacketAction = "net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket\$Action" // 1.19.3 and above
    val ClientboundAddEntityPacket = "net.minecraft.network.protocol.game.ClientboundAddEntityPacket"
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
    val ClientboundLevelParticlesPacket = "net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket"
    val ClientboundSetDisplayObjectivePacket = "net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket"
    val ClientboundSetObjectivePacket = "net.minecraft.network.protocol.game.ClientboundSetObjectivePacket"
    val ClientboundSetScorePacket = "net.minecraft.network.protocol.game.ClientboundSetScorePacket"
    val ClientboundUpdateMobEffectPacket = "net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket"
    val ClientboundRemoveMobEffectPacket = "net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket"
    val ServerboundPlayerActionPacket = "net.minecraft.network.protocol.game.ServerboundPlayerActionPacket"
    val ServerboundPlayerActionPacketAction = "net.minecraft.network.protocol.game.ServerboundPlayerActionPacket\$Action"
    val ServerboundInteractPacket = "net.minecraft.network.protocol.game.ServerboundInteractPacket"
    val ServerboundInteractPacketAction = "net.minecraft.network.protocol.game.ServerboundInteractPacket\$Action"
    val ServerboundInteractPacketActionType = "net.minecraft.network.protocol.game.ServerboundInteractPacket\$ActionType"
    val ServerboundInteractPacketActionInteractAt = "net.minecraft.network.protocol.game.ServerboundInteractPacket\$InteractionAtLocationAction"
    val ServerboundInteractPacketActionInteract = "net.minecraft.network.protocol.game.ServerboundInteractPacket\$InteractionAction"
    val ServerboundKeepAlivePacket = "net.minecraft.network.protocol.game.ServerboundKeepAlivePacket"
    val ServerboundClientInformationPacket = "net.minecraft.network.protocol.game.ServerboundClientInformationPacket"
    val ServerPlayer = "net.minecraft.server.level.ServerPlayer"
    val Player = "net.minecraft.world.entity.player.Player"
    val ServerLevel = "net.minecraft.server.level.ServerLevel"
    val ServerLevelAccessor = "net.minecraft.world.level.ServerLevelAccessor"
    val ServerPlayerGameMode = "net.minecraft.server.level.ServerPlayerGameMode"
    val Level = "net.minecraft.world.level.Level"
    var LevelSpigot = "net.minecraft.server.VVV.World"
    val LevelWriter = "net.minecraft.world.level.LevelWriter"
    val LevelChunk = "net.minecraft.world.level.chunk.LevelChunk"
    val ChunkAccess = "net.minecraft.world.level.chunk.ChunkAccess"
    val ChunkStatus = "net.minecraft.world.level.chunk.ChunkStatus"
    val LevelLightEngine = "net.minecraft.world.level.lighting.LevelLightEngine"
    val Packet = "net.minecraft.network.protocol.Packet"
    val ServerGamePacketListenerImpl = "net.minecraft.server.network.ServerGamePacketListenerImpl"
    val ServerCommonPacketListenerImpl = "net.minecraft.server.network.ServerCommonPacketListenerImpl" // 1.20.2 and above
    val Connection = "net.minecraft.network.Connection"
    val MinecraftServer = "net.minecraft.server.MinecraftServer"
    val GameType = "net.minecraft.world.level.GameType"
    val MobSpawnType = "net.minecraft.world.entity.MobSpawnType"
    val Pose = "net.minecraft.world.entity.Pose"
    val Vec3 = "net.minecraft.world.phys.Vec3"
    val Vec3i = "net.minecraft.core.Vec3i"
    val Rotations = "net.minecraft.core.Rotations"
    val Mob = "net.minecraft.world.entity.Mob"
    val Entity = "net.minecraft.world.entity.Entity"
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
    val ComponentSerializer = "net.minecraft.network.chat.Component\$Serializer"
    val Item = "net.minecraft.world.item.Item"
    val ItemStack = "net.minecraft.world.item.ItemStack"
    val Potion = "net.minecraft.world.item.alchemy.Potion"
    val Potions = "net.minecraft.world.item.alchemy.Potions"
    val PotionUtils = "net.minecraft.world.item.alchemy.PotionUtils"
    val SynchedEntityData = "net.minecraft.network.syncher.SynchedEntityData"
    val DataItem = "net.minecraft.network.syncher.SynchedEntityData\$DataItem"
    val Tag = "net.minecraft.nbt.Tag"
    val CompoundTag = "net.minecraft.nbt.CompoundTag"
    val ListTag = "net.minecraft.nbt.ListTag"
    val StringTag = "net.minecraft.nbt.StringTag"
    val TagParser = "net.minecraft.nbt.TagParser"
    val EntityDataSerializer = "net.minecraft.network.syncher.EntityDataSerializer"
    val EntityDataSerializers = "net.minecraft.network.syncher.EntityDataSerializers"
    val EntityDataAccessor = "net.minecraft.network.syncher.EntityDataAccessor"
    val ResourceLocation = "net.minecraft.resources.ResourceLocation"
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
    val VillagerData = "net.minecraft.world.entity.npc.VillagerData"
    val VillagerType = "net.minecraft.world.entity.npc.VillagerType"
    val VillagerProfession = "net.minecraft.world.entity.npc.VillagerProfession"
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
    val PacketFlow = "net.minecraft.network.protocol.PacketFlow"
    val CommonListenerCookie = "net.minecraft.server.network.CommonListenerCookie"
    val GameProfile = "com.mojang.authlib.GameProfile"
    val Lifecycle = "com.mojang.serialization.Lifecycle"
    val CrossbowItem = "net.minecraft.world.item.CrossbowItem"
    val ArmorStand = "net.minecraft.world.entity.decoration.ArmorStand"
    val Arrow = "net.minecraft.world.entity.projectile.Arrow"
    val ThrownPotion = "net.minecraft.world.entity.projectile.ThrownPotion"
    val ThrownTrident = "net.minecraft.world.entity.projectile.ThrownTrident"
    val ThrowableItemProjectile = "net.minecraft.world.entity.projectile.ThrowableItemProjectile"
    val ItemEntity = "net.minecraft.world.entity.item.ItemEntity"
    val FallingBlockEntity = "net.minecraft.world.entity.item.FallingBlockEntity"
    val AreaEffectCloud = "net.minecraft.world.entity.AreaEffectCloud"
    val FishingHook = "net.minecraft.world.entity.projectile.FishingHook"
    val LegacyFishingHook = "spigot:EntityFishingHook"
    val FireworkRocketEntity = "net.minecraft.world.entity.projectile.FireworkRocketEntity"
    val LightningBolt = "net.minecraft.world.entity.LightningBolt"
    val SignBlockEntity = "net.minecraft.world.level.block.entity.SignBlockEntity"
    val Villager = "net.minecraft.world.entity.npc.Villager"
    val Boat = "net.minecraft.world.entity.vehicle.Boat"
    val Creeper = "net.minecraft.world.entity.monster.Creeper"

    mapClass(ClientboundPlayerInfoUpdatePacket) {
        constructor(ClientboundPlayerInfoUpdatePacketAction, ServerPlayer)
        constructor(EnumSet::class, Collection::class)
        fieldInferred("entries", "1.20.4")
        methodInferred("createPlayerInitializing", "1.20.4", Collection::class)
        methodInferred("entries", "1.20.4")
    }
    mapClass(ClientboundPlayerInfoUpdatePacketEntry) {
        constructor(UUID::class, "com.mojang.authlib.GameProfile", Boolean::class, Int::class, GameType, Component, RemoteChatSessionData)
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
    }
    mapClass(ClientboundAddEntityPacket) {
        constructor(Int::class, UUID::class, Double::class, Double::class, Double::class, Float::class, Float::class, EntityType, Int::class, Vec3)
        constructor(Entity, Int::class)
        constructor(Entity)
        methodInferred("getId", "1.20.4")
    }
    /* REMOVED
    mapClass(ClientboundAddPlayerPacket) {
        constructor(Player)
        methodInferred("getEntityId", "1.16.5")
        methodInferred("getPlayerId", "1.16.5")
        methodInferred("getX", "1.20.4")
        methodInferred("getY", "1.20.4")
        methodInferred("getZ", "1.20.4")
        methodInferred("getyRot", "1.20.4")
        methodInferred("getxRot", "1.20.4")
    }*/
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
        //TODO (removed in 1.14.0): constructor(Int::class, String::class, Component, Int::class)
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
        methodInferred("getMessage", "1.16.5")
        methodInferred("getType", "1.16.5")
        methodInferred("getSender", "1.16.5")
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
        //methodInferred("getMessage")
        //fieldInferred("signedContent", "1.20.4")
        //fieldInferred("unsignedContent", "1.20.4")
        //fieldInferred("typeId", "1.20.4")
        //fieldInferred("sender", "1.20.4")
        //fieldInferred("timeStamp", "1.20.4")
        //fieldInferred("saltSignature", "1.20.4")
    }
    mapClass(ClientboundSystemChatPacket) {
        methodInferred("content", "1.20.4")
        //methodInferred("typeId", "1.16.5")
    }
    mapClass(ClientboundSetCameraPacket) {
        fieldInferred("cameraId", "1.20.4")
    }
    mapClass(ClientboundContainerSetContentPacket) {
        constructor(Int::class, Int::class, NonNullList, ItemStack)
        fieldInferred("items", "1.20.4")
    }
    mapClass(ClientboundLevelParticlesPacket) {
        constructor(EnumParticle, Boolean::class, Float::class, Float::class, Float::class, Float::class, Float::class, Float::class, Float::class, Int::class, IntArray::class) //1.8.8 - 1.12.2
        constructor(ParticleOptions, Boolean::class, Float::class, Float::class, Float::class, Float::class, Float::class, Float::class, Float::class, Int::class) //1.13 - 1.14.4
        constructor(ParticleOptions, Boolean::class, Double::class, Double::class, Double::class, Float::class, Float::class, Float::class, Float::class, Int::class) //1.15 and above
    }
    mapClass(ClientboundSetDisplayObjectivePacket) {
        constructor(Int::class, Objective)
        methodInferred("getObjectiveName", "1.20.4")
    }
    mapClass(ClientboundSetObjectivePacket) {
        constructor(Objective, Int::class)
    }
    mapClass(ClientboundSetScorePacket) {
        constructor(ServerScoreboardMethod, String::class, String::class, Int::class)
    }
    mapClass(ClientboundUpdateMobEffectPacket) {
        constructor(Int::class, MobEffectInstance)
    }
    mapClass(ClientboundRemoveMobEffectPacket) {
        constructor(Int::class, MobEffect)
    }
    mapClass(ServerboundPlayerActionPacket) {
        methodInferred("getPos", "1.20.4")
        methodInferred("getDirection", "1.20.4")
        methodInferred("getAction", "1.20.4")
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

    }
    mapClass(ServerLevel) {

    }
    mapClass(ServerLevelAccessor) {

    }
    mapClass(ServerPlayerGameMode) {
        constructor(ServerLevel)
        constructor(Level)
    }
    mapClass(Level) {

    }
    mapClass(LevelWriter) {

    }
    mapClass(LevelChunk) {

    }
    mapClass(ChunkAccess) {

    }
    mapClass(ChunkStatus) {

    }
    mapClass(LevelLightEngine) {

    }
    mapClass(Packet) {

    }
    mapClass(ServerGamePacketListenerImpl) {
        methodInferred("send", "1.16.5", Packet)
        fieldInferred("connection", "1.16.5")
    }
    mapClass(ServerCommonPacketListenerImpl) {
        fieldInferred("connection", "1.20.4")
        method(Void.TYPE, "send", Packet)
    }
    mapClass(Connection) {
        fieldInferred("channel", "1.20.4")
    }
    mapClass(MinecraftServer) {

    }
    mapClass(GameType) {
        enumConstant(
            "SURVIVAL",
            "CREATIVE",
            "SPECTATOR",
            "ADVENTURE"
        )
    }
    mapClass(MobSpawnType) {

    }
    mapClass(Pose) {

    }
    mapClass(Vec3) {

    }
    mapClass(Vec3i) {

    }
    mapClass(Rotations) {

    }
    mapClass(Mob) {

    }
    mapClass(Entity) {
        methodInferred("getUUID", "1.20.4")
    }
    mapClass(LivingEntity) {

    }
    mapClass(BlockEntity) {

    }
    mapClass(SpawnerBlockEntity) {

    }
    mapClass(BaseSpawner) {

    }
    mapClass(SpawnData) {

    }
    mapClass(EntityType) {

    }
    mapClass(EquipmentSlot) {

    }
    mapClass(InteractionHand) {

    }
    mapClass(BlockPos) {

    }
    mapClass(ChunkPos) {

    }
    mapClass(Direction) {

    }
    mapClass(BlockState) {

    }
    mapClass(BlockBehaviour) {

    }
    mapClass(BlockStateBase) {

    }
    mapClass(Blocks) {

    }
    mapClass(Block) {

    }
    mapClass(Component) {

    }
    mapClass(ComponentSerializer) {

    }
    mapClass(Item) {

    }
    mapClass(ItemStack) {

    }
    mapClass(Potion) {

    }
    mapClass(Potions) {

    }
    mapClass(PotionUtils) {

    }
    mapClass(SynchedEntityData) {

    }
    mapClass(DataItem) {

    }
    mapClass(Tag) {

    }
    mapClass(CompoundTag) {

    }
    mapClass(ListTag) {

    }
    mapClass(StringTag) {

    }
    mapClass(TagParser) {

    }
    mapClass(EntityDataSerializer) {

    }
    mapClass(EntityDataSerializers) {

    }
    mapClass(EntityDataAccessor) {

    }
    mapClass(ResourceLocation) {

    }
    mapClass(ResourceKey) {

    }
    mapClass(Advancement) {

    }
    mapClass(AdvancementHolder) {

    }
    mapClass(AdvancementBuilder) {

    }
    mapClass(AdvancementProgress) {

    }
    mapClass(AdvancementRequirements) {

    }
    mapClass(ServerAdvancementManager) {

    }
    mapClass(DeserializationContext) {

    }
    mapClass(PredicateManager) {

    }
    mapClass(LootDataManager) {

    }
    mapClass(GsonHelper) {

    }
    mapClass(CreativeModeTab) {

    }
    mapClass(AbstractContainerMenu) {

    }
    mapClass(MenuType) {

    }
    mapClass(DimensionType) {

    }
    mapClass(ParticleOptions) {

    }
    mapClass(DifficultyInstance) {

    }
    mapClass(SpawnGroupData) {

    }
    mapClass(ChatType) {

    }
    mapClass(VillagerData) {

    }
    mapClass(VillagerType) {

    }
    mapClass(VillagerProfession) {

    }
    mapClass(ChatFormatting) {

    }
    mapClass(BoatType) {

    }
    mapClass(Registry) {

    }
    /*mapClass(BuiltInRegistries) {

    }*/
    mapClass(MappedRegistry) {

    }
    mapClass(WritableRegistry) {

    }
    mapClass(RegistryAccess) {

    }
    /*mapClass(BuiltinRegistries) {

    }*/
    mapClass(CoreBuiltInRegistries) {

    }
    mapClass(Holder) {

    }
    mapClass(Biome) {

    }
    mapClass(BiomeBuilder) {

    }
    mapClass(BiomeCategory) {

    }
    mapClass(BiomePrecipitation) {

    }
    mapClass(TemperatureModifier) {

    }
    mapClass(BiomeGenerationSettings) {

    }
    mapClass(BiomeSpecialEffects) {

    }
    mapClass(BiomeSpecialEffectsBuilder) {

    }
    mapClass(BiomeSpecialEffectsGrassColorModifier) {

    }
    mapClass(MobSpawnSettings) {

    }
    mapClass(SoundEvent) {

    }
    mapClass(SoundType) {

    }
    mapClass(ChatSender) {

    }
    mapClass(CryptSaltSignaturePair) {

    }
    mapClass(PlayerChatMessage) {

    }
    mapClass(ProfilePublicKey) {

    }
    mapClass(NonNullList) {

    }
    mapClass(MobEffectInstance) {
        constructor(MobEffect, Int::class, Int::class, Boolean::class, Boolean::class, Boolean::class)
        fieldInferred("effect", "1.20.4")
    }
    mapClass(MobEffect) {
        methodInferred("byId", "1.16.5", Int::class)
    }
    mapClass(Scoreboard) {

    }
    mapClass(PlayerTeam) {

    }
    mapClass(Team) {

    }
    mapClass(CollisionRule) {

    }
    mapClass(Visibility) {

    }
    mapClass(DyeColor) {

    }
    mapClass(SignText) {
        //constructor(Array<Component>::class, Array<Component>::class, DyeColor, Boolean::class)
    }
    mapClass(ParticleType) {

    }
    mapClass(PositionSource) {

    }
    mapClass(BlockPositionSource) {

    }
    mapClass(EntityPositionSource) {

    }
    mapClass(VibrationPath) {

    }
    mapClass(DustParticleOptions) {

    }
    mapClass(DustColorTransitionOptions) {

    }
    mapClass(BlockParticleOption) {

    }
    mapClass(ItemParticleOption) {

    }
    mapClass(VibrationParticleOption) {

    }
    mapClass(ShriekParticleOption) {

    }
    mapClass(SculkChargeParticleOptions) {

    }
    mapClass(Vector3f) {

    }
    mapClass(Objective) {

    }
    mapClass(ObjectiveCriteria) {

    }
    mapClass(ObjectiveCriteriaRenderType) {

    }
    mapClass(ServerScoreboardMethod) {

    }
    mapClass(RemoteChatSessionData) {

    }
    mapClass(ClientInformation) {

    }
    mapClass(CrossbowItem) {

    }
    mapClass(ArmorStand) {

    }
    mapClass(Arrow) {

    }
    mapClass(ThrownPotion) {
        constructor(LevelSpigot)
    }
    mapClass(ThrownTrident) {

    }
    mapClass(ThrowableItemProjectile) {

    }
    mapClass(ItemEntity) {

    }
    mapClass(FallingBlockEntity) {

    }
    mapClass(AreaEffectCloud) {

    }
    mapClass(FishingHook) {

    }
    mapClass(FireworkRocketEntity) {

    }
    mapClass(LightningBolt) {

    }
    mapClass(SignBlockEntity) {

    }
    mapClass(Villager) {

    }
    mapClass(Boat) {

    }
    mapClass(Creeper) {

    }

}
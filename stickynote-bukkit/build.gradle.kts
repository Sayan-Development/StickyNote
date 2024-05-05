import me.kcra.takenaka.generator.accessor.AccessorType
import me.kcra.takenaka.generator.accessor.plugin.accessorRuntime

plugins {
    id("me.kcra.takenaka.accessor") version "1.1.2"
}

repositories {
    // Takenaka
    maven("https://repo.screamingsandals.org/public")

    // SpigotAPI
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

    // cloud
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.6-R0.1-SNAPSHOT")

    mappingBundle("me.kcra.takenaka:mappings:1.8.8+1.20.4")
    implementation(accessorRuntime())

    implementation("net.kyori:adventure-platform-bukkit:4.3.2")

    implementation("org.incendo:cloud-core:2.0.0-SNAPSHOT")
    implementation("org.incendo:cloud-paper:2.0.0-SNAPSHOT")
    implementation("org.incendo:cloud-minecraft-extras:2.0.0-SNAPSHOT")
    implementation("org.incendo:cloud-kotlin-extensions:2.0.0-SNAPSHOT")

    implementation("org.reflections:reflections:0.10.2")

    compileOnly(project(":stickynote-core"))
}

tasks {
    sourcesJar {
        dependsOn(generateAccessors)
    }

    shadowJar {
        relocate("org.incendo", "org.sayandev.stickynote.lib.incendo")
    }
}

@Suppress("LocalVariableName")
accessors {
    basePackage("me.sayandevelopment.stickynote.nms.accessors")
    accessedNamespaces("spigot")
    accessorType(AccessorType.METHOD_HANDLES)

    val ClientboundPlayerInfoPacket = "net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket"
    val ClientboundPlayerInfoPacketAction = "net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket\$Action"
    val ClientboundPlayerInfoPacketPlayerUpdate = "net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket\$PlayerUpdate"
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
    val ClientboundBlockBreakAckPacket = "net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket"
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
    val ServerboundPlayerActionPacket = "net.minecraft.network.protocol.game.ServerboundPlayerActionPacket"
    val ServerboundPlayerActionPacketAction = "net.minecraft.network.protocol.game.ServerboundPlayerActionPacket\$Action"
    val ServerboundInteractPacket = "net.minecraft.network.protocol.game.ServerboundInteractPacket"
    val ServerboundInteractPacketAction = "net.minecraft.network.protocol.game.ServerboundInteractPacket\$Action"
    val ServerboundInteractPacketActionType = "net.minecraft.network.protocol.game.ServerboundInteractPacket\$ActionType"
    val ServerboundInteractPacketActionInteractAt = "net.minecraft.network.protocol.game.ServerboundInteractPacket\$InteractionAtLocationAction"
    val ServerboundInteractPacketActionInteract = "net.minecraft.network.protocol.game.ServerboundInteractPacket\$InteractionAction"
    val ServerboundChatPreviewPacket = "net.minecraft.network.protocol.game.ServerboundChatPreviewPacket"
    val ServerboundKeepAlivePacket = "net.minecraft.network.protocol.game.ServerboundKeepAlivePacket"
    val ServerboundClientInformationPacket = "net.minecraft.network.protocol.game.ServerboundClientInformationPacket"
    val ServerPlayer = "net.minecraft.server.level.ServerPlayer"
    val Player = "net.minecraft.world.entity.player.Player"
    val ServerLevel = "net.minecraft.server.level.ServerLevel"
    val ServerLevelAccessor = "net.minecraft.world.level.ServerLevelAccessor"
    val ServerPlayerGameMode = "net.minecraft.server.level.ServerPlayerGameMode"
    val Level = "net.minecraft.world.level.Level"
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
    val EnumParticle = "spigot:EnumParticle"
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

    /*mapClass(ClientboundPlayerInfoPacket) {

    }
    mapClass(ClientboundPlayerInfoPacketAction) {

    }
    mapClass(ClientboundPlayerInfoPacketPlayerUpdate) {

    }
    mapClass(ClientboundPlayerInfoUpdatePacket) {

    }
    mapClass(ClientboundPlayerInfoUpdatePacketEntry) {

    }
    mapClass(ClientboundPlayerInfoRemovePacket) {

    }
    mapClass(ClientboundPlayerInfoUpdatePacketAction) {

    }
    mapClass(ClientboundAddEntityPacket) {

    }
    mapClass(ClientboundAddPlayerPacket) {

    }
    mapClass(ClientboundRotateHeadPacket) {

    }
    mapClass(ClientboundRemoveEntitiesPacket) {

    }
    mapClass(ClientboundMoveEntityPacketRot) {

    }
    mapClass(ClientboundMoveEntityPacketPos) {

    }
    mapClass(ClientboundMoveEntityPacketPosRot) {

    }
    mapClass(ClientboundAnimatePacket) {

    }
    mapClass(ClientboundBlockBreakAckPacket) {

    }
    mapClass(ClientboundSetEntityDataPacket) {

    }
    mapClass(ClientboundSetEquipmentPacket) {

    }
    mapClass(ClientboundTeleportEntityPacket) {

    }
    mapClass(ClientboundSetEntityMotionPacket) {

    }
    mapClass(ClientboundTakeItemEntityPacket) {

    }
    mapClass(ClientboundBlockEventPacket) {

    }
    mapClass(ClientboundSetPassengersPacket) {

    }
    mapClass(ClientboundBlockDestructionPacket) {

    }
    mapClass(ClientboundUpdateAdvancementsPacket) {

    }
    mapClass(ClientboundLevelChunkPacketData) {

    }
    mapClass(ClientboundLevelChunkWithLightPacket) {

    }
    mapClass(ClientboundLevelChunkPacket) {

    }
    mapClass(ClientboundLightUpdatePacket) {

    }
    mapClass(ClientboundOpenScreenPacket) {

    }
    mapClass(ClientboundRespawnPacket) {

    }
    mapClass(ClientboundEntityEventPacket) {

    }
    mapClass(ClientboundChatPacket) {

    }
    mapClass(ClientboundSetPlayerTeamPacket) {

    }
    mapClass(ClientboundSetPlayerTeamPacketParameters) {

    }
    mapClass(ClientboundSetDisplayChatPreviewPacket) {

    }
    mapClass(ClientboundChatPreviewPacket) {

    }
    mapClass(ClientboundPlayerChatPacket) {

    }
    mapClass(ClientboundSystemChatPacket) {

    }
    mapClass(ClientboundKeepAlivePacket) {

    }
    mapClass(ClientboundSetCameraPacket) {

    }
    mapClass(ClientboundContainerSetContentPacket) {

    }
    mapClass(ClientboundLevelParticlesPacket) {

    }
    mapClass(ClientboundSetDisplayObjectivePacket) {

    }
    mapClass(ClientboundSetObjectivePacket) {

    }
    mapClass(ClientboundSetScorePacket) {

    }
    mapClass(ServerboundPlayerActionPacket) {

    }
    mapClass(ServerboundPlayerActionPacketAction) {

    }
    mapClass(ServerboundInteractPacket) {

    }
    mapClass(ServerboundInteractPacketAction) {

    }
    mapClass(ServerboundInteractPacketActionType) {

    }
    mapClass(ServerboundInteractPacketActionInteractAt) {

    }
    mapClass(ServerboundInteractPacketActionInteract) {

    }
    mapClass(ServerboundChatPreviewPacket) {

    }
    mapClass(ServerboundKeepAlivePacket) {

    }
    mapClass(ServerboundClientInformationPacket) {

    }
    mapClass(ServerPlayer) {

    }
    mapClass(Player) {

    }
    mapClass(ServerLevel) {

    }
    mapClass(ServerLevelAccessor) {

    }
    mapClass(ServerPlayerGameMode) {

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

    }
    mapClass(ServerCommonPacketListenerImpl) {

    }
    mapClass(Connection) {

    }
    mapClass(MinecraftServer) {

    }
    mapClass(GameType) {

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
    mapClass(FrameType) {

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
    mapClass(BuiltInRegistries) {

    }
    mapClass(MappedRegistry) {

    }
    mapClass(WritableRegistry) {

    }
    mapClass(RegistryAccess) {

    }
    mapClass(BuiltinRegistries) {

    }
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

    }
    mapClass(MobEffect) {

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

    }
    mapClass(EnumParticle) {

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
    mapClass(LegacyFishingHook) {

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

    }*/

}
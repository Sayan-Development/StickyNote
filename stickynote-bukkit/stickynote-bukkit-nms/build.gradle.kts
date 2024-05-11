import me.kcra.takenaka.generator.accessor.AccessorType
import me.kcra.takenaka.generator.accessor.CodeLanguage
import me.kcra.takenaka.generator.accessor.plugin.accessorRuntime
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

    mappingBundle("me.kcra.takenaka:mappings:1.8.8+1.20.4")
    implementation(accessorRuntime())
}

tasks {
    sourcesJar {
        dependsOn(generateAccessors)
    }
}

/*
nmsGen {
    basePackage = 'me.mohamad82.ruom.nmsaccessors'
    sourceSet = 'src/generated/java'

    minMinecraftVersion = '1.8.8'

    nullableAnnotation = "org.jetbrains.annotations.Nullable"
    notNullAnnotation = "org.jetbrains.annotations.NotNull"

    cleanOnRebuild = true

    var ClientboundPlayerInfoPacket = reqClass("net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket")
    var ClientboundPlayerInfoPacketAction = reqClass('net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket$Action')
    var ClientboundPlayerInfoPacketPlayerUpdate = reqClass('net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket$PlayerUpdate')
    var ClientboundPlayerInfoUpdatePacket = reqClass("net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket") //1.19.3 and above
    var ClientboundPlayerInfoUpdatePacketEntry = reqClass('net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$Entry')
    var ClientboundPlayerInfoRemovePacket = reqClass("net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket") //1.19.3 and above
    var ClientboundPlayerInfoUpdatePacketAction = reqClass('net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket$Action') //1.19.3 and above
    var ClientboundAddEntityPacket = reqClass("net.minecraft.network.protocol.game.ClientboundAddEntityPacket")
    var ClientboundAddPlayerPacket = reqClass("net.minecraft.network.protocol.game.ClientboundAddPlayerPacket")
    var ClientboundRotateHeadPacket = reqClass("net.minecraft.network.protocol.game.ClientboundRotateHeadPacket")
    var ClientboundRemoveEntitiesPacket = reqClass("net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket")
    var ClientboundMoveEntityPacketRot = reqClass('net.minecraft.network.protocol.game.ClientboundMoveEntityPacket$Rot')
    var ClientboundMoveEntityPacketPos = reqClass('net.minecraft.network.protocol.game.ClientboundMoveEntityPacket$Pos')
    var ClientboundMoveEntityPacketPosRot = reqClass('net.minecraft.network.protocol.game.ClientboundMoveEntityPacket$PosRot')
    var ClientboundAnimatePacket = reqClass("net.minecraft.network.protocol.game.ClientboundAnimatePacket")
    var ClientboundBlockBreakAckPacket = reqClass("net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket")
    var ClientboundSetEntityDataPacket = reqClass("net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket")
    var ClientboundSetEquipmentPacket = reqClass("net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket")
    var ClientboundTeleportEntityPacket = reqClass("net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket")
    var ClientboundSetEntityMotionPacket = reqClass("net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket")
    var ClientboundTakeItemEntityPacket = reqClass("net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket")
    var ClientboundBlockEventPacket = reqClass("net.minecraft.network.protocol.game.ClientboundBlockEventPacket")
    var ClientboundSetPassengersPacket = reqClass("net.minecraft.network.protocol.game.ClientboundSetPassengersPacket")
    var ClientboundBlockDestructionPacket = reqClass("net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket")
    var ClientboundUpdateAdvancementsPacket = reqClass("net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket")
    var ClientboundLevelChunkPacketData = reqClass("net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData") //1.18 and above
    var ClientboundLevelChunkWithLightPacket = reqClass("net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket") //1.18 and above
    var ClientboundLevelChunkPacket = reqClass("net.minecraft.network.protocol.game.ClientboundLevelChunkPacket") //1.17 and below
    var ClientboundLightUpdatePacket = reqClass("net.minecraft.network.protocol.game.ClientboundLightUpdatePacket")
    var ClientboundOpenScreenPacket = reqClass("net.minecraft.network.protocol.game.ClientboundOpenScreenPacket")
    var ClientboundRespawnPacket = reqClass("net.minecraft.network.protocol.game.ClientboundRespawnPacket")
    var ClientboundEntityEventPacket = reqClass("net.minecraft.network.protocol.game.ClientboundEntityEventPacket")
    var ClientboundChatPacket = reqClass("net.minecraft.network.protocol.game.ClientboundChatPacket")
    var ClientboundSetPlayerTeamPacket = reqClass("net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket")
    var ClientboundSetPlayerTeamPacketParameters = reqClass('net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket$Parameters')
    var ClientboundSetDisplayChatPreviewPacket = reqClass("net.minecraft.network.protocol.game.ClientboundSetDisplayChatPreviewPacket")
    var ClientboundChatPreviewPacket = reqClass("net.minecraft.network.protocol.game.ClientboundChatPreviewPacket")
    var ClientboundPlayerChatPacket = reqClass("net.minecraft.network.protocol.game.ClientboundPlayerChatPacket")
    var ClientboundSystemChatPacket = reqClass("net.minecraft.network.protocol.game.ClientboundSystemChatPacket")
    var ClientboundKeepAlivePacket = reqClass("net.minecraft.network.protocol.game.ClientboundKeepAlivePacket")
    var ClientboundSetCameraPacket = reqClass("net.minecraft.network.protocol.game.ClientboundSetCameraPacket")
    var ClientboundContainerSetContentPacket = reqClass("net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket")
    var ClientboundContainerSetSlotPacket = reqClass("net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket")
    var ClientboundLevelParticlesPacket = reqClass("net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket")
    var ClientboundSetDisplayObjectivePacket = reqClass("net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket")
    var ClientboundSetObjectivePacket = reqClass("net.minecraft.network.protocol.game.ClientboundSetObjectivePacket")
    var ClientboundSetScorePacket = reqClass("net.minecraft.network.protocol.game.ClientboundSetScorePacket")

    var ServerboundPlayerActionPacket = reqClass("net.minecraft.network.protocol.game.ServerboundPlayerActionPacket")
    var ServerboundPlayerActionPacketAction = reqClass('net.minecraft.network.protocol.game.ServerboundPlayerActionPacket$Action')
    var ServerboundInteractPacket = reqClass("net.minecraft.network.protocol.game.ServerboundInteractPacket")
    var ServerboundInteractPacketAction = reqClass('net.minecraft.network.protocol.game.ServerboundInteractPacket$Action')
    var ServerboundInteractPacketActionType = reqClass('net.minecraft.network.protocol.game.ServerboundInteractPacket$ActionType')
    var ServerboundInteractPacketActionInteractAt = reqClass('net.minecraft.network.protocol.game.ServerboundInteractPacket$InteractionAtLocationAction')
    var ServerboundInteractPacketActionInteract = reqClass('net.minecraft.network.protocol.game.ServerboundInteractPacket$InteractionAction')
    var ServerboundChatPreviewPacket = reqClass("net.minecraft.network.protocol.game.ServerboundChatPreviewPacket")
    var ServerboundKeepAlivePacket = reqClass("net.minecraft.network.protocol.game.ServerboundKeepAlivePacket")
    var ServerboundClientInformationPacket = reqClass("net.minecraft.network.protocol.game.ServerboundClientInformationPacket")

    var ServerPlayer = reqClass("net.minecraft.server.level.ServerPlayer")
    var Player = reqClass("net.minecraft.world.entity.player.Player")
    var ServerLevel = reqClass("net.minecraft.server.level.ServerLevel")
    var ServerLevelAccessor = reqClass("net.minecraft.world.level.ServerLevelAccessor")
    var ServerPlayerGameMode = reqClass("net.minecraft.server.level.ServerPlayerGameMode")
    var Level = reqClass("net.minecraft.world.level.Level")
    var LevelWriter = reqClass("net.minecraft.world.level.LevelWriter")
    var LevelChunk = reqClass("net.minecraft.world.level.chunk.LevelChunk")
    var ChunkAccess = reqClass("net.minecraft.world.level.chunk.ChunkAccess")
    var ChunkStatus = reqClass("net.minecraft.world.level.chunk.ChunkStatus")
    var LevelLightEngine = reqClass("net.minecraft.world.level.lighting.LevelLightEngine")
    var Packet = reqClass("net.minecraft.network.protocol.Packet")
    var ServerGamePacketListenerImpl = reqClass("net.minecraft.server.network.ServerGamePacketListenerImpl")
    var ServerCommonPacketListenerImpl = reqClass("net.minecraft.server.network.ServerCommonPacketListenerImpl") //1.20.2 and above
    var Connection = reqClass("net.minecraft.network.Connection")
    var MinecraftServer = reqClass("net.minecraft.server.MinecraftServer")
    var GameType = reqClass("net.minecraft.world.level.GameType")
    var MobSpawnType = reqClass("net.minecraft.world.entity.MobSpawnType")
    var Pose = reqClass("net.minecraft.world.entity.Pose")
    var Vec3 = reqClass("net.minecraft.world.phys.Vec3")
    var Vec3i = reqClass("net.minecraft.core.Vec3i")
    var Rotations = reqClass("net.minecraft.core.Rotations")
    var Mob = reqClass("net.minecraft.world.entity.Mob")
    var Entity = reqClass("net.minecraft.world.entity.Entity")
    var LivingEntity = reqClass("net.minecraft.world.entity.LivingEntity")
    var BlockEntity = reqClass("net.minecraft.world.level.block.entity.BlockEntity")
    var SpawnerBlockEntity = reqClass("net.minecraft.world.level.block.entity.SpawnerBlockEntity")
    var BaseSpawner = reqClass("net.minecraft.world.level.BaseSpawner")
    var SpawnData = reqClass("net.minecraft.world.level.SpawnData")
    var EntityType = reqClass("net.minecraft.world.entity.EntityType")
    var EquipmentSlot = reqClass("net.minecraft.world.entity.EquipmentSlot")
    var InteractionHand = reqClass("net.minecraft.world.InteractionHand")
    var BlockPos = reqClass("net.minecraft.core.BlockPos")
    var ChunkPos = reqClass("net.minecraft.world.level.ChunkPos")
    var Direction = reqClass("net.minecraft.core.Direction")
    var BlockState = reqClass("net.minecraft.world.level.block.state.BlockState")
    var BlockBehaviour = reqClass("net.minecraft.world.level.block.state.BlockBehaviour")
    var BlockStateBase = reqClass('net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase')
    var Blocks = reqClass("net.minecraft.world.level.block.Blocks")
    var Block = reqClass("net.minecraft.world.level.block.Block")
    var Component = reqClass("net.minecraft.network.chat.Component")
    var ComponentSerializer = reqClass('net.minecraft.network.chat.Component$Serializer')
    var Item = reqClass("net.minecraft.world.item.Item")
    var ItemStack = reqClass("net.minecraft.world.item.ItemStack")
    var Potion = reqClass("net.minecraft.world.item.alchemy.Potion")
    var Potions = reqClass("net.minecraft.world.item.alchemy.Potions")
    var PotionUtils = reqClass("net.minecraft.world.item.alchemy.PotionUtils")
    var SynchedEntityData = reqClass("net.minecraft.network.syncher.SynchedEntityData")
    var DataItem = reqClass('net.minecraft.network.syncher.SynchedEntityData$DataItem')
    var Tag = reqClass("net.minecraft.nbt.Tag")
    var CompoundTag = reqClass("net.minecraft.nbt.CompoundTag")
    var ListTag = reqClass("net.minecraft.nbt.ListTag")
    var StringTag = reqClass("net.minecraft.nbt.StringTag")
    var TagParser = reqClass("net.minecraft.nbt.TagParser")
    var EntityDataSerializer = reqClass("net.minecraft.network.syncher.EntityDataSerializer")
    var EntityDataSerializers = reqClass("net.minecraft.network.syncher.EntityDataSerializers")
    var EntityDataAccessor = reqClass("net.minecraft.network.syncher.EntityDataAccessor")
    var ResourceLocation = reqClass("net.minecraft.resources.ResourceLocation")
    var ResourceKey = reqClass("net.minecraft.resources.ResourceKey")
    var Advancement = reqClass("net.minecraft.advancements.Advancement")
    var AdvancementHolder = reqClass("net.minecraft.advancements.AdvancementHolder") //1.20.2 and above
    var AdvancementBuilder = reqClass('net.minecraft.advancements.Advancement$Builder')
    var AdvancementProgress = reqClass("net.minecraft.advancements.AdvancementProgress")
    var AdvancementRequirements = reqClass("net.minecraft.advancements.AdvancementRequirements")
    var ServerAdvancementManager = reqClass("net.minecraft.server.ServerAdvancementManager")
    var FrameType = reqClass("net.minecraft.advancements.FrameType")
    var DeserializationContext = reqClass("net.minecraft.advancements.critereon.DeserializationContext")
    var PredicateManager = reqClass("net.minecraft.world.level.storage.loot.PredicateManager")
    var LootDataManager = reqClass("net.minecraft.world.level.storage.loot.LootDataManager") //1.20.1 and above
    var GsonHelper = reqClass("net.minecraft.util.GsonHelper")
    var CreativeModeTab = reqClass("net.minecraft.world.item.CreativeModeTab")
    var AbstractContainerMenu = reqClass("net.minecraft.world.inventory.AbstractContainerMenu")
    var MenuType = reqClass("net.minecraft.world.inventory.MenuType")
    var DimensionType = reqClass("net.minecraft.world.level.dimension.DimensionType")
    var ParticleOptions = reqClass("net.minecraft.core.particles.ParticleOptions")
    var DifficultyInstance = reqClass("net.minecraft.world.DifficultyInstance")
    var SpawnGroupData = reqClass("net.minecraft.world.entity.SpawnGroupData")
    var ChatType = reqClass("net.minecraft.network.chat.ChatType")
    var VillagerData = reqClass("net.minecraft.world.entity.npc.VillagerData")
    var VillagerType = reqClass("net.minecraft.world.entity.npc.VillagerType")
    var VillagerProfession = reqClass("net.minecraft.world.entity.npc.VillagerProfession")
    var ChatFormatting = reqClass("net.minecraft.ChatFormatting")
    var BoatType = reqClass('net.minecraft.world.entity.vehicle.Boat$Type')
    var Registry = reqClass('net.minecraft.core.Registry')
    var BuiltInRegistries = reqClass("net.minecraft.core.registries.BuiltInRegistries")
    var MappedRegistry = reqClass('net.minecraft.core.MappedRegistry')
    var WritableRegistry = reqClass('net.minecraft.core.WritableRegistry')
    var RegistryAccess = reqClass('net.minecraft.core.RegistryAccess')
    var BuiltinRegistries = reqClass('net.minecraft.data.BuiltinRegistries')
    var CoreBuiltInRegistries = reqClass("net.minecraft.core.registries.BuiltInRegistries")
    var Holder = reqClass('net.minecraft.core.Holder')
    var Biome = reqClass('net.minecraft.world.level.biome.Biome')
    var BiomeBuilder = reqClass('net.minecraft.world.level.biome.Biome$BiomeBuilder')
    var BiomeCategory = reqClass('net.minecraft.world.level.biome.Biome$BiomeCategory')
    var BiomePrecipitation = reqClass('net.minecraft.world.level.biome.Biome$Precipitation')
    var TemperatureModifier = reqClass('net.minecraft.world.level.biome.Biome$TemperatureModifier')
    var BiomeGenerationSettings = reqClass('net.minecraft.world.level.biome.BiomeGenerationSettings')
    var BiomeSpecialEffects = reqClass('net.minecraft.world.level.biome.BiomeSpecialEffects')
    var BiomeSpecialEffectsBuilder = reqClass('net.minecraft.world.level.biome.BiomeSpecialEffects$Builder')
    var BiomeSpecialEffectsGrassColorModifier = reqClass('net.minecraft.world.level.biome.BiomeSpecialEffects$GrassColorModifier')
    var MobSpawnSettings = reqClass('net.minecraft.world.level.biome.MobSpawnSettings')
    var SoundEvent = reqClass('net.minecraft.sounds.SoundEvent')
    var SoundType = reqClass('net.minecraft.world.level.block.SoundType')
    var ChatSender = reqClass("net.minecraft.network.chat.ChatSender")
    var CryptSaltSignaturePair = reqClass('net.minecraft.util.Crypt$SaltSignaturePair')
    var PlayerChatMessage = reqClass("net.minecraft.network.chat.PlayerChatMessage")
    var ProfilePublicKey = reqClass("net.minecraft.world.entity.player.ProfilePublicKey")
    var NonNullList = reqClass("net.minecraft.core.NonNullList")
    var MobEffectInstance = reqClass("net.minecraft.world.effect.MobEffectInstance")
    var MobEffect = reqClass("net.minecraft.world.effect.MobEffect")
    var Scoreboard = reqClass("net.minecraft.world.scores.Scoreboard")
    var PlayerTeam = reqClass("net.minecraft.world.scores.PlayerTeam")
    var Team = reqClass("net.minecraft.world.scores.Team")
    var CollisionRule = reqClass('net.minecraft.world.scores.Team$CollisionRule')
    var Visibility = reqClass('net.minecraft.world.scores.Team$Visibility')
    var DyeColor = reqClass("net.minecraft.world.item.DyeColor")
    var SignText = reqClass("net.minecraft.world.level.block.entity.SignText")
    var EnumParticle = reqClass("spigot:EnumParticle")
    var ParticleType = reqClass("net.minecraft.core.particles.ParticleType")
    var PositionSource = reqClass("net.minecraft.world.level.gameevent.PositionSource")
    var BlockPositionSource = reqClass("net.minecraft.world.level.gameevent.BlockPositionSource")
    var EntityPositionSource = reqClass("net.minecraft.world.level.gameevent.EntityPositionSource")
    var VibrationPath = reqClass("net.minecraft.world.level.gameevent.vibrations.VibrationPath")
    var DustParticleOptions = reqClass("net.minecraft.core.particles.DustParticleOptions")
    var DustColorTransitionOptions = reqClass("net.minecraft.core.particles.DustColorTransitionOptions")
    var BlockParticleOption = reqClass("net.minecraft.core.particles.BlockParticleOption")
    var ItemParticleOption = reqClass("net.minecraft.core.particles.ItemParticleOption")
    var VibrationParticleOption = reqClass("net.minecraft.core.particles.VibrationParticleOption")
    var ShriekParticleOption = reqClass("net.minecraft.core.particles.ShriekParticleOption")
    var SculkChargeParticleOptions = reqClass("net.minecraft.core.particles.SculkChargeParticleOptions")
    var Vector3f = reqClass("com.mojang.math.Vector3f")
    var Objective = reqClass("net.minecraft.world.scores.Objective")
    var ObjectiveCriteria = reqClass("net.minecraft.world.scores.criteria.ObjectiveCriteria")
    var ObjectiveCriteriaRenderType = reqClass('net.minecraft.world.scores.criteria.ObjectiveCriteria$RenderType')
    var ServerScoreboardMethod = reqClass('net.minecraft.server.ServerScoreboard$Method')
    var RemoteChatSessionData = reqClass('net.minecraft.network.chat.RemoteChatSession$Data')
    var ClientInformation = reqClass("net.minecraft.server.level.ClientInformation")
    var PacketFlow = reqClass("net.minecraft.network.protocol.PacketFlow")
    var CommonListenerCookie = reqClass("net.minecraft.server.network.CommonListenerCookie")
    var InventoryMenu = reqClass("net.minecraft.world.inventory.InventoryMenu")

    var CrossbowItem = reqClass("net.minecraft.world.item.CrossbowItem")

    var ArmorStand = reqClass("net.minecraft.world.entity.decoration.ArmorStand")
    var Arrow = reqClass("net.minecraft.world.entity.projectile.Arrow")
    var ThrownPotion = reqClass("net.minecraft.world.entity.projectile.ThrownPotion")
    var ThrownTrident = reqClass("net.minecraft.world.entity.projectile.ThrownTrident")
    var ThrowableItemProjectile = reqClass("net.minecraft.world.entity.projectile.ThrowableItemProjectile")
    var ItemEntity = reqClass("net.minecraft.world.entity.item.ItemEntity")
    var FallingBlockEntity = reqClass("net.minecraft.world.entity.item.FallingBlockEntity")
    var AreaEffectCloud = reqClass("net.minecraft.world.entity.AreaEffectCloud")
    var FishingHook = reqClass("net.minecraft.world.entity.projectile.FishingHook")
    var LegacyFishingHook = reqClass("spigot:EntityFishingHook")
    var FireworkRocketEntity = reqClass("net.minecraft.world.entity.projectile.FireworkRocketEntity")
    var LightningBolt = reqClass("net.minecraft.world.entity.LightningBolt")
    var SignBlockEntity = reqClass("net.minecraft.world.level.block.entity.SignBlockEntity")
    var Villager = reqClass("net.minecraft.world.entity.npc.Villager")
    var Boat = reqClass("net.minecraft.world.entity.vehicle.Boat")
    var Creeper = reqClass("net.minecraft.world.entity.monster.Creeper")

    ClientboundPlayerInfoPacket
            .reqConstructor(ClientboundPlayerInfoPacketAction, ServerPlayer.array())
            .reqField("action")
            .reqField("entries")
    ClientboundPlayerInfoPacketAction
            .reqEnumField("ADD_PLAYER")
            .reqEnumField("UPDATE_GAME_MODE")
            .reqEnumField("UPDATE_LATENCY")
            .reqEnumField("UPDATE_DISPLAY_NAME")
            .reqEnumField("REMOVE_PLAYER")
    ClientboundPlayerInfoUpdatePacket
            .reqConstructor(ClientboundPlayerInfoUpdatePacketAction, ServerPlayer)
            .reqConstructor(EnumSet, Collection)
            .reqField("entries")
            .reqMethod("createPlayerInitializing", Collection)
            .reqMethod("entries")
    ClientboundPlayerInfoUpdatePacketEntry
            .reqConstructor(UUID, "com.mojang.authlib.GameProfile", boolean, int, GameType, Component, RemoteChatSessionData)
    ClientboundPlayerInfoRemovePacket
            .reqConstructor(List)
    ClientboundPlayerInfoUpdatePacketAction
            .reqEnumField("ADD_PLAYER")
            .reqEnumField("INITIALIZE_CHAT")
            .reqEnumField("UPDATE_GAME_MODE")
            .reqEnumField("UPDATE_LISTED")
            .reqEnumField("UPDATE_LATENCY")
            .reqEnumField("UPDATE_DISPLAY_NAME")
    ClientboundPlayerInfoPacketPlayerUpdate
            .reqConstructor("com.mojang.authlib.GameProfile", int, GameType, Component)
            .reqMethod("getProfile")
            .reqMethod("getLatency")
            .reqMethod("getGameMode")
            .reqMethod("getDisplayName")
    ClientboundAddEntityPacket
            .reqConstructor(int, UUID, double, double, double, float, float, EntityType, int, Vec3)
            .reqConstructor(Entity, int)
            .reqConstructor(Entity)
            .reqMethod("getId")
            .reqMethod("getUUID")
            .reqMethod("getX")
            .reqMethod("getY")
            .reqMethod("getZ")
            .reqMethod("getXa")
            .reqMethod("getYa")
            .reqMethod("getZa")
            .reqMethod("getyRot")
            .reqMethod("getxRot")
            .reqMethod("getType")
            .reqMethod("getData")
    ClientboundAddPlayerPacket
            .reqConstructor(Player)
            .reqMethod("getEntityId")
            .reqMethod("getPlayerId")
            .reqMethod("getX")
            .reqMethod("getY")
            .reqMethod("getZ")
            .reqMethod("getyRot")
            .reqMethod("getxRot")
    ClientboundRotateHeadPacket
            .reqConstructor(Entity, byte)
            .reqMethod("getEntity", Level)
            .reqMethod("getYHeadRot")
    ClientboundRemoveEntitiesPacket
            .reqConstructor(int[])
            .reqMethod("getEntityIds")
    ClientboundMoveEntityPacketRot
            .reqConstructor(int, byte, byte, boolean)
    ClientboundMoveEntityPacketPos
            .reqConstructor(int, short, short, short, boolean)
            .reqConstructor(int, long, long, long, boolean)
            .reqConstructor(int, byte, byte, byte, boolean)
    ClientboundMoveEntityPacketPosRot
            .reqConstructor(int, short, short, short, byte, byte, boolean)
            .reqConstructor(int, long, long, long, byte, byte, boolean)
            .reqConstructor(int, byte, byte, byte, byte, byte, boolean)
    ClientboundAnimatePacket
            .reqConstructor(Entity, int)
            .reqMethod("getId")
            .reqMethod("getAction")
    ClientboundBlockBreakAckPacket
            .reqConstructor(BlockPos, BlockState, ServerboundPlayerActionPacketAction, boolean)
    ClientboundSetEntityDataPacket
            .reqConstructor(int, SynchedEntityData, boolean)
            .reqConstructor(int, List) //1.19.3 and higher
            .reqMethod("getId")
    ClientboundSetEquipmentPacket
            .reqConstructor(int, List)
            .reqConstructor(int, EquipmentSlot, ItemStack)
    ClientboundTeleportEntityPacket
            .reqConstructor(Entity)
            .reqField("id")
            .reqField("x")
            .reqField("y")
            .reqField("z")
            .reqField("yRot")
            .reqField("xRot")
            .reqField("onGround")
    ClientboundSetEntityMotionPacket
            .reqConstructor(int, Vec3)
            .reqConstructor(int, double, double, double)
            .reqMethod("getId")
            .reqMethod("getXa")
            .reqMethod("getYa")
            .reqMethod("getZa")
    ClientboundTakeItemEntityPacket
            .reqConstructor(int, int, int)
            .reqConstructor(int, int)
            .reqMethod("getItemId")
            .reqMethod("getPlayerId")
            .reqMethod("getAmount")
    ClientboundBlockEventPacket
            .reqConstructor(BlockPos, Block, int, int)
            .reqMethod("getPos")
            .reqMethod("getB0")
            .reqMethod("getB1")
            .reqMethod("getBlock")
    ClientboundSetPassengersPacket
            .reqConstructor(Entity)
            .reqMethod("getPassengers")
            .reqMethod("getVehicle")
            .reqField("vehicle")
            .reqField("passengers")
    ClientboundBlockDestructionPacket
            .reqConstructor(int, BlockPos, int)
            .reqMethod("getId")
            .reqMethod("getPos")
            .reqMethod("getProgress")
    ClientboundUpdateAdvancementsPacket
            .reqConstructor(boolean, Collection, Set, Map)
            .reqMethod("getAdded")
            .reqMethod("getRemoved")
            .reqMethod("getProgress")
            .reqMethod("shouldReset")
    ClientboundLevelChunkPacketData
            .reqConstructor(LevelChunk)
    ClientboundLevelChunkWithLightPacket
            .reqConstructor(LevelChunk, LevelLightEngine, BitSet, BitSet, boolean)
    ClientboundLevelChunkPacket
            .reqConstructor(LevelChunk)
            .reqConstructor(LevelChunk, int)
    ClientboundLightUpdatePacket
            .reqConstructor(ChunkPos, LevelLightEngine, BitSet, BitSet, boolean)
            .reqConstructor(ChunkPos, LevelLightEngine, boolean)
    ClientboundOpenScreenPacket
            .reqConstructor(int, MenuType, Component)
            .reqConstructor(int, String, Component, int)
    ClientboundRespawnPacket
            .reqConstructor(DimensionType, ResourceKey, long, GameType, GameType, boolean, boolean, boolean)
            .reqConstructor(ResourceKey, ResourceKey, long, GameType, GameType, boolean, boolean, byte, java.util.Optional)
    ClientboundEntityEventPacket
            .reqConstructor(Entity, byte)
    ClientboundChatPacket
            .reqConstructor(Component, ChatType, UUID)
            .reqConstructor(Component, ChatType)
            .reqConstructor(Component, byte)
            .reqMethod("getMessage")
            .reqMethod("getType")
            .reqMethod("getSender")
            .reqField("message")
    ClientboundSetPlayerTeamPacket
            .reqConstructor(String, int, java.util.Optional, Collection)
            .reqConstructor()
            .reqField("name")
            .reqField("displayName")
            .reqField("playerPrefix")
            .reqField("playerSuffix")
            .reqField("nametagVisibility")
            .reqField("collisionRule")
            .reqField("color")
            .reqField("players")
            .reqField("method")
            .reqField("options")
    ClientboundSetPlayerTeamPacketParameters
            .reqConstructor(PlayerTeam)
    ClientboundSetDisplayChatPreviewPacket
            .reqConstructor(boolean)
            .reqMethod("enabled")
    ClientboundChatPreviewPacket
            .reqConstructor(int, Component)
            .reqMethod("queryId")
            .reqMethod("preview")
    ClientboundPlayerChatPacket
            .reqConstructor(Component, java.util.Optional, int, ChatSender, Instant, CryptSaltSignaturePair)
            .reqMethod("getMessage")
            .reqField("signedContent")
            .reqField("unsignedContent")
            .reqField("typeId")
            .reqField("sender")
            .reqField("timeStamp")
            .reqField("saltSignature")
    ClientboundSystemChatPacket
            .reqMethod("content")
            .reqMethod("typeId")
    ClientboundSetCameraPacket
            .reqField("cameraId")
    ClientboundContainerSetContentPacket
            .reqConstructor(int, int, NonNullList, ItemStack)
            .reqField("items")
    ClientboundContainerSetSlotPacket
            .reqConstructor(int, int, int, ItemStack)
            .reqField("slot")
            .reqField("itemStack")
    ClientboundLevelParticlesPacket
            .reqConstructor(EnumParticle, boolean, float, float, float, float, float, float, float, int, int[]) //1.8.8 - 1.12.2
            .reqConstructor(ParticleOptions, boolean, float, float, float, float, float, float, float, int) //1.13 - 1.14.4
            .reqConstructor(ParticleOptions, boolean, double, double, double, float, float, float, float, int) //1.15 and above
    ClientboundSetDisplayObjectivePacket
            .reqConstructor(int, Objective)
            .reqMethod("getObjectiveName")
    ClientboundSetObjectivePacket
            .reqConstructor(Objective, int)
    ClientboundSetScorePacket
            .reqConstructor(ServerScoreboardMethod, String, String, int)

    ServerboundPlayerActionPacket
            .reqMethod("getPos")
            .reqMethod("getDirection")
            .reqMethod("getAction")
    ServerboundPlayerActionPacketAction
            .reqEnumField("START_DESTROY_BLOCK")
            .reqEnumField("ABORT_DESTROY_BLOCK")
            .reqEnumField("STOP_DESTROY_BLOCK")
            .reqEnumField("DROP_ALL_ITEMS")
            .reqEnumField("DROP_ITEM")
            .reqEnumField("RELEASE_USE_ITEM")
            .reqEnumField("SWAP_ITEM_WITH_OFFHAND")
    ServerboundInteractPacket
            .reqField("entityId")
            .reqField("action")
            .reqField("usingSecondaryAction")
            .reqField("location")
            .reqField("hand")
    ServerboundInteractPacketAction
            .reqMethod("getType")
            .reqField("INTERACT")
            .reqField("ATTACK")
            .reqField("INTERACT_AT")
    ServerboundInteractPacketActionType
            .reqEnumField("INTERACT")
            .reqEnumField("ATTACK")
            .reqEnumField("INTERACT_AT")
    ServerboundInteractPacketActionInteractAt
            .reqField("hand")
            .reqField("location")
    ServerboundInteractPacketActionInteract
            .reqField("hand")

    ServerPlayer
            .reqConstructor(MinecraftServer, ServerLevel, "com.mojang.authlib.GameProfile")
            .reqConstructor(MinecraftServer, ServerLevel, "com.mojang.authlib.GameProfile", ServerPlayerGameMode)
            .reqConstructor(MinecraftServer, ServerLevel, "com.mojang.authlib.GameProfile", ProfilePublicKey)
            .reqConstructor(MinecraftServer, ServerLevel, "com.mojang.authlib.GameProfile", ClientInformation) //1.20.2 and above
            .reqMethod("setCamera", Entity)
            .reqMethod("refreshContainer", AbstractContainerMenu)
            .reqField("connection")
            .reqField("latency")
    ServerPlayerGameMode
            .reqConstructor(ServerLevel)
            .reqConstructor(Level)
    ServerboundChatPreviewPacket
            .reqMethod("queryId")
            .reqMethod("query")
    Level
            .reqMethod("getChunkAt", BlockPos)
            .reqMethod("getChunk", int, int)
            .reqMethod("obfuscated:c:1.8.8", BlockPos)
            .reqMethod("getBlockState", BlockPos)
            .reqMethod("getLightEngine")
            .reqMethod("dimension")
            .reqMethod("dimensionType")
            .reqMethod("dimensionTypeId")
            .reqMethod("getBlockEntity", BlockPos)
            .reqMethod("getCurrentDifficultyAt", BlockPos)
    LevelChunk
            .reqMethod("getBlockState", BlockPos)
            .reqMethod("getFluidState", BlockPos)
            .reqMethod("setBlockState", BlockPos, BlockState, boolean)
            .reqMethod("getBlockEntityNbtForSaving", BlockPos)
            .reqMethod("getLevel")
    ChunkAccess
            .reqMethod("getPos")
    ChunkStatus
            .reqField("EMPTY")
            .reqField("FULL")
            .reqField("BIOMES")
            .reqMethod("byName", String)
    LevelLightEngine
            .reqMethod("checkBlock", BlockPos)
            .reqMethod("hasLightWork")
    Player
            .reqMethod("setEntityOnShoulder", CompoundTag)
            .reqMethod("setShoulderEntityRight", CompoundTag)
            .reqMethod("setShoulderEntityLeft", CompoundTag)
            .reqMethod("getGameProfile")
            .reqMethod("playSound", SoundEvent, float, float)
            .reqField("containerMenu")
            .reqField("DATA_PLAYER_MODE_CUSTOMISATION")
    ServerLevel
            .reqMethod("getSeed")
            .reqMethod("addFreshEntity", Entity)
    ServerGamePacketListenerImpl
            .reqConstructor(MinecraftServer, Connection, ServerPlayer, CommonListenerCookie)
            .reqMethod("send", Packet)
            .reqField("connection")
    ServerCommonPacketListenerImpl
            .reqMethod("send", Packet)
            .reqField("connection")
            .reqMethod("latency")
    Connection
            .reqConstructor(PacketFlow)
            .reqMethod("disconnect", Component)
            .reqMethod("connectToServer", InetSocketAddress, boolean)
            .reqMethod("connectToLocalServer", SocketAddress)
            .reqMethod("getAverageReceivedPackets")
            .reqMethod("getAverageSentPackets")
            .reqField("channel")
            .reqField("address")
    MinecraftServer
            .reqMethod("registryAccess")
    GameType
            .reqEnumField("SURVIVAL")
            .reqEnumField("CREATIVE")
            .reqEnumField("ADVENTURE")
            .reqEnumField("SPECTATOR")
    MobSpawnType
            .reqEnumField("NATURAL")
            .reqEnumField("CHUNK_GENERATION")
            .reqEnumField("SPAWNER")
            .reqEnumField("STRUCTURE")
            .reqEnumField("BREEDING")
            .reqEnumField("MOB_SUMMONED")
            .reqEnumField("JOCKEY")
            .reqEnumField("EVENT")
            .reqEnumField("CONVERSION")
            .reqEnumField("REINFORCEMENT")
            .reqEnumField("TRIGGERED")
            .reqEnumField("BUCKET")
            .reqEnumField("SPAWN_EGG")
            .reqEnumField("COMMAND")
            .reqEnumField("DISPENSER")
            .reqEnumField("PATROL")
    Pose
            .reqEnumField("STANDING")
            .reqEnumField("FALL_FLYING")
            .reqEnumField("SLEEPING")
            .reqEnumField("SWIMMING")
            .reqEnumField("SPIN_ATTACK")
            .reqEnumField("CROUCHING")
            .reqEnumField("LONG_JUMPING")
            .reqEnumField("DYING")
    Vec3
            .reqConstructor(double, double, double)
            .reqMethod("x")
            .reqMethod("y")
            .reqMethod("z")
    Vec3i
            .reqMethod("getX")
            .reqMethod("getY")
            .reqMethod("getZ")
    Mob
            .reqMethod("finalizeSpawn", ServerLevelAccessor, DifficultyInstance, MobSpawnType, SpawnGroupData, CompoundTag)
    Entity
            .reqConstructor(EntityType, Level)
            .reqMethod("getType")
            .reqMethod("getId")
            .reqMethod("setId", int)
            .reqMethod("setPose", Pose)
            .reqMethod("hasPose", Pose)
            .reqMethod("isCrouching")
            .reqMethod("setPos", double, double, double)
            .reqMethod("setRot", float, float)
            .reqMethod("setGlowingTag", boolean) //1.17 and higher
            .reqMethod("hasGlowingTag")
            .reqMethod("setGlowing", boolean) //1.16.5 and below
            .reqMethod("isGlowing")
            .reqMethod("setCustomName", Component)
            .reqMethod("getCustomName")
            .reqMethod("setCustomNameVisible", boolean)
            .reqMethod("isCustomNameVisible")
            .reqMethod("setInvisible", boolean)
            .reqMethod("isInvisible")
            .reqMethod("setInvulnerable", boolean)
            .reqMethod("setIsInPowderSnow", boolean)
            .reqMethod("setItemSlot", EquipmentSlot, ItemStack)
            .reqMethod("setNoGravity", boolean)
            .reqMethod("isNoGravity")
            .reqMethod("setOnGround", boolean)
            .reqMethod("isOnGround")
            .reqMethod("setSprinting", boolean)
            .reqMethod("isSprinting")
            .reqMethod("setSwimming", boolean)
            .reqMethod("isSwimming")
            .reqMethod("setTicksFrozen", int) //1.17 and higher
            .reqMethod("getTicksFrozen") //1.17 and higher
            .reqMethod("setUUID", UUID)
            .reqMethod("getUUID")
            .reqMethod("getEntityData")
            .reqMethod("setSharedFlag", int, boolean)
            .reqMethod("getSharedFlag", int)
            .reqMethod("moveTo", double, double, double)
            .reqField("position")
            .reqField("spigot:locX:1.8.8")
            .reqField("spigot:locY:1.8.8")
            .reqField("spigot:locZ:1.8.8")
            .reqField("DATA_CUSTOM_NAME")
            .reqField("DATA_CUSTOM_NAME_VISIBLE")
            .reqField("DATA_SILENT")
            .reqField("DATA_NO_GRAVITY")
            .reqField("DATA_POSE")
            .reqField("DATA_TICKS_FROZEN")
    LivingEntity
            .reqMethod("setArrowCount", int)
            .reqMethod("getArrowCount")
            .reqMethod("setSleepingPos", BlockPos)
            .reqMethod("getSleepingPos")
            .reqMethod("removeEffectParticles")
            .reqMethod("setStingerCount", int)
            .reqMethod("getStingerCount")
            .reqMethod("triggerItemUseEffects", ItemStack, int)
            .reqMethod("startUsingItem", InteractionHand)
            .reqMethod("stopUsingItem")
            .reqMethod("getUseItem")
            .reqMethod("getUseItemRemainingTicks")
            .reqMethod("setLivingEntityFlag", int, boolean)
            .reqField("useItem")
            .reqField("DATA_LIVING_ENTITY_FLAGS")
            .reqField("DATA_HEALTH_ID")
            .reqField("DATA_EFFECT_COLOR_ID")
            .reqField("DATA_EFFECT_AMBIENCE_ID")
            .reqField("DATA_ARROW_COUNT_ID")
            .reqField("DATA_STINGER_COUNT_ID")
            .reqField("SLEEPING_POS_ID")
    SpawnerBlockEntity
            .reqMethod("getSpawner")
    BaseSpawner
            .reqField("nextSpawnData")
    SpawnData
            .reqMethod("getEntityToSpawn")
    EntityType
            .reqMethod("loadEntityRecursive", CompoundTag, Level, Function)
            .reqAllEnumFieldsOfVersion(latestMinecraftVersion)
    EquipmentSlot
            .reqEnumField("MAINHAND")
            .reqEnumField("OFFHAND")
            .reqEnumField("FEET")
            .reqEnumField("LEGS")
            .reqEnumField("CHEST")
            .reqEnumField("HEAD")
    InteractionHand
            .reqEnumField("MAIN_HAND")
            .reqEnumField("OFF_HAND")
    BlockPos
            .reqConstructor(int, int, int)
            .reqConstructor(double, double, double)
            .reqConstructor(Vec3)
    ChunkPos
            .reqConstructor(int, int)
            .reqMethod("getMiddleBlockX")
            .reqMethod("getMiddleBlockZ")
            .reqMethod("getMinBlockX")
            .reqMethod("getMinBlockZ")
            .reqMethod("getMaxBlockX")
            .reqMethod("getMaxBlockZ")
            .reqMethod("getBlockX", int)
            .reqMethod("getBlockZ", int)
    Direction
            .reqMethod("getName")
            .reqEnumField("DOWN")
            .reqEnumField("UP")
            .reqEnumField("NORTH")
            .reqEnumField("SOUTH")
            .reqEnumField("WEST")
            .reqEnumField("EAST")
    BlockState
            .reqMethod("spigot:getBlock:1.12.2")
    BlockStateBase
            .reqMethod("getBlock")
    Rotations
            .reqConstructor(float, float, float)
            .reqMethod("getX")
            .reqMethod("getY")
            .reqMethod("getZ")
            .reqMethod("getWrappedX")
            .reqMethod("getWrappedY")
            .reqMethod("getWrappedZ")
    Block
            .reqField("spigot:stepSound:1.8.8")
            .reqMethod("byItem", Item)
            .reqMethod("spigot:getById:1.8.8", int)
            .reqMethod("getSoundType", BlockState)
            .reqMethod("defaultBlockState")
            .reqMethod("getId", BlockState)
    Component
            .reqMethod("getStyle")
            .reqMethod("getContents")
            .reqMethod("getString", int)
            .reqMethod("getSiblings")
            .reqMethod("plainCopy")
            .reqMethod("copy")
    ComponentSerializer
            .reqMethod("fromJsonLenient", String)
    Item
            .reqMethod("getItemCategory")
    ItemStack
            .reqConstructor(CompoundTag)
            .reqField("EMPTY")
            .reqField("TAG_ENCH")
            .reqField("TAG_DISPLAY")
            .reqField("TAG_DISPLAY_NAME")
            .reqField("TAG_LORE")
            .reqField("TAG_DAMAGE")
            .reqField("TAG_COLOR")
            .reqField("TAG_UNBREAKABLE")
            .reqField("TAG_REPAIR_COST")
            .reqField("TAG_CAN_DESTROY_BLOCK_LIST")
            .reqField("TAG_CAN_PLACE_ON_BLOCK_LIST")
            .reqField("TAG_HIDE_FLAGS")
            .reqMethod("of", CompoundTag)
            .reqMethod("spigot:createStack:1.10.2", CompoundTag)
            .reqMethod("getTag")
            .reqMethod("getOrCreateTag")
            .reqMethod("setTag", CompoundTag)
            .reqMethod("getHoverName")
            .reqMethod("getDisplayName")
            .reqMethod("getItem")
            .reqMethod("save", CompoundTag)
    Potion
            .reqField("name")
    PotionUtils
            .reqMethod("getMobEffects", ItemStack)
            .reqMethod("getColor", ItemStack)
            .reqMethod("getPotion", ItemStack)
            .reqMethod("getPotion", CompoundTag)
            .reqMethod("setPotion", ItemStack, Potion)
    SynchedEntityData
            .reqConstructor(Entity)
            .reqField("itemsById")
            .reqMethod("packDirty") //1.19.3 and higher
            .reqMethod("getNonDefaultValues") //1.19.3 and higher
            .reqMethod("define", EntityDataAccessor, Object)
            .reqMethod("defineId", Class, EntityDataSerializer)
            .reqMethod("set", EntityDataAccessor, Object)
            .reqMethod("get", EntityDataAccessor)
            .reqMethod("getItem", EntityDataAccessor)
            .reqMethod("obfuscated:a:1.8.8", int, Object)
            .reqMethod("spigot:add:1.8.8", int, int)
            .reqMethod("spigot:watch:1.8.8", int, Object)
    DataItem
            .reqConstructor(EntityDataAccessor, Object)
            .reqField("initialValue")
            .reqMethod("getAccessor")
            .reqMethod("setValue", Object)
            .reqMethod("getValue")
            .reqMethod("isSetToDefault")
            .reqMethod("value")
    Tag
            .reqField("OBJECT_HEADER")
            .reqField("ARRAY_HEADER")
            .reqField("OBJECT_REFERENCE")
            .reqField("STRING_SIZE")
            .reqField("TAG_END")
            .reqField("TAG_BYTE")
            .reqField("TAG_SHORT")
            .reqField("TAG_INT")
            .reqField("TAG_LONG")
            .reqField("TAG_FLOAT")
            .reqField("TAG_DOUBLE")
            .reqField("TAG_BYTE_ARRAY")
            .reqField("TAG_STRING")
            .reqField("TAG_LIST")
            .reqField("TAG_COMPOUND")
            .reqField("TAG_INT_ARRAY")
            .reqField("TAG_LONG_ARRAY")
            .reqField("TAG_ANY_NUMERIC")
            .reqField("MAX_DEPTH")
    CompoundTag
            .reqConstructor()
            .reqMethod("getAllKeys")
            .reqMethod("size")
            .reqMethod("put", String, Tag)
            .reqMethod("putString", String, String)
            .reqMethod("get", String)
            .reqMethod("getList", String, int)
            .reqMethod("getString", String)
            .reqMethod("getCompound", String)
            .reqMethod("remove", String)
            .reqMethod("copy")
    ListTag
            .reqConstructor(List, byte)
            .reqConstructor()
    StringTag
            .reqConstructor(String)
    TagParser
            .reqMethod("parseTag", String)
    EntityDataSerializer
            .reqMethod("createAccessor", int)
    EntityDataSerializers
            .reqField("BYTE")
            .reqField("INT")
            .reqField("FLOAT")
            .reqField("STRING")
            .reqField("COMPONENT")
            .reqField("OPTIONAL_COMPONENT")
            .reqField("ITEM_STACK")
            .reqField("BLOCK_STATE")
            .reqField("BOOLEAN")
            .reqField("PARTICLE")
            .reqField("ROTATIONS")
            .reqField("BLOCK_POS")
            .reqField("OPTIONAL_BLOCK_POS")
            .reqField("DIRECTION")
            .reqField("OPTIONAL_UUID")
            .reqField("COMPOUND_TAG")
            .reqField("VILLAGER_DATA")
            .reqField("OPTIONAL_UNSIGNED_INT")
            .reqField("POSE")
    EntityDataAccessor
            .reqMethod("getId")
    ResourceLocation
            .reqConstructor(String)
            .reqConstructor(String, String)
            .reqMethod("getPath")
            .reqMethod("getNamespace")
            .reqMethod("toString")
    ResourceKey
            .reqMethod("create", ResourceKey, ResourceLocation)
    Advancement
            .reqMethod("getDisplay")
            .reqMethod("getRewards")
            .reqMethod("getCriteria")
            .reqMethod("criteria")
            .reqMethod("toString")
            .reqMethod("getId")
            .reqMethod("getRequirements")
            .reqMethod("requirements")
            .reqMethod("getChatComponent")
            .reqMethod("fromJson", "com.google.gson.JsonObject", DeserializationContext)
            .reqMethod("serializeToJson")
            .reqField("CODEC")
    AdvancementHolder
            .reqConstructor(ResourceLocation, Advancement)
    AdvancementBuilder
            .reqMethod("advancement")
            .reqMethod("parent", Advancement)
            .reqMethod("parent", ResourceLocation)
            .reqMethod("serializeToJson")
            .reqMethod("fromJson", "com.google.gson.JsonObject", DeserializationContext)
            .reqMethod("mojang:build:1.20.1", ResourceLocation) //1.12 - 1.20.1
    AdvancementProgress
            .reqConstructor()
            .reqMethod("update", Map, "java.lang.String[][]")
            .reqMethod("update", AdvancementRequirements) //1.20.2 and above
            .reqMethod("isDone")
            .reqMethod("grantProgress", String)
            .reqMethod("revokeProgress", String)
            .reqMethod("getRemainingCriteria")
    AdvancementRequirements
            .reqConstructor("java.lang.String[][]")
            .reqMethod("mojang:requirements:1.20.2")
            .reqMethod("requirements")
    ServerAdvancementManager
            .reqField("GSON")
    DeserializationContext
            .reqConstructor(ResourceLocation, PredicateManager)
            .reqConstructor(ResourceLocation, LootDataManager)
    PredicateManager
            .reqConstructor()
    LootDataManager
            .reqConstructor()
    GsonHelper
            .reqMethod("fromJson", "com.google.gson.Gson", String, Class)
    CreativeModeTab
            .reqField("langId")
    AbstractContainerMenu
            .reqMethod("sendAllDataToRemote")
            .reqField("containerId")
    MenuType
            .reqEnumField("GENERIC_9x1")
            .reqEnumField("GENERIC_9x2")
            .reqEnumField("GENERIC_9x3")
            .reqEnumField("GENERIC_9x4")
            .reqEnumField("GENERIC_9x5")
            .reqEnumField("GENERIC_9x6")
    DimensionType
            .reqField("DEFAULT_OVERWORLD")
            .reqField("DEFAULT_NETHER")
            .reqField("DEFAULT_END")
    ChatType
            .reqEnumField("CHAT")
            .reqEnumField("SYSTEM")
            .reqEnumField("GAME_INFO")
    VillagerData
            .reqConstructor(VillagerType, VillagerProfession, int)
            .reqMethod("getType")
            .reqMethod("getProfession")
    VillagerType
            .reqField("DESERT")
            .reqField("JUNGLE")
            .reqField("PLAINS")
            .reqField("SAVANNA")
            .reqField("SNOW")
            .reqField("SWAMP")
            .reqField("TAIGA")
    VillagerProfession
            .reqField("NONE")
            .reqField("ARMORER")
            .reqField("BUTCHER")
            .reqField("CARTOGRAPHER")
            .reqField("CLERIC")
            .reqField("FARMER")
            .reqField("FISHERMAN")
            .reqField("FLETCHER")
            .reqField("LEATHERWORKER")
            .reqField("LIBRARIAN")
            .reqField("MASON")
            .reqField("NITWIT")
            .reqField("SHEPHERD")
            .reqField("TOOLSMITH")
            .reqField("WEAPONSMITH")
    ChatFormatting
            .reqEnumField("BLACK")
            .reqEnumField("DARK_BLUE")
            .reqEnumField("DARK_GREEN")
            .reqEnumField("DARK_AQUA")
            .reqEnumField("DARK_RED")
            .reqEnumField("DARK_PURPLE")
            .reqEnumField("GOLD")
            .reqEnumField("GRAY")
            .reqEnumField("DARK_GRAY")
            .reqEnumField("BLUE")
            .reqEnumField("GREEN")
            .reqEnumField("AQUA")
            .reqEnumField("RED")
            .reqEnumField("LIGHT_PURPLE")
            .reqEnumField("YELLOW")
            .reqEnumField("WHITE")
            .reqEnumField("OBFUSCATED")
            .reqEnumField("BOLD")
            .reqEnumField("STRIKETHROUGH")
            .reqEnumField("UNDERLINE")
            .reqEnumField("ITALIC")
            .reqEnumField("RESET")
    BoatType
            .reqEnumField("OAK")
            .reqEnumField("SPRUCE")
            .reqEnumField("BIRCH")
            .reqEnumField("JUNGLE")
            .reqEnumField("ACACIA")
            .reqEnumField("DARK_OAK")
    Registry
            .reqField("BIOME_REGISTRY")
            .reqField("PARTICLE_TYPE")
            .reqField("BLOCK")
            .reqMethod("getOrThrow", ResourceKey)
            .reqMethod("get", ResourceKey)
            .reqMethod("get", ResourceLocation)
            .reqMethod("register", Registry, ResourceLocation, Object)
            .reqMethod("register", Registry, ResourceKey, Object)
    MappedRegistry
            .reqField("frozen")
    WritableRegistry
            .reqMethod("register", ResourceKey, Object, "com.mojang.serialization.Lifecycle")
    RegistryAccess
            .reqMethod("ownedRegistryOrThrow", ResourceKey)
    BuiltinRegistries
            .reqField("BIOME")
            .reqMethod("register", Registry, ResourceKey, Object)
    CoreBuiltInRegistries
            .reqField("PARTICLE_TYPE")
            .reqField("BLOCK")
    Holder
            .reqMethod("direct", Object)
    Biome
            .reqField("generationSettings")
            .reqField("mobSettings")
            .reqMethod("getPrecipitation")
            .reqMethod("getBiomeCategory")
            .reqMethod("getSpecialEffects")
    BiomeBuilder
            .reqConstructor()
            .reqMethod("from", Biome)
            .reqMethod("precipitation", BiomePrecipitation)
            .reqMethod("biomeCategory", BiomeCategory)
            .reqMethod("temperature", float)
            .reqMethod("downfall", float)
            .reqMethod("specialEffects", BiomeSpecialEffects)
            .reqMethod("mobSpawnSettings", MobSpawnSettings)
            .reqMethod("generationSettings", BiomeGenerationSettings)
            .reqMethod("temperatureAdjustment", TemperatureModifier)
            .reqMethod("build")
    TemperatureModifier
            .reqEnumField("NONE")
            .reqEnumField("FROZEN")
    BiomeSpecialEffects
            .reqMethod("getFogColor")
            .reqMethod("getWaterColor")
            .reqMethod("getWaterFogColor")
            .reqMethod("getSkyColor")
            .reqMethod("getFoliageColorOverride")
            .reqMethod("getGrassColorOverride")
            .reqMethod("getGrassColorModifier")
            .reqMethod("getAmbientParticleSettings")
            .reqMethod("getAmbientLoopSoundEvent")
            .reqMethod("getAmbientMoodSettings")
            .reqMethod("getAmbientAdditionsSettings")
            .reqMethod("getBackgroundMusic")
    BiomeSpecialEffectsBuilder
            .reqConstructor()
            .reqMethod("fogColor", int)
            .reqMethod("waterColor", int)
            .reqMethod("waterFogColor", int)
            .reqMethod("skyColor", int)
            .reqMethod("foliageColorOverride", int)
            .reqMethod("grassColorModifier", BiomeSpecialEffectsGrassColorModifier)
            .reqMethod("grassColorOverride", int)
            .reqMethod("build")
    BiomeSpecialEffectsGrassColorModifier
            .reqEnumField("NONE")
            .reqEnumField("DARK_FOREST")
            .reqEnumField("SWAMP")
    SoundEvent
            .reqMethod("getLocation")
    SoundType
            .reqField("breakSound")
            .reqField("stepSound")
            .reqField("placeSound")
            .reqField("hitSound")
            .reqField("fallSound")
    PlayerChatMessage
            .reqMethod("serverContent")
            .reqMethod("signedContent")
            .reqMethod("signature")
            .reqMethod("unsignedContent")
    NonNullList
            .reqConstructor(List, Object)
            .reqMethod("create")
            .reqMethod("withSize", int, Object)
            .reqMethod("get", int)
            .reqMethod("set", int, Object)
            .reqMethod("add", int, Object)
            .reqMethod("remove", int)
            .reqMethod("size")
            .reqMethod("clear")
    MobEffectInstance
            .reqMethod("getEffect")
            .reqMethod("getDuration")
            .reqMethod("getAmplifier")
            .reqMethod("isAmbient")
            .reqMethod("isVisible")
            .reqMethod("showIcon")
    MobEffect
            .reqMethod("getDescriptionId")
            .reqMethod("getDisplayName")
            .reqMethod("getCategory")
            .reqMethod("getColor")
    Scoreboard
            .reqConstructor()
            .reqMethod("hasObjective", String)
            .reqMethod("getOrCreateObjective", String)
            .reqMethod("getObjective", String)
            .reqMethod("addObjective", String, ObjectiveCriteria, Component, ObjectiveCriteriaRenderType)
            .reqMethod("getOrCreatePlayerScore", String, Objective)
            .reqMethod("getTrackedPlayers")
            .reqMethod("resetPlayerScore", String, Objective)
            .reqMethod("removeObjective", Objective)
    PlayerTeam
            .reqConstructor(Scoreboard, String)
            .reqField("scoreboard")
            .reqField("name")
            .reqField("players")
            .reqField("displayName")
            .reqField("playerPrefix")
            .reqField("playerSuffix")
            .reqField("allowFriendlyFire")
            .reqField("seeFriendlyInvisibles")
            .reqField("nameTagVisibility")
            .reqField("deathMessageVisibility")
            .reqField("color")
            .reqField("collisionRule")
            .reqField("displayNameStyle")
    CollisionRule
            .reqEnumField("ALWAYS")
            .reqEnumField("NEVER")
            .reqEnumField("PUSH_OTHER_TEAMS")
            .reqEnumField("PUSH_OWN_TEAM")
    Visibility
            .reqEnumField("ALWAYS")
            .reqEnumField("NEVER")
            .reqEnumField("HIDE_FOR_OTHER_TEAMS")
            .reqEnumField("HIDE_FOR_OWN_TEAM")
    DyeColor
            .reqAllEnumFieldsOfVersion(latestMinecraftVersion)
            .reqMethod("getName")
            .reqMethod("getFireworkColor")
            .reqMethod("byId", int)
    SignText
            .reqConstructor(Component.array(), Component.array(), DyeColor, boolean)
            .reqMethod("emptyMessages")
            .reqMethod("hasGlowingText")
            .reqMethod("setHasGlowingText", boolean)
            .reqMethod("getColor")
            .reqMethod("setColor", DyeColor)
            .reqMethod("getMessage", int, boolean)
            .reqMethod("setMessage", int, Component)
    BlockPositionSource
            .reqConstructor(BlockPos)
    EntityPositionSource
            .reqConstructor(int)
            .reqConstructor(Entity, float)
    VibrationPath
            .reqConstructor(BlockPos, PositionSource, int)
    DustParticleOptions
            .reqConstructor(float, float, float, float)
            .reqConstructor("org.joml.Vector3f", float)
            .reqConstructor(Vector3f, float)
    DustColorTransitionOptions
            .reqConstructor("org.joml.Vector3f", "org.joml.Vector3f", float)
            .reqConstructor(Vector3f, Vector3f, float)
    BlockParticleOption
            .reqConstructor(ParticleType, BlockState)
    ItemParticleOption
            .reqConstructor(ParticleType, ItemStack)
    VibrationParticleOption
            .reqConstructor(VibrationPath)
            .reqConstructor(PositionSource, int)
    ShriekParticleOption
            .reqConstructor(int)
    SculkChargeParticleOptions
            .reqConstructor(float)
    Objective
            .reqConstructor(Scoreboard, String, ObjectiveCriteria, Component, ObjectiveCriteriaRenderType)
    ObjectiveCriteria
            .reqField("TRIGGER")
            .reqField("HEALTH")
            .reqField("FOOD")
            .reqField("AIR")
            .reqField("ARMOR")
            .reqField("EXPERIENCE")
            .reqField("LEVEL")
            .reqField("TEAM_KILL")
            .reqField("KILLED_BY_TEAM")
    ObjectiveCriteriaRenderType
            .reqField("INTEGER")
            .reqField("HEARTS")
    ServerScoreboardMethod
            .reqEnumField("CHANGE")
            .reqEnumField("REMOVE")
    ClientInformation
            .reqMethod("createDefault")
    PacketFlow
            .reqEnumField("CLIENTBOUND")
            .reqEnumField("SERVERBOUND")
    CommonListenerCookie
            .reqConstructor("com.mojang.authlib.GameProfile", int, ClientInformation)

    CrossbowItem
            .reqMethod("isCharged", ItemStack)
            .reqMethod("setCharged", ItemStack, boolean)
            .reqMethod("getChargedProjectiles", ItemStack)
            .reqMethod("clearChargedProjectiles", ItemStack)
            .reqMethod("getChargeDuration", ItemStack)
            .reqMethod("getStartSound", int)
            .reqMethod("getPowerForTime", int, ItemStack)

    ArmorStand
            .reqConstructor(EntityType, Level)
            .reqMethod("setHeadPose", Rotations)
            .reqMethod("setBodyPose", Rotations)
            .reqMethod("setLeftArmPose", Rotations)
            .reqMethod("setRightArmPose", Rotations)
            .reqMethod("setLeftLegPose", Rotations)
            .reqMethod("setRightLegPose", Rotations)
            .reqMethod("setMarker", boolean)
            .reqMethod("setNoBasePlate", boolean)
            .reqMethod("setShowArms", boolean)
            .reqMethod("setSmall", boolean)
            .reqMethod("setYBodyRot", float)
            .reqMethod("setYHeadRot", float)
            .reqMethod("getHeadPose")
            .reqMethod("getBodyPose")
            .reqMethod("getLeftArmPose")
            .reqMethod("getRightArmPose")
            .reqMethod("getLeftLegPose")
            .reqMethod("getRightLegPose")
            .reqMethod("isMarker")
            .reqMethod("isNoBasePlate")
            .reqMethod("isShowArms")
            .reqMethod("isSmall")
            .reqMethod("spigot:setGravity:1.8.8", boolean)
            .reqMethod("spigot:hasGravity:1.8.8")
    Arrow
            .reqConstructor(EntityType, Level)
            .reqMethod("setEffectsFromItem", ItemStack)
            .reqMethod("makeParticle", int)
            .reqMethod("getColor")
            .reqMethod("setFixedColor", int)
    ThrownPotion
            .reqConstructor(EntityType, Level)
            .reqConstructor(Level)
            .reqField("DATA_ITEM_STACK")
    ThrownTrident
            .reqConstructor(EntityType, Level)
            .reqConstructor(Level, LivingEntity, ItemStack)
            .reqField("tridentItem")
            .reqField("clientSideReturnTridentTickCount")
            .reqField("ID_LOYALTY")
            .reqField("ID_FOIL")
    ThrowableItemProjectile
            .reqMethod("setItem", ItemStack)
            .reqMethod("getItemRaw")
            .reqField("DATA_ITEM_STACK")
    ItemEntity
            .reqConstructor(Level, double, double, double, ItemStack)
            .reqMethod("setItem", ItemStack)
            .reqMethod('getItem')
            .reqField("DATA_ITEM")
    FallingBlockEntity
            .reqConstructor(Level, double, double, double, BlockState)
            .reqMethod("setStartPos", BlockPos)
            .reqMethod("getBlockState")
            .reqMethod("getAddEntityPacket")
            .reqField("blockState")
    AreaEffectCloud
            .reqConstructor(Level, double, double, double)
            .reqMethod("setRadius", float)
            .reqMethod("getRadius")
            .reqMethod("getColor")
            .reqMethod("setFixedColor", int)
            .reqMethod("getParticle")
            .reqMethod("setParticle", ParticleOptions)
            .reqMethod("setWaiting", boolean)
            .reqMethod("isWaiting")
            .reqMethod("getPotion")
    FishingHook
            .reqConstructor(EntityType, Level) //1.17
            .reqMethod("setOwner", Entity)
            .reqMethod("getAddEntityPacket")
            .reqField("DATA_HOOKED_ENTITY")
            .reqField("DATA_BITING")
    LegacyFishingHook
            .reqConstructor(Player, Level, int, int) //1.14 - 1.16
            .reqConstructor(Level, Player) //1.8 - 1.13
            .reqField("obfuscated:b:1.12.2")
    FireworkRocketEntity
            .reqConstructor(EntityType, Level)
            .reqMethod("hasExplosion")
            .reqMethod("isAttachedToEntity")
            .reqField("DATA_ID_FIREWORKS_ITEM")
            .reqField("DATA_ATTACHED_TO_TARGET")
            .reqField("DATA_SHOT_AT_ANGLE")
    LightningBolt
            .reqConstructor(EntityType, Level)
    SignBlockEntity
            .reqConstructor(BlockPos, BlockState)
            .reqMethod("updateText", UnaryOperator, boolean)
            .reqMethod("getMessage", int, boolean)
            .reqMethod("setMessage", int, Component, Component)
            .reqMethod("setMessage", int, Component)
            .reqMethod("getUpdatePacket")
            .reqMethod("hasGlowingText")
            .reqMethod("setHasGlowingText", boolean)
            .reqMethod("markUpdated")
            .reqField("messages")
    Villager
            .reqConstructor(EntityType, Level)
            .reqField("DATA_VILLAGER_DATA")
    Boat
            .reqConstructor(EntityType, Level)
            .reqMethod("setDamage", float)
            .reqMethod("getDamage")
            .reqMethod("setHurtTime", int)
            .reqMethod("getHurtTime")
            .reqMethod("setBubbleTime", int)
            .reqMethod("getBubbleTime")
            .reqMethod("setHurtDir", int)
            .reqMethod("getHurtDir")
            .reqMethod("setType", BoatType)
            .reqMethod("getBoatType")
            .reqField("DATA_ID_PADDLE_LEFT")
            .reqField("DATA_ID_PADDLE_RIGHT")
    Creeper
            .reqField("DATA_SWELL_DIR")
            .reqField("DATA_IS_POWERED")
            .reqField("DATA_IS_IGNITED")
}
 */

@Suppress("LocalVariableName")
accessors {
    basePackage("org.sayandev.stickynote.nms.accessors")
    accessedNamespaces("spigot")
    accessorType(AccessorType.REFLECTION)
    codeLanguage(CodeLanguage.KOTLIN)

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
    val ServerboundChatPreviewPacket = "net.minecraft.network.protocol.game.ServerboundChatPreviewPacket"
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
        methodInferred("getId", "1.16.5")
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
        //TODO (removed in 1.12.0) constructor(Component, Byte::class)
        //methodInferred("getMessage", "1.16.5")
        //methodInferred("getType", "1.16.5")
        //methodInferred("getSender", "1.16.5")
        //fieldInferred("message", "1.16.5")
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

    }
    mapClass(ClientboundSetDisplayChatPreviewPacket) {

    }
    mapClass(ClientboundChatPreviewPacket) {

    }
    mapClass(ClientboundPlayerChatPacket) {

    }
    mapClass(ClientboundSystemChatPacket) {

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
    mapClass(ClientboundUpdateMobEffectPacket) {
        constructor(Int::class, MobEffectInstance)
    }
    mapClass(ClientboundRemoveMobEffectPacket) {
        constructor(Int::class, MobEffect)
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
    mapClass(ServerPlayer) {
        field(ServerGamePacketListenerImpl, "connection")
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
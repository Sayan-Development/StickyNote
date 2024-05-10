import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy

fun getProjectRelocations(): List<BuildRelocation> {
    return listOf(
        BuildRelocation("org.spongepowered", "org.sayandev.stickynote.lib.spongepowered", listOf(Platform.CORE, Platform.BUKKIT)),
        BuildRelocation("com.zaxxer", "org.sayandev.stickynote.lib.zaxxer", listOf(Platform.CORE, Platform.BUKKIT, Platform.PAPER)),
        BuildRelocation("org.slf4j", "org.sayandev.stickynote.lib.slf4j", listOf(Platform.CORE, Platform.BUKKIT, Platform.PAPER)),
        BuildRelocation("org.reflections", "org.sayandev.stickynote.lib.reflections", listOf(Platform.BUKKIT, Platform.PAPER)),
        BuildRelocation("org.jetbrains", "org.sayandev.stickynote.lib.jetbrains", listOf(Platform.CORE, Platform.BUKKIT, Platform.PAPER)),
        BuildRelocation("net.kyori", "org.sayandev.stickynote.lib.kyori", listOf(Platform.CORE, Platform.BUKKIT)),
        BuildRelocation("com.cryptomorin", "org.sayandev.stickynote.lib.xseries", listOf(Platform.BUKKIT, Platform.PAPER)),
        BuildRelocation("org.incendo", "org.sayandev.stickynote.lib.incendo", listOf(Platform.BUKKIT, Platform.PAPER)),
        BuildRelocation("com.github.stefvanschie.inventoryframework", "org.sayandev.stickynote.lib.inventoryframework", listOf(Platform.BUKKIT, Platform.PAPER)),
        BuildRelocation("com.alessiodp.libby", "org.sayandev.stickynote.lib.libby", listOf(Platform.CORE, Platform.BUKKIT, Platform.PAPER)),
    )
}

fun Project.getRelocations(): List<BuildRelocation> = getProjectRelocations()

fun getProjectRelocations(platform: Platform): List<BuildRelocation> {
    return getProjectRelocations().filter { it.relocatePlatforms.contains(platform) }
}

fun Project.getRelocations(platform: Platform): List<BuildRelocation> = getProjectRelocations(platform)

fun ShadowJar.applyShadowRelocation(platform: Platform) {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    getProjectRelocations(platform).forEach { relocate ->
        relocate(relocate.from, relocate.to)
    }
}

data class BuildRelocation(
    val from: String,
    val to: String,
    val relocatePlatforms: List<Platform>
)
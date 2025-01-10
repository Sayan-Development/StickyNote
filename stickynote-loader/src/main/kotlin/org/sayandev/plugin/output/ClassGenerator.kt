package org.sayandev.plugin.output

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.kotlinpoet.javapoet.JClassName
import com.squareup.kotlinpoet.javapoet.JTypeSpec
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import org.apache.groovy.json.internal.Type
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.file.Directory
import org.sayandev.plugin.ModuleConfiguration
import org.sayandev.plugin.StickyLoadDependency
import javax.lang.model.element.Modifier
import kotlin.jvm.optionals.getOrNull

@KotlinPoetJavaPoetPreview
class ClassGenerator(
    val project: Project,
    val outputDir: Directory,
    val modules: List<ModuleConfiguration>,
    val relocate: Boolean,
    val relocation: Pair<String, String>,
    val stickyLoadDependencies: List<StickyLoadDependency>
) {
    private val basePackage = "org.sayandev.stickynote.generated"

    fun generateStickyNotesClass() {
        val stickynotesClass = JClassName.get(basePackage, "StickyNotes")
        val file = JavaFile.builder(basePackage,
            JTypeSpec.classBuilder(stickynotesClass)
                .addModifiers(Modifier.PUBLIC)
                /*.addField(FieldSpec.builder(Boolean::class.java, "LOAD_KOTLIN")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$loadKotlin")
                    .build())
                .addField(FieldSpec.builder(String::class.java, "KOTLIN_VERSION")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("\$S", kotlinVersion)
                    .build())*/
                .addField(FieldSpec.builder(Boolean::class.java, "RELOCATE")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("\$L", relocate)
                    .build())
                .addField(FieldSpec.builder(JClassName.get(basePackage, "Relocation"), "RELOCATION")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("new Relocation(\$S, \$S)", relocation.first.replace(".", "{}"), relocation.second.replace(".", "{}"))
                    .build()
                )
                .apply {
                    val versionCatalogs = project.extensions.getByType(VersionCatalogsExtension::class.java)
                    val libs = versionCatalogs.named("stickyNoteLibs")
                    for (module in modules) {
                        this.addField(FieldSpec.builder(JClassName.get(basePackage, "Dependency"), "DEPENDENCY_".plus(module.type.artifact.replace("-", "_").replace(".", "_")).uppercase())
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer("new Dependency(\$S, \$S, \$S, \$S, \$L)", "org{}sayandev", module.type.artifact, module.version, null, false)
                            .build())
                        val bundleName = module.type.artifact.removePrefix("stickynote-")
                        val moduleBundleProvider = libs.findBundle("implementation-$bundleName").getOrNull() ?: continue
                        for (library in moduleBundleProvider.get()) {
                            this.addField(FieldSpec.builder(JClassName.get(basePackage, "Dependency"), "DEPENDENCY_".plus(library.module.group.replace(".", "_").plus(library.module.name.replace("-", "_").replace(".", "_"))).uppercase())
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                .initializer("new Dependency(\$S, \$S, \$S, \$S, \$L)", library.group.replace(".", "{}"), library.name, library.version, null, false)
                                .build())
                        }
                    }

                    for (externalDependency in stickyLoadDependencies) {
                        this.addField(FieldSpec.builder(JClassName.get(basePackage, "Dependency"), "DEPENDENCY_STICKYLOAD_".plus(externalDependency.name.replace("-", "_").replace(".", "_")).uppercase())
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer("new Dependency(\$S, \$S, \$S, \$S, \$L)", externalDependency.group.replace(".", "{}"), externalDependency.name, externalDependency.version, externalDependency.relocation?.replace(".", "{}"), true)
                            .build())
                    }

                    for (repository in project.repositories.filterIsInstance<MavenArtifactRepository>().distinctBy { it.url }) {
                        this.addField(FieldSpec.builder(String::class.java, "REPOSITORY_".plus(repository.name.replace("-", "_")).uppercase())
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer("\$S", repository.url.toString())
                            .build())
                    }
                }
                .build())
            .build()
        file.writeTo(outputDir.asFile)
    }

    fun generateDependencyClass() {
        val file = JavaFile.builder(basePackage, dependencyClass()).build()
        file.writeTo(outputDir.asFile)
    }

    fun generateRelocationClass() {
        val file = JavaFile.builder(basePackage, relocationClass()).build()
        file.writeTo(outputDir.asFile)
    }

    fun dependencyClass(): JTypeSpec {
        return JTypeSpec.classBuilder("Dependency")
            .addModifiers(Modifier.PUBLIC)
            .addField(String::class.java, "group", Modifier.PRIVATE, Modifier.FINAL)
            .addField(String::class.java, "name", Modifier.PRIVATE, Modifier.FINAL)
            .addField(String::class.java, "version", Modifier.PRIVATE, Modifier.FINAL)
            .addField(String::class.java, "relocation", Modifier.PRIVATE, Modifier.FINAL)
            .addField(Boolean::class.java, "isStickyLoad", Modifier.PRIVATE, Modifier.FINAL)
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addParameter(String::class.java, "group")
                    .addParameter(String::class.java, "name")
                    .addParameter(String::class.java, "version")
                    .addParameter(String::class.java, "relocation")
                    .addParameter(Boolean::class.java, "isStickyLoad")
                    .addStatement("this.\$N = \$N", "group", "group")
                    .addStatement("this.\$N = \$N", "name", "name")
                    .addStatement("this.\$N = \$N", "version", "version")
                    .addStatement("this.\$N = \$N", "relocation", "relocation")
                    .addStatement("this.\$N = \$N", "isStickyLoad", "isStickyLoad")
                    .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("getGroup")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(String::class.java)
                    .addStatement("return \$N", "group")
                    .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("getName")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(String::class.java)
                    .addStatement("return \$N", "name")
                    .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("getVersion")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(String::class.java)
                    .addStatement("return \$N", "version")
                    .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("getRelocation")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(String::class.java)
                    .addStatement("return \$N", "relocation")
                    .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("isStickyLoad")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(Boolean::class.java)
                    .addStatement("return \$N", "isStickyLoad")
                    .build()
            )
            .build()
    }

    fun relocationClass(): JTypeSpec {
        return JTypeSpec.classBuilder("Relocation")
            .addModifiers(Modifier.PUBLIC)
            .addField(String::class.java, "from", Modifier.PRIVATE, Modifier.FINAL)
            .addField(String::class.java, "to", Modifier.PRIVATE, Modifier.FINAL)
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addParameter(String::class.java, "from")
                    .addParameter(String::class.java, "to")
                    .addStatement("this.\$N = \$N", "from", "from")
                    .addStatement("this.\$N = \$N", "to", "to")
                    .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("getFrom")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(String::class.java)
                    .addStatement("return \$N", "from")
                    .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("getTo")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(String::class.java)
                    .addStatement("return \$N", "to")
                    .build()
            )
            .build()
    }
}
package org.sayandev.plugin.output

import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.kotlinpoet.javapoet.JClassName
import com.squareup.kotlinpoet.javapoet.JTypeSpec
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.sayandev.plugin.ModuleConfiguration
import javax.lang.model.element.Modifier

@KotlinPoetJavaPoetPreview
class ClassGenerator(
    val project: Project,
    val outputDir: Directory,
    val modules: List<ModuleConfiguration>,
    val useLoader: Boolean,
    val relocate: Boolean,
    val relocation: Pair<String, String>
) {
    private val basePackage = "org.sayandev.generated"

    fun generateStickyNotesClass() {
        val stickynotesClass = JClassName.get(basePackage, "StickyNotes")
        val file = JavaFile.builder(basePackage,
            JTypeSpec.classBuilder(stickynotesClass)
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec.builder(Boolean::class.java, "USE_LOADER")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$useLoader")
                    .build())
                .addField(FieldSpec.builder(Boolean::class.java, "RELOCATE")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$relocate")
                    .build())
                /*.addField(FieldSpec.builder(Boolean::class.java, "LOAD_KOTLIN")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$loadKotlin")
                    .build())
                .addField(FieldSpec.builder(String::class.java, "KOTLIN_VERSION")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("\$S", kotlinVersion)
                    .build())*/
                .addField(FieldSpec.builder(JClassName.get(basePackage, "Relocation"), "RELOCATION")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("new Relocation(\$S, \$S)", relocation.first.replace(".", "{}"), relocation.second.replace(".", "{}"))
                    .build()
                )
                .apply {
                    for (module in modules) {
                        this.addField(FieldSpec.builder(JClassName.get(basePackage, "Dependency"), module.type.artifact.replace("-", "_").uppercase())
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                            .initializer("new Dependency(\$S, \$S, \$S)", "org.sayandev", module.type.artifact, module.version)
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
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addParameter(String::class.java, "group")
                    .addParameter(String::class.java, "name")
                    .addParameter(String::class.java, "version")
                    .addStatement("this.\$N = \$N", "group", "group")
                    .addStatement("this.\$N = \$N", "name", "name")
                    .addStatement("this.\$N = \$N", "version", "version")
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
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
// Configuration plugin version
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.fabric.loom)
}
val fabricLoaderVersion = project.property("loader_version") as String
val kotlinLoaderVersion = project.property("kotlin_loader_version") as String
val minecraftVersion = project.property("minecraft_version") as String
val targetJavaVersion = 21

base {
    version = project.property("mod_version") as String
    group = project.property("maven_group") as String
    archivesName = project.property("archives_base_name") as String
}
// Configuration Java Environment and Source Generate
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    withSourcesJar()
}
// Configuration Fabric API
fabricApi {
    configureDataGeneration {
        client = true
    }
}
allprojects {
    repositories{
        mavenCentral()
        mavenLocal()
        maven {
            name = "Jitpack"
            url = uri("https://jitpack.io")
        }
    }
}


dependencies {

    minecraft(libs.minecraft)
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modApi(libs.fabric.loader)
    modApi(libs.fabric.kotlin)
    modApi(libs.fabric.api)
    modApi(libs.mcef)
    modApi(libs.mcef)
    include(libs.mcef)

}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to project.version,
                "minecraft_version" to minecraftVersion,
                "loader_version" to fabricLoaderVersion,
                "kotlin_loader_version" to kotlinLoaderVersion
            )
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}

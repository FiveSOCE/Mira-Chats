import java.net.URI
import java.security.MessageDigest

plugins {
    java
}

group = "com.mira"
version = "0.3.2"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

val essentialsVersion = "2.22.0"
val essentialsSha256 = "bda4685105977fca2e209820a9f0ad24275bd103390a03236f38e59bfdac58e6"
val essentialsJar = layout.projectDirectory.file("libs/EssentialsX-$essentialsVersion.jar").asFile

fun sha256(file: File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(file.readBytes()).joinToString("") { byte -> "%02x".format(byte) }
}

val downloadEssentialsX by tasks.registering {
    doLast {
        if (essentialsJar.exists() && sha256(essentialsJar) == essentialsSha256) return@doLast
        essentialsJar.parentFile.mkdirs()
        URI("https://github.com/EssentialsX/Essentials/releases/download/$essentialsVersion/EssentialsX-$essentialsVersion.jar")
            .toURL().openStream().use { input ->
                essentialsJar.outputStream().use { output -> input.copyTo(output) }
            }
        check(sha256(essentialsJar) == essentialsSha256) {
            "Downloaded EssentialsX JAR failed SHA-256 verification"
        }
    }
}

val paperApiVersion = providers.gradleProperty("paperApiVersion").orElse("1.21.11-R0.1-SNAPSHOT")
val compileJavaVersion = providers.gradleProperty("compileJavaVersion").map(String::toInt).orElse(21)
val bytecodeJavaVersion = providers.gradleProperty("bytecodeJavaVersion").map(String::toInt).orElse(21)

dependencies {
    compileOnly("io.papermc.paper:paper-api:${paperApiVersion.get()}")
    compileOnly(files(essentialsJar))

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(compileJavaVersion.get()))
}

tasks.withType<JavaCompile>().configureEach {
    dependsOn(downloadEssentialsX)
    options.encoding = "UTF-8"
    options.release.set(bytecodeJavaVersion.get())
}

tasks.test {
    useJUnitPlatform()
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.jar {
    archiveFileName.set("MiraChats-${project.version}.jar")
}

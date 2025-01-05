plugins {
    id("java")
    id("com.gradleup.shadow") version("8.3.2")
}

group = "com.artillexstudios.axvanish"
version = "1.0.0"

allprojects {
    repositories {
        mavenCentral()

        maven("https://jitpack.io/")
        maven("https://repo.artillex-studios.com/releases/")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
}

subprojects {
    apply {
        plugin("java")
        plugin("com.gradleup.shadow")
    }

    dependencies {
        implementation("com.artillexstudios.axapi:axapi:1.4.509:all")
        implementation("dev.jorel:commandapi-bukkit-shade:9.7.0")
        implementation("com.h2database:h2:2.3.232")
        compileOnly("com.github.ben-manes.caffeine:caffeine:3.1.8")
        compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
        compileOnly("org.apache.commons:commons-lang3:3.14.0")
        compileOnly("commons-io:commons-io:2.16.1")
        compileOnly("it.unimi.dsi:fastutil:8.5.13")
        compileOnly("me.clip:placeholderapi:2.11.6")
        compileOnly("org.slf4j:slf4j-api:2.0.9")
        compileOnly("com.zaxxer:HikariCP:5.1.0")
        compileOnly("org.jooq:jooq:3.19.10")
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        relocate("com.github.benmanes", "com.artillexstudios.axvanish.libs.axapi.libs.caffeine")
        relocate("com.artillexstudios.axapi", "com.artillexstudios.axvanish.libs.axapi")
        relocate("dev.jorel.commandapi", "com.artillexstudios.axvanish.libs.commandapi")
        relocate("com.zaxxer", "com.artillexstudios.axvanish.libs.hikaricp")
        relocate("org.jooq", "com.artillexstudios.axvanish.libs.jooq")
        relocate("org.h2", "com.artillexstudios.axvanish.libs.h2")
    }
}
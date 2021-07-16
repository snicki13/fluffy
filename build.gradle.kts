import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    java
    kotlin("jvm") version "1.5.10"
}

group = "me.domin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "m2-dv8tion"
        url = URI("https://m2.dv8tion.net/releases")
    }
    maven {
        url = URI("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.10")
    implementation("net.dv8tion:JDA:4.3.0_277")
    implementation("io.github.serpro69:kotlin-faker:1.7.1")
    implementation("io.insert-koin:koin-core:3.1.2")
    implementation("org.slf4j:slf4j-api:1.7.31")
    implementation("org.slf4j:slf4j-simple:1.7.31")
}

tasks.jar {
    manifest {
        attributes(Pair("Main-Class", "de.snickit.fluffy.Fluffy"))
    }
    // To add all of the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

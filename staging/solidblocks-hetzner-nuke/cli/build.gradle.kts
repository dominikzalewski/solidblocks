plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:3.5.2")
    implementation("me.tomsdevsn:hetznercloud-api:3.1.0")
    implementation("io.github.resilience4j:resilience4j-kotlin:1.7.1")
    implementation("io.github.resilience4j:resilience4j-retry:1.7.1")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useKotlinTest("1.7.10")

            dependencies {
                implementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
            }
        }
    }
}

application {
    mainClass.set("de.solidblocks.hetzner.nuke.AppKt")
}

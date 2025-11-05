import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kobwebx.markdown)
    alias(libs.plugins.serialization.plugin)
}

group = "com.example.blogmultiplatform"
version = "1.0-SNAPSHOT"

kobweb {
    app {
        index {
            description.set("Powered by Kobweb")
        }
    }
}

kotlin {
    // This example is frontend only. However, for a fullstack app, you can uncomment the includeServer parameter
    // and the `jvmMain` source set below.
    configAsKobwebApplication("blogmultiplatform" , includeServer = true)

    sourceSets {
        commonMain.dependencies {
          // Add shared dependencies between JS and JVM here if building a fullstack app
            implementation(libs.kotlinx.serialization)
            implementation(project(":shared"))
        }

        jsMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
            implementation(libs.silk.icons.fa)
            // This default template uses built-in SVG icons, but what's available is limited.
            // Uncomment the following if you want access to a large set of font-awesome icons:
            // implementation(libs.silk.icons.fa)
            implementation(libs.kobwebx.markdown)
            implementation(libs.kotlin.bootstrap)
        }

        // Uncomment the following if you pass `includeServer = true` into the `configAsKobwebApplication` call.
        jvmMain.dependencies {
            compileOnly(libs.kobweb.api) // Provided by Kobweb backend at runtime
            implementation(libs.mongodb.kotlin.driver)
            implementation(libs.kotlinx.serialization)
        }
    }
}

// Ensure KSP cache path exists before KSP tasks run to avoid "symbols (No such file or directory)" errors
// Resolve the paths at configuration time so task actions don't access `project` during execution (avoids configuration cache problems).
val kspJsDir = layout.buildDirectory.dir("kspCaches/js/jsMain").get().asFile
val kspSymbolsFile = layout.buildDirectory.file("kspCaches/js/jsMain/symbols").get().asFile
val kspJvmDir = layout.buildDirectory.dir("kspCaches/jvm/jvmMain").get().asFile
val kspJvmSymbolsFile = layout.buildDirectory.file("kspCaches/jvm/jvmMain/symbols").get().asFile

// Create them now at configuration time (preemptively) to avoid race/NotFound issues during KSP tasks
if (!kspJsDir.exists()) {
    kspJsDir.mkdirs()
}
if (!kspSymbolsFile.exists()) {
    kspSymbolsFile.createNewFile()
}
// If file is empty, write minimal JSON object to avoid KSP JSON decoding EOF errors
if (kspSymbolsFile.length() == 0L) {
    kspSymbolsFile.writeText("{}")
}

// Create JVM KSP caches as well
if (!kspJvmDir.exists()) {
    kspJvmDir.mkdirs()
}
if (!kspJvmSymbolsFile.exists()) {
    kspJvmSymbolsFile.createNewFile()
}
if (kspJvmSymbolsFile.length() == 0L) {
    kspJvmSymbolsFile.writeText("{}")
}

tasks.matching { it.name == "kspKotlinJs" || it.name == "kspKotlinJvm" }.configureEach {
    doFirst {
        if (!kspJsDir.exists()) {
            kspJsDir.mkdirs()
        }
        if (!kspSymbolsFile.exists()) {
            kspSymbolsFile.createNewFile()
        }
        if (kspSymbolsFile.length() == 0L) {
            kspSymbolsFile.writeText("{}")
        }
        // Ensure JVM ksp cache exists before running KSP tasks
        if (!kspJvmDir.exists()) {
            kspJvmDir.mkdirs()
        }
        if (!kspJvmSymbolsFile.exists()) {
            kspJvmSymbolsFile.createNewFile()
        }
        if (kspJvmSymbolsFile.length() == 0L) {
            kspJvmSymbolsFile.writeText("{}")
        }
    }
}

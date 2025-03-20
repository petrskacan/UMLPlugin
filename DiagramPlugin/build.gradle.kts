val ver = "1.1.7-beta"
group = "com.thesis"
version = ver

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.3.0" // Migrated to 2.x
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}
val intellijVersion = "2024.3"
val ideType = "IU" //
dependencies {
    intellijPlatform {

        create(ideType, intellijVersion)
        when (ideType) {
            "PC" -> {
                plugin("PythonCore", "243.24978.46")
            }
            "PY" -> {
                plugin("Pythonid", "243.26053.27")
            }
            "IC" -> {
                bundledPlugin("com.intellij.java")
                plugin("PythonCore", "243.24978.46")
            }
            else -> {
                bundledPlugin("com.intellij.java")
                plugin("PythonCore", "243.24978.46")
                plugin("Pythonid", "243.26053.27")
            }
        }
    }

    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.26.3")
    implementation("org.dom4j:dom4j:2.1.4")
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.shadowJar {
    dependsOn(tasks.named("jar")) // Ensure the JAR task runs first
    archiveClassifier.set("all")
    mergeServiceFiles()
    dependencies {
        include(dependency("com.github.javaparser:javaparser-core:3.25.3"))
        include(dependency("org.dom4j:dom4j:2.1.4"))
    }
}

tasks.named("buildPlugin") {
    dependsOn(tasks.named("shadowJar"))
}

intellijPlatform {
    buildSearchableOptions = true
    instrumentCode = true
    projectName = project.name

    pluginConfiguration {
        id = "com.thesis.diagramplugin"
        name = "DiagramPlugin"
        version = ver
        description = "A plugin for generating class diagrams and kopenograms."
        changeNotes = """
            <ul>
                <li>Fixed compatibility issues with newer IntelliJ versions.</li>
                <li>Enhanced diagram rendering.</li>
            </ul>
        """.trimIndent()

        ideaVersion {
            sinceBuild = "223"
            untilBuild = "243.*"
        }


        vendor {
            name = "Prague University of Economics and Business"
            email = "skap04@vse.cz"
            url = "https://github.com/petrskacan/UMLPlugin"
        }
    }

    publishing {
        token.set(System.getenv("PUBLISH_TOKEN"))
        channels.set(listOf("default"))
    }

    signing {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }
}



tasks.named<Test>("test") {
    useJUnitPlatform()
    maxHeapSize = "1G"

    testLogging {
        events("passed")
    }
}
tasks.withType<Jar> {
    manifest {
        attributes["Implementation-Title"] = "DiagramPlugin"
        attributes["Implementation-Version"] = version
    }
}

tasks.clean {
    doFirst {
        file("build").deleteRecursively()
    }
}

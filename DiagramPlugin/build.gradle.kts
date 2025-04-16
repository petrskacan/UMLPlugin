

group = "com.thesis"
version = "1.1.96-beta"

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://cache-redirector.jetbrains.com/intellij-dependencies")
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html

val intellijVersion = "2022.3.2"
val ideType = "IC" //
val ideaVersion = "223.8617.56"

intellij {
    type.set(ideType)
    version.set(ideaVersion)
    pluginName.set("DiagramPlugin")
    updateSinceUntilBuild.set(false)

    if (ideType == "PC") {
        plugins.add("python-ce")
    } else if (ideType == "PY") {
        plugins.add("PythonCore:223.8617.56")
        plugins.add("Pythonid:231.8770.65")
        plugins.add("python")
        plugins.add("python-ce")
    } else if (ideType == "IC") {
        plugins.add("java")
        plugins.add("PythonCore:223.8617.56")
    } else {
        plugins.add("java")
        plugins.add("Pythonid:231.8770.65")
    }
}

dependencies {
    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.25.3")
    implementation("org.dom4j:dom4j:2.1.4")
    implementation("jaxen:jaxen:2.0.0")
    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

tasks.shadowJar {
    mergeServiceFiles()
    dependencies {
        include(dependency("com.github.javaparser:javaparser-core:3.25.3"))
        include(dependency("org.dom4j:dom4j:2.1.4"))
        include(dependency("jaxen:jaxen:2.0.0"))
    }
}

tasks.named("buildPlugin") {
    dependsOn(tasks.named("shadowJar"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    patchPluginXml {
        sinceBuild.set("223")
        untilBuild.set("243.*")
        changeNotes.set("""
        <ul>
        Changes for Class Diagrams
            <li>Added bend points</li>
            <li>Fixed custom dependencies for PyCharm</li>
            <li>Arrow is now facing right direction</li> 
             KNOWN ISSUES          
             <li>Non-standard characters are displayed incorrectly</li>
        </ul>
    """.trimIndent())
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    maxHeapSize = "1G"

    testLogging {
        events("passed")
    }
}
tasks {
    runPluginVerifier {
        ideVersions.set(
                listOf(
                        "IC-2022.3.2", // IntelliJ IDEA Community
                        "PS-2022.3.2", // PyCharm Professional
                        "IC-2023.1",
                        "PS-2023.1",
                        "IC-2023.3",
                        "PS-2023.3"
                )
        )
        failureLevel.set(
                setOf(
                        org.jetbrains.intellij.tasks.RunPluginVerifierTask.FailureLevel.COMPATIBILITY_PROBLEMS,
                        org.jetbrains.intellij.tasks.RunPluginVerifierTask.FailureLevel.MISSING_DEPENDENCIES
                )
        )


    }
}

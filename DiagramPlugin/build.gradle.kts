

group = "com.thesis"
version = "1.0.49-beta"

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

    if (ideType == "PC") {
        plugins.add("python-ce")
    } else if (ideType == "PY") {
        plugins.add("python")
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
            <li>XML should be automaticaly cahnged when creating new class diagram</li>            
            <li>Interfaces now correctly display inheritance</li>
            <li>KNOWN ISSUE - NO SUPPORT FOR 243</li>
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

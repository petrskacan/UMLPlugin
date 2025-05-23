

group = "com.thesis"
version = "1.2.0"

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
        <h2>Release Notes – Version 1.2.0</h2>
            <ul>
              <li>Fixed exception occurring in Python kopenogram generation</li>
              <li>Improved editing stability, fixed arrow rendering, adjusted color changes to match display</li>
              <li>Added line style toggle: straight or orthogonal lines</li>
              <li>Initial support for orthogonal lines – functional but still fragile</li>
              <li>Orthogonal lines now react to class movement, though display might still be incorrect</li>
              <li>Dependency line rendering redesigned to support different styles (straight vs. right-angled)</li>
              <li>Improved selection and behavior of custom dependencies, including support for PyCharm</li>
              <li>Bend points on lines are now movable and selectable</li>
              <li>Fixed self-dependency rendering and added automatic recalculation when custom dependencies are modified</li>
              <li>Users can now add, edit, and remove custom dependencies in diagrams</li>
              <li>Diagrams automatically update when source code changes, no need to manually refresh</li>
              <li>Major improvements to dependency logic and rendering – association, aggregation, and composition types are now visualized</li>
              <li>Updated plugin compatibility for PyCharm version 2024.3</li>
            </ul>
        <h2>Release Notes – Version 1.1.0</h2>
            <ul>
              <li>Interface now correctly shows inheritance of other intefraces</li>            
              <li>Added custom color settings for every package</li>
              <li>Text display limit increased to show full content (was 35 characters).</li>
              <li>Added zoom functionality for diagrams.</li>
              <li>Fixed <code>continue</code> statement visualization (now correctly appears as a red rectangle with four white triangles).</li>
              <li>Corrected <code>return</code> statement (now displays all three black triangles).</li>
              <li>Fixed <code>break</code> statement (now correctly displays white triangles instead of black).</li>
              <li>Recursive calls now appear in yellow instead of red.</li>
              <li>Improved <code>if – elseif – else</code> rendering (no longer nests <code>elseif</code> branches incorrectly).</li>
              <li><code>break</code> statements no longer appear inside <code>switch</code> case branches.</li>
              <li>Exception blocks now have the correct colors:</li>
              <ul>
                 <li>Expected exception block: purple with a white header.</li>
                 <li>Thrown exception text: yellow.</li>
                 <li>Caught exception block: white header with an orange body.</li>
              </ul>
              <li><code>return</code>, <code>break</code>, and <code>continue</code> statements now extend to the end of their respective blocks.</li>
              <li>Kopenogram generation now works correctly for Python methods inside classes.</li>
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

plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.lwjgl:lwjgl:3.3.6")
    implementation("org.lwjgl:lwjgl-glfw:3.3.6")
    implementation("org.lwjgl:lwjgl-opengl:3.3.6")
    implementation(files("libs/TheHonoredMathLibrary-1.0.0.jar"))

    runtimeOnly("org.lwjgl:lwjgl:3.3.6:natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-glfw:3.3.6:natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-opengl:3.3.6:natives-windows")
}

tasks.test {
    useJUnitPlatform()
}
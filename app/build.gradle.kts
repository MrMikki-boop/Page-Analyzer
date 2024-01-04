plugins {
    id("java")

    id("application")

    id("checkstyle")
    id("jacoco")

    id("com.github.johnrengelman.shadow") version "8.1.1"

    id("se.patrikerdes.use-latest-versions") version "0.2.18"
    id("com.github.ben-manes.versions") version "0.50.0"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("hexlet.code.App")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("info.picocli:picocli:4.7.5")
    implementation("io.javalin:javalin:6.0.0-beta.4")
    implementation("org.slf4j:slf4j-simple:2.1.0-alpha1")
    implementation("io.javalin:javalin-rendering:5.6.3")
    implementation("gg.jte:jte:3.1.6")
}

tasks.withType<JavaExec> {
    environment("PORT", "7000") // Установите порт по умолчанию
}

jacoco {
    toolVersion = "0.8.9"
    reportsDirectory.set(layout.buildDirectory.dir("customJacocoReportDir"))
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}

checkstyle {
    toolVersion = "10.2"
}
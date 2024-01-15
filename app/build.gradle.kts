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

    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.h2database:h2:2.2.224")

    implementation("info.picocli:picocli:4.7.5")
    implementation("io.javalin:javalin:6.0.0-beta.4")
    implementation("org.slf4j:slf4j-simple:2.1.0-alpha1")
    implementation("io.javalin:javalin-rendering:5.6.3")
    implementation("gg.jte:jte:3.1.6")
    implementation("io.javalin:javalin-jte:6.0.0-beta.4")
    implementation("io.javalin:javalin-jte:1.3.7")
}

tasks.withType<JavaExec> {
    environment("PORT", "7070") // Установите порт по умолчанию
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.withType<Test> {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
    }
}

checkstyle {
    toolVersion = "10.2"
}
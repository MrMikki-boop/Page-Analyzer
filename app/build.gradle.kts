plugins {
    application
    id("java")
    id("checkstyle")
    id("jacoco")

    id("com.adarshr.test-logger") version "4.0.0"
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
    // Тестирование
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.25.1")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
    testImplementation("com.squareup.okhttp3:mockwebserver:5.0.0-alpha.12")

    // Логика приложения
    implementation("info.picocli:picocli:4.7.5")
    implementation("org.slf4j:slf4j-simple:2.1.0-alpha1")
    implementation("io.javalin:javalin:5.6.3")
    implementation("io.javalin:javalin-bundle:5.6.3")
    implementation("io.javalin:javalin-rendering:5.6.3")
    implementation("gg.jte:jte:3.1.6")
    implementation("com.h2database:h2:2.2.224")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("org.postgresql:postgresql:42.7.1")
    implementation("com.konghq:unirest-java:4.0.0-RC2")
    implementation("org.jsoup:jsoup:1.17.2")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
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
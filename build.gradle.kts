plugins {
    java
    war
}

group = "ru.angelich"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework:spring-webmvc:7.0.2")
    implementation("tools.jackson.core:jackson-databind:3.0.3")
    implementation("org.springframework.data:spring-data-jdbc:4.0.1")
    implementation("org.mapstruct:mapstruct:1.6.3")
    implementation("jakarta.servlet:jakarta.servlet-api:6.1.0")
    implementation("com.jayway.jsonpath:json-path:2.10.0")

    // Обязательно для совместной работы mapstruct и lombok:
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    runtimeOnly("com.h2database:h2:2.4.240")

    compileOnly("org.projectlombok:lombok:1.18.42")

    testImplementation("org.junit.jupiter:junit-jupiter:6.0.2")
    testImplementation("org.springframework:spring-test:7.0.2")
    testImplementation("org.mockito:mockito-core:5.21.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.21.0")
    testImplementation("org.hamcrest:hamcrest:3.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.war {
    archiveFileName = "ROOT.war"
}
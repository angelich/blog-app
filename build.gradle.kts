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
    implementation(libs.spring.webmvc)
    implementation(libs.jackson.databind)
    implementation(libs.spring.data.jdbc)
//    implementation(libs.lombok)
//    implementation(libs.mapstruct)
    implementation("org.mapstruct:mapstruct:1.6.3")
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    // Обязательно для их совместной работы:
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    runtimeOnly(libs.h2)

    compileOnly(libs.jakarta.servlet.api)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly(libs.junit.jupiter.launcher)
}

tasks.test {
    useJUnitPlatform()
}

tasks.war {
    archiveFileName = "ROOT.war"
}

//dependencies {
//    providedCompile("javax.servlet:servlet-api:2.5")
//}
//
//tasks.war {
//    webAppDirectory = file("src/main/webapp")
//    from("src/rootContent") // adds a file-set to the root of the archive
//    webInf { from("src/additionalWebInf") } // adds a file-set to the WEB-INF dir.
//    webXml = file("src/someWeb.xml") // copies a file to WEB-INF/web.xml
//}
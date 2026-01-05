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
    implementation(libs.h2)
    implementation(libs.spring.data.jdbc)

    compileOnly(libs.jakarta.servlet.api)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly(libs.junit.jupiter.launcher)
}

tasks.test {
    useJUnitPlatform()
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
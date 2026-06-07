plugins {
    alias(libs.plugins.java)
    alias(libs.plugins.application)
    alias(libs.plugins.springBoot)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.lombok)
}

val rulerVersion = providers.gradleProperty("ruler.version")
val rulerGroup = providers.gradleProperty("ruler.group")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            version = rulerVersion.get()
            groupId = rulerGroup.getOrNull()
            artifactId = "tbot-ruler-service"
        }
    }
}

tasks.withType(Test::class) {
    useJUnitPlatform()

    maxParallelForks = 1

    testLogging {
        events("failed")
        showStandardStreams = false
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

project.dependencies {
    implementation(libs.jwavezCore)
    implementation(libs.jwavezSerial)

    implementation(libs.springBootStarter)
    implementation(libs.springBootStarterTomcat)
    implementation(libs.springBootStarterSecurity)
    implementation(libs.springBootStarterMail)
    implementation(libs.springBootStarterDataJdbc)
    implementation(libs.springBootStarterLiquibase)
    implementation(libs.springBootStarterThymeleaf)
    implementation(libs.springBootRestClient)
    implementation(libs.springWebMvc)

    implementation(libs.fasterXmlCore)
    implementation(libs.fasterXmlAnnotations)
    implementation(libs.fasterXmlDatabind)
    implementation(libs.fasterXmlDatatypeJdk8)
    implementation(libs.fasterXmlModuleParameterNames)

    implementation(libs.retrofit)
    implementation(libs.retrofitConverterGson)
    implementation(libs.retrofitConverterJackson)
    implementation(libs.reflections)
    implementation(libs.sunriseSunsetCalculator)

    implementation(libs.dbH2)
    implementation(libs.dbLiquibase)

//
//    implementation "org.liquibase:liquibase-core"
//    implementation "org.mariadb.jdbc:mariadb-java-client:${dbMariaDbVersion}"
//
//    implementation "jakarta.validation:jakarta.validation-api:${javaxValidationVersion}"
//
//    implementation "org.apache.httpcomponents:httpclient:4.5.5"
//
    testImplementation(libs.springBootStarterTest)
    testImplementation(libs.springBootMvcTest)
//    testImplementation(libs.springBootTestContainers)
    testImplementation(libs.springTest)
    testImplementation(libs.junitJupiter)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.mockitoJupiter)
    testImplementation(libs.assertJ)
}

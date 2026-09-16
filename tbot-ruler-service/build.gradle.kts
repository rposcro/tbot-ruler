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

val modelJar by tasks.registering(Jar::class) {
    archiveClassifier.set("model")
    dependsOn(tasks.compileJava)
    from(sourceSets.main.get().output) {
        include("com/tbot/ruler/controller/admin/payload/**")
        include("com/tbot/ruler/controller/advisor/payload/**")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.named("bootJar"))
            artifact(modelJar)
            version = rulerVersion.get()
            groupId = rulerGroup.getOrNull()
            artifactId = "tbot-ruler-service"
        }
    }
}

val bootJarProvider = tasks.named("bootJar")
val jarName = "${project.name}.jar"

tasks.register<Copy>("prepareDockerBuild") {
    group = "Docker"
    description = "Prepares the Docker build context"
    dependsOn(bootJarProvider)

    from(layout.projectDirectory.file("Dockerfile"))
    from(tasks.named("bootJar").flatMap { (it as AbstractArchiveTask).archiveFile }) {
        rename { jarName }
    }
    from(layout.projectDirectory.dir("src/main/sh").file("run-ruler-service.sh"))

    into(layout.buildDirectory.dir("docker"))
}

tasks.register<Exec>("dockerBuild") {
    group = "Docker"
    description = "Builds the local Docker image tagged as :latest"
    dependsOn(tasks.named("prepareDockerBuild"))
    workingDir = layout.buildDirectory.dir("docker").get().asFile
    commandLine("docker", "build", "-t", "${project.name}:latest", ".")
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

    implementation(libs.dbMaria)
    implementation(libs.dbH2)
    implementation(libs.dbLiquibase)

//    implementation "jakarta.validation:jakarta.validation-api:${javaxValidationVersion}"
//    implementation "org.apache.httpcomponents:httpclient:4.5.5"

    testImplementation(libs.springBootStarterTest)
    testImplementation(libs.springBootMvcTest)
//    testImplementation(libs.springBootTestContainers)
    testImplementation(libs.springTest)
    testImplementation(libs.junitJupiter)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.mockitoJupiter)
    testImplementation(libs.assertJ)
}

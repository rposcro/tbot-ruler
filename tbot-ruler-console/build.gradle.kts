plugins {
    alias(libs.plugins.java)
    alias(libs.plugins.application)
    alias(libs.plugins.springBoot)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.lombok)
    alias(libs.plugins.vaadin)
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
            artifact(tasks.named("bootJar"))
            version = rulerVersion.get()
            groupId = rulerGroup.getOrNull()
            artifactId = "tbot-ruler-console"
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

    into(layout.buildDirectory.dir("docker"))
}

tasks.register<Exec>("dockerBuild") {
    group = "Docker"
    description = "Builds the local Docker image tagged as :latest"
    dependsOn(tasks.named("prepareDockerBuild"))
    workingDir = layout.buildDirectory.dir("docker").get().asFile
    commandLine("docker", "build", "-t", "${project.name}:latest", ".")
}

project.dependencies {
    implementation(project(":tbot-ruler-service"))

    implementation(libs.vaadinSpringBootStarter)

    implementation(libs.springBootStarter)
    implementation(libs.springBootStarterTomcat)
    implementation(libs.springBootStarterSecurity)

    implementation(libs.fasterXmlCore)
    implementation(libs.fasterXmlAnnotations)
    implementation(libs.fasterXmlDatabind)
    implementation(libs.fasterXmlDatatypeJdk8)
    implementation(libs.fasterXmlModuleParameterNames)

    implementation(libs.retrofit)
//    implementation(libs.retrofitConverterGson)
    implementation(libs.retrofitConverterJackson)
//    implementation(libs.reflections)

//    implementation "jakarta.validation:jakarta.validation-api:${javaxValidationVersion}"
//    implementation "org.apache.httpcomponents:httpclient:4.5.5"

//    testImplementation(libs.springBootStarterTest)
//    testImplementation(libs.springBootMvcTest)
//    testImplementation(libs.springBootTestContainers)
//    testImplementation(libs.springTest)
//    testImplementation(libs.junitJupiter)
//    testImplementation(libs.mockitoCore)
//    testImplementation(libs.mockitoJupiter)
//    testImplementation(libs.assertJ)
}

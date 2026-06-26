tasks.register<Copy>("prepareDockerBuild") {
    group = "Docker"
    description = "Prepares the Docker build context"

    from(layout.projectDirectory.file("Dockerfile"))
    from(layout.projectDirectory.dir("sql-files")) {
        into("sql-files")
    }

    into(layout.buildDirectory.dir("docker"))
}

tasks.register<Exec>("dockerBuild") {
    group = "Docker"
    description = "Builds the local Docker image tagged as :latest"
    dependsOn(tasks.named("prepareDockerBuild"))
    workingDir = layout.buildDirectory.dir("docker").get().asFile
    commandLine("/usr/local/bin/docker", "build", "-t", "${project.name}:latest", ".")
}

tasks.register<Exec>("dockerStop") {
    group = "Docker"
    description = "Stops and removes the running container"
    commandLine("/usr/local/bin/docker", "rm", "-f", project.name)
    isIgnoreExitValue = true
}

tasks.register<Exec>("dockerRun") {
    group = "Docker"
    description = "Runs the Docker container locally on port 3306"
    dependsOn(tasks.named("dockerBuild"))
    commandLine(
        "/usr/local/bin/docker", "run", "-d",
        "--name", project.name,
        "-p", "3306:3306",
        "-e", "MYSQL_ROOT_PASSWORD=root",
        "${project.name}:latest"
    )
}

tasks.named("dockerRun") {
    mustRunAfter(tasks.named("dockerStop"))
}

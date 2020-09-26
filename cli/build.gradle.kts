val cliktVersion: String by project

plugins {
    // Apply core plugins.
    application
}

application {
    applicationName = "stan"
    mainClassName = "dev.schuberth.stan.cli.MainKt"
}

dependencies {
    implementation(project(":lib"))

    implementation("com.github.ajalt.clikt:clikt:$cliktVersion")
}

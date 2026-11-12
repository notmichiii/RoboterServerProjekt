plugins {
    id("java")
}

group = "com.server"
version = "1.0"

val mainClass: String = "${group}.Main"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}

sourceSets {
    main {
        java.srcDir("build/generated-src/jooq")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(libs.jetbrains.annotations)
    annotationProcessor(libs.jetbrains.annotations)

    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    testCompileOnly(libs.jetbrains.annotations)
    testAnnotationProcessor(libs.jetbrains.annotations)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    dependsOn("fatJar")
    manifest {
        attributes["Main-Class"] = mainClass;
    }
}

tasks.register<Jar>("fatJar") {
    description = "Creates a so called 'fatJar' including all of the applications dependencies."
    group = JavaBasePlugin.BUILD_TASK_NAME
    manifest {
        attributes["Main-Class"] = mainClass;
    }

    archiveBaseName = "${rootProject.name}-fat"
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    from(configurations.compileClasspath.get().filter { it.isDirectory || it.isFile }.map {
        if (it.isDirectory) it else zipTree(it)
    })

    with(tasks.jar.get())
}

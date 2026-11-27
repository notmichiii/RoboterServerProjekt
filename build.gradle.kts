import org.jooq.meta.jaxb.MatcherRule
import org.jooq.meta.jaxb.MatchersForeignKeyType

plugins {
    id("java")
    alias(libs.plugins.jooq)
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

    implementation(libs.jackson)
    compileOnly(libs.jakarta.websocket)
    implementation(libs.tyrus.server)

    jooqCodegen(libs.mariadb)
    implementation(libs.jooq)
    runtimeOnly(libs.mariadb)
    implementation(libs.hikari)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jooq {
    configuration {
        jdbc {
            driver = "org.mariadb.jdbc.Driver"
            url = providers.gradleProperty("jooqDbURL")
                .orElse(providers.environmentVariable("JOOQ_DB_URL"))
                .getOrElse("")
            user = providers.gradleProperty("jooqDbUser")
                .orElse(providers.environmentVariable("JOOQ_DB_USER"))
                .getOrElse("")
            password = providers.gradleProperty("jooqDbPassword")
                .orElse(providers.environmentVariable("JOOQ_DB_PASSWORD"))
                .getOrElse("")
        }

        generator {
            database {
                name = "org.jooq.meta.mariadb.MariaDBDatabase"
                inputSchema = "robot"
            }

            target {
                packageName = "${group}.jooq"
                directory = "build/generated-src/jooq"
            }

            strategy {
                matchers {
                    foreignKeys.add(
                        MatchersForeignKeyType().apply {
                            expression = "USERS_USERS_FK"

                            pathMethodName = MatcherRule().apply {
                                expression = "PARENT"
                            }

                            pathMethodNameInverse = MatcherRule().apply {
                                expression = "CHILDREN"
                            }
                        }
                    )
                }
            }
        }
    }
}

tasks.jar {
    dependsOn("fatJar")
    manifest {
        attributes["Main-Class"] = mainClass
    }
}

tasks.register<Jar>("fatJar") {
    description = "Creates a so called 'fatJar' including all of the applications dependencies."
    group = JavaBasePlugin.BUILD_TASK_NAME
    manifest {
        attributes["Main-Class"] = mainClass
    }

    archiveBaseName = "${rootProject.name}-fat"
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    from(configurations.compileClasspath.get().filter { it.isDirectory || it.isFile }.map {
        if (it.isDirectory) it else zipTree(it)
    })

    with(tasks.jar.get())
}

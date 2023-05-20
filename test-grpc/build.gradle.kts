import com.google.protobuf.gradle.*

val applicationMode: String by project

plugins {
    idea
    java
    application
    id("com.google.protobuf") version "0.9.3"
}

group = "org.demo"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

application {
    if (applicationMode == "server") {
        logger.info("Start server")
        mainClass.set("org.demo.GrpcServer")
    } else {
        logger.info("Start client")
        mainClass.set("org.demo.GrpcClient")
    }
}

sourceSets {
    main {
        proto {
            srcDirs("src/main/grpc")
        }
        java {
            srcDirs("src/main/java")
            srcDirs("build/generated/source/proto/main/java")
        }
    }
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.26")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("io.grpc:grpc-netty-shaded:1.55.1")
    implementation("io.grpc:grpc-protobuf:1.55.1")
    implementation("io.grpc:grpc-stub:1.55.1")
    implementation("ch.qos.logback:logback-classic:1.4.7")

    annotationProcessor("org.projectlombok:lombok:1.18.26")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.23.1"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.55.1"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.create<JavaExec>("runServer") {
    dependsOn("jar")
    mainClass.set("org.demo.GrpcServer")
}

tasks.create<JavaExec>("runClient") {
    dependsOn("jar")
    mainClass.set("org.demo.GrpcClient")
}
plugins {
	java
	id("org.springframework.boot") version "3.1.5"
	id("io.spring.dependency-management") version "1.1.3"
}

group = "bob.geunrobeol"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework:spring-websocket")
	implementation("org.springframework:spring-messaging")
	implementation("software.amazon.kinesis:amazon-kinesis-client:2.5.1")

	testImplementation("org.springframework.boot:spring-boot-starter-test")

	//추가
	implementation(files("/home/ysh/platform-tech/libs/libjgroupsig-1.1.0.jar"))

}

tasks.withType<Test> {
    classpath = files(classpath, "libs/libjgroupsig-0.1.jar")

    jvmArgs("-Djava.library.path=./libs")
	useJUnitPlatform()
}

tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> {
    classpath = files(classpath, "libs/libjgroupsig-0.1.jar")
    jvmArgs("-Djava.library.path=./libs")
}
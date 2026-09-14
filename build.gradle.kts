import java.io.FileFilter
import java.util.Collections
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

plugins {
	alias(libs.plugins.loom)
    java
}

val lwjglNatives = resolveLwjglNatives()

val modVersion = "${providers.gradleProperty("mod_version").get()}+${libs.versions.bta.get()}"
val modGroup: Provider<String> = providers.gradleProperty("mod_group")
val modName: Provider<String> = providers.gradleProperty("mod_name")

val javaVersion: Provider<Int> = libs.versions.java.map { it.toInt() }

val obfuscateJarEnabled = providers.gradleProperty("obfuscate").map(String::toBoolean).getOrElse(true)
val buildSourcesJar = providers.gradleProperty("sourcesJar").map(String::toBoolean).getOrElse(false)

base.archivesName = modName
group = modGroup.get()
version = modVersion
loom {
	val btaChannel = libs.versions.btaChannel.get()
	val btaVersion = (if (btaChannel == "nightly") "" else "v") + libs.versions.bta.get()
    customMinecraftMetadata.set("https://downloads.betterthanadventure.net/bta-client/${btaChannel}/$btaVersion/manifest.json")
}
repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/") { name = "Fabric" }
    maven("https://maven.thesignalumproject.net/infrastructure") { name = "SignalumMavenInfrastructure" }
    maven("https://maven.thesignalumproject.net/releases") { name = "SignalumMavenReleases" }
	maven("https://maven.thesignalumproject.net/nightly") { name = "SignalumMavenNightly" }
    ivy("https://piston-data.mojang.com") {
        patternLayout { artifact("v1/[organisation]/[revision]/[module].jar") }
        metadataSources { artifact() }
    }
}
dependencies {
    minecraft("::${libs.versions.bta.get()}")

	implementation(libs.loader)
	implementation(libs.halplibe)

	include(libs.halplibe) { isTransitive = false }

	compileOnly(libs.bundles.btaLwjgl)
	compileOnly(libs.joml)
	compileOnly(libs.joml.primitives)
	compileOnly(libs.slf4jApi)

	localRuntime(libs.modMenu)
	runtimeClasspath(libs.clientJar)
	val lwjglVer = libs.versions.lwjgl.get()
	localRuntime(platform("org.lwjgl:lwjgl-bom:${lwjglVer}"))
	localRuntime("org.lwjgl:lwjgl::$lwjglNatives")
	localRuntime("org.lwjgl:lwjgl-glfw::$lwjglNatives")
	localRuntime("org.lwjgl:lwjgl-openal::$lwjglNatives")
	localRuntime("org.lwjgl:lwjgl-opengl::$lwjglNatives")
	localRuntime("org.lwjgl:lwjgl-stb::$lwjglNatives")
}
java {
	toolchain {
		languageVersion = javaVersion.map { JavaLanguageVersion.of(it) }
		vendor = JvmVendorSpec.ADOPTIUM
	}
	sourceCompatibility = JavaVersion.toVersion(javaVersion.get())
	targetCompatibility = JavaVersion.toVersion(javaVersion.get())

	if (buildSourcesJar) withSourcesJar()
}
val licenseFile = run {
	val rootLicense = layout.projectDirectory.file("LICENSE")
	val parentLicense = layout.projectDirectory.file("../LICENSE")
	when {
		rootLicense.asFile.exists() -> {
			logger.lifecycle("Using LICENSE from project root: {}", rootLicense.asFile)
			rootLicense
		}
		parentLicense.asFile.exists() -> {
			logger.lifecycle("Using LICENSE from parent directory: {}", parentLicense.asFile)
			parentLicense
		}
		else -> {
			logger.warn("No LICENSE file found in project or parent directory.")
			null
		}
	}
}
tasks {
	withType<JavaCompile>().configureEach {
		options.encoding = "UTF-8"
		sourceCompatibility = javaVersion.get().toString()
		targetCompatibility = javaVersion.get().toString()
		if (javaVersion.get() > 8) options.release = javaVersion
	}
	named<UpdateDaemonJvm>("updateDaemonJvm") {
		languageVersion = libs.versions.gradleJava.map { JavaLanguageVersion.of(it.toInt()) }
		vendor = JvmVendorSpec.ADOPTIUM
	}
	withType<JavaExec>().configureEach { defaultCharacterEncoding = "UTF-8" }
	withType<Javadoc>().configureEach { options.encoding = "UTF-8" }
	withType<Jar>().configureEach {
		licenseFile?.let {
			from(it) {
				rename { original -> "${original}_${archiveBaseName.get()}" }
			}
		}
	}
	processResources {
		val resourceMap = mapOf(
			"version" to modVersion,
			"fabricloader" to libs.versions.loader.get(),
			"halplibe" to libs.versions.halplibe.get(),
			"java" to libs.versions.java.get(),
			"modmenu" to libs.versions.modMenu.get()
		)

		inputs.properties(resourceMap)

		duplicatesStrategy = DuplicatesStrategy.INCLUDE
		with(copySpec {
			from("src/main/resources/") {
				include("fabric.mod.json")
				include("*.mixins.json")
				expand(resourceMap)
			}
		})
	}
}

val verifyNoBridgedAssets by tasks.registering(Exec::class) {
	group = "verification"
	description = "Fails if art the asset sidecar should supply is present in the tree."
	isIgnoreExitValue = false
	commandLine("python", "tools/strip_bridged_assets.py", "--check")

	onlyIf {
		try {
			ProcessBuilder("python", "--version").start().waitFor() == 0
		} catch (e: Exception) {
			logger.warn("Python not found; skipping the bridged-asset check.")
			false
		}
	}
}

tasks.named("check") { dependsOn(verifyNoBridgedAssets) }

configurations.configureEach {
	exclude(group = "org.lwjgl.lwjgl")
	exclude(group = "net.java.jutils")
	exclude(group = "net.java.jinput")
	exclude(group = "net.sf.jopt-simple")
	exclude(group = "net.minecraft", module = "launchwrapper")
}

fun resolveLwjglNatives(): String {
	return Pair(
		System.getProperty("os.name")!!,
		System.getProperty("os.arch")!!
	).let { (name, arch) ->
		when {
			arrayOf("Linux", "SunOS", "Unit").any { name.startsWith(it) } ->
				if (arrayOf("arm", "aarch64").any { arch.startsWith(it) })
					"natives-linux${if (arch.contains("64") || arch.startsWith("armv8")) "-arm64" else "-arm32"}"
				else
					"natives-linux"
			arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) } ->
				"natives-macos${if (arch.startsWith("aarch64")) "-arm64" else ""}"
			arrayOf("Windows").any { name.startsWith(it) } ->
				if (arch.contains("64"))
					"natives-windows${if (arch.startsWith("aarch64")) "-arm64" else ""}"
				else
					"natives-windows-x86"
			else ->
				throw Error("Unrecognized or unsupported platform. Please set \"lwjglNatives\" manually")
		}
	}
}

val proguardTool: Configuration by configurations.creating {
	isCanBeConsumed = false
	isCanBeResolved = true
}

dependencies {
	add(proguardTool.name, "com.guardsquare:proguard-base:7.7.0")
}

fun copyZipEntries(
	from: ZipFile,
	into: ZipOutputStream,
	seen: MutableSet<String>,
	accept: (String) -> Boolean
) {
	for (entry in Collections.list(from.entries())) {
		if (!accept(entry.name) || !seen.add(entry.name)) continue
		val copy = ZipEntry(entry.name)
		if (entry.time != -1L) copy.time = entry.time
		into.putNextEntry(copy)
		if (!entry.isDirectory) from.getInputStream(entry).use { it.copyTo(into) }
		into.closeEntry()
	}
}

tasks.named<Jar>("jar") {
	archiveClassifier = "dev"
}

val proguardMapping = layout.buildDirectory.file("proguard/mapping.txt")
val obfuscationLauncher = javaToolchains.launcherFor {
	languageVersion = javaVersion.map { JavaLanguageVersion.of(it) }
	vendor = JvmVendorSpec.ADOPTIUM
}

if (obfuscateJarEnabled) {
	val proguardRules = layout.projectDirectory.file("gradle/proguard.pro")
	val proguardPaths = layout.buildDirectory.file("tmp/proguard/paths.pro")
	val proguardSeeds = layout.buildDirectory.file("proguard/seeds.txt")
	val proguardOutput = layout.buildDirectory.file("proguard/obfuscated-no-nested.jar")
	val releaseJar = layout.buildDirectory.file("libs/${modName.get()}-$modVersion.jar")

	val libraryPath = files(sourceSets["main"].compileClasspath, configurations["runtimeClasspath"])

	val obfuscateJar by tasks.registering(JavaExec::class) {
		group = "build"
		description = "Renames and repackages the mod's classes into the released jar."

		val devJar = tasks.named<Jar>("jar")
		dependsOn(devJar)

		classpath = proguardTool
		mainClass = "proguard.ProGuard"
		javaLauncher = obfuscationLauncher

		maxHeapSize = "2g"

		inputs.file(devJar.flatMap { it.archiveFile })
		inputs.file(proguardRules)
		inputs.files(libraryPath)
		outputs.file(releaseJar)
		outputs.file(proguardMapping)

		args(
			"@${proguardPaths.get().asFile.absolutePath}",
			"@${proguardRules.asFile.absolutePath}"
		)

		doFirst {
			val pathsFile = proguardPaths.get().asFile
			pathsFile.parentFile.mkdirs()
			proguardMapping.get().asFile.parentFile.mkdirs()
			releaseJar.get().asFile.parentFile.mkdirs()

			fun p(file: File) = file.absolutePath.replace('\\', '/')

			val jdkHome = obfuscationLauncher.get().metadata.installationPath.asFile
			val jmods = File(jdkHome, "jmods")
				.listFiles(FileFilter { it.name.endsWith(".jmod") })
				.orEmpty()
				.sortedBy { it.name }
			if (jmods.isEmpty()) {

				logger.warn("No jmods under {} -- ProGuard has no platform classes to work from.", jdkHome)
			}

			pathsFile.writeText(buildString {
				appendLine("# Generated by build.gradle.kts. Edits here are overwritten every build;")
				appendLine("# the rules live in gradle/proguard.pro.")

				appendLine("-injars  \"${p(devJar.get().archiveFile.get().asFile)}\"(!META-INF/jars/**)")
				appendLine("-outjars \"${p(proguardOutput.get().asFile)}\"")
				appendLine()
				for (file in libraryPath.files.distinct().filter { it.exists() }) {
					appendLine("-libraryjars \"${p(file)}\"")
				}
				for (jmod in jmods) {
					appendLine("-libraryjars \"${p(jmod)}\"(!**.jar;!module-info.class)")
				}
				appendLine()

				appendLine("-printmapping \"${p(proguardMapping.get().asFile)}\"")
				appendLine("-printseeds   \"${p(proguardSeeds.get().asFile)}\"")
			})

			if (!buildSourcesJar) {
				layout.buildDirectory.dir("libs").get().asFile
					.listFiles(FileFilter { it.name.endsWith("-sources.jar") })
					.orEmpty()
					.forEach { it.delete() }
			}
		}

		doLast {

			val obfuscated = proguardOutput.get().asFile
			val nestedSource = devJar.get().archiveFile.get().asFile
			val target = releaseJar.get().asFile
			val seen = mutableSetOf<String>()
			ZipOutputStream(target.outputStream().buffered()).use { out ->
				ZipFile(obfuscated).use { copyZipEntries(it, out, seen) { true } }
				ZipFile(nestedSource).use { source ->
					copyZipEntries(source, out, seen) { it.startsWith("META-INF/jars/") }
				}
			}
			logger.lifecycle("Obfuscated jar: {}", target)
		}
	}

	tasks.named("assemble") { dependsOn(obfuscateJar) }
}

val retraceTool: Configuration by configurations.creating {
	isCanBeConsumed = false
	isCanBeResolved = true
}

dependencies {

	add(retraceTool.name, "com.guardsquare:proguard-retrace:7.7.0")
}

tasks.register<JavaExec>("retrace") {
	group = "help"
	description = "Decodes an obfuscated stack trace: gradlew retrace -Ptrace=crash.txt"

	classpath = retraceTool
	mainClass = "proguard.retrace.ReTrace"
	javaLauncher = obfuscationLauncher

	val trace = providers.gradleProperty("trace")
	val mapping = providers.gradleProperty("mapping")
		.orElse(proguardMapping.map { it.asFile.absolutePath })

	doFirst {
		if (!trace.isPresent) {
			throw GradleException("No trace given. Usage: gradlew retrace -Ptrace=<file with the stack trace>")
		}
		if (!File(mapping.get()).isFile) {
			throw GradleException("No mapping at ${mapping.get()} -- run an obfuscated build first, or pass -Pmapping=<file>.")
		}
	}

	argumentProviders.add(CommandLineArgumentProvider {
		if (trace.isPresent) listOf(mapping.get(), trace.get()) else emptyList()
	})
}

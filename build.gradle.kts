plugins {
    id("dev.isxander.modstitch.base") version "0.7.1-unstable"
}

val loader: String = stonecutter.current.project.substringAfter("-", "vanilla")
fun getProperty(propertyName: String) = findProperty(propertyName) as? String

modstitch {
    minecraftVersion = stonecutter.current.version
    javaVersion = 21

    metadata {
        modId = getProperty("mod.id")
        modVersion = getProperty("mod.version")
        modGroup = getProperty("mod.group")
        modName = getProperty("mod.name")
        modDescription = getProperty("mod.description")
        modAuthor = getProperty("mod.author")
        modLicense = getProperty("mod.license")

        fun MapProperty<String, String>.populate(block: MapProperty<String, String>.() -> Unit) = block()
        replacementProperties.populate {
            put("minecraft", getProperty("meta.minecraft").orEmpty())
            put("loader", getProperty("meta.loader").orEmpty())
        }
    }

    parchment {
        mappingsVersion = getProperty("deps.parchment")
    }

    loom {
        fabricLoaderVersion = getProperty("deps.fabric-loader")
    }

    moddevgradle {
        neoForgeVersion = getProperty("deps.neoforge")
        defaultRuns()
    }

    mixin {
        addMixinsToModManifest = true
        configs.register(getProperty("mod.id").orEmpty())
    }

    if (loader == "vanilla") modLoaderManifest = "fabric.vanilla.json"
}

stonecutter {
    constants.match(
        loader,
        "fabric",
        "neoforge"
    )
}

dependencies {
    if (loader == "fabric") {
        modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:${getProperty("deps.fabric-api").orEmpty()}")
        getProperty("deps.modmenu")?.takeIf { it.isNotBlank() }?.let {
            modstitchModImplementation("com.terraformersmc:modmenu:$it")
        }
    }

    val devAuthLoader = loader.takeIf { it != "vanilla" } ?: "fabric"
    modstitchRuntimeOnly("me.djtheredstoner:DevAuth-$devAuthLoader:${getProperty("deps.devauth").orEmpty()}")
}
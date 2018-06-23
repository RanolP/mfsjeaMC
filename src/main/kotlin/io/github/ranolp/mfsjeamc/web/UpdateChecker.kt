package io.github.ranolp.mfsjeamc.web

import com.github.zafarkhaja.semver.Version
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.plugin.PluginDescriptionFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


object UpdateChecker {
    val jsonParser = JsonParser()

    data class ReleaseInfo(
        val version: Version,
        val url: String,
        val updateLog: String,
        val downloadUrl: String
    )

    fun check(
        pluginDescriptionFile: PluginDescriptionFile,
        owner: String,
        repo: String = pluginDescriptionFile.name,
        fileName: String = "${pluginDescriptionFile.name}.jar",
        requireUpdateLog: Boolean = true,
        failure: () -> Unit = {},
        success: ReleaseInfo.() -> Unit
    ) {
        getReleaseInfo(owner, repo, fileName, requireUpdateLog) {
            if (version > Version.valueOf(pluginDescriptionFile.version)) {
                success()
            }
        }
    }

    fun getReleaseInfo(
        owner: String,
        repo: String,
        fileName: String,
        requireUpdateLog: Boolean = true,
        failure: () -> Unit = {},
        callback: ReleaseInfo.() -> Unit
    ) {
        thread(name = "UpdateChecker", isDaemon = true, start = true) {
            val url = URL("https://api.github.com/repos/$owner/$repo/releases")
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.setRequestProperty("Content-Type", "application/json")
            httpURLConnection.connect()
            if (httpURLConnection.responseCode != 200) {
                failure()
                return@thread
            }
            BufferedReader(
                InputStreamReader(
                    httpURLConnection.inputStream,
                    "UTF8"
                )
            ).useLines {
                for (json in (jsonParser.parse(it.joinToString("")) as? JsonArray
                        ?: JsonArray()).mapNotNull { it as? JsonObject }) {
                    if (requireUpdateLog && json["body"].isJsonNull) {
                        continue
                    }
                    ReleaseInfo(
                        Version.valueOf(json["tag_name"].asString?.let { if (it[0] == 'v') it.substring(1) else it }),
                        json["html_url"].asString,
                        json["body"].let { if (it.isJsonNull) "" else it.asString },
                        json["assets"].asJsonArray.mapNotNull {
                            it as? JsonObject
                        }.first {
                            it["name"].asString == fileName
                        }["browser_download_url"].asString
                    ).callback()
                    return@thread
                }
                failure()
            }
        }
    }
}

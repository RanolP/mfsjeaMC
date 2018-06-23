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
        requireUpdateLog: Boolean = true
    ): ReleaseInfo? {
        val latest = getReleaseInfo(owner, repo, fileName, requireUpdateLog) ?: return null
        if (latest.version <= Version.valueOf(pluginDescriptionFile.version)) {
            return null
        }
        return latest
    }

    fun getReleaseInfo(owner: String, repo: String, fileName: String, requireUpdateLog: Boolean = true): ReleaseInfo? {
        val url = URL("https://api.github.com/repos/$owner/$repo/releases")
        val httpURLConnection = url.openConnection() as HttpURLConnection
        httpURLConnection.setRequestProperty("Content-Type", "application/json")
        httpURLConnection.connect()
        if (httpURLConnection.responseCode != 200) {
            return null
        }
        return BufferedReader(
            InputStreamReader(
                httpURLConnection.inputStream,
                "UTF8"
            )
        ).useLines {
            var result: ReleaseInfo? = null
            for (json in (jsonParser.parse(it.joinToString("")) as? JsonArray
                    ?: JsonArray()).mapNotNull { it as? JsonObject }) {
                if (requireUpdateLog && json["body"].isJsonNull) {
                    continue
                }
                result = ReleaseInfo(
                    Version.valueOf(json["tag_name"].asString?.let { if (it[0] == 'v') it.substring(1) else it }),
                    json["html_url"].asString,
                    json["body"].let { if (it.isJsonNull) "" else it.asString },
                    json["assets"].asJsonArray.mapNotNull {
                        it as? JsonObject
                    }.first {
                        it["name"].asString == fileName
                    }["browser_download_url"].asString
                )
                break
            }
            result
        }
    }
}

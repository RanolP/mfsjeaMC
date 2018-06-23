package io.github.ranolp.mfsjeamc.web

import com.github.zafarkhaja.semver.Version
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import org.bukkit.Bukkit
import org.bukkit.plugin.PluginDescriptionFile
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import kotlin.concurrent.thread


object UpdateChecker {
    enum class FailureReason(val message: String) {
        ALREADY_LATEST(""),
        INVALID_GITHUB_RESPONSE("Github API의 올바르지 않은 상태 반환"),
        TIMEOUT("시간 초과"),
        INVALID_JSON("Github API의 올바르지 않은 JSON 반환"),
        UNKNOWN("알 수 없음")
    }

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
        failure: (FailureReason) -> Unit = {},
        success: ReleaseInfo.() -> Unit
    ) {
        getReleaseInfo(owner, repo, fileName, requireUpdateLog, failure) {
            if (version > Version.valueOf(pluginDescriptionFile.version)) {
                success()
            } else {
                failure(FailureReason.ALREADY_LATEST)
            }
        }
    }

    fun getReleaseInfo(
        owner: String,
        repo: String,
        fileName: String,
        requireUpdateLog: Boolean = true,
        failure: (FailureReason) -> Unit = {},
        callback: ReleaseInfo.() -> Unit
    ) {
        thread(name = "UpdateChecker", isDaemon = true, start = true) {
            try {
                val url = URL("https://api.github.com/repos/$owner/$repo/releases")
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.setRequestProperty("Content-Type", "application/json")
                httpURLConnection.connectTimeout = 10000
                httpURLConnection.connect()
                if (httpURLConnection.responseCode != 200) {
                    failure(FailureReason.INVALID_GITHUB_RESPONSE)
                    return@thread
                }
                BufferedReader(
                    InputStreamReader(
                        httpURLConnection.inputStream,
                        "UTF8"
                    )
                ).useLines {
                    try {
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
                    } catch (exception: JsonParseException) {
                    }
                    failure(FailureReason.INVALID_JSON)
                }
            } catch (exception: SocketTimeoutException) {
                failure(FailureReason.TIMEOUT)
            } catch (exception: IOException) {
                failure(FailureReason.UNKNOWN)
                Bukkit.getLogger().throwing("UpdateChecker", "getReleaseInfo", exception)
            } catch (throwable: Throwable) {
                failure(FailureReason.UNKNOWN)
                Bukkit.getLogger().throwing("UpdateChecker", "getReleaseInfo", throwable)
            }
        }
    }
}

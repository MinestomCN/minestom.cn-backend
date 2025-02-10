package cn.cyanbukkit

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json // 导入 Json 对象
import java.net.URL
import java.net.HttpURLConnection

@Serializable
data class Library(
    val name: String,
    val owner: String,
    val description: String,
    val stars: Int,
    val url: String
)

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/api/libraries") {
            val libraries = getLibraries()
            call.respondText(Json.encodeToString(libraries), ContentType.Application.Json) // 修改为使用 Json 对象
        }
    }
}

private var json: Json = Json { ignoreUnknownKeys = true }

private fun getLibraries(): List<Library> {
    val url = URL("https://api.github.com/search/repositories?q=topic:minestom-library&sort=stars&order=desc")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.setRequestProperty("Accept", "application/vnd.github.v3+json")

    val responseCode = connection.responseCode
    if (responseCode == HttpURLConnection.HTTP_OK) {
        val response = connection.inputStream.bufferedReader().use { it.readText() }
        val responseData = json.decodeFromString<GithubResponse>(response)
        return responseData.items.map { repo ->
            Library(
                name = repo.name,
                owner = repo.owner.login,
                description = repo.description ?: "",
                stars = repo.stargazers_count,
                url = repo.html_url
            )
        }
    } else {
        throw Exception("Failed to fetch data from GitHub API")
    }
}

@kotlinx.serialization.Serializable
data class GithubResponse(
    val items: List<GithubRepo>
)

@kotlinx.serialization.Serializable
data class GithubRepo(
    val name: String,
    val owner: GithubOwner,
    val description: String?,
    val stargazers_count: Int,
    val html_url: String
)

@Serializable
data class GithubOwner(
    val login: String
)
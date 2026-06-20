package de.fh.erfurt.moviemate.movies.data.remote.client

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation


class MoviesHttpClientFactory(
    private val json: Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

) {
    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
            installContentNegotiation()
        }
    }

    fun create(engineFactory: HttpClientEngineFactory<*>): HttpClient {
        return HttpClient(engineFactory) {
            installContentNegotiation()
        }
    }

    private fun HttpClientConfig<*>.installContentNegotiation() {
        install(ContentNegotiation) {
            json(json)
        }
    }

}
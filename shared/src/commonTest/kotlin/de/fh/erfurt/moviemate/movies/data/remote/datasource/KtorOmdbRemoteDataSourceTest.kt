package de.fh.erfurt.moviemate.movies.data.remote.datasource

import de.fh.erfurt.moviemate.movies.data.remote.client.MoviesHttpClientFactory
import de.fh.erfurt.moviemate.movies.data.repository.remote.OmdbMovieMetadataRepository
import de.fh.erfurt.moviemate.movies.domain.model.movie.MediaType
import de.fh.erfurt.moviemate.movies.domain.usecase.movie.FetchMovieDataUseCase
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest

class KtorOmdbRemoteDataSourceTest {
    @Test
    fun searchFiltersByYearAndLoadsTheSelectedImdbId() = runTest {
        var requestCount = 0
        val engine = MockEngine { request ->
            requestCount++
            val query = request.url.parameters
            assertEquals("movie", query["type"])
            assertNull(query["t"])
            val body = if (requestCount == 1) {
                assertEquals("Dune", query["s"])
                assertEquals("2021", query["y"])
                """{"Search":[{"Title":"Dune","Year":"2021","imdbID":"tt1160419","Poster":"N/A"}],"totalResults":"2","Response":"True"}"""
            } else {
                assertEquals("tt1160419", query["i"])
                assertNull(query["y"])
                """{"Title":"Dune","Year":"2021","Plot":"A desert planet.","Runtime":"155 min","Response":"True"}"""
            }
            respond(body, headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()))
        }

        val client = MoviesHttpClientFactory().create(engine)
        try {
            val useCase = FetchMovieDataUseCase(OmdbMovieMetadataRepository(KtorOmdbRemoteDataSource(client, "test-key")))
            val results = useCase.search(" Dune ", "2021")
            assertEquals(1, requestCount)
            assertEquals(2, results.totalResults)
            assertNull(results.singleMatch)
            assertNull(results.matches.single().posterUrl)
            val details = useCase.byImdbId(results.matches.single().imdbId)
            assertEquals(2021, details?.releaseYear)
            assertEquals(155, details?.runtimeMinutes)
        } finally {
            client.close()
        }
    }

    @Test
    fun seriesSearchAndDetailsUseTheSeriesType() = runTest {
        val engine = MockEngine { request ->
            val query = request.url.parameters
            assertEquals("series", query["type"])
            val body = if (query["s"] == "Breaking Bad") {
                """{"Search":[{"Title":"Breaking Bad","Year":"2008–2013","imdbID":"tt0903747"}],"totalResults":"1","Response":"True"}"""
            } else {
                assertEquals("tt0903747", query["i"])
                """{"Title":"Breaking Bad","Year":"2008–2013","totalSeasons":"5","Response":"True"}"""
            }
            respond(body, headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()))
        }

        val client = MoviesHttpClientFactory().create(engine)
        try {
            val useCase = FetchMovieDataUseCase(OmdbMovieMetadataRepository(KtorOmdbRemoteDataSource(client, "test-key")))
            val match = useCase.search("Breaking Bad", "", MediaType.SERIES).matches.single()
            val details = useCase.byImdbId(match.imdbId, MediaType.SERIES)
            assertEquals(2008, details?.releaseYear)
            assertEquals(5, details?.totalSeasons)
        } finally {
            client.close()
        }
    }
}

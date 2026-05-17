package de.fh.erfurt.moviemate.movies.data.remote.datasource

import de.fh.erfurt.moviemate.movies.data.remote.client.MoviesApiConfiguration
import de.fh.erfurt.moviemate.movies.data.remote.dto.CategoryDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class KtorCategoryRemoteDataSource(
        private val httpClient: HttpClient,
        private val apiConfiguration: MoviesApiConfiguration

):CategoryRemoteDataSource {

    override suspend fun fetchCategories(): List<CategoryDto> {
        return httpClient.get(apiConfiguration.categoriesCollectionUrl()).body()
    }

    override suspend fun fetchCategory(categoryId: String): CategoryDto? {
        val response = httpClient.get(apiConfiguration.categoryItemUrl(categoryId))
        if (response.status == HttpStatusCode.NotFound) return null
        return response.body()
    }
}

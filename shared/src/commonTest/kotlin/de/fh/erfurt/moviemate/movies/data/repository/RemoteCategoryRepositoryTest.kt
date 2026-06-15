package de.fh.erfurt.moviemate.movies.data.repository

import de.fh.erfurt.moviemate.movies.data.remote.datasource.CategoryRemoteDataSource
import de.fh.erfurt.moviemate.movies.data.remote.dto.CategoryDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.CreateCategoryRequestDto
import de.fh.erfurt.moviemate.movies.data.remote.dto.UpdateCategoryRequestDto
import de.fh.erfurt.moviemate.movies.data.repository.remote.RemoteCategoryRepository
import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class RemoteCategoryRepositoryTest {

    @Test
    fun `fetchCategories returns list of categories from remote data source`() = runTest {
        val remoteCategoryRepository = RemoteCategoryRepository(
            remoteDataSource = FakeRemoteCategoryDataSource(
                fetchedCategories = listOf(
                    dto("1", "Action", 1L),
                    dto("2", "Adventure", 2L),
                    dto("3", "Animation", 3L),
                    dto("4", "Biography", 4L),
                    dto("5", "Comedy", 5L),
                    dto("6", "Crime", 6L),
                    dto("7", "Documentary", 7L),
                    dto("8", "Drama", 8L),
                    dto("9", "Family", 9L),
                    dto("10", "Fantasy", 10L),
                    dto("11", "History", 11L),
                    dto("12", "Horror", 12L),
                    dto("13", "Mystery", 13L),
                    dto("14", "Romance", 14L),
                    dto("15", "Sci_Fi", 15L),
                    dto("16", "Thriller", 16L),
                    dto("17", "War", 17L),
                    dto("18", "Western", 18L),
                    dto("19", "Other", 19L)
                )
            )
        )

        val categories = remoteCategoryRepository.getCategories()

        assertTrue(categories.isNotEmpty())
        assertEquals(
            expected = listOf(
                category("1", "Action", 1L),
                category("2", "Adventure", 2L),
                category("3", "Animation", 3L),
                category("4", "Biography", 4L),
                category("5", "Comedy", 5L),
                category("6", "Crime", 6L),
                category("7", "Documentary", 7L),
                category("8", "Drama", 8L),
                category("9", "Family", 9L),
                category("10", "Fantasy", 10L),
                category("11", "History", 11L),
                category("12", "Horror", 12L),
                category("13", "Mystery", 13L),
                category("14", "Romance", 14L),
                category("15", "Sci_Fi", 15L),
                category("16", "Thriller", 16L),
                category("17", "War", 17L),
                category("18", "Western", 18L),
                category("19", "Other", 19L)
            ),
            actual = categories
        )
    }

    @Test
    fun `fetchCategory returns category from remote data source`() = runTest {
        val remoteCategoryRepository = RemoteCategoryRepository(
            remoteDataSource = FakeRemoteCategoryDataSource(
                fetchedCategories = listOf(
                    dto("1", "Action", 1L),
                    dto("2", "Adventure", 2L),
                    dto("3", "Animation", 3L)
                )
            )
        )

        val category = remoteCategoryRepository.getCategory("2")

        assertEquals(
            expected = category("2", "Adventure", 2L),
            actual = category
        )

    }
}

private class FakeRemoteCategoryDataSource (
    private val fetchedCategories: List<CategoryDto> = emptyList(),
): CategoryRemoteDataSource {


    override suspend fun fetchCategories(): List<CategoryDto> {
        return fetchedCategories
    }

    override suspend fun fetchCategory(categoryId: String): CategoryDto? {
        return fetchedCategories.firstOrNull { it.id == categoryId }
    }

    override suspend fun createCategory(request: CreateCategoryRequestDto): CategoryDto {
        return dto("created", request.name, request.updatedAtEpochMillis, request.iconKey)
    }

    override suspend fun updateCategory(categoryId: String, request: UpdateCategoryRequestDto): CategoryDto {
        return dto(categoryId, request.name, request.updatedAtEpochMillis, request.iconKey)
    }

    override suspend fun deleteCategory(categoryId: String) = Unit


}

private fun dto(
    id: String,
    name: String,
    updatedAtEpochMillis: Long,
    iconKey: String? = null,
) = CategoryDto(
    id = id,
    name = name,
    iconKey = iconKey,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

private fun category(
    id: String,
    name: String,
    updatedAtEpochMillis: Long,
    iconKey: String? = null,
) = Category(
    id = id,
    name = name,
    iconKey = iconKey,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

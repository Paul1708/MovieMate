package de.fh.erfurt.moviemate.movies.data.repository

import de.fh.erfurt.moviemate.movies.data.remote.datasource.CategoryRemoteDataSource
import de.fh.erfurt.moviemate.movies.data.remote.dto.CategoryDto
import de.fh.erfurt.moviemate.movies.data.repository.remote.RemoteCategoryRepository
import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.collections.listOf


class RemoteCategoryRepositoryTest {

    @Test
    fun `fetchCategories returns list of categories from remote data source`() = runTest {
        val remoteCategoryRepository = RemoteCategoryRepository(
            remoteDataSource = FakeRemoteCategoryDataSource(
                fetchedCategories = listOf(
                    CategoryDto(id = "1", name = "Action"),
                    CategoryDto(id = "2", name = "Adventure"),
                    CategoryDto(id = "3", name = "Animation"),
                    CategoryDto(id = "4", name = "Biography"),
                    CategoryDto(id = "5", name = "Comedy"),
                    CategoryDto(id = "6", name = "Crime"),
                    CategoryDto(id = "7", name = "Documentary"),
                    CategoryDto(id = "8", name = "Drama"),
                    CategoryDto(id = "9", name = "Family"),
                    CategoryDto(id = "10", name = "Fantasy"),
                    CategoryDto(id = "11", name = "History"),
                    CategoryDto(id = "12", name = "Horror"),
                    CategoryDto(id = "13", name = "Mystery"),
                    CategoryDto(id = "14", name = "Romance"),
                    CategoryDto(id = "15", name = "Sci_Fi"),
                    CategoryDto(id = "16", name = "Thriller"),
                    CategoryDto(id = "17", name = "War"),
                    CategoryDto(id = "18", name = "Western"),
                    CategoryDto(id = "19", name = "Other")
                )
            )
        )

        val categories = remoteCategoryRepository.getCategories()

        assertTrue(categories.isNotEmpty())
        assertEquals(
            expected = listOf(
                Category(id = "1", type = CategoryType.ACTION),
                Category(id = "2", type = CategoryType.ADVENTURE),
                Category(id = "3", type = CategoryType.ANIMATION),
                Category(id = "4", type = CategoryType.BIOGRAPHY),
                Category(id = "5", type = CategoryType.COMEDY),
                Category(id = "6", type = CategoryType.CRIME),
                Category(id = "7", type = CategoryType.DOCUMENTARY),
                Category(id = "8", type = CategoryType.DRAMA),
                Category(id = "9", type = CategoryType.FAMILY),
                Category(id = "10", type = CategoryType.FANTASY),
                Category(id = "11", type = CategoryType.HISTORY),
                Category(id = "12", type = CategoryType.HORROR),
                Category(id = "13", type = CategoryType.MYSTERY),
                Category(id = "14", type = CategoryType.ROMANCE),
                Category(id = "15", type = CategoryType.SCI_FI),
                Category(id = "16", type = CategoryType.THRILLER),
                Category(id = "17", type = CategoryType.WAR),
                Category(id = "18", type = CategoryType.WESTERN),
                Category(id = "19", type = CategoryType.UNKNOWN)
            ),
            actual = categories
        )
    }

    @Test
    fun `fetchCategory returns category from remote data source`() = runTest {
        val remoteCategoryRepository = RemoteCategoryRepository(
            remoteDataSource = FakeRemoteCategoryDataSource(
                fetchedCategories = listOf(
                    CategoryDto(id = "1", name = "Action"),
                    CategoryDto(id = "2", name = "Adventure"),
                    CategoryDto(id = "3", name = "Animation")
                )
            )
        )

        val category = remoteCategoryRepository.getCategory("2")

        assertEquals(
            expected = Category(id = "2", type = CategoryType.ADVENTURE),
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


}
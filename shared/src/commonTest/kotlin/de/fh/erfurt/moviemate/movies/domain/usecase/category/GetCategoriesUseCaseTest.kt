package de.fh.erfurt.moviemate.movies.domain.usecase.category

import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType
import de.fh.erfurt.moviemate.movies.domain.repository.FakeCategoryRepository
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals


class GetCategoriesUseCaseTest {

    @Test
    fun `invoke returns the categories from the repository`() = runTest {
        // Given
        val exceptedCategories = listOf(
            Category(id = "1", type = CategoryType.ACTION),
            Category(id = "2", type = CategoryType.COMEDY),
        )
        val useCase = GetCategoriesUseCase(
            categoryRepository = FakeCategoryRepository(exceptedCategories)
        )

        val actualCategories = useCase()
        assertEquals( exceptedCategories,actualCategories)
    }
}
package de.fh.erfurt.moviemate.movies.domain.usecase.category

import de.fh.erfurt.moviemate.movies.domain.model.category.Category
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType
import de.fh.erfurt.moviemate.movies.domain.repository.FakeCategoryRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetCategoryUseCaseTest {

    val exceptedCategories = listOf(
        Category(id = "1", type = CategoryType.ACTION),
        Category(id = "2", type = CategoryType.COMEDY),
    )
    val repository = FakeCategoryRepository(exceptedCategories)
    val useCase = GetCategoryUseCase(repository)


    @Test
    fun `invoke returns the Category from the repository`() = runTest {

        val actualCategory = useCase("1")
        assertEquals( exceptedCategories.first(),actualCategory)
    }

    @Test
    fun `invoke rejects a blank id`() = runTest {

        val error = assertFailsWith<IllegalArgumentException> {
            useCase(" ")
        }
        assertEquals( "Category ID cannot be empty",error.message)
    }
}
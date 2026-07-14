package de.fh.erfurt.moviemate.movies.data.mapper.remote

import de.fh.erfurt.moviemate.movies.data.remote.dto.OmdbMovieDto
import kotlin.test.Test
import kotlin.test.assertEquals

class OmdbMappersTest {
    @Test
    fun mapsCommaSeparatedGenres() {
        val result = OmdbMovieDto(
            title = "Alien",
            genre = "Horror, Sci-Fi, Thriller",
        ).toFetchedMovieData()

        assertEquals(listOf("Horror", "Sci-Fi", "Thriller"), result.genres)
    }
}

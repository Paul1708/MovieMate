package de.fh.erfurt.moviemate.presentation.movies.categories

import androidx.annotation.DrawableRes
import de.fh.erfurt.moviemate.R
import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType

data class CategoryIconOption(
    val key: String,
    val label: String,
    @param:DrawableRes val icon: Int,
)

const val DefaultCategoryIconKey = "other"

val CategoryIconOptions: List<CategoryIconOption> = listOf(
    CategoryIconOption("action", "Action", R.drawable.ic_genre_action_24),
    CategoryIconOption("comedy", "Comedy", R.drawable.ic_genre_comedy_24),
    CategoryIconOption("drama", "Drama", R.drawable.ic_genre_drama_24),
    CategoryIconOption("theater", "Theater", R.drawable.ic_genre_theater_24),
    CategoryIconOption("romance", "Romance", R.drawable.ic_genre_romance_24),
    CategoryIconOption("fantasy", "Fantasy", R.drawable.ic_genre_fantasy_24),
    CategoryIconOption("adventure", "Adventure", R.drawable.ic_genre_adventure_24),
    CategoryIconOption("sci_fi", "Science Fiction", R.drawable.ic_genre_sci_fi_24),
    CategoryIconOption("thriller", "Thriller", R.drawable.ic_genre_thriller_24),
    CategoryIconOption("horror", "Horror", R.drawable.ic_genre_horror_24),
    CategoryIconOption("mystery", "Mystery", R.drawable.ic_genre_mystery_24),
    CategoryIconOption("crime", "Crime", R.drawable.ic_genre_crime_24),
    CategoryIconOption("history", "History", R.drawable.ic_genre_history_24),
    CategoryIconOption("documentary", "Documentary", R.drawable.ic_genre_documentary_24),
    CategoryIconOption("musical", "Musical", R.drawable.ic_genre_musical_24),
    CategoryIconOption("biography", "Biography", R.drawable.ic_genre_biography_24),
    CategoryIconOption("family", "Family", R.drawable.ic_genre_family_24),
    CategoryIconOption("animation", "Animation", R.drawable.ic_genre_animation_24),
    CategoryIconOption("kids", "Kids", R.drawable.ic_genre_kids_24),
    CategoryIconOption("gaming", "Gaming", R.drawable.ic_genre_gaming_24),
    CategoryIconOption("superhero", "Superhero", R.drawable.ic_genre_superhero_24),
    CategoryIconOption("war", "War", R.drawable.ic_genre_war_24),
    CategoryIconOption("western", "Western", R.drawable.ic_genre_western_24),
    CategoryIconOption("road_movie", "Road Movie", R.drawable.ic_genre_road_movie_24),
    CategoryIconOption("nature", "Nature", R.drawable.ic_genre_nature_24),
    CategoryIconOption("sport", "Sport", R.drawable.ic_genre_sport_24),
    CategoryIconOption("magic", "Magic", R.drawable.ic_genre_magic_24),
    CategoryIconOption("art_house", "Art House", R.drawable.ic_genre_art_house_24),
    CategoryIconOption("mind_bending", "Mind-Bending", R.drawable.ic_genre_mind_bending_24),
    CategoryIconOption("time_travel", "Time Travel", R.drawable.ic_genre_time_travel_24),
    CategoryIconOption("cult", "Cult", R.drawable.ic_genre_cult_24),
    CategoryIconOption("disaster", "Disaster", R.drawable.ic_genre_disaster_24),
    CategoryIconOption("survival", "Survival", R.drawable.ic_genre_survival_24),
    CategoryIconOption("other", "Other", R.drawable.ic_genre_other_24),
)

fun String?.categoryIconOption(): CategoryIconOption {
    return CategoryIconOptions.firstOrNull { option -> option.key == this }
        ?: CategoryIconOptions.first { option -> option.key == DefaultCategoryIconKey }
}

fun CategoryType.defaultIconKey(): String {
    return when (this) {
        CategoryType.ACTION -> "action"
        CategoryType.ADVENTURE -> "adventure"
        CategoryType.FANTASY -> "fantasy"
        CategoryType.ANIMATION -> "animation"
        CategoryType.BIOGRAPHY -> "biography"
        CategoryType.COMEDY -> "comedy"
        CategoryType.CRIME -> "crime"
        CategoryType.DOCUMENTARY -> "documentary"
        CategoryType.DRAMA -> "drama"
        CategoryType.FAMILY -> "family"
        CategoryType.HISTORY -> "history"
        CategoryType.HORROR -> "horror"
        CategoryType.MYSTERY -> "mystery"
        CategoryType.ROMANCE -> "romance"
        CategoryType.SCI_FI -> "sci_fi"
        CategoryType.THRILLER -> "thriller"
        CategoryType.WAR -> "war"
        CategoryType.WESTERN -> "western"
        CategoryType.UNKNOWN -> DefaultCategoryIconKey
    }
}

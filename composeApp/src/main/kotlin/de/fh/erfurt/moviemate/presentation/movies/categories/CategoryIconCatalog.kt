package de.fh.erfurt.moviemate.presentation.movies.categories

import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType

data class CategoryIconOption(
    val key: String,
    val label: String,
    internal val style: CategoryIconStyle,
)

enum class CategoryIconStyle {
    Action,
    Comedy,
    Drama,
    Theater,
    Romance,
    Fantasy,
    Adventure,
    ScienceFiction,
    Thriller,
    Horror,
    Mystery,
    Crime,
    History,
    Documentary,
    Musical,
    Biography,
    Family,
    Animation,
    Kids,
    Gaming,
    Superhero,
    War,
    Western,
    RoadMovie,
    Nature,
    Sport,
    Magic,
    ArtHouse,
    MindBending,
    TimeTravel,
    Cult,
    Disaster,
    Survival,
    Other,
}

const val DefaultCategoryIconKey = "other"

val CategoryIconOptions: List<CategoryIconOption> = listOf(
    CategoryIconOption("action", "Action", CategoryIconStyle.Action),
    CategoryIconOption("comedy", "Comedy", CategoryIconStyle.Comedy),
    CategoryIconOption("drama", "Drama", CategoryIconStyle.Drama),
    CategoryIconOption("theater", "Theater", CategoryIconStyle.Theater),
    CategoryIconOption("romance", "Romance", CategoryIconStyle.Romance),
    CategoryIconOption("fantasy", "Fantasy", CategoryIconStyle.Fantasy),
    CategoryIconOption("adventure", "Adventure", CategoryIconStyle.Adventure),
    CategoryIconOption("sci_fi", "Science Fiction", CategoryIconStyle.ScienceFiction),
    CategoryIconOption("thriller", "Thriller", CategoryIconStyle.Thriller),
    CategoryIconOption("horror", "Horror", CategoryIconStyle.Horror),
    CategoryIconOption("mystery", "Mystery", CategoryIconStyle.Mystery),
    CategoryIconOption("crime", "Crime", CategoryIconStyle.Crime),
    CategoryIconOption("history", "History", CategoryIconStyle.History),
    CategoryIconOption("documentary", "Documentary", CategoryIconStyle.Documentary),
    CategoryIconOption("musical", "Musical", CategoryIconStyle.Musical),
    CategoryIconOption("biography", "Biography", CategoryIconStyle.Biography),
    CategoryIconOption("family", "Family", CategoryIconStyle.Family),
    CategoryIconOption("animation", "Animation", CategoryIconStyle.Animation),
    CategoryIconOption("kids", "Kids", CategoryIconStyle.Kids),
    CategoryIconOption("gaming", "Gaming", CategoryIconStyle.Gaming),
    CategoryIconOption("superhero", "Superhero", CategoryIconStyle.Superhero),
    CategoryIconOption("war", "War", CategoryIconStyle.War),
    CategoryIconOption("western", "Western", CategoryIconStyle.Western),
    CategoryIconOption("road_movie", "Road Movie", CategoryIconStyle.RoadMovie),
    CategoryIconOption("nature", "Nature", CategoryIconStyle.Nature),
    CategoryIconOption("sport", "Sport", CategoryIconStyle.Sport),
    CategoryIconOption("magic", "Magic", CategoryIconStyle.Magic),
    CategoryIconOption("art_house", "Art House", CategoryIconStyle.ArtHouse),
    CategoryIconOption("mind_bending", "Mind-Bending", CategoryIconStyle.MindBending),
    CategoryIconOption("time_travel", "Time Travel", CategoryIconStyle.TimeTravel),
    CategoryIconOption("cult", "Cult", CategoryIconStyle.Cult),
    CategoryIconOption("disaster", "Disaster", CategoryIconStyle.Disaster),
    CategoryIconOption("survival", "Survival", CategoryIconStyle.Survival),
    CategoryIconOption("other", "Other", CategoryIconStyle.Other),
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

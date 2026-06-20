package de.fh.erfurt.moviemate.presentation.movies.categories

import de.fh.erfurt.moviemate.movies.domain.model.category.CategoryType

data class CategoryIconOption(
    val key: String,
    val label: String,
    internal val style: CategoryIconStyle,
)

enum class CategoryIconStyle {
    Spark,
    Map,
    Reel,
    Document,
    Smile,
    Lens,
    Frame,
    Crystal,
    Orbit,
    Arc,
    Grid,
}

const val DefaultCategoryIconKey = "grid"

val CategoryIconOptions: List<CategoryIconOption> = listOf(
    CategoryIconOption("spark", "Spark", CategoryIconStyle.Spark),
    CategoryIconOption("map", "Map", CategoryIconStyle.Map),
    CategoryIconOption("reel", "Reel", CategoryIconStyle.Reel),
    CategoryIconOption("document", "Document", CategoryIconStyle.Document),
    CategoryIconOption("smile", "Smile", CategoryIconStyle.Smile),
    CategoryIconOption("lens", "Lens", CategoryIconStyle.Lens),
    CategoryIconOption("frame", "Frame", CategoryIconStyle.Frame),
    CategoryIconOption("crystal", "Crystal", CategoryIconStyle.Crystal),
    CategoryIconOption("orbit", "Orbit", CategoryIconStyle.Orbit),
    CategoryIconOption("arc", "Arc", CategoryIconStyle.Arc),
    CategoryIconOption("grid", "Grid", CategoryIconStyle.Grid),
)

fun String?.categoryIconOption(): CategoryIconOption {
    return CategoryIconOptions.firstOrNull { option -> option.key == this }
        ?: CategoryIconOptions.first { option -> option.key == DefaultCategoryIconKey }
}

fun CategoryType.defaultIconKey(): String {
    return when (this) {
        CategoryType.ACTION -> "spark"
        CategoryType.ADVENTURE -> "map"
        CategoryType.FANTASY -> "crystal"
        CategoryType.ANIMATION -> "reel"
        CategoryType.BIOGRAPHY -> "document"
        CategoryType.COMEDY -> "smile"
        CategoryType.CRIME -> "lens"
        CategoryType.DOCUMENTARY -> "frame"
        CategoryType.DRAMA -> "frame"
        CategoryType.FAMILY -> "smile"
        CategoryType.HISTORY -> "document"
        CategoryType.HORROR -> "arc"
        CategoryType.MYSTERY -> "lens"
        CategoryType.ROMANCE -> "spark"
        CategoryType.SCI_FI -> "orbit"
        CategoryType.THRILLER -> "arc"
        CategoryType.WAR -> "grid"
        CategoryType.WESTERN -> "map"
        CategoryType.UNKNOWN -> DefaultCategoryIconKey
    }
}

package de.fh.erfurt.moviemate.movies.domain.model.category

enum class CategoryType(val storageKey: String) {
    ACTION("action"),
    ADVENTURE("adventure"),
    ANIMATION("animation"),
    BIOGRAPHY("biography"),
    COMEDY("comedy"),
    CRIME("crime"),
    DOCUMENTARY("documentary"),
    DRAMA("drama"),
    FAMILY("family"),
    FANTASY("fantasy"),
    HISTORY("history"),
    HORROR("horror"),
    MYSTERY("mystery"),
    ROMANCE("romance"),
    SCI_FI("sci_fi"),
    THRILLER("thriller"),
    WAR("war"),
    WESTERN("western"),
    UNKNOWN("unknown");

    companion object {
        fun fromString(value: String?): CategoryType {
            if (value == null) return UNKNOWN
            return entries.firstOrNull {
                it.storageKey.equals(value, ignoreCase = true) ||
                        it.name.equals(value, ignoreCase = true)
            } ?: UNKNOWN
        }
    }
}
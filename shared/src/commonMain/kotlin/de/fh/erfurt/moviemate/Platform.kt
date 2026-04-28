package de.fh.erfurt.moviemate

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
package de.fh.erfurt.moviemate.movies.data.local.idpolicy

import kotlin.random.Random

interface LocalIdPolicy {
    fun nextId(existingIds: Collection<String> = emptyList()): String
}

object MovieLocalIdPolicy : LocalIdPolicy {
    override fun nextId(existingIds: Collection<String>): String = generateId("movie", existingIds)
}

object CategoryLocalIdPolicy : LocalIdPolicy {
    override fun nextId(existingIds: Collection<String>): String = generateId("category", existingIds)
}

private fun generateId(prefix: String, existingIds: Collection<String>): String {
    var candidate: String
    do {
        candidate = "$prefix-${Random.nextLong().toString(16)}-${Random.nextInt(Int.MAX_VALUE)}"
    } while (candidate in existingIds)
    return candidate
}



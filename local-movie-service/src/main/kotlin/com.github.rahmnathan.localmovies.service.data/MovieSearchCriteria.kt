package com.github.rahmnathan.localmovies.service.data

data class MovieSearchCriteria(val path: String, val page: Int?, val itemsPerPage: Int?, val client: MovieClient?, val order: MovieOrder?)

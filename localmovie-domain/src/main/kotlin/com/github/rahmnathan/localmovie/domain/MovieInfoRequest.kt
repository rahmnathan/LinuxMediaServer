package com.github.rahmnathan.localmovie.domain

data class MovieInfoRequest(val path: String, val page: Int?, val resultsPerPage: Int?, val client: MovieClient?,
                               val order: MovieOrder?, val deviceId: String?, val pushToken: String?)

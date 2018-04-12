package com.github.rahmnathan.localmovies.web.data

import com.github.rahmnathan.localmovies.service.data.MovieClient
import com.github.rahmnathan.localmovies.service.data.MovieOrder

data class MovieInfoRequest(val path: String, val page: Int?, val resultsPerPage: Int?, val client: MovieClient?,
                               val order: MovieOrder?, val deviceId: String?, val pushToken: String?)

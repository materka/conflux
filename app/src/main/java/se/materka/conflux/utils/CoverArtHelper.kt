package se.materka.conflux.utils

import kaaes.spotify.webapi.android.SpotifyApi
import kaaes.spotify.webapi.android.SpotifyCallback
import kaaes.spotify.webapi.android.SpotifyError
import kaaes.spotify.webapi.android.models.ArtistsPager
import retrofit.client.Response
import timber.log.Timber
import java.net.URL

/**
 * Copyright 2017 Mattias Karlsson

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class CoverArtHelper {
    private lateinit var callback: (URL?) -> Unit


    private val searchArtists: SpotifyCallback<ArtistsPager> = object : SpotifyCallback<ArtistsPager>() {
        override fun success(artist: ArtistsPager?, response: Response?) {
            artist?.artists?.items?.let { artists ->
                if (!artists.isEmpty()) {
                    artists.sortedWith(compareBy { it.popularity })
                    val images = artists[0].images
                    if (!images.isEmpty()) {
                        callback.invoke(URL(images[0].url))
                    } else {
                        callback.invoke(null)
                    }
                }
            }
        }

        override fun failure(error: SpotifyError?) {
            Timber.d(error)
            callback.invoke(null)
        }
    }

    fun getArtwork(artist: String?, callback: (URL?) -> Unit) {
        this.callback = callback
        try {
            SpotifyApi().service.searchArtists(artist, searchArtists)
        } catch (e: Exception) {

        }
    }
}

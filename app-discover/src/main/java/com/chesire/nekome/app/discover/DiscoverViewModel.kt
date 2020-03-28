package com.chesire.nekome.app.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chesire.nekome.core.extensions.postError
import com.chesire.nekome.core.extensions.postSuccess
import com.chesire.nekome.core.flags.AsyncState
import com.chesire.nekome.core.models.SeriesModel
import com.chesire.nekome.server.Resource
import com.chesire.nekome.server.api.TrendingApi
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel to aid with performing series discovery.
 */
class DiscoverViewModel @Inject constructor(private val trending: TrendingApi) : ViewModel() {
    private val _trendingAnime by lazy {
        val trendingData = MutableLiveData<TrendingData>(AsyncState.Loading())
        viewModelScope.launch {
            when (val animeList = trending.trendingAnime()) {
                is Resource.Success -> trendingData.postSuccess(animeList.data)
                is Resource.Error -> trendingData.postError(DiscoverError.Error)
            }
        }
        return@lazy trendingData
    }

    private val _trendingManga by lazy {
        val trendingData = MutableLiveData<TrendingData>(AsyncState.Loading())
        viewModelScope.launch {
            when (val mangaList = trending.trendingManga()) {
                is Resource.Success -> trendingData.postSuccess(mangaList.data)
                is Resource.Error -> trendingData.postError(DiscoverError.Error)
            }
        }
        return@lazy trendingData
    }

    /**
     * Get the current trending Anime series.
     */
    val trendingAnime: LiveData<TrendingData>
        get() = _trendingAnime

    /**
     * Get the current trending Manga series.
     */
    val trendingManga: LiveData<TrendingData>
        get() = _trendingManga
}

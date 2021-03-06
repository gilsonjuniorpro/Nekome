package com.chesire.nekome.app.series.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chesire.nekome.core.AuthCaster
import com.chesire.nekome.core.IOContext
import com.chesire.nekome.core.extensions.postError
import com.chesire.nekome.core.extensions.postLoading
import com.chesire.nekome.core.extensions.postSuccess
import com.chesire.nekome.core.flags.AsyncState
import com.chesire.nekome.core.models.SeriesModel
import com.chesire.nekome.series.SeriesRepository
import com.chesire.nekome.server.Resource
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * ViewModel to store the current series detail model, and allow interactions with its data.
 */
class SeriesDetailViewModel @Inject constructor(
    private val repo: SeriesRepository,
    private val authCaster: AuthCaster,
    @IOContext private val ioContext: CoroutineContext
) : ViewModel() {

    lateinit var mutableModel: MutableSeriesModel

    private val _updatingStatus = LiveEvent<AsyncState<MutableSeriesModel, SeriesDetailError>>()
    val updatingStatus: LiveData<AsyncState<MutableSeriesModel, SeriesDetailError>>
        get() = _updatingStatus

    /**
     * Sets the model object into the ViewModel.
     */
    fun setModel(model: SeriesModel) {
        mutableModel = MutableSeriesModel.from(model)
    }

    /**
     * Sends an update request with the new information in [target].
     */
    fun sendUpdate(target: MutableSeriesModel) {
        _updatingStatus.postLoading()

        viewModelScope.launch(ioContext) {
            val response = repo.updateSeries(
                target.userSeriesId,
                target.seriesProgress,
                target.userSeriesStatus
            )
            if (response is Resource.Error && response.code == Resource.Error.CouldNotRefresh) {
                authCaster.issueRefreshingToken()
            } else if (response is Resource.Error) {
                _updatingStatus.postError(
                    target,
                    SeriesDetailError.Error
                )
            } else {
                _updatingStatus.postSuccess(target)
            }
        }
    }
}

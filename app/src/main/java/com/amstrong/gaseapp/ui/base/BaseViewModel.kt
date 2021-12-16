package com.amstrong.gaseapp.ui.base

import androidx.lifecycle.ViewModel
import com.amstrong.gaseapp.data.network.AuthApi
import com.amstrong.gaseapp.data.repositories.BaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseViewModel(
    private val repository: BaseRepository
) : ViewModel() {

    suspend fun logout(api: AuthApi) = withContext(Dispatchers.IO) { repository.logout(api) }

}
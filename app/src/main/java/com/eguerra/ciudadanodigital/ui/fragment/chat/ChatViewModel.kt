package com.eguerra.ciudadanodigital.ui.fragment.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eguerra.ciudadanodigital.data.Resource
import com.eguerra.ciudadanodigital.data.local.entity.ChatModel
import com.eguerra.ciudadanodigital.data.repository.ChatRepository
import com.eguerra.ciudadanodigital.ui.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _getUserChatsStateFlow: MutableStateFlow<Status<List<ChatModel>>> =
        MutableStateFlow(Status.Default())
    val getUserChatsStateFlow: StateFlow<Status<List<ChatModel>>> = _getUserChatsStateFlow

    fun getChats(remote: Boolean = false) {
        _getUserChatsStateFlow.value = Status.Loading()
        viewModelScope.launch {
            when (val result = repository.getUserChats(
                remote = remote
            )) {
                is Resource.Success -> {
                    _getUserChatsStateFlow.value = Status.Success(result.data)
                }

                else -> {
                    _getUserChatsStateFlow.value =
                        Status.Error(result.message ?: "Ocurrió un error al obtener los mensajes.")
                }
            }
        }
    }
}
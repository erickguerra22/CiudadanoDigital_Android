package com.eguerra.ciudadanodigital.ui.fragment.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eguerra.ciudadanodigital.data.Resource
import com.eguerra.ciudadanodigital.data.local.entity.MessageModel
import com.eguerra.ciudadanodigital.data.repository.MessageRepository
import com.eguerra.ciudadanodigital.ui.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val repository: MessageRepository
) : ViewModel() {

    private val _createMessageStateFlow: MutableStateFlow<Status<MessageModel>> =
        MutableStateFlow(Status.Default())
    val createMessageStateFlow: StateFlow<Status<MessageModel>> = _createMessageStateFlow

    fun newMessage(content: String, chatId: String?) {
        _createMessageStateFlow.value = Status.Loading()
        viewModelScope.launch {
            when (val result = repository.createMessage(
                content = content,
                chatId = chatId
            )) {
                is Resource.Success -> {
                    _createMessageStateFlow.value = Status.Success(result.data.first)
                }

                else -> {
                    _createMessageStateFlow.value =
                        Status.Error(result.code ?: 500,result.message ?: "Ocurrió un error al enviar el mensaje.")
                }
            }
        }
    }

    private val _getResponseStateFlow: MutableStateFlow<Status<Pair<MessageModel, Boolean>>> =
        MutableStateFlow(Status.Default())
    val getResponseStateFlow: StateFlow<Status<Pair<MessageModel, Boolean>>> = _getResponseStateFlow

    fun getResponse(question: String, chatId: String?) {
        _getResponseStateFlow.value = Status.Loading()
        viewModelScope.launch {
            when (val result = repository.getResponse(
                question = question,
                chatId = chatId
            )) {
                is Resource.Success -> {
                    _getResponseStateFlow.value =
                        Status.Success(Pair(result.data.first, result.data.third))
                }

                else -> {
                    _getResponseStateFlow.value =
                        Status.Error(result.code ?: 500,result.message ?: "Ocurrió un error al obtener la respuesta.")
                }
            }
        }
    }

    private val _assignMessageStateFlow: MutableStateFlow<Status<Boolean>> =
        MutableStateFlow(Status.Default())
    val assignMessageStateFlow: StateFlow<Status<Boolean>> = _assignMessageStateFlow

    fun assignMessage(messageId: String, chatId: String) {
        _assignMessageStateFlow.value = Status.Loading()
        viewModelScope.launch {
            when (val result = repository.assignMessage(
                messageId = messageId,
                chatId = chatId
            )) {
                is Resource.Success -> {
                    _assignMessageStateFlow.value =
                        Status.Success(true)
                }

                else -> {
                    _assignMessageStateFlow.value =
                        Status.Error(result.code ?: 500,result.message ?: "Ocurrió un error al asignar el mensaje.")
                }
            }
        }
    }
}
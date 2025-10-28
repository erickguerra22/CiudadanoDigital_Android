package com.eguerra.ciudadanodigital.ui.fragment.chat

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.eguerra.ciudadanodigital.data.local.entity.ChatModel
import com.eguerra.ciudadanodigital.data.local.entity.MessageModel
import com.eguerra.ciudadanodigital.databinding.FragmentChatBinding
import com.eguerra.ciudadanodigital.ui.Status
import com.eguerra.ciudadanodigital.ui.activity.LoadingViewModel
import com.eguerra.ciudadanodigital.ui.activity.MainActivity
import com.eguerra.ciudadanodigital.ui.adapters.ChatListAdapter
import com.eguerra.ciudadanodigital.ui.adapters.MessageListAdapter
import com.eguerra.ciudadanodigital.ui.util.showConfirmationDialog
import com.eguerra.ciudadanodigital.ui.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment(), MessageListAdapter.MessageListener, ChatListAdapter.ChatListener {
    private lateinit var binding: FragmentChatBinding
    private val messageViewModel: MessageViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private val loadingViewModel: LoadingViewModel by activityViewModels()
    private var chatId: String? = null
    private var isMessagesRecyclerUp: Boolean = false
    private var isChatsRecyclerUp: Boolean = false
    private var messageAdapter: MessageListAdapter? = null
    private var chatAdapter: ChatListAdapter? = null
    private var unassignedMessages: MutableList<MessageModel> = mutableListOf()
    private var isPanelVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMessagesRecycler()
        setupChatsRecycler()
        initEvents()
        setListeners()
        setObservers()
    }

    private fun setupMessagesRecycler() {
        messageAdapter = MessageListAdapter(mutableListOf(), this)
        binding.chatFragmentMessagesRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = messageAdapter
        }
    }

    private fun setupChatsRecycler() {
        chatAdapter = ChatListAdapter(mutableListOf(), this)
        binding.chatFragmentChatsRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }
    }

    private fun initEvents() {
        if (chatId != null) {
            messageViewModel.getMessages(chatId!!, 20, null, false)
        }
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            messageViewModel.createMessageStateFlow.collectLatest { result ->
                when (result) {
                    is Status.Loading -> {
                        binding.chatFragmentMessageTextInput.isEnabled = false
                    }

                    is Status.Success -> {
                        binding.chatFragmentMessageEditText.setText("")
                        binding.chatFragmentMessageTextInput.isEnabled = true
                        val message = result.value

                        showMessagesRecycler()
                        println("ADDTOUNASSIGNED: ${message.chatId == null}")

                        if (message.chatId == null) unassignedMessages.add(message)

                        messageAdapter?.addMessage(message)
                        binding.chatFragmentMessagesRecycler.post {
                            val itemCount = messageAdapter?.itemCount ?: 0
                            if (itemCount > 0) {
                                binding.chatFragmentMessagesRecycler.smoothScrollToPosition(
                                    itemCount - 1
                                )
                            }
                        }
                        messageViewModel.getResponse(question = message.content, chatId)
                    }

                    is Status.Error -> {
                        binding.chatFragmentMessageTextInput.isEnabled = true
                        showToast(result.error, requireContext())
                    }

                    else -> {}
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            messageViewModel.getResponseStateFlow.collectLatest { result ->
                when (result) {
                    is Status.Loading -> {
                        binding.chatFragmentMessageTextInput.isEnabled = false
                        loadingViewModel.showLoadingDialog()
                    }

                    is Status.Success -> {
                        binding.chatFragmentMessageEditText.setText("")
                        binding.chatFragmentMessageTextInput.isEnabled = true
                        val (message, newChat) = result.value

                        chatId = message.chatId?.toString()
                        println("NEWCHAT: $newChat")
                        if (newChat) {
                            for (m in unassignedMessages) {
                                println("MESSAGETOASSIGN: $m")
                                messageViewModel.assignMessage(
                                    messageId = m.messageId.toString(),
                                    chatId = message.chatId.toString()
                                )
                            }
                        }

                        showMessagesRecycler()

                        messageAdapter?.addMessage(message)
                        binding.chatFragmentMessagesRecycler.post {
                            val itemCount = messageAdapter?.itemCount ?: 0
                            if (itemCount > 0) {
                                binding.chatFragmentMessagesRecycler.smoothScrollToPosition(
                                    itemCount - 1
                                )
                            }
                        }
                        loadingViewModel.hideLoadingDialog()
                    }

                    is Status.Error -> {
                        binding.chatFragmentMessageTextInput.isEnabled = true
                        showToast(result.error, requireContext())
                        loadingViewModel.hideLoadingDialog()
                    }

                    else -> {}
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            messageViewModel.assignMessageStateFlow.collectLatest { result ->
                println("RESULT: $result")
                when (result) {
                    is Status.Error -> {
                        showToast(result.error, requireContext())
                    }

                    else -> {}
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            messageViewModel.getChatMessagesStateFlow.collectLatest { result ->
                when (result) {
                    is Status.Loading -> {
                        binding.chatFragmentMessageEditText.setText("")
                        binding.chatFragmentMessageTextInput.isEnabled = false
                    }

                    is Status.Success -> {
                        binding.chatFragmentMessageTextInput.isEnabled = true
                        val messages = result.value

                        if (messages.isNotEmpty()) {
                            showMessagesRecycler()
                            messageAdapter?.setMessages(messages)

                            binding.chatFragmentMessagesRecycler.post {
                                binding.chatFragmentMessagesRecycler.scrollToPosition(messages.size - 1)
                            }
                        } else {
                            hideMessagesRecycler()
                        }
                    }

                    is Status.Error -> {
                        binding.chatFragmentMessageTextInput.isEnabled = true
                        hideMessagesRecycler()
                        showToast(result.error, requireContext())
                    }

                    else -> {}
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            chatViewModel.getUserChatsStateFlow.collectLatest { result ->
                when (result) {
                    is Status.Loading -> {}

                    is Status.Success -> {
                        val chats = result.value

                        if (chats.isNotEmpty()) {
                            showChatsRecycler()
                            chatAdapter?.setChats(chats)
                        }
                    }

                    is Status.Error -> {
                        chatAdapter?.setChats(emptyList())
                        showToast(result.error, requireContext())
                    }

                    else -> {}
                }
            }
        }
    }

    private fun hideMessagesRecycler() {
        messageAdapter?.setMessages(emptyList())
        binding.chatFragmentWelcomeTextView.isVisible = true
        binding.chatFragmentLogoImageView.isVisible = true
        isMessagesRecyclerUp = false
    }

    private fun showMessagesRecycler() {
        if (!isMessagesRecyclerUp) {
            isMessagesRecyclerUp = true
            binding.chatFragmentMessagesRecycler.isVisible = true
            binding.chatFragmentLogoImageView.isVisible = false
            binding.chatFragmentWelcomeTextView.isVisible = false
        }
    }

    private fun showChatsRecycler() {
        if (!isChatsRecyclerUp) {
            isChatsRecyclerUp = true
            binding.chatFragmentChatsRecycler.isVisible = true
        }
    }

    private fun setListeners() {
        binding.apply {
            chatFragmentMessageTextInput.setEndIconOnClickListener {
                val content = chatFragmentMessageEditText.text
                if (content?.trim().isNullOrBlank()) return@setEndIconOnClickListener
                sendMessage(content.toString())
            }

            chatFragmentPanelImageButton.setOnClickListener {
                toggleSidePanel()
                chatViewModel.getChats()
            }

            chatFragmentNewChatButton.setOnClickListener {
                hideMessagesRecycler()
                chatId = null
                hideSidePanel()
            }

            chatFragmentLogoutButton.setOnClickListener {
                showConfirmationDialog(
                    "Cerrar Sesión",
                    "¿Está seguro que desea terminar la sesión actual?",
                    requireContext()
                ) { confirm ->
                    if (confirm)
                        (requireActivity() as MainActivity).handleLogoutAction()
                }
            }

            chatFragmentOverlayView.apply {
                setOnTouchListener { v, event ->
                    val panelRect = Rect()
                    binding.chatFragmentSidePanelContainer.getGlobalVisibleRect(panelRect)

                    if (panelRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        return@setOnTouchListener false
                    }

                    if (event.action == MotionEvent.ACTION_UP) {
                        v.performClick()
                        hideSidePanel()
                    }

                    true
                }

                setOnClickListener {}
            }
        }
    }

    private fun toggleSidePanel() {
        if (isPanelVisible) hideSidePanel() else showSidePanel()
    }

    private fun showSidePanel() {
        val panel = binding.chatFragmentSidePanelContainer
        val overlay = binding.chatFragmentOverlayView

        overlay.isVisible = true
        overlay.alpha = 0f
        overlay.animate().alpha(1f).setDuration(200).start()

        panel.isVisible = true
        panel.animate()
            .translationX(0f)
            .setDuration(250)
            .start()

        isPanelVisible = true
    }

    private fun hideSidePanel() {
        val panel = binding.chatFragmentSidePanelContainer
        val overlay = binding.chatFragmentOverlayView

        overlay.animate().alpha(0f).setDuration(200)
            .withEndAction { overlay.isVisible = false }.start()

        panel.animate()
            .translationX(-panel.width.toFloat())
            .setDuration(250)
            .withEndAction { panel.isVisible = false }
            .start()

        isPanelVisible = false
    }

    private fun sendMessage(content: String) {
        messageViewModel.newMessage(content = content, chatId = chatId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        messageAdapter = null
    }

    override fun onItemClicked(chat: ChatModel) {
        chatId = chat.chatId.toString()
        hideSidePanel()
        messageViewModel.getMessages(chatId!!, 20, null, false)
    }
}
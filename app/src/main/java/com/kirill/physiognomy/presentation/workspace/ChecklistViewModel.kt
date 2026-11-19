package com.kirill.physiognomy.presentation.workspace

import androidx.lifecycle.ViewModel
import com.kirill.physiognomy.domain.model.PhysiognomyCatalog
import com.kirill.physiognomy.domain.model.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChecklistViewModel(
    private val isBlitzMode: Boolean
) : ViewModel() {

    // Фильтруем каталог: если Блиц — берем только isBlitz = true
    private val activeQuestions = PhysiognomyCatalog.questions.filter {
        if (isBlitzMode) it.isBlitz else true
    }

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _answers = MutableStateFlow<Map<String, String>>(emptyMap())
    val answers: StateFlow<Map<String, String>> = _answers.asStateFlow()

    val totalQuestions: Int get() = activeQuestions.size

    fun getCurrentQuestion(): Question? {
        return activeQuestions.getOrNull(_currentQuestionIndex.value)
    }

    fun selectOption(questionId: String, optionId: String) {
        _answers.update { current -> current + (questionId to optionId) }
    }

    fun nextQuestion() {
        if (_currentQuestionIndex.value < totalQuestions - 1) {
            _currentQuestionIndex.value++
        }
    }

    fun previousQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value--
        }
    }

    fun isLastQuestion(): Boolean {
        return _currentQuestionIndex.value == totalQuestions - 1
    }
}
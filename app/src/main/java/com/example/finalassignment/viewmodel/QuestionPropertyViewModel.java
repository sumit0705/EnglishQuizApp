package com.example.finalassignment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.finalassignment.models.QuestionsModel;

import java.util.ArrayList;

public class QuestionPropertyViewModel extends AndroidViewModel {

    private MutableLiveData<QuestionsModel> currentQuestion = new MutableLiveData<>(); // for current question
    private MutableLiveData<ArrayList<Boolean>> bookmarkStatus = new MutableLiveData<>(); // for overall bookmarkstatus
    private MutableLiveData<Integer> position = new MutableLiveData<>(); // for the current position of question that is shown on QuestionDetailScreen
    private MutableLiveData<ArrayList<String>> selectedOption = new MutableLiveData<>(); // storing the option selected by the user
    private MutableLiveData<ArrayList<Boolean>> answerStatus = new MutableLiveData<>(); // storing if a question is being answered or not
    private MutableLiveData<Integer> timerValue = new MutableLiveData<>(); // timer value in milliseconds
    private MutableLiveData<Integer> endvalue = new MutableLiveData<>(); // for printing toast message of auto submission

    public QuestionPropertyViewModel(@NonNull Application application) {
        super(application);
        currentQuestion.setValue(null);
        bookmarkStatus.setValue(null);
        answerStatus.setValue(null);
        position.setValue(null);
        selectedOption.setValue(null);
        timerValue.setValue(null);
        endvalue.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public LiveData<QuestionsModel> getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(QuestionsModel question) {
        currentQuestion.setValue(question);
    }

    public LiveData<ArrayList<Boolean>> getBookmarkStatus() {
        return bookmarkStatus;
    }

    public void setBookmarkStatus(ArrayList<Boolean> status) {
        bookmarkStatus.setValue(status);
    }

    public LiveData<ArrayList<Boolean>> getAnswerStatus() {
        return answerStatus;
    }

    public void setAnswerStatus(ArrayList<Boolean> status) {
        answerStatus.setValue(status);
    }

    public LiveData<Integer> getActualTimer() {
        return timerValue;
    }

    public void setActualTimer(int val) {
        timerValue.setValue(val);
    }

    public LiveData<Integer> getQuestionPosition() {
        return position;
    }

    public void setQuestionPosition(int pos) {
        position.setValue(pos);
    }

    public LiveData<ArrayList<String>> getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(ArrayList<String> selectedOption1) {
        selectedOption.setValue(selectedOption1);
    }

    public LiveData<Integer> getEndValue() {
        return endvalue;
    }

    public void setEndValue(int val) {
        endvalue.setValue(val);
    }


}

package com.example.finalassignment.models;

import java.util.ArrayList;

public class QuestionsModel {

    String question;
    ArrayList<String> options;
    String correct_option;

    public QuestionsModel(String question, ArrayList<String> options, String correct_option) {
        this.question = question;
        this.options = options;
        this.correct_option = correct_option;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public String getCorrect_option() {
        return correct_option;
    }

    public void setCorrect_option(String correct_option) {
        this.correct_option = correct_option;
    }
}

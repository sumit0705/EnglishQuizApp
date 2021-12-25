package com.example.finalassignment.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalassignment.models.QuestionsModel;
import com.example.finalassignment.R;
import com.example.finalassignment.viewmodel.QuestionPropertyViewModel;
import com.example.finalassignment.viewmodel.QuestionsViewModel;

import java.util.ArrayList;
import java.util.List;

public class QuestionDetailScreenFragment extends Fragment {

    private QuestionPropertyViewModel qViewModel;
    private QuestionsViewModel viewModel;
    private TextView timerTextView, questionTextView;
    private Button nextButton, prevButton, submitButton;
    private Switch toggleButton;
    private RadioButton option1, option2, option3, option4;
    private List<QuestionsModel> questions;
    private AlertDialog alertDialog;
    private FragmentActivity myContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContext = (FragmentActivity) context;
        qViewModel = new ViewModelProvider(requireActivity()).get(QuestionPropertyViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(QuestionsViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_question_detail_screen, container, false);
        timerTextView = v.findViewById(R.id.timer_tv2);
        questionTextView = v.findViewById(R.id.question);
        nextButton = v.findViewById(R.id.next_button);
        prevButton = v.findViewById(R.id.previous_button);
        toggleButton = v.findViewById(R.id.toggleButton);
        submitButton = v.findViewById(R.id.submit_button2);
        option1 = v.findViewById(R.id.radio_option1);
        option2 = v.findViewById(R.id.radio_option2);
        option3 = v.findViewById(R.id.radio_option3);
        option4 = v.findViewById(R.id.radio_option4);

        timerTextView.setText("time limit: " + viewModel.getTimerValue().getValue() / 1000 + " sec");
        questionTextView.setText(qViewModel.getCurrentQuestion().getValue().getQuestion() + "");
        option1.setText(qViewModel.getCurrentQuestion().getValue().getOptions().get(0) + "");
        option2.setText(qViewModel.getCurrentQuestion().getValue().getOptions().get(1) + "");
        option3.setText(qViewModel.getCurrentQuestion().getValue().getOptions().get(2) + "");
        option4.setText(qViewModel.getCurrentQuestion().getValue().getOptions().get(3) + "");

        //setting up state of prevButton if clicked question is the first question
        if (qViewModel.getQuestionPosition().getValue() == 0) {
            prevButton.setEnabled(false);
            prevButton.setClickable(false);
            prevButton.setBackgroundColor(getResources().getColor(R.color.grey));
        }

        //setting up state of nextButton if clicked question is the last question
        if (qViewModel.getQuestionPosition().getValue() == 9) {
            nextButton.setEnabled(false);
            nextButton.setClickable(false);
            nextButton.setBackgroundColor(getResources().getColor(R.color.grey));
        }

        //initially setting up bookmarkStatus list (of QuestionPropertyViewModel) with false values
        if (qViewModel.getBookmarkStatus().getValue() == null) {
            int size = viewModel.getQuestionsLiveData().getValue().size();
            ArrayList<Boolean> flag = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                flag.add(false);
            }
            qViewModel.setBookmarkStatus(flag);
            toggleButton.setChecked(false);
        } else {

            // setting up state of toggleButton based on bookmarkStatus list (of QuestionPropertyViewModel)
            ArrayList<Boolean> blist = qViewModel.getBookmarkStatus().getValue();
            for (int i = 0; i < blist.size(); i++) {
                if (i == qViewModel.getQuestionPosition().getValue()) {
                    if (blist.get(i)) {
                        toggleButton.setChecked(true);
                    } else {
                        toggleButton.setChecked(false);
                    }
                    break;
                }
            }
        }

        // initially setting up the selectedOption list (of QuestionPropertyViewModel) with nothing
        if (qViewModel.getSelectedOption().getValue() == null) {
            int size = viewModel.getQuestionsLiveData().getValue().size();
            ArrayList<String> flag = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                flag.add("nothing");
            }
            qViewModel.setSelectedOption(flag);

        } else {
            //setting up state of radioButtons based on selectedOption list (of QuestionPropertyViewModel)
            setRadioButtons();
        }

        //initially setting up answerStatus (of QuestionPropertyViewModel) with false values
        if (qViewModel.getAnswerStatus().getValue() == null) {
            int size = viewModel.getQuestionsLiveData().getValue().size();
            ArrayList<Boolean> flag = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                flag.add(false);
            }
            qViewModel.setAnswerStatus(flag);
        }

        //setting bookmarkStatus list (of QuestionPropertyViewModel) based on click of toggleButton
        toggleButton.setOnClickListener(view -> {

            ArrayList<Boolean> alist = qViewModel.getBookmarkStatus().getValue();
            for (int i = 0; i < alist.size(); i++) {
                if (i == qViewModel.getQuestionPosition().getValue()) {
                    alist.set(i, !(alist.get(qViewModel.getQuestionPosition().getValue())));
                    break;
                }
            }
            qViewModel.setBookmarkStatus(alist);
            changeBookMarkText();
        });

        questions = viewModel.getQuestionsLiveData().getValue();

        prevButton.setOnClickListener(view -> {

            // if prevButton is clicked so enable nextButton if disabled
            if (!nextButton.isEnabled()) {
                nextButton.setEnabled(true);
                nextButton.setClickable(true);
                nextButton.setBackgroundColor(getResources().getColor(R.color.blue));
            }
            qViewModel.setQuestionPosition(qViewModel.getQuestionPosition().getValue() - 1);

            if (qViewModel.getQuestionPosition().getValue() >= 0) {
                qViewModel.setCurrentQuestion(questions.get(qViewModel.getQuestionPosition().getValue()));
                setQuestionWithOption();
                setRadioButtons();

                // setting up state of prevButton based on position list (of QuestionPropertyViewModel)
                if (qViewModel.getQuestionPosition().getValue() == 0) {
                    prevButton.setEnabled(false);
                    prevButton.setClickable(false);
                    prevButton.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
            changeBookMarkText();

        });

        nextButton.setOnClickListener(view -> {

            // if nextButton is clicked so enable prevButton if disabled
            if (!prevButton.isEnabled()) {
                prevButton.setEnabled(true);
                prevButton.setClickable(true);
                prevButton.setBackgroundColor(getResources().getColor(R.color.blue));
            }
            qViewModel.setQuestionPosition(qViewModel.getQuestionPosition().getValue() + 1);
            if (qViewModel.getQuestionPosition().getValue() < 10) {
                qViewModel.setCurrentQuestion(questions.get(qViewModel.getQuestionPosition().getValue()));
                setQuestionWithOption();
                setRadioButtons();

                // setting up state of nextButton based on position list (of QuestionPropertyViewModel)
                if (qViewModel.getQuestionPosition().getValue() == 9) {
                    nextButton.setEnabled(false);
                    nextButton.setClickable(false);
                    nextButton.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
            changeBookMarkText();

        });


        option1.setOnClickListener(view -> {
            //setting other radioButtons as false ans clicked as true
            setAnswerStatus();
            option4.setChecked(false);
            option3.setChecked(false);
            option2.setChecked(false);
            option1.setChecked(true);
            setSelectedOptionsList();
            printList();
        });

        option2.setOnClickListener(view -> {
            //setting other radioButtons as false ans clicked as true
            setAnswerStatus();
            option4.setChecked(false);
            option3.setChecked(false);
            option1.setChecked(false);
            option2.setChecked(true);
            setSelectedOptionsList();
            printList();
        });

        option3.setOnClickListener(view -> {
            //setting other radioButtons as false ans clicked as true
            setAnswerStatus();
            option4.setChecked(false);
            option1.setChecked(false);
            option2.setChecked(false);
            option3.setChecked(true);
            setSelectedOptionsList();
            printList();
        });

        option4.setOnClickListener(view -> {
            //setting other radioButtons as false ans clicked as true
            setAnswerStatus();
            option1.setChecked(false);
            option2.setChecked(false);
            option3.setChecked(false);
            option4.setChecked(true);
            setSelectedOptionsList();
            printList();
        });

        //starting the timer
        if (viewModel.getTimerValue().getValue() != 0) {
            new CountDownTimer(viewModel.getTimerValue().getValue(), 1000) {

                public void onTick(long millisUntilFinished) {
                    int minute = (int) ((millisUntilFinished / 1000) / 60);
                    int second = (int) ((millisUntilFinished / 1000) % 60);

                    timerTextView.setText("time limit: " +minute+":"+second);

                    viewModel.changeTimerValue((int) millisUntilFinished);

                }

                public void onFinish() {
                    timerTextView.setText("time limit: 00:00");

                    final FragmentManager fragmentManager = myContext.getSupportFragmentManager();

                    final Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);

                    if (alertDialog != null)
                        alertDialog.dismiss();

                    if (fragment instanceof QuestionDetailScreenFragment) {
                        launchSummaryScreen();
                    }
                    viewModel.changeTimerValue(0);
                }

            }.start();
        }

        submitButton.setOnClickListener(view -> {
            viewModel.setIsButtonActive(true);
            alertDialog = getAlertDialog();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        });

        if (viewModel.getIsButtonActive().getValue()) {
            alertDialog = getAlertDialog();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
        return v;
    }

    //setting answerStatus list of (of QuestionPropertyViewModel)
    private void setAnswerStatus() {
        ArrayList<Boolean> alist = qViewModel.getAnswerStatus().getValue();
        for (int i = 0; i < alist.size(); i++) {
            if (i == qViewModel.getQuestionPosition().getValue()) {
                alist.set(i, true);
                break;
            }
        }
        qViewModel.setAnswerStatus(alist);
    }

    //setting selectedOption list of (of QuestionPropertyViewModel)
    private void setSelectedOptionsList() {
        ArrayList<String> list = qViewModel.getSelectedOption().getValue();
        for (int i = 0; i < list.size(); i++) {
            if (i == qViewModel.getQuestionPosition().getValue()) {
                String text = "";
                if (option1.isChecked()) {
                    text += option1.getText().toString();
                } else if (option2.isChecked()) {
                    text += option2.getText().toString();
                } else if (option3.isChecked()) {
                    text += option3.getText().toString();
                } else {
                    text += option4.getText().toString();
                }
                list.set(i, text);
                break;
            }
        }
        qViewModel.setSelectedOption(list);
    }

    //setting up checked state of radioButtons based on selectedOption (of QuestionPropertyViewModel)
    private void setRadioButtons() {
        String text = qViewModel.getSelectedOption().getValue().get(qViewModel.getQuestionPosition().getValue());
        if (text.equals("nothing")) {
            option1.setChecked(false);
            option2.setChecked(false);
            option3.setChecked(false);
            option4.setChecked(false);
        } else {

            if (option1.getText().toString().equals(text)) {
                option2.setChecked(false);
                option3.setChecked(false);
                option4.setChecked(false);
                option1.setChecked(true);
            } else if (option2.getText().toString().equals(text)) {
                option1.setChecked(false);
                option3.setChecked(false);
                option4.setChecked(false);
                option2.setChecked(true);

            } else if (option3.getText().toString().equals(text)) {
                option1.setChecked(false);
                option2.setChecked(false);
                option4.setChecked(false);
                option3.setChecked(true);
            } else {
                option1.setChecked(false);
                option2.setChecked(false);
                option3.setChecked(false);
                option4.setChecked(true);
            }
        }
    }

    //setting up toggleButton based on bookmarkStatus (of QuestionPropertyViewModel)
    private void changeBookMarkText() {
        if (qViewModel.getBookmarkStatus().getValue().get(qViewModel.getQuestionPosition().getValue()) == false) {
            toggleButton.setChecked(false);
        } else {
            toggleButton.setChecked(true);
        }
    }

    //launching SummaryScreen
    private void launchSummaryScreen() {
        final FragmentManager fragmentManager = myContext.getSupportFragmentManager();

        final Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);

        if (fragment != null) {
            final FragmentFactory fragmentFactory = fragmentManager.getFragmentFactory();
            final String questionsScreen = SummaryScreeenFragment.class.getName();
            final Fragment fragment1 = fragmentFactory.instantiate(myContext.getClassLoader(), questionsScreen);

            Bundle args = new Bundle();
            args.putInt("timer", viewModel.getTimerValue().getValue());
            fragment1.setArguments(args);
            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, fragment1);
            fragmentTransaction.commit();
        }

    }

    //setting the options based on currentQuestion (of QuestionPropertyViewModel)
    private void setQuestionWithOption() {
        questionTextView.setText(qViewModel.getCurrentQuestion().getValue().getQuestion() + "");
        option1.setText(qViewModel.getCurrentQuestion().getValue().getOptions().get(0) + "");
        option2.setText(qViewModel.getCurrentQuestion().getValue().getOptions().get(1) + "");
        option3.setText(qViewModel.getCurrentQuestion().getValue().getOptions().get(2) + "");
        option4.setText(qViewModel.getCurrentQuestion().getValue().getOptions().get(3) + "");
    }

    private AlertDialog getAlertDialog() {
        return new AlertDialog.Builder(myContext)
                .setTitle("MCQ Quiz")
                .setMessage("Are you sure you want to Submit the test?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    viewModel.setIsButtonActive(false);
                    launchSummaryScreen();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                    viewModel.setIsButtonActive(false);
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .create();

    }

    //printing the selectedOption  list (of QuestionPropertyViewModel)
    void printList() {
        ArrayList<String> alist = qViewModel.getSelectedOption().getValue();
        if (alist != null) {
            for (int i = 0; i < alist.size(); i++) {
                Log.d("TAG", " i = " + i + ", " + alist.get(i) + ", ");
            }
        }
    }

    //for solving window leak
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(alertDialog!=null){
            alertDialog.dismiss();
            alertDialog=null;
        }
    }
}

package com.example.finalassignment.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalassignment.R;
import com.example.finalassignment.viewmodel.QuestionPropertyViewModel;
import com.example.finalassignment.viewmodel.QuestionsViewModel;

import java.util.ArrayList;

public class SummaryScreeenFragment extends Fragment {

    private Button restartButton, exitButton;
    private TextView scoreTV, timeTakenTV;
    private QuestionPropertyViewModel qViewModel;
    private QuestionsViewModel viewModel;
    private FragmentActivity myContext;

    private int timerValue = 0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContext = (FragmentActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_summary_screen, container, false);


        timerValue = getArguments().getInt("timer", -1);
        restartButton = v.findViewById(R.id.restart_button);
        exitButton = v.findViewById(R.id.exit_button);
        scoreTV = v.findViewById(R.id.score);
        timeTakenTV = v.findViewById(R.id.time_taken);

        qViewModel = new ViewModelProvider(requireActivity()).get(QuestionPropertyViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(QuestionsViewModel.class);

        if (viewModel.getTimerValue().getValue() == 0 && qViewModel.getEndValue().getValue() == null) {
            qViewModel.setEndValue(-123);
            Toast.makeText(myContext, "Test Auto Submitted", Toast.LENGTH_SHORT).show();
        }

        timerValue = qViewModel.getActualTimer().getValue() - timerValue;

        ArrayList<String> selectedOptions = qViewModel.getSelectedOption().getValue();
        ArrayList<String> actualAnswer = new ArrayList<>();
        if (selectedOptions != null) {
            for (int i = 0; i < selectedOptions.size(); i++) {
                actualAnswer.add(viewModel.getQuestionsLiveData().getValue().get(i).getCorrect_option());
            }

            int count = 0;
            for (int i = 0; i < selectedOptions.size(); i++) {
                String userChoice = selectedOptions.get(i);
                String originalAnswer = actualAnswer.get(i);
                if (userChoice.equals(originalAnswer)) {
                    count++;
                }
            }

            scoreTV.setText("Score: " + count + "/" + 10);

            if (timerValue == 0) {
                int minute = (qViewModel.getActualTimer().getValue() / 1000) / 60;
                int second = (qViewModel.getActualTimer().getValue() / 1000) % 60;
                timeTakenTV.setText("time taken: " + minute + " min & " + second+" sec");
            } else {
                int minute = (timerValue / 1000) / 60;
                int second = (timerValue / 1000) % 60;
                timeTakenTV.setText("time taken: " + minute + " min & " + second+" sec");
            }
        } else {
            int minute = (timerValue / 1000) / 60;
            int second = (timerValue / 1000) % 60;
            timeTakenTV.setText("time taken: " + minute + " min & " + second+" sec");
            scoreTV.setText("Score: 0/" + 10);
        }

        restartButton.setOnClickListener(view -> {
            myContext.finish();
            startActivity(myContext.getIntent());
        });

        exitButton.setOnClickListener(view -> {
            myContext.finish();
            System.exit(0);
        });

        //handling when back button is pressed
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                myContext.finish();
                System.exit(0);

            }
        });

        return v;
    }
}

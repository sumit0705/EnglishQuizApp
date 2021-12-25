package com.example.finalassignment.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalassignment.R;
import com.example.finalassignment.viewmodel.QuestionPropertyViewModel;
import com.example.finalassignment.viewmodel.QuestionsViewModel;

import java.util.Random;

public class SetupScreenFragment extends Fragment {

    private FragmentActivity myContext;
    private QuestionPropertyViewModel qViewModel;

    TextView timer_editText;
    private int i=0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContext = (FragmentActivity)context;
        qViewModel = new ViewModelProvider(requireActivity()).get(QuestionPropertyViewModel.class);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) {
            i=savedInstanceState.getInt("timerValue");

        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_setup_screen, container, false);

        timer_editText = v.findViewById(R.id.time_limit);

        if(i != 0) {
              timer_editText.setText("Time limit is: "+i/60+":"+i%60);
        }
        else {

            // generating random number in seconds between 2-4 minute
            Random random = new Random();
            int answer = random.nextInt(240 - 120 + 1) + 120;
            i=answer;
            timer_editText.setText("Time limit is: "+i/60+" min & "+i%60+ " sec");
        }
        v.findViewById(R.id.start_button).setOnClickListener(view -> {
            showQuestionsListScreen();
        });

        return v;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("timerValue",i);
    }


    public void showQuestionsListScreen() {
        final FragmentManager fragmentManager = myContext.getSupportFragmentManager();

        final Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);

        if (fragment != null) {
            final FragmentFactory fragmentFactory = fragmentManager.getFragmentFactory();
            final String questionsScreen = QuestionsListScreenFragment.class.getName();
            final Fragment fragment1 = fragmentFactory.instantiate(myContext.getClassLoader(), questionsScreen);

            Bundle args = new Bundle();
            args.putInt("timer", i);
            if(qViewModel.getActualTimer().getValue() == null){
                qViewModel.setActualTimer(i*1000);
            }
            fragment1.setArguments(args);
            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, fragment1);
            fragmentTransaction.commit();
        }
    }
}

package com.example.finalassignment.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalassignment.R;

public class SplashScreenFragment extends Fragment {

    private FragmentActivity myContext;
    private int i=1500;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContext = (FragmentActivity) context;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState!=null) {
            i = savedInstanceState.getInt("timerValue");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_splash_screen, container, false);

        if(i!=0) {

        new CountDownTimer(i, 1000) {

                public void onTick(long millisUntilFinished) {
                    i = (int) millisUntilFinished;
                }

                public void onFinish() {
                    i=0;
                    final FragmentManager fragmentManager = myContext.getSupportFragmentManager();

                    final Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);

                    if (fragment != null) {
                        final FragmentFactory fragmentFactory = fragmentManager.getFragmentFactory();
                        final String setupScreen = SetupScreenFragment.class.getName();
                        final Fragment fragment1 = fragmentFactory.instantiate(myContext.getClassLoader(), setupScreen);

                        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentContainer, fragment1);
                        fragmentTransaction.commit();
                    }
                }

            }.start();
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("timerValue",i);
    }
}

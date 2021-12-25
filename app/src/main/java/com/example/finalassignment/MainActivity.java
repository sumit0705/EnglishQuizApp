package com.example.finalassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;

import com.example.finalassignment.ui.SplashScreenFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        final Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            final FragmentFactory fragmentFactory = fragmentManager.getFragmentFactory();
            final String splashScreen = SplashScreenFragment.class.getName();
            final Fragment fragment1 = fragmentFactory.instantiate(getClassLoader(), splashScreen);

            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragmentContainer, fragment1);
            fragmentTransaction.commit();
        }
    }

}
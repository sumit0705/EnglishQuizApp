package com.example.finalassignment.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalassignment.models.QuestionsModel;
import com.example.finalassignment.R;
import com.example.finalassignment.adaper.QuestionsListAdapter;
import com.example.finalassignment.viewmodel.QuestionPropertyViewModel;
import com.example.finalassignment.viewmodel.QuestionsViewModel;

import java.util.ArrayList;

public class QuestionsListScreenFragment extends Fragment implements QuestionsListAdapter.OnItemClickListener {

    private FragmentActivity myContext;
    private QuestionsViewModel viewModel;
    private QuestionPropertyViewModel qViewModel;
    private TextView timerTextView;
    private Button submitButton;
    private RecyclerView recyclerView;
    private QuestionsListAdapter adapter;
    private AlertDialog alertDialog;
    private ProgressDialog loadingDialog;
    private AlertDialog failureDialog;

    public int timerValue;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContext = (FragmentActivity) context;

        viewModel = new ViewModelProvider(requireActivity()).get(QuestionsViewModel.class);
        if (viewModel.getTimerValue().getValue() == -123) {
            Log.d("TAG", "in if");
            initializeViewModel();
            timerValue = getArguments().getInt("timer", -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_questions_list_screen, container, false);

        recyclerView = v.findViewById(R.id.recyclerView);
        timerTextView = v.findViewById(R.id.timer_tv);
        submitButton = v.findViewById(R.id.submit_button);

        if (viewModel.getTimerValue().getValue() == -123) {
            viewModel.changeTimerValue(timerValue * 1000);
        } else {
            setUpRecycler();
        }

        if (viewModel.getIsButtonActive().getValue()) {
            alertDialog = getAlertDialog();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }

        return v;
    }

    public void setUpRecycler() {
        qViewModel = new ViewModelProvider(requireActivity()).get(QuestionPropertyViewModel.class);

        final Fragment fragment = myContext.getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        //setting up arraylist with false values
        ArrayList<Boolean> flag = new ArrayList<>();
        for (int i = 0; i < viewModel.getQuestionsLiveData().getValue().size(); i++) {
            flag.add(false);
        }

        //setting up adapter
        if (qViewModel.getBookmarkStatus().getValue() == null) {
            if (qViewModel.getAnswerStatus().getValue() == null) {
                adapter = new QuestionsListAdapter(viewModel.getQuestionsLiveData().getValue(),flag,flag, (QuestionsListAdapter.OnItemClickListener) fragment);
            } else {
                adapter = new QuestionsListAdapter(viewModel.getQuestionsLiveData().getValue(),qViewModel.getAnswerStatus().getValue(),flag, (QuestionsListAdapter.OnItemClickListener) fragment);
            }
        } else {
            if (qViewModel.getAnswerStatus().getValue() != null) {
                adapter = new QuestionsListAdapter(viewModel.getQuestionsLiveData().getValue(),
                        qViewModel.getAnswerStatus().getValue(), qViewModel.getBookmarkStatus().getValue(), (QuestionsListAdapter.OnItemClickListener) fragment);
            } else {
                adapter = new QuestionsListAdapter(viewModel.getQuestionsLiveData().getValue(),
                        flag, qViewModel.getBookmarkStatus().getValue(), (QuestionsListAdapter.OnItemClickListener) fragment);
            }
        }
        recyclerView.setAdapter(adapter);
        timerTextView.setText("time limit: " + viewModel.getTimerValue().getValue() + " sec");

        submitButton.setOnClickListener(view -> {
            viewModel.setIsButtonActive(true);
            alertDialog = getAlertDialog();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        });

        // starting the timer
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

                    if (fragment instanceof QuestionsListScreenFragment) {
                        launchSummaryScreen();

                    }
                    viewModel.changeTimerValue(0);

                }

            }.start();
        }
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

    @Override
    public void onItemClicked(int position) {
        QuestionsModel model = viewModel.getQuestionsLiveData().getValue().get(position);

        //show questions detail screen
        final FragmentManager fragmentManager = myContext.getSupportFragmentManager();

        final Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);

        if (fragment != null) {
            final FragmentFactory fragmentFactory = fragmentManager.getFragmentFactory();
            final String detailScreen = QuestionDetailScreenFragment.class.getName();
            final Fragment fragment1 = fragmentFactory.instantiate(myContext.getClassLoader(), detailScreen);

            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, fragment1);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            qViewModel.setCurrentQuestion(model);
            qViewModel.setQuestionPosition(position);
        }

    }

    public void initializeViewModel() {
        setUpLiveData();

    }

    private void setUpLiveData() {
        viewModel.getQuestionsLiveData().observe(this,list -> setUpAdapter());
        viewModel.getRequestStatusLiveData().observe(this, requestStatus -> handleRequestStatus(requestStatus));

    }

    private void setUpAdapter() {
        setUpRecycler();

    }

    private void handleRequestStatus(QuestionsViewModel.RequestStatus requestStatus) {
        switch (requestStatus) {
            case IN_PROGRESS:
                showSpinner();
                break;
            case SUCCEEDED:
                hideSpinner();
                break;
            case FAILED:
                showError();
                break;
        }
    }

    private void showSpinner() {
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(myContext);
            loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loadingDialog.setTitle("Fetching Questions");
            loadingDialog.setMessage("Please wait...");
            loadingDialog.setIndeterminate(true);
            loadingDialog.setCancelable(false);
            loadingDialog.setCanceledOnTouchOutside(false);
        }
        loadingDialog.show();
    }

    private void hideSpinner() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    private void showError() {
        hideSpinner();
        if (failureDialog == null) {
            failureDialog = getFailureDialog();

        }
        failureDialog.show();
        failureDialog.setCanceledOnTouchOutside(false);

    }

    private AlertDialog getFailureDialog() {
        return new AlertDialog.Builder(myContext)
                .setTitle("Questions list request failed")
                .setMessage("Questions list fetching is failed, do you want to retry?")
                .setPositiveButton("Retry", (dialog, which) -> {
                    dialog.dismiss();
                    viewModel.refetchQuestions();
                })
                .setNegativeButton("Close app", (dialog, which) -> {
                    dialog.dismiss();
                    myContext.finish();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (failureDialog != null) {
            failureDialog.dismiss();
        }

        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }

        if(alertDialog!=null){
            alertDialog.dismiss();
            alertDialog=null;
        }


    }
}
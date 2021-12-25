package com.example.finalassignment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalassignment.models.QuestionsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This {@link AndroidViewModel} class contains logic to fetch Game list on initialization and post
 * the result in games live data. It also maintains {@link RequestStatus} of Game list API.
 */
public class QuestionsViewModel extends AndroidViewModel
        implements Response.Listener<String>, Response.ErrorListener {

    private static final String API_URL = "https://raw.githubusercontent.com/tVishal96/sample-english-mcqs/master/db.json";
    private static final String RESPONSE_ENTRY_KEY = "questions";
    private static final String RESPONSE_QUESTION_NAME_KEY = "question";
    private static final String RESPONSE_OPTIONS_NAME_KEY = "options";
    private static final String RESPONSE_CORRECT_OPTION_NAME_KEY = "correct_option";

    private MutableLiveData<List<QuestionsModel>> questionsLiveData = new MutableLiveData<>();
    private MutableLiveData<RequestStatus> requestStatusLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> timerValue= new MutableLiveData<>();
    private MutableLiveData<Boolean> isButtonActive= new MutableLiveData<>();

    private RequestQueue queue;



    public QuestionsViewModel(@NonNull Application application) {
        super(application);
        queue = Volley.newRequestQueue(application);
        requestStatusLiveData.postValue(RequestStatus.IN_PROGRESS);
        timerValue.setValue(-123);
        isButtonActive.setValue(false);
        fetchQuestions();
    }

    /**
     * Start re-fetching Game list from service.
     */
    public void refetchQuestions() {
        requestStatusLiveData.postValue(RequestStatus.IN_PROGRESS);
        fetchQuestions();
    }

    /**
     * @return the {@link LiveData} instance containing list of games.
     */
    public LiveData<List<QuestionsModel>> getQuestionsLiveData() {
        return questionsLiveData;
    }

    /**
     * @return the {@link LiveData} instance containing request status of Game list API.
     */
    public LiveData<RequestStatus> getRequestStatusLiveData() {
        return requestStatusLiveData;
    }

    public LiveData<Integer> getTimerValue() {
        return timerValue;
    }

    public void changeTimerValue(int timer) {
        timerValue.setValue(timer);
    }

    public LiveData<Boolean> getIsButtonActive() {
        return isButtonActive;
    }

    public void setIsButtonActive(boolean buttonState) {
        isButtonActive.setValue(buttonState);
    }

    @Override
    public void onResponse(String response) {
        try {
            List<QuestionsModel> countryList = parseResponse(response);
            questionsLiveData.postValue(countryList);
            requestStatusLiveData.postValue(RequestStatus.SUCCEEDED);
        } catch (JSONException e) {
            e.printStackTrace();
            requestStatusLiveData.postValue(RequestStatus.FAILED);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        requestStatusLiveData.postValue(RequestStatus.FAILED);

    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    private void fetchQuestions() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL, this, this);
        queue.add(stringRequest);
    }

    private List<QuestionsModel> parseResponse(String response) throws JSONException {

        List<QuestionsModel> models = new ArrayList<>();
        JSONObject res = new JSONObject(response);
        JSONArray entries = res.optJSONArray(RESPONSE_ENTRY_KEY);

        if (entries == null) {
            return models;
        }

        for (int i = 0; i < entries.length(); i++) {
            JSONObject obj = (JSONObject) entries.get(i);
            String ques = obj.optString(RESPONSE_QUESTION_NAME_KEY);
            JSONArray options = obj.optJSONArray(RESPONSE_OPTIONS_NAME_KEY);
            ArrayList<String> options_list = new ArrayList<>();
            for(int j=0;j<options.length();j++) {
                options_list.add(options.getString(j));
            }
            //converting correct_option index to its corresponding string value
            String correct_option = options_list.get(obj.optInt(RESPONSE_CORRECT_OPTION_NAME_KEY));

            //options shuffling
            Collections.shuffle(options_list);
            QuestionsModel questionsModel = new QuestionsModel(ques,options_list,correct_option);
            models.add(questionsModel);
        }

        //questions shuffling
        Collections.shuffle(models);

        // for adding serial number at the start of the question
        for(int i=0;i<models.size();i++){
            QuestionsModel mo = models.get(i);
            mo.setQuestion((i+1)+". "+mo.getQuestion());
            models.set(i,mo);
        }
        return models;
    }

    /**
     * Enum class to define status of Game list API request.
     */
    public enum RequestStatus {
        /* Show API is in progress. */
        IN_PROGRESS,

        /* Show API request is failed. */
        FAILED,

        /* Show API request is successfully completed. */
        SUCCEEDED
    }

}

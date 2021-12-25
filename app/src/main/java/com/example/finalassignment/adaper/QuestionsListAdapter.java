package com.example.finalassignment.adaper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalassignment.models.QuestionsModel;
import com.example.finalassignment.R;

import java.util.List;

public class QuestionsListAdapter extends RecyclerView.Adapter<QuestionsListAdapter.QuestionsListHolder> {

    private List<QuestionsModel> questionsList;
    private List<Boolean> answerStatus;
    private List<Boolean> bookmarkStatus;
    private OnItemClickListener mItemClickListener;

    public QuestionsListAdapter(List<QuestionsModel> questionsList,List<Boolean> answerStatus,List<Boolean> bookmarkStatus, @NonNull OnItemClickListener listener) {
        this.questionsList = questionsList;
        this.answerStatus = answerStatus;
        this.bookmarkStatus = bookmarkStatus;
        mItemClickListener = listener;
    }

    @NonNull
    @Override
    public QuestionsListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_item_view, parent, false);
        return new QuestionsListHolder(itemView, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionsListHolder holder, int position) {
        holder.bind(questionsList.get(position),answerStatus.get(position),bookmarkStatus.get(position));
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    static class QuestionsListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private OnItemClickListener mItemClickListener;
        private TextView questionTV;
        private TextView bookmarkTV;
        private TextView answeredTV;
        private Context myContext;
        public QuestionsListHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            mItemClickListener = listener;
            questionTV = itemView.findViewById(R.id.question_title);
            bookmarkTV = itemView.findViewById(R.id.bookmark_status);
            answeredTV = itemView.findViewById(R.id.answered_status);
            myContext= itemView.getContext();
            itemView.setOnClickListener(this);


        }

        public void bind(QuestionsModel questionsModel, boolean answerStatus, boolean bookmarkStatus) {
            questionTV.setText(questionsModel.getQuestion());
            answeredTV.setText("Not Answered");
            int tintColor = ContextCompat.getColor(itemView.getContext(), R.color.grey);
            if(answerStatus) {
                tintColor = ContextCompat.getColor(itemView.getContext(), R.color.blue);
                answeredTV.setText("Answered");
            }

            Drawable drawable = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_answered_status);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable.mutate(), tintColor);

            drawable.setBounds( 0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

            answeredTV.setCompoundDrawables(drawable, null, null, null);

            bookmarkTV.setText("Not Bookmarked");
            tintColor = ContextCompat.getColor(itemView.getContext(), R.color.grey);

            if(bookmarkStatus) {
                tintColor = ContextCompat.getColor(itemView.getContext(), R.color.red);
                bookmarkTV.setText("Bookmarked");
            }
            drawable = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_bookmark);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable.mutate(), tintColor);

            drawable.setBounds( 0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

            bookmarkTV.setCompoundDrawables(drawable, null, null, null);
        }


        @Override
        public void onClick(View view) {
            mItemClickListener.onItemClicked(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }

}

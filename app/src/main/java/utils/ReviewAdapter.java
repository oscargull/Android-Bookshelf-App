package utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.example.bookshelfapp.R;
import models.Review;

import java.util.List;

public class ReviewAdapter extends ArrayAdapter<Review> {
    private Context context;
    private List<Review> reviews;

    public ReviewAdapter(@NonNull Context context, List<Review> reviews) {
        super(context, 0, reviews);
        this.context=context;
        this.reviews=reviews;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Review review = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        }

        TextView tvTextoReview = convertView.findViewById(R.id.tvReviewTexto);
        TextView tvRatingReview = convertView.findViewById(R.id.tvReviewRating);

        tvTextoReview.setText(review.getText());
        if(review.getRating()==0){
            tvRatingReview.setText("");
        }else {
            tvRatingReview.setText(String.valueOf(review.getRating()) + "/5");
        }
        return convertView;
    }
}

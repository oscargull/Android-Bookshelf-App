package fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import androidx.fragment.app.DialogFragment;
import com.example.bookshelfapp.R;

public class ReviewDialogFragment extends DialogFragment {

    private EditText etReview;
    private Button btnSubmit;
    private Button btnCancelar;
    private NumberPicker npRating;

    private MiListener listener;


    public interface MiListener {
        void onDialogFragmentEvent(String reviewText, int rating);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        getDialog().setTitle("Hacer review");

        etReview = view.findViewById(R.id.etReview);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnCancelar = view.findViewById(R.id.btnCancel);
        npRating = view.findViewById(R.id.npRating);

        npRating.setMaxValue(1);
        npRating.setMaxValue(5);

        btnSubmit.setOnClickListener(v -> {
            String reviewText = etReview.getText().toString();
            if (!reviewText.isEmpty()) {
                listener.onDialogFragmentEvent(reviewText, npRating.getValue());
                dismiss(); // cierra dialogo
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MiListener) {
            listener = (MiListener) context;
        } else {
            throw new RuntimeException();
        }
    }
}

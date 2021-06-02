package android.example.walkee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class HistoryAdapter extends ArrayAdapter<HistoryItems> {
    private List<HistoryItems> dataset;
    private Context context;

    public HistoryAdapter(@NonNull Context context, @NonNull List<HistoryItems> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_history, parent, false);
        }

        HistoryItems currentHistory = getItem(position);

        TextView steps = (TextView) convertView.findViewById(R.id.steps_history);
        steps.setText(currentHistory.getSteps());
        TextView mil = (TextView) convertView.findViewById(R.id.mil_history);
        mil.setText(currentHistory.getMil());
        TextView cal = (TextView) convertView.findViewById(R.id.cal_history);
        cal.setText(currentHistory.getCal());

        return convertView;
    }
}
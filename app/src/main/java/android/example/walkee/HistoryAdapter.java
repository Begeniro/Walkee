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

        TextView day = (TextView) convertView.findViewById(R.id.day_history);
        day.setText(currentHistory.getDay());
        TextView month = (TextView) convertView.findViewById(R.id.month_history);
        month.setText(currentHistory.getMonth());
        TextView year = (TextView) convertView.findViewById(R.id.year_history);
        year.setText(currentHistory.getYear());
        TextView steps = (TextView) convertView.findViewById(R.id.steps_history);
        steps.setText(currentHistory.getSteps());
        TextView mil = (TextView) convertView.findViewById(R.id.km_history);
        mil.setText(currentHistory.getKm());
        TextView cal = (TextView) convertView.findViewById(R.id.cal_history);
        cal.setText(currentHistory.getCal());

        return convertView;
    }
}

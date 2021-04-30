package dartmouth.cs.qiyaozuo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ManualInputAdapter extends ArrayAdapter<ManualEntryModel> {
    private ArrayList<ManualEntryModel> items;
    private Context context;


    public ManualInputAdapter(@NonNull Context context, ArrayList<ManualEntryModel> items) {
        super(context, 0, items);
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ManualEntryModel entry = items.get(position);
        view = LayoutInflater.from(context).inflate(R.layout.manual_item_entry, parent, false);
        TextView title = view.findViewById(R.id.item_title);
        TextView data = view.findViewById(R.id.item_data);
        title.setText(entry.getTitle());
        data.setText(entry.getData());
        return view;

    }
}

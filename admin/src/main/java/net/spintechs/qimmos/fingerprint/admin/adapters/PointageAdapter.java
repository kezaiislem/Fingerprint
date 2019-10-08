package net.spintechs.qimmos.fingerprint.admin.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.spintechs.qimmos.fingerprint.admin.R;
import net.spintechs.qimmos.fingerprint.admin.model.Pointage;

import java.util.List;

/**
 * Created by ISLEM-PC on 5/2/2018.
 */

public class PointageAdapter extends ArrayAdapter {

    List list_Ptg;

    public PointageAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.list_Ptg = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.pointage_row, parent, false);

        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.fullName = convertView.findViewById(R.id.fullname);
            viewHolder.type = convertView.findViewById(R.id.type);
            viewHolder.time = convertView.findViewById(R.id.time);
            viewHolder.date = convertView.findViewById(R.id.date);
            convertView.setTag(viewHolder);
        }

        Pointage pointage = (Pointage) list_Ptg.get(position);
        viewHolder.fullName.setText(pointage.getUser().getFirstName()+" "+pointage.getUser().getLastName());
        viewHolder.type.setText(pointage.getType());
        viewHolder.time.setText(pointage.getTime());
        viewHolder.date.setText(pointage.getDate());

        return convertView;
    }

    public class ViewHolder {
        TextView fullName;
        TextView date;
        TextView type;
        TextView time;
    }

}

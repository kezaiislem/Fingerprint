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
import net.spintechs.qimmos.fingerprint.admin.model.User;

import java.util.List;

/**
 * Created by ISLEM-PC on 5/2/2018.
 */

public class UserAdapter extends ArrayAdapter {

    List list_Users;

    public UserAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.list_Users = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.user_row, parent, false);

        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.firtname = convertView.findViewById(R.id.firstname);
            //viewHolder.lastname = convertView.findViewById(R.id.lastname);
            viewHolder.email = convertView.findViewById(R.id.email);
            viewHolder.departemnt = convertView.findViewById(R.id.departemnt);
            convertView.setTag(viewHolder);
        }

        User user = (User) list_Users.get(position);
        viewHolder.firtname.setText(user.getFirstName()+" "+user.getLastName());
        //viewHolder.lastname.setText(user.getLastName());
        viewHolder.email.setText(user.getEmail());
        viewHolder.departemnt.setText(user.getDepartement());

        return convertView;
    }

    public class ViewHolder {
        TextView departemnt;
        TextView email;
        TextView firtname;
        //TextView lastname;
    }

}

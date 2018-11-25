package com.example.laura_000.inclass10;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class MessageAdapter extends ArrayAdapter<MessageItem> {

    String id;

    public MessageAdapter(@NonNull Context context, int resource, @NonNull List<MessageItem> objects, String id) {
        super(context, resource, objects);
        this.id = id;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        final MessageItem currentMessage = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.user = convertView.findViewById(R.id.textUserChat);
            viewHolder.message = convertView.findViewById(R.id.textMessageChat);
            viewHolder.time = convertView.findViewById(R.id.textTimeChat);
            viewHolder.delete = convertView.findViewById(R.id.imageDeleteChat);
            viewHolder.addOn = convertView.findViewById(R.id.imageAddOnChat);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.user.setText(currentMessage.getUser());
        viewHolder.time.setText(currentMessage.getDateTime());
        viewHolder.message.setText(currentMessage.getContent());

        if (currentMessage.getUserId().equals(id)) {
            viewHolder.delete.setVisibility(View.VISIBLE);
        } else {
            viewHolder.delete.setVisibility(View.GONE);
        }

        if (currentMessage.getMsgImage().equals("")) {
            viewHolder.addOn.setVisibility(View.GONE);
        }

        PrettyTime p = new PrettyTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy, hh:mm a");
        df.setTimeZone(TimeZone.getTimeZone("EST"));
        try {
            viewHolder.time.setText(p.format(df.parse(currentMessage.getDateTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Picasso.get().load(currentMessage.getMsgImage()).into(viewHolder.addOn);

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Messages").child(currentMessage.getMsgKey()).removeValue();
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView message, user, time;
        ImageView delete, addOn;
    }
}

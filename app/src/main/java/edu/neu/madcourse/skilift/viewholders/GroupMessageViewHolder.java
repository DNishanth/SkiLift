package edu.neu.madcourse.skilift.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.skilift.R;
import edu.neu.madcourse.skilift.interfaces.IGroupMessageClickListener;

public class GroupMessageViewHolder extends RecyclerView.ViewHolder {
    public TextView groupMembers;

    public GroupMessageViewHolder(View itemView,
                                  IGroupMessageClickListener messageGroupClickListener) {
        super(itemView);
        groupMembers = itemView.findViewById(R.id.group_members);
        itemView.setOnClickListener(view -> {
            int itemPosition = getLayoutPosition();
            messageGroupClickListener.onItemClick(itemPosition);
        });
    }
}

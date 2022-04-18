package edu.neu.madcourse.skilift.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.skilift.R;
import edu.neu.madcourse.skilift.interfaces.IGroupMessageClickListener;
import edu.neu.madcourse.skilift.models.GroupMessage;
import edu.neu.madcourse.skilift.viewholders.GroupMessageViewHolder;

public class GroupMessageRVAdapter extends RecyclerView.Adapter<GroupMessageViewHolder> {
    private final ArrayList<GroupMessage> groupMessageList;
    private final IGroupMessageClickListener groupMessageClickListener;

    public GroupMessageRVAdapter(ArrayList<GroupMessage> groupMessageList,
                                 IGroupMessageClickListener groupMessageClickListener) {
        this.groupMessageList = groupMessageList;
        this.groupMessageClickListener = groupMessageClickListener;
    }

    @NonNull
    @Override
    public GroupMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_message_card,
                parent, false);
        return new GroupMessageViewHolder(v, groupMessageClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMessageViewHolder holder, int position) {
        GroupMessage groupMessage = groupMessageList.get(position);
        ArrayList<String> groupMembersList = groupMessage.getGroupMembers();
        groupMembersList.sort(String.CASE_INSENSITIVE_ORDER);
        String groupMembers = groupMembersList.toString();
        String sortedGroupMembers = groupMembers.substring(1, groupMembers.length() - 1);
        holder.groupMembers.setText(sortedGroupMembers);
    }

    @Override
    public int getItemCount() {
        return groupMessageList.size();
    }
}

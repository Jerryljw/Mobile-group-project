package com.comp90018.proj2.ui.post;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.comp90018.proj2.R;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<CommentItem> commentData;

    public CommentAdapter(Context mContext, List<CommentItem> mData) {
        this.mContext = mContext;
        this.commentData = mData;
    }

    @NonNull
    @NotNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.comment_layout,parent,false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommentViewHolder holder, int position) {
        Glide.with(mContext).load(commentData.get(position).getUimg()).apply(new RequestOptions()
                .placeholder(R.drawable.ic_card_portrait)
                .fitCenter()).into(holder.img_user);
        holder.tv_name.setText(commentData.get(position).getUname());
        holder.tv_content.setText(commentData.get(position).getContent());
        Log.d("TAG", "setTimestamp: "+ commentData.get(position).getTimestamp().toString());
        holder.tv_date.setText(timestampToString((long)commentData.get(position).getTimestamp().getSeconds()));
    }

    @Override
    public int getItemCount() {
        return commentData.size();
    }



    public class CommentViewHolder extends RecyclerView.ViewHolder{
        ImageView img_user;
        TextView tv_name,tv_content,tv_date;

        public CommentViewHolder(View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.comment_headicon);
            tv_name = itemView.findViewById(R.id.comment_username);
            tv_content = itemView.findViewById(R.id.comment_text);
            tv_date = itemView.findViewById(R.id.comment_time);
        }
    }
    private String timestampToString(long time) {
        Date thisdate = new Date(time*1000);
        String string_date = DateFormat.format("dd/MM/yy hh:mm",thisdate).toString();
        return string_date;
    }
}

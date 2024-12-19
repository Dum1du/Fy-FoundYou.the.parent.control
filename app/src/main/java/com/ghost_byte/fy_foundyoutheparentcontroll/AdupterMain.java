package com.ghost_byte.fy_foundyoutheparentcontroll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdupterMain extends RecyclerView.Adapter<AdupterMain.ViewHolder> {
    private List<MainRecItemsData> userList;
    private Context context;

    public AdupterMain(List<MainRecItemsData> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recy_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainRecItemsData data = userList.get(position);
        holder.uniqueCodeTextView.setText(data.getUniqueCode());
        holder.userNameTextView.setText(data.getUserName());
        holder.bind(data, context);  // Bind data and context for click handling
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView uniqueCodeTextView;
        TextView userNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            uniqueCodeTextView = itemView.findViewById(R.id.UniqueCodeRecycleItem);
            userNameTextView = itemView.findViewById(R.id.UsernameRecycleItem);
        }

        public void bind(final MainRecItemsData data, final Context context) {
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailsWIndow.class);
                intent.putExtra("name", data.getUserName());
                intent.putExtra("uniqueCode", data.getUniqueCode());
                context.startActivity(intent);

                if (context instanceof Activity){
                    ((Activity) context).finish();
                }
            });
        }

    }
}

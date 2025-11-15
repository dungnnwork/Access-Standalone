package com.example.test_pro.ui.add_new_employee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test_pro.R;
import com.example.test_pro.model.PositionModel;

import java.util.List;

public class PositionAdapter extends RecyclerView.Adapter<PositionAdapter.PositionViewHolder> {

    private final List<PositionModel> positions;
    private final Context context;
    private int selectedPosition = -1;

    public PositionAdapter(Context context, List<PositionModel> positions) {
        this.context = context;
        this.positions = positions;
    }

    @NonNull
    @Override
    public PositionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_position, parent, false);
        return new PositionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PositionViewHolder holder, int position) {
        holder.txtPosition.setText(positions.get(position).getPosition());

        if (position == selectedPosition) {
            holder.positionContainer.setBackgroundResource(R.drawable.bg_selected_position);
            holder.txtPosition.setTextColor(context.getColor(R.color.white));
        } else {
            holder.positionContainer.setBackgroundResource(R.drawable.bg_unselected_position);
            holder.txtPosition.setTextColor(context.getColor(R.color.colorBLuePrimary));
        }

        holder.itemView.setOnClickListener(v -> {
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition == RecyclerView.NO_POSITION) return;

            int previousPosition = selectedPosition;
            selectedPosition = clickedPosition;
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
        });
    }


    @Override
    public int getItemCount() {
        return positions.size();
    }

    public static class PositionViewHolder extends RecyclerView.ViewHolder {
        TextView txtPosition;
        LinearLayout positionContainer;
        public PositionViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPosition = itemView.findViewById(R.id.txtPosition);
            positionContainer = itemView.findViewById(R.id.positionContainer);
        }
    }
}

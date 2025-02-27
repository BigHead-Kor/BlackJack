package com.foo.blackjack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private List<User> userList;

    public RankingAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_rank, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvUsername.setText(user.getUsername());
        holder.tvNetProfit.setText("순이익: $" + user.getNetProfit());
        holder.tvBankruptcies.setText("파산 횟수: " + user.getBankruptcies());
        holder.tvTotalGames.setText("총 게임 횟수: "+user.getTotalGames());
        holder.tvWinRate.setText(String.format("승률: %.2f%%", user.getWinRate()));
        holder.tvGoalAmount.setText("목표 금액: $" + user.getGoalAmount());

        // 순이익 색상 설정
        if (user.getNetProfit() >= 0) {
            holder.tvNetProfit.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvNetProfit.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_blue_dark));
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvNetProfit, tvBankruptcies, tvWinRate, tvGoalAmount,tvTotalGames;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvNetProfit = itemView.findViewById(R.id.tvNetProfit);
            tvBankruptcies = itemView.findViewById(R.id.tvBankruptcies);
            tvTotalGames = itemView.findViewById(R.id.tvTotalGames);
            tvWinRate = itemView.findViewById(R.id.tvWinRate);
            tvGoalAmount = itemView.findViewById(R.id.tvGoalAmount);
        }
    }
}

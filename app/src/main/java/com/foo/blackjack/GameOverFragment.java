package com.foo.blackjack;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class GameOverFragment extends DialogFragment {

    private String message;
    private boolean isGoalAchieved; // 목표 달성 여부 플래그
    private OnRestartListener listener;

    public interface OnRestartListener {
        void onRestart();
    }

    public GameOverFragment(String message, boolean isGoalAchieved, OnRestartListener listener) {
        this.message = message;
        this.isGoalAchieved = isGoalAchieved;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_over, container, false);

        TextView tvMessage = view.findViewById(R.id.tvGameOverMessage);
        Button btnRestart = view.findViewById(R.id.btnRestart);

        tvMessage.setText(message);

        if (isGoalAchieved) {
            btnRestart.setText("확인");
            btnRestart.setOnClickListener(v -> {
                // 목표 금액 달성 시 로그인 화면으로 이동
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                dismiss();
            });
        } else {
            btnRestart.setText("다시 시작");
            btnRestart.setOnClickListener(v -> {
                // 파산 시 게임 재시작
                listener.onRestart();
                dismiss();
            });
        }

        return view;
    }
}

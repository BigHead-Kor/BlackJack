package com.foo.blackjack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class GameFragment extends Fragment {

    private TextView tvGoalAmount, tvGameResult, tvTotalAmount, tvStats;
    private EditText etGoalAmount, etBetAmount;
    private Button btnSaveGoalAmount, btnStartGame, btnExitCasino;

    private DatabaseReference userRef;
    private String username;

    private int totalAmount, wins, losses, draws, goalAmount, bankruptcies;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        // UI 초기화
        tvGoalAmount = view.findViewById(R.id.tvGoalAmount);
        etGoalAmount = view.findViewById(R.id.etGoalAmount);
        etBetAmount = view.findViewById(R.id.etBetAmount);
        btnSaveGoalAmount = view.findViewById(R.id.btnSaveGoalAmount);
        btnStartGame = view.findViewById(R.id.btnStartGame);
        btnExitCasino = view.findViewById(R.id.btnExitCasino);
        tvGameResult = view.findViewById(R.id.tvGameResult);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        tvStats = view.findViewById(R.id.tvStats);

        // Firebase 참조
        username = getActivity().getIntent().getStringExtra("USERNAME");
        userRef = FirebaseDatabase.getInstance().getReference("users").child(username);

        checkGoalAmount();

        btnSaveGoalAmount.setOnClickListener(v -> saveGoalAmount());
        btnStartGame.setOnClickListener(v -> playGame());
        btnExitCasino.setOnClickListener(v -> showExitCasinoFragment());

        return view;
    }

    private void checkGoalAmount() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("goalAmount").exists()) {
                    // 기존 유저
                    goalAmount = snapshot.child("goalAmount").getValue(Integer.class);
                    totalAmount = snapshot.child("totalAmount").getValue(Integer.class);
                    wins = snapshot.child("wins").getValue(Integer.class);
                    losses = snapshot.child("losses").getValue(Integer.class);
                    draws = snapshot.child("draws").getValue(Integer.class);
                    bankruptcies = snapshot.child("bankruptcies").getValue(Integer.class);

                    tvGoalAmount.setText("목표 금액: $" + goalAmount);
                    tvTotalAmount.setText("현재 금액: $" + totalAmount);
                    tvStats.setText(String.format("승리: %d | 패배: %d | 무승부: %d | 파산: %d", wins, losses, draws, bankruptcies));
                    etGoalAmount.setVisibility(View.GONE);
                    btnSaveGoalAmount.setVisibility(View.GONE);
                } else {
                    // 새로운 유저
                    etGoalAmount.setVisibility(View.VISIBLE);
                    btnSaveGoalAmount.setVisibility(View.VISIBLE);
                    btnStartGame.setVisibility(View.GONE);
                    btnExitCasino.setVisibility(View.GONE);
                    etBetAmount.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void saveGoalAmount() {
        String goalInput = etGoalAmount.getText().toString().trim();
        if (!goalInput.isEmpty()) {
            int enteredGoalAmount = Integer.parseInt(goalInput);

            // 목표 금액이 1000보다 높은지 확인
            if (enteredGoalAmount <= 1000) {
                tvGameResult.setText("목표 금액은 1000보다 높아야 합니다.");
                return;
            }

            // 목표 금액 저장 및 초기화
            goalAmount = enteredGoalAmount;
            userRef.child("goalAmount").setValue(goalAmount);
            userRef.child("totalAmount").setValue(1000);
            userRef.child("wins").setValue(0);
            userRef.child("losses").setValue(0);
            userRef.child("draws").setValue(0);
            userRef.child("bankruptcies").setValue(0);

            tvGoalAmount.setText("목표 금액: $" + goalAmount);
            etGoalAmount.setVisibility(View.GONE);
            btnSaveGoalAmount.setVisibility(View.GONE);
            btnStartGame.setVisibility(View.VISIBLE);
            btnExitCasino.setVisibility(View.VISIBLE);
            etBetAmount.setVisibility(View.VISIBLE);

            totalAmount = 1000;
            wins = 0;
            losses = 0;
            draws = 0;
            bankruptcies = 0;

            tvTotalAmount.setText("현재 금액: $1000");
            tvStats.setText("승리: 0 | 패배: 0 | 무승부: 0 | 파산: 0");
        } else {
            tvGameResult.setText("목표 금액을 입력하세요.");
        }
    }

    private void playGame() {
        String betInput = etBetAmount.getText().toString().trim();
        if (betInput.isEmpty()) {
            tvGameResult.setText("배팅 금액을 입력하세요.");
            return;
        }

        int betAmount = Integer.parseInt(betInput);
        if (betAmount > totalAmount || betAmount <= 0) {
            tvGameResult.setText("올바른 배팅 금액을 입력하세요.");
            return;
        }

        double random = Math.random();
        String result;

        if (random < 0.5025) { // 딜러 승리
            losses++;
            totalAmount -= betAmount;
            result = "패배!";
        } else if (random < 0.5075) { // 무승부
            draws++;
            result = "비겼다!";
        } else { // 플레이어 승리
            wins++;
            boolean blackjack = Math.random() < 0.1;
            if (blackjack) {
                totalAmount += betAmount * 1.5;
                result = "승리! (블랙잭)";
            } else {
                totalAmount += betAmount;
                result = "승리!";
            }
        }

        tvGameResult.setText(result);
        tvTotalAmount.setText("현재 금액: $" + totalAmount);
        tvStats.setText(String.format("승리: %d | 패배: %d | 무승부: %d | 파산: %d", wins, losses, draws, bankruptcies));
        updateFirebaseData();

        if (totalAmount <= 0) {
            bankruptcies++;
            userRef.child("bankruptcies").setValue(bankruptcies);
            showGameOverFragment("당신은 파산했습니다.",false);
        }

        if (totalAmount >= goalAmount) {
            userRef.child("graduate").setValue(true);
            userRef.child("banned").setValue(true); // 졸업 시 banned도 true로 설정
            showGameOverFragment("축하합니다! 목표 금액을 달성했습니다.(목표 금액 달성으로 영구제명 됩니다)", true); // 목표 달성 시 isGoalAchieved = true
        }
    }

    private void updateFirebaseData() {
        userRef.child("totalAmount").setValue(totalAmount);
        userRef.child("wins").setValue(wins);
        userRef.child("losses").setValue(losses);
        userRef.child("draws").setValue(draws);
        userRef.child("bankruptcies").setValue(bankruptcies);
    }

    private void showGameOverFragment(String message, boolean isGoalAchieved) {
        GameOverFragment fragment = new GameOverFragment(message, isGoalAchieved, this::resetGame);
        fragment.show(getParentFragmentManager(), "gameOver");
    }

    private void showExitCasinoFragment() {
        ExitCasinoFragment fragment = new ExitCasinoFragment(username);
        fragment.show(getParentFragmentManager(), "exitCasino");
    }

    private void resetGame() {
        totalAmount = 1000;
        wins = 0;
        losses = 0;
        draws = 0;

        updateFirebaseData();

        tvTotalAmount.setText("현재 금액: $1000");
        tvStats.setText("승리: 0 | 패배: 0 | 무승부: 0 | 파산: "+bankruptcies);
        tvGameResult.setText("");
    }
}

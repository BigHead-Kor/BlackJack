package com.foo.blackjack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private Button btnStartGame, btnViewRanking;
    private DatabaseReference database;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase 데이터베이스 참조
        database = FirebaseDatabase.getInstance().getReference("users");

        // UI 요소 초기화
        etUsername = findViewById(R.id.etUsername);
        btnStartGame = findViewById(R.id.btnStartGame);
        btnViewRanking = findViewById(R.id.btnViewRanking);

        btnStartGame.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(LoginActivity.this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
            } else {
                checkUserStatus(username);
            }
        });

        btnViewRanking.setOnClickListener(view -> {
            openRankingFragment();
        });
    }

    private void checkUserStatus(String username) {
        DatabaseReference userRef = database.child(username);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean isBanned = snapshot.child("banned").getValue(Boolean.class);
                    if (Boolean.TRUE.equals(isBanned)) {
                        showBannedFragment();
                    } else {
                        Toast.makeText(LoginActivity.this, "기존 사용자로 로그인합니다.", Toast.LENGTH_SHORT).show();
                        goToMainActivity(username);
                    }
                } else {
                    saveUserToFirebase(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "데이터베이스 오류: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserToFirebase(String username) {
        DatabaseReference userRef = database.child(username);

        userRef.child("totalAmount").setValue(1000);
        userRef.child("wins").setValue(0);
        userRef.child("losses").setValue(0);
        userRef.child("draws").setValue(0);
        userRef.child("bankruptcies").setValue(0);
        userRef.child("banned").setValue(false)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(LoginActivity.this, "새 사용자 데이터가 저장되었습니다", Toast.LENGTH_SHORT).show();
                    goToMainActivity(username);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "데이터 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Firebase 데이터 저장 실패", e);
                });
    }

    private void goToMainActivity(String username) {
        Intent intent = new Intent( LoginActivity.this, MainActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    private void showBannedFragment() {
        BannedFragment bannedFragment = new BannedFragment();
        bannedFragment.show(getSupportFragmentManager(), "banned");
    }

    private void openRankingFragment() {
        // 로그인 레이아웃 숨기기
        findViewById(R.id.loginLayout).setVisibility(View.GONE);
        // FragmentContainerView 보이기
        findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);

        // 프래그먼트 표시
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, new RankingFragment());
        transaction.addToBackStack(null);
        transaction.commit();

        // 백스택 변경 리스너 추가
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            // 만약 백스택이 비어 있으면, 로그인 레이아웃을 다시 표시
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                findViewById(R.id.loginLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
            }
        });
    }


}

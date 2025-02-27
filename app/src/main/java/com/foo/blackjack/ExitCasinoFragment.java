package com.foo.blackjack;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ExitCasinoFragment extends DialogFragment {

    private String username;
    private DatabaseReference userRef;

    public ExitCasinoFragment(String username) {
        this.username = username;
        userRef = FirebaseDatabase.getInstance().getReference("users").child(username);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exit_casino, container, false);

        TextView tvExitMessage = view.findViewById(R.id.tvExitMessage);
        Button btnConfirmExit = view.findViewById(R.id.btnConfirmExit);
        Button btnCancelExit = view.findViewById(R.id.btnCancelExit);

        // 경고 메시지 설정
        tvExitMessage.setText("이 버튼을 누르면 더 이상 이 계정으로 블랙잭을 진행할 수 없습니다.\n영구 추방 당하시겠습니까?\n(결과는 랭킹 화면에 기록됩니다)");

        // "예" 버튼 클릭 이벤트
        btnConfirmExit.setOnClickListener(v -> exitCasino());

        // "아니오" 버튼 클릭 이벤트
        btnCancelExit.setOnClickListener(v -> dismiss());

        return view;
    }

    private void exitCasino() {
        // 계정을 비활성화하고 랭킹에 기록
        userRef.child("active").setValue(false);
        userRef.child("banned").setValue(true);  // 추방된 상태 기록

        // 메인 화면으로 이동
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        dismiss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false); // 다이얼로그 바깥을 눌러도 닫히지 않도록 설정
        return dialog;
    }
}

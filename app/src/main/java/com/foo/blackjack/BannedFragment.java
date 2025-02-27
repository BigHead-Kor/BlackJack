package com.foo.blackjack;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class BannedFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_banned, container, false);

        TextView tvBannedMessage = view.findViewById(R.id.tvBannedMessage);
        Button btnDismiss = view.findViewById(R.id.btnDismiss);

        tvBannedMessage.setText("이 계정은 더 이상 카지노에 접근할 수 없습니다.");

        btnDismiss.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false); // 다이얼로그 바깥을 눌러도 닫히지 않도록 설정
        return dialog;
    }
}

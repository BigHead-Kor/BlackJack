package com.foo.blackjack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RankingFragment extends Fragment {

    private RecyclerView recyclerView;
    private RankingAdapter rankingAdapter;
    private List<User> userList;
    private DatabaseReference databaseRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        rankingAdapter = new RankingAdapter(userList);
        recyclerView.setAdapter(rankingAdapter);

        databaseRef = FirebaseDatabase.getInstance().getReference("users");
        fetchRankingData();

        return view;
    }

    private void fetchRankingData() {
        databaseRef.orderByChild("banned").equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String username = userSnapshot.getKey();
                    int totalAmount = userSnapshot.child("totalAmount").getValue(Integer.class);
                    int bankruptcies = userSnapshot.child("bankruptcies").getValue(Integer.class);
                    int wins = userSnapshot.child("wins").getValue(Integer.class);
                    int losses = userSnapshot.child("losses").getValue(Integer.class);
                    int draws = userSnapshot.child("draws").getValue(Integer.class);
                    int goalAmount = userSnapshot.child("goalAmount").getValue(Integer.class);
                    boolean graduate = userSnapshot.child("graduate").getValue(Boolean.class) != null &&
                            userSnapshot.child("graduate").getValue(Boolean.class);

                    // 순이익 계산
                    int netProfit = graduate ? -1000+(totalAmount - 1000 - bankruptcies * 1000) : -1000+(totalAmount -bankruptcies * 1000);



                    // 승률 계산
                    int totalGames = wins + losses + draws;
                    double winRate = totalGames > 0 ? (wins / (double) totalGames) * 100 : 0;

                    userList.add(new User(username, netProfit, bankruptcies, winRate, goalAmount,totalGames));
                }

                // 순이익 기준으로 내림차순 정렬
                Collections.sort(userList, (u1, u2) -> Integer.compare(u2.getNetProfit(), u1.getNetProfit()));

                rankingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "데이터를 불러오지 못했습니다: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

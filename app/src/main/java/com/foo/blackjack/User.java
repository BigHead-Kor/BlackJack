package com.foo.blackjack;

public class User {
    private String username;
    private int netProfit;
    private int bankruptcies;
    private double winRate;
    private int totalGames;
    private int goalAmount;

    public User(String username, int netProfit, int bankruptcies, double winRate, int goalAmount,int totalGames) {
        this.username = username;
        this.netProfit = netProfit;
        this.bankruptcies = bankruptcies;
        this.winRate = winRate;
        this.goalAmount = goalAmount;
        this.totalGames = totalGames;
    }

    public String getUsername() { return username; }
    public int getNetProfit() { return netProfit; }
    public int getBankruptcies() { return bankruptcies; }
    public double getWinRate() { return winRate; }
    public int getGoalAmount() { return goalAmount; }
    public int getTotalGames() { return totalGames;}
}

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;



interface Scorable {
    void addRuns(int runs);
    void addBallFaced();
    void addWicket();
    void addover();
    void addRunsgiven(int runs);
}

// Abstract class for match
abstract class CricketMatch {
    protected Team team1;
    protected Team team2;
    protected String stadium;
    protected Team tossWinner;

    public CricketMatch(Team team1, Team team2, String stadium) {
        this.team1 = team1;
        this.team2 = team2;
        this.stadium = stadium;
    }

    public abstract void conductToss();
    public abstract void startMatch();
}

// Player class
class Player implements Scorable {
    private String name;
    private int runs;
    private int ballsFaced;
    private int wickets;
    private int Runsgiven;
    private int over;
    public Player(String name) {
        this.name = name;
        this.runs = 0;
        this.ballsFaced = 0;
        this.wickets = 0;
        this.Runsgiven=0;
        this.over=0;
    }

    public String getName() {
        return name;
    }

    @Override
    public void addRuns(int runs) {
        this.runs += runs;
    }
    @Override
    public void addRunsgiven(int runs) {
        Runsgiven += runs;
    }

    @Override
    public void addBallFaced() {
        this.ballsFaced++;
    }

    @Override
    public void addWicket() {
        this.wickets++;
    }
    @Override
    public void addover() {
        this.over++;
    }
    public int getover(){
        return over;
    }
    public int getRuns() {
        return runs;
    }
    public int getRunsgiven() {
        return Runsgiven;
    }
    public int getBallsFaced() {
        return ballsFaced;
    }

    public int getWickets() {
        return wickets;
    }

    public double getStrikeRate() {
        return ballsFaced == 0 ? 0 : (runs / (double) ballsFaced) * 100;
    }
}

// Team class
class Team  {
    private String name;
    private List<Player> players;
    private int totalRuns;
    private int wicketsLost;
    
    public Team(String name) {
        this.name = name;
        this.players = new ArrayList<>();
        this.totalRuns = 0;
        this.wicketsLost = 0;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return players;
    }
    
    public void addRuns(int runs) {
        totalRuns += runs;
    }
    
    public void addWicket() {
        wicketsLost++;
    }
    
    public int getTotalRuns() {
        return totalRuns;
    }
    
    public int getWicketsLost() {
        return wicketsLost;
    }

    
}

// Match implementation
class CricketScorecard extends CricketMatch {
    private Team battingTeam;
    private Team bowlingTeam;

    public CricketScorecard(Team team1, Team team2, String stadium) {
        super(team1, team2, stadium);
    }

    @Override
    public void conductToss() {
        Random random = new Random();
        tossWinner = random.nextBoolean() ? team1 : team2;
        boolean choice=random.nextBoolean();
        if(choice){
            battingTeam = tossWinner;
        bowlingTeam = tossWinner == team1 ? team2 : team1;
        System.out.println(tossWinner.getName() + " won the toss and will bat first.");
        }
        else{
            battingTeam = tossWinner == team1 ? team2 : team1;
        bowlingTeam = tossWinner;
        System.out.println(tossWinner.getName() + " won the toss and will bowl first.");
        }
        
    }

    @Override
    public void startMatch() {
        Scanner scanner = new Scanner(System.in);

        // Batting order setup
        List<Player> players = battingTeam.getPlayers();
        List<Player> bowlers = bowlingTeam.getPlayers();
        
        Player striker = players.get(0);
        Player nonStriker = players.get(1);
        int nextBatsmanIndex = 2;

        // Conduct 6 overs
        for (int over = 0; over < 6; over++) {
            Player bowler=bowlers.get(3+over%3);
            bowler.addover();
            System.out.println("Over " + (over+1));
            for (int ball = 1; ball <= 6; ball++) {
                System.out.print("Enter runs for ball " + ball + " (7 for wicket,8 for wide,and negative for runs on a no-ball): ");
                int runs = scanner.nextInt();

                if (runs == 7) { // Wicket falls
                    striker.addBallFaced();
                    battingTeam.addWicket();
                    bowler.addWicket();
                    if (battingTeam.getWicketsLost() >= 5 || nextBatsmanIndex >= players.size()) {
                        System.out.println("All out!");
                        over=7;break;
                    }
                    striker = players.get(nextBatsmanIndex++); // New batsman takes the striker position
                }
                else if(runs==8){
                    battingTeam.addRuns(1);bowler.addRunsgiven(1);ball--;
                }

                else if(runs<0)
                {
                    striker.addRuns((runs)*(-1)-1);battingTeam.addRuns((runs)*(-1));bowler.addRunsgiven((runs)*(-1));ball--;
                    if (runs-1 % 2 != 0) {
                        Player temp = striker;
                        striker = nonStriker;
                        nonStriker = temp;
                    }
                } 
                else if(runs>8 || runs<-7) {System.out.println("Invalid Entry");ball--;}
                else {
                    striker.addRuns(runs);
                    striker.addBallFaced();
                    battingTeam.addRuns(runs);
                    bowler.addRunsgiven(runs);
                    // Rotate strike on odd runs
                    if (runs % 2 != 0) {
                        Player temp = striker;
                        striker = nonStriker;
                        nonStriker = temp;
                    }
                }
                System.out.println(battingTeam.getTotalRuns()+"/"+battingTeam.getWicketsLost());
            }

            // Rotate strike after each over
            Player temp = striker;
            striker = nonStriker;
            nonStriker = temp;
            
        }
         List<Player> players1 =bowlingTeam.getPlayers();
          List<Player> bowlers1 =battingTeam.getPlayers();
         striker = players1.get(0);
         nonStriker = players1.get(1);
         nextBatsmanIndex = 2;

        // Conduct 6 overs
        for (int over = 0; over < 6; over++) {
            Player bowler=bowlers1.get(3+over%3);
            System.out.println("Over " + (over+1));
            bowler.addover();
            for (int ball = 1; ball <= 6; ball++) {
                System.out.print("Enter runs for ball " + ball + " (7 for wicket,8 for wide,and negative for runs on a no-ball): ");
                int runs = scanner.nextInt();

                if (runs == 7) { // Wicket falls
                    bowlingTeam.addWicket();
                    striker.addBallFaced();
                    bowler.addWicket();
                    if (bowlingTeam.getWicketsLost() >= 5 || nextBatsmanIndex >= players1.size()) {
                        System.out.println("All out!");
                        return;
                    }
                    striker = players1.get(nextBatsmanIndex++); // New batsman takes the striker position
                } 
                else if(runs==8){
                    bowlingTeam.addRuns(1);bowler.addRunsgiven(1);ball--;
                }
                else if(runs<0)
                {
                    striker.addRuns((runs)*(-1)-1);bowlingTeam.addRuns((runs)*(-1));bowler.addRunsgiven((runs)*(-1));ball--;
                    if (runs-1 % 2 != 0) {
                        Player temp = striker;
                        striker = nonStriker;
                        nonStriker = temp;
                    }
                } 
                else if(runs>8 || runs<-7) {System.out.println("Invalid Entry");ball--;}
                else {
                    striker.addRuns(runs);
                    striker.addBallFaced();
                    bowlingTeam.addRuns(runs);
                    bowler.addRunsgiven(runs);
                    // Rotate strike on odd runs
                    if (runs % 2 != 0) {
                        Player temp = striker;
                        striker = nonStriker;
                        nonStriker = temp;
                    }
                }
                System.out.println(bowlingTeam.getTotalRuns()+"/"+bowlingTeam.getWicketsLost());
                if(battingTeam.getTotalRuns() < bowlingTeam.getTotalRuns()) return;
            }

            // Rotate strike after each over
            Player temp = striker;
            striker = nonStriker;
            nonStriker = temp;
            
        }
    }
    private int max(int a,int b){
            if(a>=b) return a;
            else return b;
        }
    // File handling for scorecard
    public void writeScorecardToFile() {
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("scorecard.txt"))) {
            writer.write("Stadium: " + stadium + "\n");
            writer.write("Toss Winner: " + tossWinner.getName() + "\n");
            writer.write("\n---Innings 1 ---\n");
             for (Player player : battingTeam.getPlayers()) {
                writer.write(player.getName() + " - Runs: " + player.getRuns() + ", Balls Faced: " + player.getBallsFaced() + ", SR: "+player.getStrikeRate()+"\n");
            }
            writer.write("\n--------------\n");
            for(Player player : bowlingTeam.getPlayers()){
                writer.write(player.getName() + " Over: " + player.getover() + ", Runs: " + player.getRunsgiven() +",Economy: "+player.getRunsgiven()/max(1,player.getover()) + ",Wickets: "+player.getWickets() +"\n");
            }
            writer.write("\n--- Innings 2 ---\n");
             for (Player player : bowlingTeam.getPlayers()) {
                writer.write(player.getName() + " - Runs: " + player.getRuns() + ", Balls Faced: " + player.getBallsFaced() +", SR: "+player.getStrikeRate()+ "\n");
            }
             writer.write("\n--------------\n");
             for(Player player : battingTeam.getPlayers()){
                writer.write(player.getName() + " Over: " + player.getover() + ", Runs: " + player.getRunsgiven() +",Economy: "+player.getRunsgiven()/max(1,player.getover()) + ",Wickets: "+player.getWickets() +"\n");
            }
            if(battingTeam.getTotalRuns() == bowlingTeam.getTotalRuns()){
                writer.write("\nMatch Tied");
            }
            else 
            {writer.write("\nWinner: " + (battingTeam.getTotalRuns() > bowlingTeam.getTotalRuns() ? battingTeam.getName() : bowlingTeam.getName()));}
            System.out.println("Scorecard saved to scorecard.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Main Application
public class bce329 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Team 1 name: ");
        Team team1 = new Team(scanner.nextLine());
        System.out.print("Enter Team 2 name: ");
        Team team2 = new Team(scanner.nextLine());

        System.out.println("Enter 6 players for Team 1:");
        for (int i = 0; i < 6; i++) team1.addPlayer(new Player(scanner.nextLine()));

        System.out.println("Enter 6 players for Team 2:");
        for (int i = 0; i < 6; i++) team2.addPlayer(new Player(scanner.nextLine()));

        System.out.print("Enter stadium name: ");
        String stadium = scanner.nextLine();

        CricketScorecard match = new CricketScorecard(team1, team2, stadium);
        match.conductToss();
        match.startMatch();
        match.writeScorecardToFile();
    }
}
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.image.BufferedImage;

public class FootballPointsTable extends JFrame {
    private JTextField team1Field, team2Field, team1GoalsField, team2GoalsField;
    private JTextField goalScorerField1, assistProviderField1, goalScorerField2, assistProviderField2;
    private JTable pointsTable, matchDetailsTable;
    private DefaultTableModel tableModel, matchDetailsModel;
    private HashMap<String, Team> teams;
    private HashMap<String, Player> players; // To track player goals and assists
    private ArrayList<String> matchHistory; // To store the match history
    private String finalist1, finalist2; // To store the names of the finalists

    // Load the background image
    private BufferedImage backgroundImage;

    public FootballPointsTable() {
        try {
            backgroundImage = ImageIO.read(new File("Football.jpg")); // Load your background image here
            Image icon = ImageIO.read(new File("icon.jpg")); // Add your icon image here
            setIconImage(icon); // Set the icon for the JFrame
        } catch (IOException e) {
            e.printStackTrace();
        }

        teams = new HashMap<>();
        players = new HashMap<>();
        matchHistory = new ArrayList<>(); // Initialize match history

        setTitle("Football Points Table");
        setLayout(new BorderLayout());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        // Team 1 input fields
        JPanel team1Panel = new JPanel();
        team1Panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        team1Panel.add(new JLabel("Team 1:"));
        team1Field = new JTextField(10);
        team1Panel.add(team1Field);
        team1Panel.add(new JLabel("Goals:"));
        team1GoalsField = new JTextField(5);
        team1Panel.add(team1GoalsField);
        team1Panel.add(new JLabel("Goal Scorers (comma-separated):"));
        goalScorerField1 = new JTextField(20);
        team1Panel.add(goalScorerField1);
        team1Panel.add(new JLabel("Assist Providers (comma-separated):"));
        assistProviderField1 = new JTextField(20);
        team1Panel.add(assistProviderField1);

        // Team 2 input fields
        JPanel team2Panel = new JPanel();
        team2Panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        team2Panel.add(new JLabel("Team 2:"));
        team2Field = new JTextField(10);
        team2Panel.add(team2Field);
        team2Panel.add(new JLabel("Goals:"));
        team2GoalsField = new JTextField(5);
        team2Panel.add(team2GoalsField);
        team2Panel.add(new JLabel("Goal Scorers (comma-separated):"));
        goalScorerField2 = new JTextField(20);
        team2Panel.add(goalScorerField2);
        team2Panel.add(new JLabel("Assist Providers (comma-separated):"));
        assistProviderField2 = new JTextField(20);
        team2Panel.add(assistProviderField2);

        // Add panels to inputPanel
        inputPanel.add(team1Panel);
        inputPanel.add(team2Panel);

        // Add the input panel to the frame at the top
        add(inputPanel, BorderLayout.NORTH);

        // Points table with background
        String[] columnNames = {"Rank", "Team", "Played", "Wins", "Draws", "Losses", "Goals For", "Goals Against", "Goal Diff", "Points"};
        tableModel = new DefaultTableModel(columnNames, 0);
        pointsTable = new JTable(tableModel);

        // Ensure the table is visible with an opaque background
        pointsTable.setOpaque(true);
        pointsTable.setBackground(new Color(0, 0, 0, 150)); // Semi-transparent background for better visibility
        pointsTable.setForeground(Color.WHITE); // Set table text color to white

        // Add the points table inside a custom panel with background
        JScrollPane pointsScrollPane = new JScrollPane(pointsTable);
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.add(pointsScrollPane, BorderLayout.CENTER);
        pointsScrollPane.setOpaque(false);
        pointsScrollPane.getViewport().setOpaque(false);

        add(backgroundPanel, BorderLayout.CENTER);

        // Add some sample data to make sure the table displays something initially
        tableModel.addRow(new Object[]{1, "CSE19", 0, 0, 0, 0, 0, 0, 0, 0});
        tableModel.addRow(new Object[]{2, "CSE20", 0, 0, 0, 0, 0, 0, 0, 0});
        tableModel.addRow(new Object[]{3, "CSE21", 0, 0, 0, 0, 0, 0, 0, 0});
        tableModel.addRow(new Object[]{4, "CSE22", 0, 0, 0, 0, 0, 0, 0, 0});
        tableModel.addRow(new Object[]{5, "CSE23", 0, 0, 0, 0, 0, 0, 0, 0});

        // Match details table
        String[] matchColumnNames = {"Team 1", "Goals", "Goal Scorer(s)", "Assist Provider(s)", "Team 2", "Goals", "Goal Scorer(s)", "Assist Provider(s)"};
        matchDetailsModel = new DefaultTableModel(matchColumnNames, 0);
        matchDetailsTable = new JTable(matchDetailsModel);
        add(new JScrollPane(matchDetailsTable), BorderLayout.SOUTH);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addResultButton = new JButton("Add Result");
        JButton saveButton = new JButton("Save to File");
        JButton loadButton = new JButton("Load from File");
        JButton showFinalistsButton = new JButton("Show Finalists");
        JButton showMatchHistoryButton = new JButton("Show Match History");
        JButton showPlayerRankingsButton = new JButton("Show Player Rankings"); // Button to show player rankings
        JButton showMapButton = new JButton("Show Final Map"); // New button to show the map of finalists
        JButton savePlayerDataButton = new JButton("Save Player Data"); // Button for saving player data
        JButton loadPlayerDataButton = new JButton("Load Player Data"); // Button for loading player data

        buttonPanel.add(addResultButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(showFinalistsButton);
        buttonPanel.add(showMatchHistoryButton);
        buttonPanel.add(showPlayerRankingsButton);
        buttonPanel.add(showMapButton);
        buttonPanel.add(savePlayerDataButton);
        buttonPanel.add(loadPlayerDataButton);

        // Add button panel to the bottom
        add(buttonPanel, BorderLayout.SOUTH);

        // Button listeners
        addResultButton.addActionListener(e -> addMatchResult());
        saveButton.addActionListener(e -> saveToFile());
        loadButton.addActionListener(e -> loadFromFile());
        showFinalistsButton.addActionListener(e -> showFinalists());
        showMatchHistoryButton.addActionListener(e -> showMatchHistory());
        showPlayerRankingsButton.addActionListener(e -> showPlayerRankings());
        showMapButton.addActionListener(e -> showFinalsMap());
        savePlayerDataButton.addActionListener(e -> savePlayerDataToFile()); // Save player data
        loadPlayerDataButton.addActionListener(e -> loadPlayerDataFromFile()); // Load player data

        setVisible(true);
    }

    // Background panel class for points table
    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    private void addMatchResult() {
        String team1 = team1Field.getText();
        String team2 = team2Field.getText();
        String goalScorers1 = goalScorerField1.getText();
        String assistProviders1 = assistProviderField1.getText();
        String goalScorers2 = goalScorerField2.getText();
        String assistProviders2 = assistProviderField2.getText();
        int team1Goals, team2Goals;

        try {
            team1Goals = Integer.parseInt(team1GoalsField.getText());
            team2Goals = Integer.parseInt(team2GoalsField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid goals.");
            return;
        }

        if (team1.isEmpty() || team2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both team names.");
            return;
        }

        // Process goal scorers and assist providers for Team 1
        processPlayerStats(goalScorers1, assistProviders1, team1, "Goal");
        processPlayerStats(goalScorers1, assistProviders1, team1, "Assist");

        // Process goal scorers and assist providers for Team 2
        processPlayerStats(goalScorers2, assistProviders2, team2, "Goal");
        processPlayerStats(goalScorers2, assistProviders2, team2, "Assist");

        // Add match to match history
        matchHistory.add(team1 + " " + team1Goals + " - " + team2Goals + " " + team2 + " | Goal Scorer(s) 1: " + goalScorers1 + ", Assist(s) 1: " + assistProviders1 +
                " | Goal Scorer(s) 2: " + goalScorers2 + ", Assist(s) 2: " + assistProviders2);

        // Add to match details table
        matchDetailsModel.addRow(new Object[]{team1, team1Goals, goalScorers1, assistProviders1, team2, team2Goals, goalScorers2, assistProviders2});

        // Update teams
        updateTeamStats(team1, team1Goals, team2, team2Goals);
        updateTable();
    }

    private void processPlayerStats(String scorers, String assists, String teamName, String type) {
        if (!scorers.isEmpty()) {
            String[] scorerNames = scorers.split(",");
            for (String scorer : scorerNames) {
                scorer = scorer.trim();
                players.putIfAbsent(scorer, new Player(scorer, teamName));
                if (type.equals("Goal")) {
                    players.get(scorer).addGoal();
                }
            }
        }

        if (!assists.isEmpty()) {
            String[] assistNames = assists.split(",");
            for (String assist : assistNames) {
                assist = assist.trim();
                players.putIfAbsent(assist, new Player(assist, teamName));
                if (type.equals("Assist")) {
                    players.get(assist).addAssist();
                }
            }
        }
    }

    private void updateTeamStats(String team1, int team1Goals, String team2, int team2Goals) {
        boolean isAway = true;

        teams.putIfAbsent(team1, new Team(team1));
        teams.putIfAbsent(team2, new Team(team2));

        teams.get(team1).updateStats(team1Goals, team2Goals, false);
        teams.get(team2).updateStats(team2Goals, team1Goals, isAway);

        // Update head-to-head
        if (team1Goals > team2Goals) {
            teams.get(team1).updateHeadToHead(team2, 3, 0);
            teams.get(team2).updateHeadToHead(team1, 0, team2Goals);
        } else if (team1Goals == team2Goals) {
            teams.get(team1).updateHeadToHead(team2, 1, 0);
            teams.get(team2).updateHeadToHead(team1, 1, team2Goals);
        } else {
            teams.get(team1).updateHeadToHead(team2, 0, 0);
            teams.get(team2).updateHeadToHead(team1, 3, team2Goals);
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0); // Clear the table

        // Sort teams based on the ranking rules
        ArrayList<Team> sortedTeams = new ArrayList<>(teams.values());
        Collections.sort(sortedTeams);

        // Update table with ranked teams
        int rank = 1;
        for (Team team : sortedTeams) {
            tableModel.addRow(team.getRow(rank));
            rank++;
        }
    }

    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("points_table.txt"))) {
            for (Team team : teams.values()) {
                writer.printf("%s,%d,%d,%d,%d,%d,%d,%d,%d%n",
                        team.name, team.played, team.wins, team.draws, team.losses,
                        team.goalsFor, team.goalsAgainst, (team.goalsFor - team.goalsAgainst), team.points);
            }
            JOptionPane.showMessageDialog(this, "Table saved to points_table.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromFile() {
        try (Scanner scanner = new Scanner(new File("points_table.txt"))) {
            teams.clear();
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");
                String name = data[0];
                int played = Integer.parseInt(data[1]);
                int wins = Integer.parseInt(data[2]);
                int draws = Integer.parseInt(data[3]);
                int losses = Integer.parseInt(data[4]);
                int goalsFor = Integer.parseInt(data[5]);
                int goalsAgainst = Integer.parseInt(data[6]);
                int points = Integer.parseInt(data[8]);

                Team team = new Team(name);
                team.played = played;
                team.wins = wins;
                team.draws = draws;
                team.losses = losses;
                team.goalsFor = goalsFor;
                team.goalsAgainst = goalsAgainst;
                team.points = points;

                teams.put(name, team);
            }
            updateTable();
            JOptionPane.showMessageDialog(this, "Table loaded from points_table.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showFinalists() {
        if (teams.size() < 2) {
            JOptionPane.showMessageDialog(this, "Not enough teams to determine finalists.");
            return;
        }

        // Sort teams based on points
        ArrayList<Team> sortedTeams = new ArrayList<>(teams.values());
        Collections.sort(sortedTeams);

        // Get the top 2 teams
        finalist1 = sortedTeams.get(0).name;
        finalist2 = sortedTeams.get(1).name;

        // Display the finalists
        JOptionPane.showMessageDialog(this, "The finalists are:\n1. " + finalist1 + "\n2. " + finalist2, "Finalists", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMatchHistory() {
        if (matchHistory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No matches have been played yet.");
            return;
        }

        StringBuilder history = new StringBuilder();
        for (String match : matchHistory) {
            history.append(match).append("\n");
        }

        JOptionPane.showMessageDialog(this, history.toString(), "Match History", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showPlayerRankings() {
        if (players.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No players have scored or assisted yet.");
            return;
        }

        // Create table to display player rankings
        String[] playerColumns = {"Rank", "Player", "Team", "Goals", "Assists"};
        DefaultTableModel playerModel = new DefaultTableModel(playerColumns, 0);
        JTable playerTable = new JTable(playerModel);

        // Sort players by goals first, then by assists
        ArrayList<Player> sortedPlayers = new ArrayList<>(players.values());
        sortedPlayers.sort((p1, p2) -> {
            if (p2.goals != p1.goals) {
                return Integer.compare(p2.goals, p1.goals);
            } else {
                return Integer.compare(p2.assists, p1.assists);
            }
        });

        // Add players to the table
        int rank = 1;
        for (Player player : sortedPlayers) {
            playerModel.addRow(player.getRow(rank));
            rank++;
        }

        // Show the table in a new window
        JOptionPane.showMessageDialog(this, new JScrollPane(playerTable), "Player Rankings", JOptionPane.INFORMATION_MESSAGE);
    }

    // New method to show the map/diagram of the two finalists
    private void showFinalsMap() {
        if (finalist1 == null || finalist2 == null) {
            JOptionPane.showMessageDialog(this, "Please determine finalists first.");
            return;
        }

        JFrame mapFrame = new JFrame("Final Match Map");
        mapFrame.setSize(400, 300);
        mapFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setFont(new Font("Serif", Font.BOLD, 24));

                // Draw the finalists
                g.drawString(finalist1, 150, 100);
                g.drawString("VS", 180, 140);
                g.drawString(finalist2, 150, 180);
            }
        };

        mapFrame.add(mapPanel);
        mapFrame.setVisible(true);
    }

    // Method to save player data to a file
    private void savePlayerDataToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("players_data.txt"))) {
            for (Player player : players.values()) {
                writer.printf("%s,%s,%d,%d%n", player.name, player.teamName, player.goals, player.assists);
            }
            JOptionPane.showMessageDialog(this, "Player data saved to players_data.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load player data from a file
    private void loadPlayerDataFromFile() {
        try (Scanner scanner = new Scanner(new File("players_data.txt"))) {
            players.clear();
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");
                String name = data[0];
                String teamName = data[1];
                int goals = Integer.parseInt(data[2]);
                int assists = Integer.parseInt(data[3]);

                Player player = new Player(name, teamName);
                player.goals = goals;
                player.assists = assists;
                players.put(name, player);
            }
            JOptionPane.showMessageDialog(this, "Player data loaded from players_data.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Player class to track goals, assists, and team name
    class Player {
        String name;
        int goals;
        int assists;
        String teamName;

        public Player(String name, String teamName) {
            this.name = name;
            this.goals = 0;
            this.assists = 0;
            this.teamName = teamName;
        }

        public void addGoal() {
            this.goals++;
        }

        public void addAssist() {
            this.assists++;
        }

        public Object[] getRow(int rank) {
            return new Object[]{rank, name, teamName, goals, assists};
        }
    }

    // Team class to store information
    class Team implements Comparable<Team> {
        String name;
        int played, wins, draws, losses, goalsFor, goalsAgainst, points, awayGoals;
        HashMap<String, Integer[]> headToHead; // Tracks head-to-head (points, away goals)

        public Team(String name) {
            this.name = name;
            this.played = 0;
            this.wins = 0;
            this.draws = 0;
            this.losses = 0;
            this.goalsFor = 0;
            this.goalsAgainst = 0;
            this.points = 0;
            this.awayGoals = 0;
            this.headToHead = new HashMap<>();
        }

        // Update the team stats based on match result
        public void updateStats(int goalsFor, int goalsAgainst, boolean isAway) {
            this.played++;
            this.goalsFor += goalsFor;
            this.goalsAgainst += goalsAgainst;

            if (goalsFor > goalsAgainst) {
                this.wins++;
                this.points += 3;
            } else if (goalsFor == goalsAgainst) {
                this.draws++;
                this.points += 1;
            } else {
                this.losses++;
            }

            if (isAway) {
                this.awayGoals += goalsFor;
            }
        }

        public void updateHeadToHead(String opponent, int points, int awayGoals) {
            this.headToHead.putIfAbsent(opponent, new Integer[]{0, 0}); // {points, away goals}
            this.headToHead.get(opponent)[0] += points;
            this.headToHead.get(opponent)[1] += awayGoals;
        }

        public Object[] getRow(int rank) {
            return new Object[]{rank, name, played, wins, draws, losses, goalsFor, goalsAgainst, (goalsFor - goalsAgainst), points};
        }

        @Override
        public int compareTo(Team other) {
            if (this.points != other.points) {
                return Integer.compare(other.points, this.points); // Descending order by points
            }
            if (this.goalsFor != other.goalsFor) {
                return Integer.compare(other.goalsFor, this.goalsFor); // Descending by goals scored
            }
            if (this.headToHead.containsKey(other.name) && other.headToHead.containsKey(this.name)) {
                int headToHeadPoints = this.headToHead.get(other.name)[0] - this.headToHead.get(other.name)[0];
                if (headToHeadPoints != 0) {
                    return headToHeadPoints; // Head-to-head points difference
                }
                return Integer.compare(this.headToHead.get(other.name)[1], this.headToHead.get(other.name)[1]); // Head-to-head away goals
            }
            return 0;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FootballPointsTable::new);
    }
}

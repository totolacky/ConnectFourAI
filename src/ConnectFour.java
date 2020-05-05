import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.io.FileWriter;

class Util {
    static int[][] intDeepCopy(int[][] a){
        int len = a.length;
        int wid = a[0].length;
        int[][] b = new int[len][wid];
        for(int i=0; i<len; i++){
            for(int j=0; j<wid; j++){
                b[i][j] = a[i][j];
            }
        }
        return b;
    }   // A deepcopy method for a 2D-int array
    static int randomPick(ArrayList<Integer> a){
        int rand = (int)(Math.random()*a.size());
        return a.get(rand);
    }   // Randomly picks an object from integer Arraylist
}   // A class with some static utilities

class tmpVariables {
    static boolean clicked = false;
    static int actionTaken = -1;
}   // Variables needed for GraphicsPlayer

abstract class GUI extends Frame{
    abstract void showChange(Board board);      // Displays the action that a player took
    abstract void gameOverMessage(Board board); // Displays the game over message
}   // Decides which way the game would be presented

class console extends GUI{
    void showChange(Board board){
        board.printBoard();
    }

    void gameOverMessage(Board board){
        System.out.print("\nGame Over! ");
        if(board.whoWon()==1){System.out.print("Black Wins!");}
        if(board.whoWon()==2){System.out.print("White Wins!");}
        if(board.whoWon()==3){System.out.print("Nobody Wins!");}
    }   // Prints the game over message
}

class Graphics extends GUI{

    private final int fw = 980, fh = 950;               // Set frame size
    private JButton position[][] = new JButton[6][7];   // Save each position as a JButton
    private JButton action[] = new JButton[7];          // Jbuttons to indicate where to press
    private ImageIcon[] icon = new ImageIcon[4];        // Save images
    private Panel p;                                    // The panel where game is going on

    private Color C0 = new Color(66, 33, 0);        // Board Color
    private Color C1 = new Color(132, 60, 12);    // Background Color

    Graphics(){
        makeGraphics();
    }

    void makeGraphics(){
        setSize(fw+10, fh+90);        // Frame의 크기 설정
        setBackground(C1);

        p = new Panel();
        add(p);                // Frame에 p 추가
        p.setLayout(null);     // BorderLayout과 같이 특별한 형태가 아닌, 레이아웃 사용
        p.setBackground(C1);

        setIcons();
        makeBoard();

        setVisible(true); // 보이기

        addWindowListener(new MyWindowAdapter()); // 윈도 창닫기 버튼 활성화
    }

    void makeBoard(){
        for (int i=0; i<6; i++) {             // 칸을 row행 생성
            for (int j = 0; j < 7; j++) {         // 칸을 col열 생성
                position[i][j] = new JButton(icon[0]);
                position[i][j].setDisabledIcon(icon[0]);
                position[i][j].setBackground(C0);
                position[i][j].setSize(130, 130);      // 칸 크기는 10 X 10
                position[i][j].setLocation(30+ j * 130, 810 - i * 130); // 칸 위치를 연산해서 넣기
                position[i][j].setBorderPainted(false);
                position[i][j].setEnabled(false);
                p.add(position[i][j]);    // p Panel에 칸 추가
            }
        }
        for (int i=0; i<7; i++){
            action[i] = new JButton(icon[3]);
            action[i].setSize(130,130);
            action[i].setBackground(C1);
            action[i].setLocation(30+i*130,30);
            action[i].setFocusPainted(false);
            action[i].setBorderPainted(false);
            action[i].setRolloverEnabled(false);
            p.add(action[i]);
            action[i].addActionListener(new ClickedListener(i));
        }
    }

    void setIcons(){
        icon[0] = new ImageIcon("pic/empty TP.png");
        icon[1] = new ImageIcon("pic/black TP.png");
        icon[2] = new ImageIcon("pic/white TP.png");
        icon[3] = new ImageIcon("pic/arrow TP.png");
    }

    class MyWindowAdapter extends WindowAdapter { // x눌러 윈도 창닫기
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }   // Used so that the window X button works

    class ClickedListener implements ActionListener {
        private int num; // Saves which row and column it is

        // Save value using constructor
        ClickedListener(int num) {
            this.num = num;
        }

        // Method that runs when clicked
        @Override
        public void actionPerformed(ActionEvent e) {
            tmpVariables.actionTaken = num;
            tmpVariables.clicked = true;
        }
    }   //

    void showChange(Board board){
        int[][] cb = board.getCB();
        for(int i=0; i<cb.length; i++){
            for(int j=0; j<cb[0].length; j++){
                position[i][j].setDisabledIcon(icon[cb[i][j]]);
            }
        }
    }

    void gameOverMessage(Board board){
        System.out.print("\nGame Over! ");
        if(board.whoWon()==1){System.out.print("Black Wins!");}
        if(board.whoWon()==2){System.out.print("White Wins!");}
        if(board.whoWon()==3){System.out.print("Nobody Wins!");}
    }   // Prints the game over message
}

class NoGUI extends GUI{
    void showChange(Board board){}
    void gameOverMessage(Board board){}
}

class Board {

    private int player;
    private int[][] cb = new int[6][7]; // Current Board. Left bottom is [0][0] (just like in coordinates)

    Board(){
        initBoard();
        player=1;
    }   // Starts by initializing the board. 0 is empty, 1 is black, 2 is white

    Board(int[][] init, int pl){
        cb = Util.intDeepCopy(init);
        player=pl;
    }   // Starts by assigning certain conformation of the board and current player

    int whoWon(){
        for(int i=0; i<6; i++){
            for(int j=0; j<7; j++){
                if(cb[i][j]==0){continue;}
                if(i<=2 && cb[i][j]==cb[i+1][j] && cb[i][j]==cb[i+2][j] && cb[i][j]==cb[i+3][j]) {return cb[i][j];}
                if(j<=3 && cb[i][j]==cb[i][j+1] && cb[i][j]==cb[i][j+2] && cb[i][j]==cb[i][j+3]) {return cb[i][j];}
                if(i<=2 && j<=3 && cb[i][j]==cb[i+1][j+1] && cb[i][j]==cb[i+2][j+2] && cb[i][j]==cb[i+3][j+3]) {return cb[i][j];}
                if(i>=3 && cb[i][j]==cb[i-1][j] && cb[i][j]==cb[i-2][j] && cb[i][j]==cb[i-3][j]) {return cb[i][j];}
                if(i>=3 && j<=3 && cb[i][j]==cb[i-1][j+1] && cb[i][j]==cb[i-2][j+2] && cb[i][j]==cb[i-3][j+3]) {return cb[i][j];}
            }
        }
        if(posActions().size()==0){return 3;}
        return 0;
    }   // Returns 0 if game is not over. Returns the winner player number otherwise.

    ArrayList<Integer> posActions(){
        ArrayList<Integer> pa = new ArrayList<>();
        for(int j=0; j<7; j++){
            if(cb[5][j]==0){ pa.add(j);}
        }
        return pa;
    }   // Returns the Arraylist containing x-coord of all non-full columns. Is arranged in order.

    boolean isPosMove(int action){
        if(cb[5][action]==0){return true;}
        else{return false;}
    }   // Checks if the given move is possible

    Board createSuccessorBoard(int action){
        if(action<0||action>6||cb[5][action]!=0){System.out.println("ERROR!!!!!!");}
        int[][] newCB = Util.intDeepCopy(cb);
        for(int i=0;i<6; i++){
            if(newCB[i][action]==0){
                newCB[i][action]=player;
                break;
            }
        }
        Board sucBoard = new Board(newCB, player);
        sucBoard.switchPlayer();
        return sucBoard;
    }   // Returns the successor board of an action(colunm #) taken by current player.

    private void initBoard(){
        for(int i=0; i<6; i++){
            for(int j=0; j<7; j++){
                cb[i][j]=0;
            }
        }
    }   // Empties the board

    void printBoard(){
        System.out.println("\n0 1 2 3 4 5 6\n");
        for(int i=5; i>=0; i--){
            for(int j=0; j<7; j++){
                System.out.print(cb[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }   // Prints the current board

    void switchPlayer(){
        if(player==1) {player=2;}
        else {player=1;}
    }   // Switches player

    int getPlayer() {return player;}

    int[][] getCB(){
        return cb;
    }

}   // Similar to the GameState class

class Game {

    Board board;        // The Board object representing the current board state
    int winner;         // Indicates who the winner is
    boolean gameOver;   // Indicates whether the game is over
    agent p1, p2;
    GUI g;
    ArrayList<Integer> howPlayed = new ArrayList<>();


    Game(String gui, String player1, String player2){
        initGame(gui,player1,player2);
        playGame();
    }   // Initializes and plays the game

    void initGame(String gui, String player1, String player2){
        board = new Board();
        winner = 0;
        gameOver = false;
        g = guiInitializer(gui);
        p1 = agentInitializer(player1);
        p2 = agentInitializer(player2);
        tmpVariables.clicked=false;
        tmpVariables.actionTaken=-1;
    }

    GUI guiInitializer(String gui){
        if(gui=="Graphics"){return new Graphics();}
        if(gui=="NoGraphics"){return new NoGUI();}
        else{return new console();}
    }

    agent agentInitializer(String p){
        if(p=="Random"){return new randomPlayer();}
        else if(p=="Console"){return new consolePlayer();}
        else if(p=="PureMCTS"){return new pureMCTSPlayer();}
        else if(p=="FastFinishMCTS"){return new fastFinishMCTSPlayer();}
        else{return new graphicsPlayer();}
    }

    void playGame(){
        g.showChange(board);
        while(board.whoWon()==0){
            int action = currentPlayer().getAction(board);
            while(!board.isPosMove(action)){
                System.out.print("Stuck! Action: ");
                System.out.println(action);
                action = currentPlayer().getAction(board);
            }
            oneTurn(action);
            g.showChange(board);
        }
        g.gameOverMessage(board);
    }   // Plays the game. Run in Game()

    agent currentPlayer(){
        int pl = board.getPlayer();
        if(pl==1){return p1;}
        else{return p2;}
    }   // Returns the current agent playing

    void takeAction(int action){
        board = board.createSuccessorBoard(action);
    }   // Takes an action

    void oneTurn(int action){
        takeAction(action);
        winner = board.whoWon();
        if(winner!=0) {gameOver=true;}
        howPlayed.add(action);
//        System.out.println();
//        System.out.println(board.getPlayer());
//        System.out.print("Action: ");
//        System.out.println(action);
    }   // Takes an action, check if game is over

    void offScreen(){

    }

}   // The main class for playing the game

abstract class agent{
    abstract int getAction(Board board);
}

class consolePlayer extends agent{
    int getAction(Board board){
        Scanner s = new Scanner(System.in);
        int action = -1;
        while(action<0||action>6){
            action = s.nextInt();
        }
        return action;
    }
}      // Gets the next action by typing in an integer from 0~6

class randomPlayer extends agent{
    int getAction(Board board){
        return Util.randomPick(board.posActions());
    }
}       // Gets the next action randomly

class graphicsPlayer extends agent{

    int getAction(Board board) {
        while(!tmpVariables.clicked) {
            //System.out.print("  ");
            timeConsumer();
        }
        int action = tmpVariables.actionTaken;
        tmpVariables.clicked=false;
        return action;
    }

    void timeConsumer(){
        for(int i=0; i<1; i++) {
            System.out.print("");
        }
    }
}     // Gets the next action by clicking on the interface

class pureMCTSPlayer extends agent{

    int k = 100;   // # of trials per each child node
    int myPlayerNo;

    int getAction(Board board){
        myPlayerNo = board.getPlayer();
        ArrayList<Integer> childAction = board.posActions();
        int[] wins = new int[childAction.size()];
        for(int i=0; i<childAction.size(); i++){
            Board sucBoard = board.createSuccessorBoard(childAction.get(i));
            for (int j=0; j<k; j++){
                wins[i] += didWin(sucBoard);
            }
        }
        int maxWins = 0;
        ArrayList<Integer> maxArg = new ArrayList<>();
        for(int i=0; i<childAction.size(); i++){
            if(wins[i]>maxWins){
                maxWins = wins[i];
                maxArg = new ArrayList<>();
                maxArg.add(childAction.get(i));
            }
            else if(wins[i]==maxWins){
                maxArg.add(childAction.get(i));
            }
        }
        return Util.randomPick(maxArg);
    }

    int didWin(Board sucBoard){
        if(sucBoard.whoWon()!=0){
            if(sucBoard.whoWon()==myPlayerNo){return 1;}
            else{return 0;}
        }
        int randomAction = Util.randomPick(sucBoard.posActions());
        return didWin(sucBoard.createSuccessorBoard(randomAction));
    }
}

class fastFinishMCTSPlayer extends agent{

    int k = 100;   // # of trials per each child node
    int myPlayerNo;

    int getAction(Board board){
        myPlayerNo = board.getPlayer();
        ArrayList<Integer> childAction = board.posActions();
        float[] wins = new float[childAction.size()];
        for(int i=0; i<childAction.size(); i++){
            Board sucBoard = board.createSuccessorBoard(childAction.get(i));
            for (int j=0; j<k; j++){
                wins[i] += didWin(sucBoard,1);
            }
        }
        float maxWins = 0;
        ArrayList<Integer> maxArg = new ArrayList<>();
        for(int i=0; i<childAction.size(); i++){
            if(wins[i]>maxWins){
                maxWins = wins[i];
                maxArg = new ArrayList<>();
                maxArg.add(childAction.get(i));
            }
            else if(wins[i]==maxWins){
                maxArg.add(childAction.get(i));
            }
        }
        return Util.randomPick(maxArg);
    }

    float didWin(Board sucBoard, int depth){
        if(sucBoard.whoWon()!=0){
            if(sucBoard.whoWon()==myPlayerNo){
                float score = 1+(float)1/depth;
                return score;
            }
            else{return 0;}
        }
        int randomAction = Util.randomPick(sucBoard.posActions());
        return didWin(sucBoard.createSuccessorBoard(randomAction),depth+1);
    }
}

public class ConnectFour {

    ConnectFour(String gui, String p1, String p2) {
        new Game(gui,p1,p2);
    }

    ConnectFour(int i, String gui, String p1, String p2) {
        int white = 0;  // # of white wins
        int black = 0;  // # of black wins
        for(int j=0; j<i; j++){
            Game g = new Game(gui,p1,p2);
            if(g.board.whoWon()==1){black++;}
            if(g.board.whoWon()==2){white++;}
            if(j%10==0){
                System.out.print("\nIterations: ");
                System.out.print(j);
            }
        }
        System.out.print("\n\nBlack wins: ");
        System.out.println(black);
        System.out.print("White wins: ");
        System.out.println(white);
    }

    public static void main(String[] args) {
        new ConnectFour( 10,"Graphics","PureMCTS","PureMCTS");
    }
}   // The class with main method


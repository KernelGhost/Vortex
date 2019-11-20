import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import javax.swing.SwingConstants;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

final class CellGrid {
    public int x;
    public int y;
 
    public CellGrid(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

final class GridBox extends JLabel {
	private static final long serialVersionUID = 7197962877418899056L;
	public int intID;
	
	public GridBox(int i) {
		this.intID = i;
	}
}

public class Vortex {
	private static int intBoardSize = 3;
	private static String[][] strBoard = new String[intBoardSize][intBoardSize];
	private static String strUsername;
	private static boolean boolGameon;
	private static int intVScore;
	private static int intUScore;
	private static int intDepth = 1;
	
	private JFrame frmVortex;
	private static JPanel panel;
	private static JLabel lblTitle;
	private static JLabel lblSubheading;
	private static GridBox[] buttons = new GridBox[intBoardSize * intBoardSize];
	private static JLabel lblScoreboard;
	private static JLabel lblVortex;
	private static JLabel lblUser;
	private static JLabel lblVScore;
	private static JLabel lblUScore;
	private static JButton btnFirst;
	private static JButton btnSecond;
	private static JButton btnNewGame;
	private static JButton btnResetScoreboard;
	private static JLabel lblPossibilities;
	private static JLabel lblAuthor;
	
	/* Launch the application. */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Vortex window = new Vortex();
					window.frmVortex.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void UserMove(int index) {
		CellGrid position = TransformCG(index);
		
		if ((strBoard[position.x][position.y] == "") && boolGameon==true) {
			strBoard[position.x][position.y] = "X";
			BoardPlace("X", index);
			
			lblPossibilities.setText("Possible Games: " + Integer.toString(PossibleGames(strBoard, false)));
			CheckWin();
			
			if (boolGameon) {
				AIMove();
			}
		}
	}
	
	public static int TransformGC (int x, int y) {
		return (intBoardSize*y)+x;
	}
	
	public static CellGrid TransformCG(int i) {
		int x = 0;
		int y = 0;
		
		x = (i % intBoardSize);
		y = (i - x)/intBoardSize;
		
		return new CellGrid(x,y);
	}
	
	public static void CheckWin() {
		String result = CheckWinLogic();
		
		if (result != "") {
			lblPossibilities.setText("Possible Games: 0");
			
			if (result == "X") {
				infoBox(strUsername + " wins this round!","Vortex", "X");
				intUScore++;
			} else if (result == "O") {
				infoBox("Vortex wins this round!","Vortex", "O");
				intVScore++;
			} else if (result == "T") {
				infoBox("It's a tie!","Vortex", "T");
				intVScore++;
				intUScore++;
			}
			
			btnNewGame.setVisible(true);
			btnFirst.setVisible(false);
			btnSecond.setVisible(false);
			btnFirst.setEnabled(true);
			btnSecond.setEnabled(true);
			btnResetScoreboard.setEnabled(true);
			lblVScore.setText(Integer.toString(intVScore));
			lblUScore.setText(Integer.toString(intUScore));
			boolGameon = false;
		}
	}
	
	public static String CheckEqual(String[] strCombination) {
		String result = "";
		boolean boolMatch = true;
		
		if (strCombination[0] != "") {
			for(int ctrB = 1; ctrB < strCombination.length; ctrB++) {
				if (strCombination[ctrB-1] != strCombination[ctrB]) {
					boolMatch = false;
				}
			}
		} else {
			boolMatch = false;
		}
		
		if (!boolMatch) {
			result = "";
		} else {
			result = strCombination[0];
		}
		
		return result;
	}
	
	public static String CheckWinLogic() { /* "X", "O", "T", "" */
		String result = "";
		boolean boolFound = false;
		boolean boolTie = true;
		String[] strComb = new String[intBoardSize];
		
		/* Horizontal & Vertical Checks */
		for(int ctrChk = 0; ctrChk < 2; ctrChk++) {
			for(int ctrY = 0; ctrY < intBoardSize; ctrY++) {
				for(int ctrX = 0; ctrX < intBoardSize; ctrX++) {
					if (!boolFound) {
						if (ctrChk == 0) {
							strComb[ctrX] = strBoard[ctrX][ctrY];
						} else {
							strComb[ctrX] = strBoard[ctrY][ctrX];
						}
					}
				}
				if ((!boolFound) && (CheckEqual(strComb)) != "") {
					boolFound = true;
					result = CheckEqual(strComb);
				}
			}
		}
		
		/* Diagonal Checks */
		for(int ctrChk = 0; ctrChk < 2; ctrChk++) {
			for(int ctrD = 0; ctrD < intBoardSize; ctrD++) {
				if (!boolFound) {
					if (ctrChk == 0) {
						strComb[ctrD] = strBoard[ctrD][ctrD];
					} else {
						strComb[ctrD] = strBoard[(intBoardSize - 1) - ctrD][ctrD];
					}
				}
			}
			if ((!boolFound) && (CheckEqual(strComb)) != "") {
				boolFound = true;
				result = CheckEqual(strComb);
			}
		}
		
		/* Tie Check */
		if (!boolFound) {
			for(int ctrY = 0; ctrY < intBoardSize; ctrY++) {
				for(int ctrX = 0; ctrX < intBoardSize; ctrX++) {
					if (strBoard[ctrX][ctrY] == "") {
						boolTie = false;
					}
				}
			}
			if (boolTie) {
				result = "T";
			}
		}
		
		return result;
	}
	
	public static void BoardPlace(String player, int index) {
		Color myMagenta = new Color(227, 39, 120);
		Color myBlue = new Color(39, 164, 227);
		
		buttons[index].setText(player);
		if (player == "X") {
			buttons[index].setForeground(myMagenta);
		} else {
			buttons[index].setForeground(myBlue);
		}
	}
	
	public static int PossibleGames(String[][] T, boolean turn) {
		int xctr = 0;
		int yctr = 0;
		int sum = 0;
		String WinState = "";
		
		for(yctr = 0; yctr < intBoardSize; yctr++){ 
			for(xctr = 0; xctr < intBoardSize; xctr++){  
				if (T[xctr][yctr] == "") {
					if (!turn) {
						T[xctr][yctr] = "O";
					} else {
						T[xctr][yctr] = "X";
					}
					WinState = CheckWinLogic();
					if (WinState != "") {
						sum++;
						T[xctr][yctr] = "";
					} else {
						turn = !turn;
						sum = sum + PossibleGames(T, turn);
						T[xctr][yctr] = "";
						turn = !turn;
					}
				}
			}
		}
		
		return sum;
	}
	
	public static int AIEngine(String[][] T, boolean turn) {
		int xctr = 0;
		int yctr = 0;
		String WinState = "";
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		for(yctr = 0; yctr < intBoardSize; yctr++){ 
			for(xctr = 0; xctr < intBoardSize; xctr++){  
				if (T[xctr][yctr] == "") {
					if (!turn) {
						T[xctr][yctr] = "O";
					} else {
						T[xctr][yctr] = "X";
					}
					WinState = CheckWinLogic();
					if (WinState == "X") {
						list.add(intDepth - ((intBoardSize * intBoardSize) + 1));
						T[xctr][yctr] = "";
					} else if (WinState == "O") {
						list.add(((intBoardSize * intBoardSize) + 1) - intDepth);
						T[xctr][yctr] = "";
					} else if (WinState == "T") {
						list.add(0);
						T[xctr][yctr] = "";
					} else {
						turn = !turn;
						intDepth++;
						list.add(AIEngine(T, turn));
						intDepth--;
						T[xctr][yctr] = "";
						turn = !turn;
					}
				}
			}
		}
		if (turn) {
			return Collections.min(list);
		} else {
			return Collections.max(list);
		}
	}
	
	public static void AIMove() {
		int[][] intarrScores = new int[intBoardSize][intBoardSize];
		CellGrid ctrCellGrid = new CellGrid(0,0);
		int intTopScore = -((intBoardSize * intBoardSize) + 1);
		int intRandom = 0;
		int ctrBestMoves = 0;
		int intBestMoves[] = new int[intBoardSize * intBoardSize];
		
		for(ctrCellGrid.y = 0; ctrCellGrid.y < intBoardSize; ctrCellGrid.y++){ 
			for(ctrCellGrid.x = 0; ctrCellGrid.x < intBoardSize; ctrCellGrid.x++){ 
				if (strBoard[ctrCellGrid.x][ctrCellGrid.y] == "") {
					strBoard[ctrCellGrid.x][ctrCellGrid.y] = "O";
					
					/* Check if the move wins, looses or draws immediately
					 * (since the AIEngine places a move before checking) */
					if (CheckWinLogic() == "O") {
						intarrScores[ctrCellGrid.x][ctrCellGrid.y] = (intBoardSize * intBoardSize) + 1;		/* Not subject to depth penalty */
					} else if (CheckWinLogic() == "X") {
						intarrScores[ctrCellGrid.x][ctrCellGrid.y] = -((intBoardSize * intBoardSize) + 1);	/* Not subject to depth penalty */
					} else if (CheckWinLogic() == "T") {
						intarrScores[ctrCellGrid.x][ctrCellGrid.y] = 0;
					} else {
						intarrScores[ctrCellGrid.x][ctrCellGrid.y] = AIEngine(strBoard, true);
					}
					strBoard[ctrCellGrid.x][ctrCellGrid.y] = "";
				}
			}
		}
		
		ctrCellGrid.x = 0;
		ctrCellGrid.y = 0;
		
		/* Find the top score */
		for(ctrCellGrid.y = 0; ctrCellGrid.y < intBoardSize; ctrCellGrid.y++){ 
			for(ctrCellGrid.x = 0; ctrCellGrid.x < intBoardSize; ctrCellGrid.x++){ 
				if ((strBoard[ctrCellGrid.x][ctrCellGrid.y] == "") && (intarrScores[ctrCellGrid.x][ctrCellGrid.y] >= intTopScore)) {
					intTopScore = intarrScores[ctrCellGrid.x][ctrCellGrid.y];
				}
			}
		}
		
		ctrCellGrid.x = 0;
		ctrCellGrid.y = 0;
		
		/* Store move(s) that result in the top score */
		for(ctrCellGrid.y = 0; ctrCellGrid.y < intBoardSize; ctrCellGrid.y++){ 
			for(ctrCellGrid.x = 0; ctrCellGrid.x < intBoardSize; ctrCellGrid.x++){ 
				if ((strBoard[ctrCellGrid.x][ctrCellGrid.y] == "") && (intarrScores[ctrCellGrid.x][ctrCellGrid.y] == intTopScore)) {
					intBestMoves[ctrBestMoves] = TransformGC(ctrCellGrid.x,ctrCellGrid.y);
					ctrBestMoves++;
				}
			}
		}
		
		/* If there are multiple equally good moves, choose one randomly */
		intRandom = ThreadLocalRandom.current().nextInt(0, ctrBestMoves);
		ctrCellGrid = TransformCG(intBestMoves[intRandom]);
		strBoard[ctrCellGrid.x][ctrCellGrid.y] = "O";
		BoardPlace("O", intBestMoves[intRandom]);
		lblPossibilities.setText("Possible Games: " + Integer.toString(PossibleGames(strBoard, true)));
		
		CheckWin();
	}
	
	public static void infoBox(String infoMessage, String titleBar, String player) {
		ImageIcon icon = null;
		if (player == "X") {
			icon = new ImageIcon(Vortex.class.getResource("Images/Win.png"));
		} else if (player == "O") {
			icon = new ImageIcon(Vortex.class.getResource("Images/Loss.png"));
		} else if (player == "T") {
			icon = new ImageIcon(Vortex.class.getResource("Images/Tie.png"));
		}
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE, icon);
    }
	
	public static String GetUserName() {
		return System.getProperty("user.name");
	}
	
	public static void ResetGame(boolean fullreset) {
		int ctrb = 0;
		int ctrx = 0;
		int ctry = 0;
		
		/* Clear the game scores */
		if (!fullreset) {
			intUScore = 0;
			intVScore = 0;
			lblVScore.setText("0");
			lblUScore.setText("0");
		} else {
			intDepth = 1;
			lblPossibilities.setText("");
			
			/* Clear the board */
			for(ctrb = 0; ctrb < (intBoardSize*intBoardSize); ctrb++){
				buttons[ctrb].setText("");
			}
			
			/* Initialize the 2D board array */
			for(ctrx = 0; ctrx < intBoardSize; ctrx++){  
				for(ctry = 0; ctry < intBoardSize; ctry++){ 
					strBoard[ctrx][ctry] = "";
				}
			}
		}
	}
	
	public static void StartGame(boolean first) {
		btnFirst.setEnabled(false);
		btnSecond.setEnabled(false);
		btnResetScoreboard.setEnabled(false);
		boolGameon = true;
		if (!first) {
			AIMove();
		}
	}

	/* Create the application */
	public Vortex() {
		initialize();
	}

	/* Initialize the contents of the frame */
	private void initialize() {
		frmVortex = new JFrame();
		frmVortex.setResizable(false);
		frmVortex.setTitle("Vortex");
		if (intBoardSize < 3) {
			frmVortex.setBounds(100, 100, 500 + (intBoardSize - 3) * 62, 300);
		} else {
			frmVortex.setBounds(100, 100, 500 + (intBoardSize - 3) * 62, 300 + (intBoardSize - 3) * 62);
		}
		frmVortex.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmVortex.getContentPane().setLayout(null);
		
		/* Scoreboard Initialization */
		strUsername = GetUserName();
		panel = new JPanel();
		panel.setBorder(null);
		panel.setBounds(220 + (intBoardSize - 3) * 62, 130, 267, 85);
		frmVortex.getContentPane().add(panel);
		panel.setLayout(null);
		
		lblUser = new JLabel("User");
		lblUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblUser.setBounds(132, 29, 128, 16);
		lblUser.setText(strUsername);
		panel.add(lblUser);
		
		lblVScore = new JLabel("0");
		lblVScore.setFont(new Font("Lucida Grande", Font.BOLD, 30));
		lblVScore.setHorizontalAlignment(SwingConstants.CENTER);
		lblVScore.setBounds(6, 46, 128, 39);
		panel.add(lblVScore);
		
		lblUScore = new JLabel("0");
		lblUScore.setFont(new Font("Lucida Grande", Font.BOLD, 30));
		lblUScore.setHorizontalAlignment(SwingConstants.CENTER);
		lblUScore.setBounds(132, 46, 128, 39);
		panel.add(lblUScore);
		
		btnResetScoreboard = new JButton("Reset Scoreboard");
		btnResetScoreboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ResetGame(false);
			}
		});
		if (intBoardSize < 3) {
			btnResetScoreboard.setBounds(220 + (intBoardSize - 3) * 62, panel.getY() + panel.getHeight() + 5, 267, 35);
		} else {
			btnResetScoreboard.setBounds(220 + (intBoardSize - 3) * 62, 92 + ((intBoardSize - 1) * 62), 267, 35);
		}
		frmVortex.getContentPane().add(btnResetScoreboard);
		
		ResetGame(false);
		
		/* The Tic-Tac-Toe Buttons */
		int bctr = 0;
		
		for(bctr = 0; bctr < (intBoardSize * intBoardSize); bctr++){ 
			buttons[bctr] = new GridBox(bctr);
			buttons[bctr].setHorizontalAlignment(SwingConstants.CENTER);
			buttons[bctr].setFont(new Font("Lucida Grande", Font.BOLD, 40));
			buttons[bctr].setBounds((25 + (62 * TransformCG(bctr).x)), (77 + (62 * TransformCG(bctr).y)), 50, 50);
			buttons[bctr].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			buttons[bctr].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					GridBox source = (GridBox) e.getSource();
					UserMove(source.intID);
				}
			});
			frmVortex.getContentPane().add(buttons[bctr]);
		}
		
		/* New Game Buttons */
		btnNewGame = new JButton("New Game");
		btnNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNewGame.setVisible(false);
				btnFirst.setVisible(true);
				btnSecond.setVisible(true);
				ResetGame(true);
			}
		});
		btnNewGame.setBounds(220 + (intBoardSize - 3) * 62, 77, 267, 50);
		frmVortex.getContentPane().add(btnNewGame);
		
		btnFirst = new JButton("Go First");
		btnFirst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblPossibilities.setText("Possible Games: " + Integer.toString(PossibleGames(strBoard, true)));
				StartGame(true);
			}
		});
		btnFirst.setBounds(220 + (intBoardSize - 3) * 62, 77, 117, 50);
		btnFirst.setVisible(false);
		frmVortex.getContentPane().add(btnFirst);
		
		btnSecond = new JButton("Go Second");
		btnSecond.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblPossibilities.setText("Possible Games: " + Integer.toString(PossibleGames(strBoard, false)));
				StartGame(false);
			}
		});
		btnSecond.setBounds(369 + (intBoardSize - 3) * 62, 77, 117, 50);
		btnSecond.setVisible(false);
		frmVortex.getContentPane().add(btnSecond);
		
		/* Other Labels */
		lblScoreboard = new JLabel("SCOREBOARD");
		lblScoreboard.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblScoreboard.setHorizontalAlignment(SwingConstants.CENTER);
		lblScoreboard.setBounds(6, 6, 254 , 16);
		panel.add(lblScoreboard);
		
		lblVortex = new JLabel("Vortex");
		lblVortex.setHorizontalAlignment(SwingConstants.CENTER);
		lblVortex.setBounds(6, 29, 128, 16);
		panel.add(lblVortex);
		
		lblTitle = new JLabel("VORTEX");
		lblTitle.setFont(new Font("Lucida Grande", Font.BOLD, 45));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(6, 0, 488 + (intBoardSize - 3) * 62, 62);
		frmVortex.getContentPane().add(lblTitle);
		
		lblSubheading = new JLabel("A Perfect Recursion-Based Tic-Tac-Toe AI");
		lblSubheading.setHorizontalAlignment(SwingConstants.CENTER);
		lblSubheading.setBounds(6, 52, 488 + (intBoardSize - 3) * 62, 16);
		frmVortex.getContentPane().add(lblSubheading);
		
		lblPossibilities = new JLabel("");
		lblPossibilities.setBounds(25, 70 + (intBoardSize * 62), 176, 16);
		frmVortex.getContentPane().add(lblPossibilities);
		
		lblAuthor = new JLabel("By Rohan Barar, 2019");
		lblAuthor.setHorizontalAlignment(SwingConstants.TRAILING);
		if (intBoardSize < 3) {
			lblAuthor.setBounds(327 + (intBoardSize - 3) * 62, btnResetScoreboard.getY() + btnResetScoreboard.getHeight() + 1, 160, 16);
		} else {
			lblAuthor.setBounds(327 + (intBoardSize - 3) * 62, 256 + (intBoardSize - 3) * 62, 160, 16);
		}
		frmVortex.getContentPane().add(lblAuthor);
	}
}
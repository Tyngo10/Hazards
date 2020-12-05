import java.util.*;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BorderFactory;


public class Hazards {

	//runs game in while loop until win condition is reached
	public static void main(String[] args) {

		//new GUI();

		//constructor
		Square[][] board = new Square[5][5];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				board[i][j] = new Square();
			}
		}

		//initialize player starting positions
		board[4][0].setState("p1");
		board[0][4].setState("p2");
		printBoard(board); //starting board
		boolean win = false; //becomes true when someone wins
        int turn = 1; //switches between 1 and 2
        boolean firstTurn = true; //checks if first turn

        //runs game and checks win conditions
        while(win==false) {
	       	if (checkWinStatus(board, turn)) { //ends game
	       		if (turn == 1)
	       			System.out.println("Player 2 wins");
	       		else
	       			System.out.println("Player 1 wins");
	       		win = true;
	       	}
	       	else {
	       		System.out.println("Player "+turn+" to move");
		       	String[] move = getMove(firstTurn);

		       	//redo until legal move
		        while(checkMoveSyntax(move, firstTurn)==false || checkMoveLegal(move, board, turn)==false) {
		           	System.out.println("Invalid entry");
		           	move = getMove(firstTurn);
		        }
		        updateBoard(move, board, turn); //does move if legal
		        printBoard(board);
		        turn = turn%2+1; //1 and 2 switch every turn
		    }
		    if (firstTurn) { //removes first turn rules after first turn
	       		firstTurn = false;
	       	}
        }
    }

	//turns move input into array
    public static String[] getMove(boolean firstTurn)
    {
    	Scanner scan = new Scanner(System.in).useDelimiter("\\n");
    	String[] list = {" ", "none", "first", "first2"}; //initialize array
    	String s = scan.next();
    	if (s.equals("help")) { //help prompt
    		return list;
    	}

    	//turns string input into array with split on first turn
    	String[] a = s.split(" "); 
    	if (firstTurn) {
    		if (a.length == 1) {
    			list[0] = a[0].trim();
    			return list;
    		}
    		if (a.length == 2) {
    			list[0] = a[0];
    			list[1] = a[1].trim();
    			return list;
    		}
    		return list;
    	}

    	//turns string input into array with split
    	list[0] = a[0];
    	if (a.length == 3)
    		list[1] = a[1];
    	String[] b = a[a.length-1].split(",");

    	try { //hazard coordinates
    		list[3] = Integer.toString(Integer.parseInt(b[0])-1); //y value
    		list[2] = Integer.toString(5-Integer.parseInt(b[1].trim())); //x value
    	}
    	catch(NumberFormatException e) { //catches format errors
    		return list;
    	}

    	return list;
    }

    //syntax checker
	public static boolean checkMoveSyntax(String[] move, boolean firstTurn)
	{
	    String[] check = {"left", "right", "up", "down", "none"}; //legal moves
	    String[] check2 = {"right", "left", "down", "up", "none"}; //parallel array
	    boolean test = false;
	    int index = 0;
	    if (move[0].equals("help")) { //help statement
	    	help();
	    	return false;
	    }
	    for (int i = 0; i < 5; i++) { //checks if first input is good
	    	if (move[0].equals(check[i])) {
	    		test = true;
	    		index = i;
	    	}
	    }
	    if (!test)
	    	return false;
	    if (move[1].equals(check2[index])) //no opposite moves (ex. left right)
	    	return false;
	    if (firstTurn) { //applies first turn rules if applies
	    	if (!move[2].equals("first"))
	    		return false;
	    	else
	    		return true;
	    }
	    try {
		    int x = Integer.parseInt(move[3]);
		    int y = Integer.parseInt(move[2]);  
		    if (x<0 || x>4 || y<0 || y>4) //makes sure coords are between 0 and 4
		    	return false;
		    return true;
		}
		catch(NumberFormatException e) { //catches format errors
			return false;
		}
	}

    //help command with rules and move syntax
    public static void help() {
        System.out.println("The objective of this game is to surroung your opponent using hazards, the wall, and even your player.");
        System.out.println("Each turn, a player ([1] or [2]) moves one or two adjacent squares (left, right, up, down / or any non-opposite pair)");
        System.out.println("and places one hazard ([X]) on any open square (x,y), unless it's the first turn.");
        System.out.println("Continue alternating turns until one person is unable to move.");
        System.out.println("Example of a move: \"left up 3,5\" (assuming there are no hazards or players along the move path or on 3,5");
        System.out.println("Example of an illegal move: \"left right 3,6\"");
        System.out.println("To reopen these screen, type \"help\"");
    }

    //legality checker
	public static boolean checkMoveLegal(String[] move, Square[][] board, int turn) {
		int x = Square.getX(board, turn);
		int y = Square.getY(board, turn);
		try { //checks certain square depending on direction moved
			for (int i=0; i<2; i++) { //checks first and second move
				if(defineDirection(move[i], board)==-1) {
					if (!board[y][x-1].getState().equals("valid"))
						return false;
					x--; //changes depending on first move
				}
				if(defineDirection(move[i], board)==1) {
					if (!board[y][x+1].getState().equals("valid"))
						return false;
					x++;
				}
				if(defineDirection(move[i], board)==-2) {
					if (!board[y+1][x].getState().equals("valid"))
						return false;
					y++;
				}
				if(defineDirection(move[i], board)==2) {
					if (!board[y-1][x].getState().equals("valid"))
						return false;
					y--;
				}
			}
			if(defineDirection(move[0], board)==0) { //shouldn't happen
				if (board[y][x].getState().equals("none"))
					return false;
			}
		}
		catch(ArrayIndexOutOfBoundsException e) { //catches bounds errors
			return false;
		}
		if(move[2].equals("first")) //first turn doesn't have hazards
			return true;
			//checks if hazard placement is legal
		if(!board[Integer.parseInt(move[2])][Integer.parseInt(move[3])].getState().equals("valid"))
			return false;
		return true; //move is legal
	}

	//defines directions (convenience method)
	public static int defineDirection(String move, Square[][] board) {
		if (move.equals("none"))
			return 0;
		if (move.equals("left"))
			return -1;
		if (move.equals("right"))
			return 1;
		if (move.equals("down"))
			return -2;
		if (move.equals("up"))
			return 2;
		return 3;//this shouldn't happen
	}

	//changes Board status
	public static void updateBoard(String[] move, Square[][] board, int turn) {
		int x = Square.getX(board, turn);
		int y = Square.getY(board, turn);

		//uses old values and moves to find end value
		int newX = x;
		int newY = y;
		for(int i = 0; i < 2; i++){
			if(defineDirection(move[i], board)==-1)
				newX--;
			if(defineDirection(move[i], board)==1)
				newX++;
			if(defineDirection(move[i], board)==-2)
				newY++;
			if(defineDirection(move[i], board)==2)
				newY--;
		}

		//swaps player to new square and resets old square
		board[newY][newX].setState("p"+Integer.toString(turn));
		board[y][x].setState("valid");

		//places hazard
		if (!move[2].equals("first"))
			board[Integer.parseInt(move[2])][Integer.parseInt(move[3])].setState("invalid");
	}

    //high quality graphics
    public static void printBoard(Square[][] board) {
    	for(int i = 0; i < 3; i++){ // can change amount of space to clear screen
    		System.out.println();
    	}
        System.out.println("  +-----------+ ");
        for(int i = 0; i < 5; i++){
            System.out.print((5-i) + " |"); //y-coordinates
            for(int j = 0; j < 5; j++){
                System.out.print(" ");

                //prints the state as one of the markers
                if(board[i][j].getState().equals("p1")){
                    System.out.print("1");
                }
                else if(board[i][j].getState().equals("p2")){
                    System.out.print("2");
                }
                else if(board[i][j].getState().equals("valid")){
                    System.out.print(" ");
                }
                else if(board[i][j].getState().equals("invalid")){
                    System.out.print("X");
                }
            }
            System.out.print(" ");
            System.out.print("|");
            System.out.println();
        }
        System.out.println("  +-----------+ ");
        System.out.println("    1 2 3 4 5 "); //x-coordinates
    }

	//checks all adjacent square around a player
	public static boolean checkWinStatus(Square[][] board, int turn) {
		int x = Square.getX(board, turn);
		int y = Square.getY(board, turn);
		int[] newXY = {x-1, x+1, y+1, y-1};
		for (int i=0; i<3; i++) {
			if (newXY[i]>4 || newXY[i]<0)
				newXY[i] = -2;
		}
		if (newXY[0]!=-2) //nested to avoid bounds error
		    if (board[y][newXY[0]].getState().equals("valid"))
				return false;
		if (newXY[1]!=-2)
		    if (board[y][newXY[1]].getState().equals("valid"))
				return false;
		if (newXY[2]!=-2)
		    if (board[newXY[2]][x].getState().equals("valid"))
				return false;
		if (newXY[3]!=-2)
		    if (board[newXY[3]][x].getState().equals("valid"))
				return false;
		return true;
	}
}

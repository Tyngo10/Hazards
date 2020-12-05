public class Square {

    private String type = "valid";

    public String getState(){
        return type;
    }

	public void setState(String typeNew){
		if(
		  typeNew.equals("valid")   ||
		  typeNew.equals("invalid") ||
		  typeNew.equals("p1")      ||
		  typeNew.equals("p2")
		  )
			type = typeNew;
	}

	public static int getX(Square[][] board, int turn){
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (board[i][j].getState().equals("p"+Integer.toString(turn))) {
					return j;
				}
			}
		}
		return -3;
	}

	public static int getY(Square[][] board, int turn){
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (board[i][j].getState().equals("p"+Integer.toString(turn))) {
					return i;
				}
			}
		}
		return -4;
	}
}
package application;
	
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class Main extends Application {
	
	static TextField[][] textFields = new TextField[9][9];
	static Timeline timeline1;
	int count = 0;
	static List<int[][]> boardCombinations = new ArrayList<>();
	static int arrayCount = 0;

	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,500,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
            Label labelMsg = new Label("Sudoku Board");
            labelMsg.setFont(new Font("Arial", 16));
            labelMsg.setPadding(new Insets(5,5,0,20));
            showMessage(labelMsg, "Sudoku Board!!", 10);
            
			//Sudoku Board grid
			GridPane boardGrid = new GridPane();
			boardGrid.setVgap(5);
			boardGrid.setHgap(5);
			boardGrid.setPadding(new Insets(20,20,20,20));
			boardGrid.setAlignment(Pos.CENTER);
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                		TextField numField = new TextField();
                		numField.setAlignment(Pos.CENTER);

                		textFields[c][r] = numField;
                		filterInput(textFields[c][r]);
                		boardGrid.add(numField, c, r);
                		if ((c==2 || c == 5) && (r==2 || r==5)) {
                			numField.setStyle("-fx-border-style: solid inside; -fx-border-width: 0 3 3 0;");
                		} else if (c == 2 || c == 5) {
                    		numField.setStyle("-fx-border-style: solid inside; -fx-border-width: 0 3 0 0;");
                		} else if (r==2 || r == 5) {
                			numField.setStyle("-fx-border-style: solid inside; -fx-border-width: 0 0 3 0;");
                		}
                		
                }
            }  

            //Bottom bar buttons
            HBox hbox = new HBox();
            hbox.setSpacing(5);
            Button solve = new Button("Solve Sudoku");
            Button solve2 = new Button("Solve & See algorithm");
            Button clear = new Button("Clear Board");
            Button buttonQuit = new Button("Quit");
            Label labelTimer = new Label("Timer: 00:00");
            hbox.setAlignment(Pos.BASELINE_LEFT);
            timeline1 = new Timeline(new KeyFrame(Duration.seconds(1), e-> {
				count++;	
				String formattedTime = formatTime(count);
				labelTimer.setText("Timer: " + formattedTime);
			}));
            timeline1.setCycleCount(Animation.INDEFINITE);
            timeline1.play();
            hbox.getChildren().addAll(clear, solve, solve2, buttonQuit, labelTimer);
            hbox.setPadding(new Insets(5,25,25,25));
            
            clear.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					for (int r = 0; r < 9; r++) {
		                for (int c = 0; c < 9; c++) {
		                	textFields[c][r].setText("");
		    	        	textFields[c][r].setEditable(true);
		                    textFields[c][r].setStyle("");
		                    if ((c==2 || c == 5) && (r==2 || r==5)) {
		    	            	textFields[c][r].setStyle("-fx-border-style: solid inside; -fx-border-width: 0 3 3 0;");
		            		} else if (c == 2 || c == 5) {
		            			textFields[c][r].setStyle("-fx-border-style: solid inside; -fx-border-width: 0 3 0 0;");
		            		} else if (r==2 || r == 5) {
		            			textFields[c][r].setStyle("-fx-border-style: solid inside; -fx-border-width: 0 0 3 0;");
		            		}
		                }
		            }  
				}
            });

            buttonQuit.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					System.exit(1);
				}
            });
            
            //Solver by: converting board to 2d Array and putting through algorithm
            solve.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					arrayCount=0;
					int[][] boardArray = boardToArray();
					System.out.println("Solvable? " + solve(boardArray));
					if (solve(boardArray)) {
						setBoard(boardArray);
						showMessage(labelMsg, "Sudoku board solved in " + arrayCount +" boards", 20);
					} else { 
						showMessage(labelMsg, "Not a valid board to solve", 15);
					}
				}
            });
            solve2.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					arrayCount=0;
					boardCombinations.clear();
					int[][] boardArray = boardToArray();
					solve2(boardArray);
					showMessage(labelMsg, "Algorithm board combinations: "+ boardCombinations.size() + ". Solving sudoku board... ", 25);
					
		            Timeline solverTimeline = new Timeline(new KeyFrame(Duration.seconds(0.02), e-> {
		            	if (arrayCount < boardCombinations.size()) {
		            		int[][] boardInstance = boardCombinations.get(arrayCount);
		            		setBoard(boardInstance);
		            		arrayCount++;
		            	}
					}));
		            solverTimeline.setCycleCount(boardCombinations.size());
		            solverTimeline.play();
		           
				}
            });
            
            //Right side bar buttons, boards and custom saved board
            VBox vbox = new VBox();
            vbox.setSpacing(10);
            Button board1 = new Button("Board 1");
            Button board2 = new Button("Board 2");
            Button board3 = new Button("Board 3");
            Button boardCustom = new Button("Saved Board");
            Button boardSave = new Button("★Save");
            vbox.getChildren().addAll(board1, board2, board3, boardCustom, boardSave);
            
            board1.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					setBoard(sudokuBoard1);
				}
            });
            board2.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					setBoard(sudokuBoard2);
				}
            });
            board3.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					setBoard(sudokuBoard3);
				}
            });
            boardCustom.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					setBoard(sudokuBoardSaved);
				}
            });
            
            //Saves a copy of valid board combination. 
            boardSave.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					int[][] savedBoard = boardToArray();
					int[][] boardCopy = Arrays.stream(savedBoard).map(int[]::clone).toArray(int[][]::new);
					int inputs = 0;
					for (int r = 0; r < 9; r++) {
		                for (int c = 0; c < 9; c++) {
		                	if (savedBoard[r][c] >=1 && savedBoard[r][c] <=9) {
		                		inputs++;
		                	}
		                }
					}
					if (inputs <20) {
						showMessage(labelMsg, "Input more numbers, board requires at least 20", 20);
					} else if (!solve(savedBoard)) {
						showMessage(labelMsg, "Board inputs not valid, unable to save", 20);
					} else if (solve(savedBoard)) {
						sudokuBoardSaved = boardCopy;
						showMessage(labelMsg, "Board Saved", 20);
					}
				}
            });
            
            //Putting all bars and gridbox together
            root.setTop(labelMsg);
            root.setRight(vbox);
			root.setCenter(boardGrid);
			root.setBottom(hbox);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Sudoku Board");
			primaryStage.getIcons().add(new Image(new FileInputStream("sudoku.png")));
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private String formatTime(int seconds) {
		int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
	}

	//takes in 2d array, puts values into grid and makes template boxes bolded and un-editable
	private static void setBoard(int[][] board) {
		for (int r = 0; r < 9; r++) {
	        for (int c = 0; c < 9; c++) {
	        	String value = String.valueOf(board[r][c]);
	            textFields[c][r].setText(value);
	            
	            if (!value.equals("0")) {
	                textFields[c][r].setEditable(false);
	                textFields[c][r].setStyle("-fx-font-weight: bold; -fx-background-color: lightgreen;");
	            } else {
	                textFields[c][r].setEditable(true);
	                textFields[c][r].setStyle("");
	            }
	            
	            // Concatenate styles to preserve previous ones
	            StringBuilder styleBuilder = new StringBuilder(textFields[c][r].getStyle());

	            if ((c == 2 || c == 5) && (r == 2 || r == 5)) {
	                styleBuilder.append("-fx-border-style: solid inside; -fx-border-width: 0 3 3 0;");
	            } else if (c == 2 || c == 5) {
	                styleBuilder.append("-fx-border-style: solid inside; -fx-border-width: 0 3 0 0;");
	            } else if (r == 2 || r == 5) {
	                styleBuilder.append("-fx-border-style: solid inside; -fx-border-width: 0 0 3 0;");
	            }

	            textFields[c][r].setStyle(styleBuilder.toString());
	            
	        }
	    }
	}
	
	//Convert input of board into 2d Array
	private int[][] boardToArray() {
		int[][] array = new int[9][9];
		for (int r = 0; r < 9; r++) {
	        for (int c = 0; c < 9; c++) {
	            String text = textFields[c][r].getText();
	            if (text==null || text.trim().isEmpty()) {
	            	array[r][c] = 0;
	            } else {
	            	int value = Integer.parseInt(text);
	            	array[r][c] = value;	            	
	            }
	        }
	    }
		return array;
	}

	//Filters input to allow numbers only
	private void filterInput(TextField numField) {
		numField.addEventFilter(KeyEvent.KEY_TYPED, event->{
			if (!isValidInput(event.getCharacter())) {
                event.consume();
            } else if (numField.getText().length() >= 1) {
            	numField.setText(event.getCharacter());
                event.consume();
            }
		}); 
	}
	//Valid input between 1-9 only
	private boolean isValidInput(String input) {
		return input.matches("[1-9]") && input.length() <= 1;
	}

/**
 * Console and Algorithm methods
*/
	public static void printBoard(int[][] board){
        //every 3rd row: print horizontal line
        // i = row, j = column
        for (int i = 0; i < board.length; i++){
            if (i % 3 == 0 && i != 0) {
                System.out.println("- - - - - - - - - - -");
            }
        //every 3rd element, print border to next 'house of 9'
            for (int j = 0; j < board.length; j++){
                if (j % 3 == 0 && j !=0){
                    System.out.print("| ");
                }       
                //separate numbers with space
                if (j == 8){
                    System.out.println(board[i][j]);
                } else { 
                    System.out.print(board[i][j] + " ");
                }
            }
        }
    }

	//find empty spot in board and returns row/col co-ord
	public static int[] findEmpty(int[][] board){
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board.length; j++){
                if (board[i][j] == 0){
                    return new int[]{i,j};
                }
            }
        }
        return null;
    }
	
	//checking if number is valid on input row/col, used to check input. 
    public static boolean validNum(int[][] board, int num, int[] pos){
        //check row,
        for (int i = 0; i < board[0].length; i++){
        //check each element in row if equal to num added. If pos checking is pos inserted, skip
            if (board[pos[0]][i] == num && pos[1] != i){
                return false;
            }
        }
        //check columns
        for (int i = 0; i < board.length; i++){
            if (board[i][pos[1]]== num && pos[0] != i){
                return false;
            }
        }
        //check 3x3 box
        int box_X =  pos[1]/3;
        int box_Y = pos[0]/3;

        for (int i = box_Y * 3; i < box_Y * 3 + 3; i++) {
            for (int j = box_X * 3; j < box_X * 3 + 3; j++){
                if (board[i][j] == num && (i != pos[0] || j != pos[1])) {
                    return false;
                }
            }
        }
        return true;
    }
    
  //recursive backtracking algorithm,
    public static boolean solve(int[][] board){
        //recursion of checking empty spaces
        int[] empty = findEmpty(board);
        if (empty == null){
            return true;    //board filled, solution found
        } else {
            int row = empty[0];
            int column = empty[1];
        
            //Check input numbers 1-9, if valid then will be added to board
            for (int i = 1; i <= 9; i++){
                if (validNum(board, i, new int[]{row, column})){
                    board[row][column] = i;
                    arrayCount++;
                    if (solve(board)){
                        return true;    //board filled and solved
                    }
                    //Backtrack if current placement cannot lead to board filled. 
                    board[row][column] = 0; 
                }
            }
            return false; //no valid numbers for position
        }
    }
    /**
     * Console methods end
     */
    //Sorting alg that stores all board combinations and plays in real time/delay
    public static boolean solve2(int[][] board){
        //recursion of checking empty spaces
        int[] empty = findEmpty(board);
        if (empty == null){
            return true;    //board filled, solution found
        } else {
            int row = empty[0];
            int column = empty[1];
        
            //Check input numbers 1-9, if valid then will be added to board
            for (int i = 1; i <= 9; i++){
                if (validNum(board, i, new int[]{row, column})){
                    board[row][column] = i;
                    
                    //Adds current board to list, creates new board copy and not a reference
                    int[][] boardCopy = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);
                    boardCombinations.add(boardCopy);
                    
                    if (solve2(board)){
                        return true;    //board filled and solved
                    }
                    //Backtrack if current placement cannot lead to board filled. 
                    board[row][column] = 0; 
                }
            }
            return false; //no valid numbers for position
        }
    }
    
    private void showMessage(Label label, String message, int seconds) {
        label.setText(message);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(seconds),new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        label.setText(""); // Clear the message after the specified seconds
                    }
                }));
        timeline.play();
    }
	
	int[][] sudokuBoard1 = {
            { 3, 0, 6, 5, 0, 8, 4, 0, 0 },
            { 5, 2, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 8, 7, 0, 0, 0, 0, 3, 1 },
            { 0, 0, 3, 0, 1, 0, 0, 8, 0 },
            { 9, 0, 0, 8, 6, 3, 0, 0, 5 },
            { 0, 5, 0, 0, 9, 0, 6, 0, 0 },
            { 1, 3, 0, 0, 0, 0, 2, 5, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 7, 4 },
            { 0, 0, 5, 2, 0, 6, 3, 0, 0 }
        };
	int[][] sudokuBoard2 = {
	        {8, 5, 0, 0, 0, 2, 4, 0, 0},
	        {7, 2, 0, 0, 0, 0, 0, 0, 9},
	        {0, 0, 4, 0, 0, 0, 0, 0, 0},
	        {0, 0, 0, 1, 0, 7, 0, 0, 2},
	        {3, 0, 5, 0, 0, 0, 9, 0, 0},
	        {0, 4, 0, 0, 0, 0, 0, 0, 0},
	        {0, 0, 0, 0, 8, 0, 0, 7, 0},
	        {0, 1, 7, 0, 0, 0, 0, 0, 0},
	        {0, 0, 0, 0, 3, 6, 0, 4, 0}
	    };
	int[][] sudokuBoard3 = {
            { 0, 5, 7, 9, 4, 0, 8, 0, 0 },
            { 2, 0, 4, 0, 0, 0, 1, 9, 6 },
            { 3, 9, 0, 1, 0, 0, 0, 0, 5 },
            { 0, 3, 1, 0, 0, 0, 2, 0, 0 },
            { 6, 0, 2, 3, 5, 0, 9, 8, 0 },
            { 5, 0, 0, 2, 0, 7, 0, 0, 0 },
            { 0, 0, 5, 6, 0, 2, 0, 0, 8 },
            { 7, 6, 0, 0, 1, 5, 0, 0, 9 },
            { 0, 0, 8, 7, 3, 0, 0, 0, 0 }
        };
	int[][] sudokuBoardSaved = {
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 }
        };
	
	public static void main(String[] args) {
		launch(args);
	}
}

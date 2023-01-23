package client;

import gameUtils.Packet;
import gameUtils.PieceType;
import gameUtils.PlayerColor;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

public class Game {
    private static PlayerColor myColor;
    private static PlayerColor playerTurn;

    private static final int DIM_WINDOW = 500;
    private static final int DIM_CHESSBOARD = 8;
    private static final int MARGIN = DIM_WINDOW / (4 * DIM_CHESSBOARD + 2);
    private static final int CELL_SIZE = (DIM_WINDOW - MARGIN * 2) / DIM_CHESSBOARD;

    // The board where all the pieces will be stored
    private static final Piece[][] board = new Piece[DIM_CHESSBOARD][DIM_CHESSBOARD];

    private final JFrame window = new JFrame();

    private JPanel chessboardPanel;

    public Game(PlayerColor color) {
        myColor = color;

        playerTurn = PlayerColor.WHITE;
    }

    public static PlayerColor getPlayerColor() {
        return myColor;
    }
    public static PlayerColor getPlayerTurn() {
        return playerTurn;
    }
    public static Piece[][] getBoard() {
        return board;
    }
    public static void changePlayerTurn() {
        playerTurn = playerTurn == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE;
    }


    /**
     * Edit a cell of the board data
     * @param cell Cell to edit
     * @param value Value to assign to the cell
     */
    public static void editBoardCell(Point cell, Piece value) {
        board[cell.y][cell.x] = value;
    }


    /**
     * Move the enemy piece
     * @param packet Packet to get data from
     */
    public void enemyMove(Packet packet) {
        Piece piece = board[packet.from.y][packet.from.x];

        // Delete previous position
        editBoardCell(packet.from, null);

        // If the target cell is a piece, kill it
        Piece pieceTo = board[packet.to.y][packet.to.x];
        if (pieceTo != null) {
            pieceTo.kill();
        }

        if (packet.type != null) {
            piece.kill();

            piece = new Piece(PieceType.QUEEN, myColor == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE);

            chessboardPanel.add(piece);
            chessboardPanel.repaint();
        }

        // Place the piece in the new position
        editBoardCell(packet.to, piece);

        // Move the piece
        piece.setPosition(packet.to.x, packet.to.y);

        // Change the player turn
        changePlayerTurn();
    }


    /**
     * Init the main window
     */
    private void initWindow() {
        window.setTitle("Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(new Dimension(DIM_WINDOW + 19, DIM_WINDOW + 39));

        Image icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("assets/icon.png"))).getImage();

        window.setIconImage(icon);

        window.setBackground(Color.RED);
        window.setLayout(null);
        window.setResizable(false);
        window.setVisible(true);
    }

    /**
     * Draw the chessboard in the UI
     */
    private void initChessboard() {
        final Color BLACK_CELL = new Color(0xFFEFD5),
                    WHITE_CELL = new Color(0x654321);

        // Create and paint the background of the panel that will contain the chessboard
        chessboardPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                for (int i = 0; i < DIM_CHESSBOARD; i++) {
                    for (int j = 0; j < DIM_CHESSBOARD; j++) {
                        if ((i + j) % 2 == 0) {
                            g.setColor(myColor == PlayerColor.WHITE ? BLACK_CELL : WHITE_CELL);
                        } else {
                            g.setColor(myColor == PlayerColor.WHITE ? WHITE_CELL : BLACK_CELL);
                        }
                        g.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        };

        chessboardPanel.setBounds(MARGIN, MARGIN, CELL_SIZE * DIM_CHESSBOARD, CELL_SIZE * DIM_CHESSBOARD);
        chessboardPanel.setBackground(Color.blue);
        chessboardPanel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        chessboardPanel.setLayout(null);

        // Add the panel to the frame
        window.add(chessboardPanel);
    }


    /**
     * Draw the pieces to the UI and initialize the board
     */
    private void initPieces() {

        /* Paint all the pieces in the board */

        final PieceType[] startRow = {
                PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
                PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };

        // Set cell size and fill the board
        Piece.setCellSize(CELL_SIZE);
        Arrays.stream(board).forEach(cell -> Arrays.fill(cell, null));

        // Create, paint and store every piece in the chessboard
        Piece piece;
        for (PlayerColor playerColor : PlayerColor.values()) {
            for (int x = 0; x < DIM_CHESSBOARD; x++) {
                // Add pieces
                piece = new Piece(startRow[x], playerColor);
                piece.setPosition(x, playerColor == myColor ? 7 : 0);
                chessboardPanel.add(piece);

                // Add pawns
                piece = new Piece(PieceType.PAWN, playerColor);
                piece.setPosition(x, playerColor == myColor ? 6 : 1);
                chessboardPanel.add(piece);
            }
        }

        // Repaint to apply changes
        window.repaint();
    }


    /**
     * Start the game
     */
    public void initGame() {
        initWindow();
        initChessboard();
        initPieces();
    }
}

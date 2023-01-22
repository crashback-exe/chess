package client;

import gameUtils.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Piece extends JLabel {
    private PieceType type;
    private final PlayerColor pieceColor;

    private final Point newPosition = new Point();
    private final Point currentPosition = new Point(-1, -1);
    private final int cellSize;

    public Piece(int cellSize, PieceType type, PlayerColor playerColor) {
        super();

        this.cellSize = cellSize;
        this.type = type;
        this.pieceColor = playerColor;

        this.setHorizontalAlignment(JLabel.LEFT);
        this.setVerticalAlignment(JLabel.TOP);

        String colorName = playerColor.toString().toLowerCase();
        String typeName = this.type.toString().toLowerCase();

        String path = "assets/" + colorName + typeName.substring(0, 1).toUpperCase() + typeName.substring(1) + ".png";

        BufferedImage icon = null;

        try {
            icon = ImageIO.read(Objects.requireNonNull(Piece.class.getResource(path)));
        } catch (IOException e) {
            System.err.println("File not found");
            System.exit(-1);
        }

        this.setIcon(new ImageIcon(icon.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH)));
        this.setVisible(true);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            boolean undo = false;
            @Override
            public void mousePressed(MouseEvent e) {
                Piece piece = (Piece) e.getSource();

                if (e.getButton() == MouseEvent.BUTTON1) {
                    undo = false;
                    piece.getParent().setComponentZOrder(piece, 0);

                    Piece.this.currentPosition.x =
                            (piece.getLocation().x + e.getX() - (cellSize >> 1)  + (cellSize >> 1)) / cellSize;
                    Piece.this.currentPosition.y =
                            (piece.getLocation().y + e.getY() - (cellSize >> 1)  + (cellSize >> 1)) / cellSize;
                }
                else {
                    undo = true;

                    Piece.this.setPosition(
                            Piece.this.currentPosition.x,
                            Piece.this.currentPosition.y
                    );
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (undo) return;

                int x = (int) Math.floor(((double) Piece.this.newPosition.x + (cellSize >> 1)) / cellSize);
                int y = (int) Math.floor(((double) Piece.this.newPosition.y + (cellSize >> 1)) / cellSize);

                if (x < 0 || x > 7 || y < 0 || y > 7) {
                    undo = true;

                    Piece.this.setPosition(
                            Piece.this.currentPosition.x,
                            Piece.this.currentPosition.y
                    );
                }

                Piece piece = (Piece) e.getSource();
                Piece.this.newPosition.x = piece.getLocation().x + e.getX() - (cellSize >> 1);
                Piece.this.newPosition.y = piece.getLocation().y + e.getY() - (cellSize >> 1);
                piece.setLocation(Piece.this.newPosition.x, Piece.this.newPosition.y);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (undo) return;

                Point to = new Point();

                to.x = (Piece.this.newPosition.x + (cellSize >> 1)) / cellSize;
                to.y = (Piece.this.newPosition.y + (cellSize >> 1)) / cellSize;

                if (canMove(to)) {
                    Point prevPosition = new Point(currentPosition.x, currentPosition.y);

                    Piece.this.setPosition(to.x, to.y);

                    Client.sendMove(new Packet(prevPosition, currentPosition));

                    Thread recieveThread = new Thread(Client::receiveMove);
                    recieveThread.start();

                    Game.changePlayerTurn();

                    return;
                }

                // Modify the position
                Piece.this.setPosition(
                        Piece.this.currentPosition.x,
                        Piece.this.currentPosition.y
                );
            }
        };

        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
    }

    public boolean canMove(Point cell) {
        if (Game.getPlayerTurn() != Game.getPlayerColor())
            return false;

        Piece[][] board = Game.getBoard();

        // If the user tries to move a piece that isn't his
        if (this.getColor() != Game.getPlayerColor()) {
            return false;
        }

        if (board[cell.y][cell.x] != null && board[cell.y][cell.x].getColor() == Game.getPlayerColor()) {
            return false;
        }

        switch (this.getType()) {
            case PAWN -> {
                int startPositionY = 6;
                int enPassantPositionY = 3;

                Point standardMove = new Point(0, -1);
                Point doubleMove = new Point(0, -2);
                Point[] takeMoves = {
                        new Point(-1, -1),
                        new Point(1, -1)
                };

                // Take move
                for (Point move : takeMoves) {
                    if ((cell.x == currentPosition.x + move.x && cell.y == currentPosition.y + move.y) &&
                            board[cell.y][cell.x] != null) {

                        board[cell.y][cell.x].kill();
                        return true;
                    }
                }

                // En passant
                for (Point move : takeMoves) {
                    if ((cell.x == currentPosition.x + move.x && cell.y == currentPosition.y + move.y) &&
                        board[cell.y + 1][cell.x] != null &&
                        currentPosition.y == enPassantPositionY) {

                        board[cell.y + 1][cell.x].kill();
                        return true;
                    }
                }

                // If the cell in front is occupied
                if (board[cell.y][cell.x] != null) {
                    return false;
                }

                // Standard move
                if (cell.x == currentPosition.x + standardMove.x && cell.y == currentPosition.y + standardMove.y) {
                    return true;
                }

                // Double move
                if (cell.x == currentPosition.x + doubleMove.x && cell.y == currentPosition.y + doubleMove.y &&
                    currentPosition.y == startPositionY) {
                    return true;
                }
            }

            case KNIGHT -> {
                boolean can = false;

                Point[] moves = {
                        new Point(-2, -1),
                        new Point(-1, -2),
                        new Point(1, -2),
                        new Point(2, -1),
                        new Point(2, 1),
                        new Point(1, 2),
                        new Point(-1, 2),
                        new Point(-2, 1)
                };

                for (Point move : moves) {
                    if (cell.x == currentPosition.x + move.x && cell.y == currentPosition.y + move.y) {
                        can = true;
                        break;
                    }
                }

                if (can) {
                    if (board[cell.y][cell.x] != null) {
                        board[cell.y][cell.x].kill();
                    }

                    return true;
                }
            }

//            case BISHOP -> {
//                Point[] directions = {
//                        new Point(-1, -1),
//                        new Point(1, -1),
//                        new Point(1, 1),
//                        new Point(-1, 1)
//                };
//
//                Point ghostBishop = new Point();
//
//                for (Point direction : directions) {
//                    ghostBishop
//                    if () {
//
//                    }
//                }
//            }
        }

        return false;
    }

    public void setPosition(int x, int y) {
        if (x == currentPosition.x && y == currentPosition.y) {
            this.setBounds(x * cellSize, y * cellSize, cellSize, cellSize);
            return;
        }

        if (currentPosition.x != -1 && currentPosition.y != -1) {
            Game.editBoardCell(currentPosition, null);
        }

        currentPosition.x = x;
        currentPosition.y = y;

        this.setBounds(x * cellSize, y * cellSize, cellSize, cellSize);

        Game.editBoardCell(currentPosition, this);
    }

    public void promote(PieceType promotion) throws IllegalArgumentException{
        if (type != PieceType.PAWN || promotion == PieceType.KING || promotion == PieceType.PAWN)
            throw new IllegalArgumentException("Cannot promote " + type.toString().toLowerCase());
        type = promotion;
    }

    public void kill() {
        Game.getBoard()[currentPosition.y][currentPosition.x] = null;
        this.getParent().remove(this);
    }

    public PieceType getType() {
        return type;
    }
    public PlayerColor getColor() {
        return pieceColor;
    }
}

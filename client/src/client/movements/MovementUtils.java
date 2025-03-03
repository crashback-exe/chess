package client.movements;

import client.Game;
import client.piece.Piece;
import gameUtils.PieceType;

import java.awt.*;

public class MovementUtils {
    private static Piece[][] board = new Piece[Game.DIM_CHESSBOARD][Game.DIM_CHESSBOARD];

    /**
     * Check if the move made is the one being checked
     *
     * @param from The starting point
     * @param to   The arrival point
     * @param move The move
     * @return <code>true</code> if the move made is the move checked, <code>false</code> if it's not
     */
    public static boolean checkMove(Point from, Point to, Point move) {
        return to.x == from.x + move.x &&
                to.y == from.y + move.y;
    }

    /**
     * Set the current board
     *
     * @param board The board to set
     */
    public static void setBoard(Piece[][] board) {
        MovementUtils.board = board;
    }

    /**
     * Check if the path between two points is valid
     *
     * @param from      The starting point
     * @param to        The arrival point
     * @param direction The direction
     * @return <code>true</code> if the path is valid, <code>false</code> if it's not valid
     */
    public static boolean isPathFree(Point from, Point to, Point direction) {
        Point ghostPiece = new Point(from.x, from.y);

        while (ghostPiece.x != to.x - direction.x || ghostPiece.y != to.y - direction.y) {
            ghostPiece.x += direction.x;
            ghostPiece.y += direction.y;

            if (board[ghostPiece.y][ghostPiece.x] != null) {
                return false;
            }
        }

        return true;
    }


    public static boolean checkCastleRules(Point rookPosition) {
        final Point KING_POSITION = new Point(4, 7);

        return
                board[KING_POSITION.y][KING_POSITION.x] != null &&
                        board[KING_POSITION.y][KING_POSITION.x].getType() == PieceType.KING &&
                        board[KING_POSITION.y][KING_POSITION.x].hasNotMoved() &&
                        board[rookPosition.y][rookPosition.x] != null &&
                        board[rookPosition.y][rookPosition.x].getType() == PieceType.ROOK &&
                        board[rookPosition.y][rookPosition.x].hasNotMoved();
    }
}

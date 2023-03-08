package client.movements;

import client.Game;
import client.piece.Piece;
import gameUtils.PieceType;
import gameUtils.SpecialMoveType;

import java.awt.*;

public class QueensideCastle implements Movement {
    private final Point ROOK_POSITION = new Point(0, 7);
    private final Point move = new Point(-2, 0);

    @Override
    public boolean canMove(Point from, Point to) {
        return MovementUtils.checkMove(from, to, move) &&
                MovementUtils.checkCastleRules(ROOK_POSITION) &&
                MovementUtils.isPathFree(from, ROOK_POSITION, new Point(-1, 0));
    }

    @Override
    public SpecialMoveType getSpecialMove() {
        return SpecialMoveType.QUEENSIDE_CASTLE;
    }
}

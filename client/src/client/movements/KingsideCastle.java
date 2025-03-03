package client.movements;

import gameUtils.SpecialMoveType;

import java.awt.*;

public class KingsideCastle implements Movement {
    private final Point ROOK_POSITION = new Point(7, 7);
    private final Point move = new Point(2, 0);

    @Override
    public boolean canMove(Point from, Point to) {
        return MovementUtils.checkMove(from, to, move) &&
                MovementUtils.checkCastleRules(ROOK_POSITION) &&
                MovementUtils.isPathFree(from, ROOK_POSITION, new Point(1, 0));
    }

    @Override
    public SpecialMoveType getSpecialMove() {
        return SpecialMoveType.KINGSIDE_CASTLE;
    }
}

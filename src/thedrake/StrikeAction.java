package thedrake;

import java.util.List;

public class StrikeAction extends TroopAction{

    public StrikeAction(Offset2D offset) {
        super(offset);
    }

    public StrikeAction(int offsetX, int offsetY) {
        super(offsetX, offsetY);
    }
    @Override
    public List<Move> movesFrom(BoardPos origin, PlayingSide side, GameState state) {
        return null;
    }
}

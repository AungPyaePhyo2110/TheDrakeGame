package thedrake;

import java.io.PrintWriter;
import java.util.Optional;

import static java.lang.Math.abs;

public class GameState implements JSONSerializable {
    private final Board board;
    private final PlayingSide sideOnTurn;
    private final Army blueArmy;
    private final Army orangeArmy;
    private final GameResult result;

    public GameState(
            Board board,
            Army blueArmy,
            Army orangeArmy) {
        this(board, blueArmy, orangeArmy, PlayingSide.BLUE, GameResult.IN_PLAY);
    }

    public GameState(
            Board board,
            Army blueArmy,
            Army orangeArmy,
            PlayingSide sideOnTurn,
            GameResult result) {
        this.board = board;
        this.sideOnTurn = sideOnTurn;
        this.blueArmy = blueArmy;
        this.orangeArmy = orangeArmy;
        this.result = result;
    }

    public Board board() {
        return board;
    }

    public PlayingSide sideOnTurn() {
        return sideOnTurn;
    }

    public GameResult result() {
        return result;
    }

    public Army army(PlayingSide side) {
        if (side == PlayingSide.BLUE) {
            return blueArmy;
        }

        return orangeArmy;
    }

    public Army armyOnTurn() {
        return army(sideOnTurn);
    }

    public Army armyNotOnTurn() {
        if (sideOnTurn == PlayingSide.BLUE)
            return orangeArmy;

        return blueArmy;
    }

    // Returns the tile located on the board at position pos.
    // So it has to check if there is a unit of some player's army
    // at that position and if not, returns a tile from the board object
    public Tile tileAt(TilePos pos) {
        // Místo pro váš kód
        if(blueArmy.boardTroops().troopPositions().contains(pos))
            return blueArmy.boardTroops().at(pos).get();
        else if(orangeArmy.boardTroops().troopPositions().contains(pos))
            return orangeArmy.boardTroops().at(pos).get();

        if(board.at(pos).canStepOn())
            return BoardTile.EMPTY;
        else
            return BoardTile.MOUNTAIN;

    }

    private boolean canStepFrom(TilePos origin) {
        // Místo pro váš kód
        if(result != GameResult.IN_PLAY)
            return  false;
        if(origin == BoardPos.OFF_BOARD)
            return false;
        if(!blueArmy.boardTroops().isLeaderPlaced() || blueArmy.boardTroops().isPlacingGuards() ||
                !orangeArmy.boardTroops().isLeaderPlaced() || orangeArmy.boardTroops().isPlacingGuards())
        {
            return false;
        }

        Tile foundTile = tileAt(origin);
        if(foundTile.hasTroop())
        {
            TroopTile troopTile = (TroopTile) foundTile;
            return troopTile.side() == sideOnTurn;
        }
        return false;

    }

    private boolean canStepTo(TilePos target) {
        // Místo pro váš kód
        if(result != GameResult.IN_PLAY)
            return  false;
        if(target == BoardPos.OFF_BOARD)
            return false;
        if(!blueArmy.boardTroops().isLeaderPlaced() || blueArmy.boardTroops().isPlacingGuards() ||
                !orangeArmy.boardTroops().isLeaderPlaced() || orangeArmy.boardTroops().isPlacingGuards())
        {
            return false;
        }

        Tile foundTile = tileAt(target);
        return foundTile.canStepOn();
    }

    private boolean canCaptureOn(TilePos target) {
        // Místo pro váš kód
        if(result != GameResult.IN_PLAY)
            return  false;
        if(target == BoardPos.OFF_BOARD)
            return false;
        if(!blueArmy.boardTroops().isLeaderPlaced() || blueArmy.boardTroops().isPlacingGuards() ||
                !orangeArmy.boardTroops().isLeaderPlaced() || orangeArmy.boardTroops().isPlacingGuards())
        {
            return false;
        }

        Tile targetTile = tileAt(target);
        if(!targetTile.hasTroop())
            return false;
        TroopTile troopTile = (TroopTile) targetTile;
        return ((TroopTile) targetTile).side() != sideOnTurn;
    }

    public boolean canStep(TilePos origin, TilePos target) {
        return canStepFrom(origin) && canStepTo(target);
    }

    public boolean canCapture(TilePos origin, TilePos target) {
        return canStepFrom(origin) && canCaptureOn(target);
    }

    public boolean canPlaceFromStack(TilePos target) {
        // Místo pro váš kód
        if(result != GameResult.IN_PLAY)
            return  false;
        if(target == BoardPos.OFF_BOARD)
            return false;
        if(!tileAt(target).canStepOn())
        {
            return false;
        }
        if(armyOnTurn().stack().isEmpty())
            return false;
        if(!armyOnTurn().boardTroops().isLeaderPlaced())
        {
            if(armyOnTurn() == orangeArmy && target.row() != board.dimension())
                return false;
            else if(armyOnTurn() == blueArmy && target.row() != 1)
                return false;
           else
           {
               return tileAt(target).canStepOn();
           }
        }
        if(armyOnTurn().boardTroops().isPlacingGuards())
        {
            if(!armyOnTurn().boardTroops().leaderPosition().isNextTo(target))
                return false;
            else
            {
                if(!board.at(target).canStepOn())
                    return false;
            }
        }
        boolean foundPlace = false;
        for(BoardPos position : armyOnTurn().boardTroops().troopPositions())
        {
            if(position.isNextTo(target))
                foundPlace = true;
        }
        return foundPlace;
    }

    public GameState stepOnly(BoardPos origin, BoardPos target) {
        if (canStep(origin, target))
            return createNewGameState(
                    armyNotOnTurn(),
                    armyOnTurn().troopStep(origin, target), GameResult.IN_PLAY);

        throw new IllegalArgumentException();
    }

    public GameState stepAndCapture(BoardPos origin, BoardPos target) {
        if (canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if (armyNotOnTurn().boardTroops().leaderPosition().equals(target))
                newResult = GameResult.VICTORY;

            return createNewGameState(
                    armyNotOnTurn().removeTroop(target),
                    armyOnTurn().troopStep(origin, target).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    public GameState captureOnly(BoardPos origin, BoardPos target) {
        if (canCapture(origin, target)) {
            Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
            GameResult newResult = GameResult.IN_PLAY;

            if (armyNotOnTurn().boardTroops().leaderPosition().equals(target))
                newResult = GameResult.VICTORY;

            return createNewGameState(
                    armyNotOnTurn().removeTroop(target),
                    armyOnTurn().troopFlip(origin).capture(captured), newResult);
        }

        throw new IllegalArgumentException();
    }

    public GameState placeFromStack(BoardPos target) {
        if (canPlaceFromStack(target)) {
            return createNewGameState(
                    armyNotOnTurn(),
                    armyOnTurn().placeFromStack(target),
                    GameResult.IN_PLAY);
        }

        throw new IllegalArgumentException();
    }

    public GameState resign() {
        return createNewGameState(
                armyNotOnTurn(),
                armyOnTurn(),
                GameResult.VICTORY);
    }

    public GameState draw() {
        return createNewGameState(
                armyOnTurn(),
                armyNotOnTurn(),
                GameResult.DRAW);
    }

    private GameState createNewGameState(Army armyOnTurn, Army armyNotOnTurn, GameResult result) {
        if (armyOnTurn.side() == PlayingSide.BLUE) {
            return new GameState(board, armyOnTurn, armyNotOnTurn, PlayingSide.BLUE, result);
        }

        return new GameState(board, armyNotOnTurn, armyOnTurn, PlayingSide.ORANGE, result);
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.print("{\"result\":");
        result.toJSON(writer);
        writer.print(",\"board\":");
        board.toJSON(writer);
        writer.print(",\"blueArmy\":");
        blueArmy.toJSON(writer);
        writer.print(",\"orangeArmy\":");
        orangeArmy.toJSON(writer);
        writer.print("}");
    }
}

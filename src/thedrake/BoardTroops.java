package thedrake;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BoardTroops {
    private final PlayingSide playingSide;
    private final Map<BoardPos, TroopTile> troopMap;
    private final TilePos leaderPosition;
    private final int guards;

    public BoardTroops(PlayingSide playingSide) {
        // Místo pro váš kód
        this.playingSide = playingSide;
        this.troopMap = Collections.emptyMap();
        leaderPosition = TilePos.OFF_BOARD;
        this.guards = 0;
    }

    public BoardTroops(
            PlayingSide playingSide,
            Map<BoardPos, TroopTile> troopMap,
            TilePos leaderPosition,
            int guards) {
        // Místo pro váš kód
        this.playingSide = playingSide;
        this.troopMap = troopMap;
        this.leaderPosition = leaderPosition;
        this.guards = guards;
    }

    public Optional<TroopTile> at(TilePos pos) {
        // Místo pro váš kód
        TroopTile troopTile = troopMap.get(pos);
        if(troopTile == null)
        {
            return Optional.empty();
        }
        return Optional.ofNullable(troopTile);
    }

    public PlayingSide playingSide() {
        // Místo pro váš kód
        return playingSide;
    }

    public TilePos leaderPosition() {
        // Místo pro váš kód
        return leaderPosition;
    }

    public int guards() {
        // Místo pro váš kód
        return guards;
    }

    public boolean isLeaderPlaced() {
        // Místo pro váš kód
        if(leaderPosition == TilePos.OFF_BOARD)
        {
            return false;
        }
        return true;
    }

    public boolean isPlacingGuards() {
        // Místo pro váš kód
        if(isLeaderPlaced())
        {
           if(guards == 2)
               return  false;
           return true;
        }
        else
        {
            return false;
        }
    }

    public Set<BoardPos> troopPositions() {
        // Místo pro váš kóds
        return troopMap.keySet();
    }

    public BoardTroops placeTroop(Troop troop, BoardPos target) {
        // Místo pro váš kód
        if(troopMap.containsKey(target))
            throw new IllegalArgumentException();
        TroopTile newTroopTile = new TroopTile(troop,playingSide,TroopFace.AVERS);
        Map<BoardPos,TroopTile> newTroopMap = new HashMap<>(troopMap);


        newTroopMap.put(target,newTroopTile);

        if(newTroopMap.size() == 1)
        {
            return new BoardTroops(playingSide,newTroopMap,target,guards);
        }
        if(newTroopMap.size()==2)
        {
            return  new BoardTroops(playingSide,newTroopMap,leaderPosition,1);
        }
        if(newTroopMap.size()==3)
        {
            return  new BoardTroops(playingSide,newTroopMap,leaderPosition,2);
        }

        return  new BoardTroops(playingSide,newTroopMap,leaderPosition,guards);
    }

    public BoardTroops troopStep(BoardPos origin, BoardPos target) {
        // Místo pro váš kód
        if(!isLeaderPlaced() || isPlacingGuards())
            throw new IllegalStateException();

        if(!troopMap.containsKey(origin) || troopMap.containsKey(target))
            throw new IllegalArgumentException();

        TroopTile movingTroopTile = troopMap.get(origin);

        troopMap.put(target,movingTroopTile.flipped());
        troopMap.remove(origin);
        if(origin.equals(leaderPosition))
        {
            return new BoardTroops(playingSide,troopMap,target,guards);
        }

        return new BoardTroops(playingSide,troopMap,leaderPosition,guards);

    }

    public BoardTroops troopFlip(BoardPos origin) {
        if (!isLeaderPlaced()) {
            throw new IllegalStateException(
                    "Cannot move troops before the leader is placed.");
        }

        if (isPlacingGuards()) {
            throw new IllegalStateException(
                    "Cannot move troops before guards are placed.");
        }

        if (!at(origin).isPresent())
            throw new IllegalArgumentException();

        Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
        TroopTile tile = newTroops.remove(origin);
        newTroops.put(origin, tile.flipped());

        return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
    }

    public BoardTroops removeTroop(BoardPos target) {
        // Místo pro váš kód

        if(!isLeaderPlaced() || isPlacingGuards())
            throw new IllegalStateException();

        if(!troopMap.containsKey(target))
            throw new IllegalArgumentException();

        Map<BoardPos,TroopTile> newTroopMap = new HashMap<>(troopMap);
        newTroopMap.remove(target);

        if(target.equals(leaderPosition))
            return new BoardTroops(playingSide,newTroopMap,TilePos.OFF_BOARD,guards);
        return new BoardTroops(playingSide,newTroopMap,leaderPosition,guards);
    }
}

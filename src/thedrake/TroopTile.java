package thedrake;

public class TroopTile implements Tile{

    private final Troop troop;
    private final PlayingSide side;

    private final TroopFace face;

    public TroopTile(Troop troop, PlayingSide side, TroopFace face) {
        this.troop = troop;
        this.side = side;
        this.face = face;
    }

    // Returns the color of the player owning the unit on this tile
    public PlayingSide side()
    {
        return side;
    }

    public TroopFace face()
    {
        return  face;
    }

    public Troop troop()
    {
        return troop;
    }



    @Override
    public boolean canStepOn() {
        return false;
    }

    @Override
    public boolean hasTroop() {
        return true;
    }

    public TroopTile flipped()
    {
        if(face == TroopFace.AVERS)
        {
            return new TroopTile(troop, side , TroopFace.REVERS);

        }
        else
        {
            return new TroopTile(troop, side , TroopFace.AVERS);

        }
    }

}

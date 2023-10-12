package thedrake;

public class Board {

    private final int dimension;
    private final int width;
    private final int height;

    private final BoardTile[][] tiles;
    // Konstruktor. Vytvoří čtvercovou hrací desku zadaného rozměru, kde všechny dlaždice jsou prázdné, tedy BoardTile.EMPTY
    public Board(int dimension) {
        // Místo pro váš kód
        this.dimension = dimension;
        this.width = dimension;
        this.height = dimension;
        this.tiles = new BoardTile[dimension][dimension];
        for ( int i = 0 ; i < dimension ; i++)
        {
            for( int j = 0 ; j < dimension ; j++ )
            {
                tiles[i][j] = BoardTile.EMPTY;
            }
        }


    }

    // Rozměr hrací desky
    public int dimension() {
        // Místo pro váš kód
        return this.dimension;
    }

    // Vrací dlaždici na zvolené pozici.
    public BoardTile at(TilePos pos) {
        // Místo pro váš kód
        return tiles[pos.i()][pos.j()];
    }

    // Vytváří novou hrací desku s novými dlaždicemi. Všechny ostatní dlaždice zůstávají stejné
    public Board withTiles(TileAt... ats) {
        // Místo pro váš kód
        Board newBoard = new Board(dimension);
        for( int i = 0 ; i < dimension ; i++ )
        {
            for ( int j = 0 ; j < dimension ; j++)
            {
                newBoard.tiles[i][j] = this.tiles[i][j];
            }
        }

        for ( TileAt newtile : ats)
        {
            newBoard.tiles[newtile.pos.i()][newtile.pos.j()] = newtile.tile;
        }
        return newBoard;
    }

    // Vytvoří instanci PositionFactory pro výrobu pozic na tomto hracím plánu
    public PositionFactory positionFactory() {
        // Místo pro váš kód
        return new PositionFactory(dimension);
    }

    public static class TileAt {
        public final BoardPos pos;
        public final BoardTile tile;

        public TileAt(BoardPos pos, BoardTile tile) {
            this.pos = pos;
            this.tile = tile;
        }
    }
}


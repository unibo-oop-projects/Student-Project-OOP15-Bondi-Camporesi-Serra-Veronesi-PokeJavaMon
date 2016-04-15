package model.map.tile;

public class Sign extends AbstractTile {

    public final static String TILE_NAME = "SIGN";
	
	private String message;
    
    public Sign(final int x, final int y, final String message) {
        super(Tile.TileType.SIGN, Direction.SOUTH, x, y);
        this.message = message;
    }
    
    public String getMessage() {
        return this.message;
    }

}

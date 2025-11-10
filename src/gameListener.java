public interface gameListener {
    void updateTileBagPanel();
    void updatePlayerTiles(Player player);
    void updateScore(int playerIndex, int newScore);
}

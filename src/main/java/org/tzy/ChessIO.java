package org.tzy;

public interface ChessIO {

    void load(byte[] data, ChessBoard board);

    byte[] save(ChessBoard board);

    String getFileTypeDescription();

    String getFileExtension();

}

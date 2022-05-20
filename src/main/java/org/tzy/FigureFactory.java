package org.tzy;

import org.tzy.figure.*;

public class FigureFactory {

	private FigureFactory() {}

	public static Figure createFigure(Color color, String type, String pos, int firstTurn) {
		if (pos.length() != 2) {
			throw new IllegalArgumentException("Invalid position: \"" + pos + "\"");
		}
		int x = pos.toLowerCase().charAt(0) - 97;
		int y = 7 - (pos.charAt(1) - 49);
		if (x < 0 || x > 7 || y < 0 || y > 7) {
			throw new IllegalArgumentException("Position not on board: \"" + pos + "\"");
		}
		Figure f;
		switch (type.toLowerCase()) {

			case "knight":
				f = new Knight(color, ChessGame.getBoard().getField(x, y));
				break;

			case "king":
				f = new King(color, ChessGame.getBoard().getField(x, y));
				break;

			default:
				throw new IllegalArgumentException("Unknown Figure: \"" + type + "\"");
		}
		f.setFirstTurn(firstTurn);
		return f;
	}

}

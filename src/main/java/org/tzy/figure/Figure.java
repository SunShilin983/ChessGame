package org.tzy.figure;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import org.tzy.ChessField;
import org.tzy.ChessGame;
import org.tzy.Helper;

import javax.security.auth.callback.LanguageCallback;
import javax.tools.Diagnostic;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Figure implements Serializable {

	public transient static final DataFormat CHESS_FIGURE = new DataFormat("chess.figure");
	private transient static Map<String, Image> imageCache = new HashMap<>();
	transient ChessField field;

	int x, y = -1;
	Color color;
	private String name;
	private String imageFileName;
	private int firstTurn = -1;

	Figure(Color color, String name, ChessField field) {
		this.color = color;
		this.name = name;
		setField(field);
		imageFileName = "images/" + color.getName() + "_" + name + ".png";
		if (!imageCache.containsKey(imageFileName)) {
			imageCache.put(imageFileName, Helper.loadImage(imageFileName, 50, 50));
		}
		if (field != null) {
			this.x = field.getX();
			this.y = field.getY();
			field.setFigure(this, true);
		}
	}

	Figure(Color color, String name, ChessField field, boolean graphic) {
		this.color = color;
		this.name = name;
		setField(field);
		imageFileName = "images/" + color.getName() + "_" + name + ".png";
		if (!imageCache.containsKey(imageFileName)) {
			imageCache.put(imageFileName, Helper.loadImage(imageFileName, 50, 50));
		}
		if (field != null) {
			this.x = field.getX();
			this.y = field.getY();
			field.setFigure(this, graphic);
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Color getColor() {
		return color;
	}

	public String getName() {
		return name;
	}

	public String getPos() {
		return ((char) (x + 97)) + "" + ((char) (7 - y + 49));
	}

	//returns whether the Figure was successfully moved
	public Figure move(ChessField field, boolean graphic) {
		Figure killedFigure = field.getFigure();
		field.setFigure(this, graphic);
		if (this.field != null) {
			this.field.setFigure(null, graphic);
		}
		int oldX = x;
		int oldY = y;
		x = field.getX();
		y = field.getY();
		setField(field);
		return killedFigure;
		/*Figure postFigure = postTurnAction(ChessGame.getBoard().getField(oldX, oldY), field, graphic);
		if (graphic && firstTurn < 0) {
			firstTurn = field.getBoard().getCurrentTurn();
		}
		if (graphic && (killedFigure != null || !(postFigure instanceof Pawn))) {
			ChessGame.getBoard().set50MoveRuleTurns(0);
		}
		return killedFigure == null ? postFigure : killedFigure;*/
	}

	public boolean canMoveTo(ChessField field) {
		return canMove() && getAllAccessibleFields().contains(field);
	}

	public boolean canMove() {
		if(field.getBoard().isGameState()) {
			return false;
		}
		if (field.getFigure() instanceof King) {
			Knight knight = field.getBoard().getKnight();
			return knight.getAccessibleFields().contains(field);
		}
		if (field.getFigure() instanceof Knight) {
			King king = field.getBoard().getKing();
			return king.getAccessibleFields().contains(field);
		}
		return false;
	}

	public void setField(ChessField field) {
		this.field = field;
	}

	public ChessField getField() {
		return field;
	}

	public ImageView getImageView() {
		return new ImageView(imageCache.get(imageFileName));
	}

	public Image getImage() {
		return imageCache.get(imageFileName);
	}

	//this also considers the king being in check
	public List<ChessField> getAllAccessibleFields() {
		List<ChessField> fields = getAccessibleFields();

		if (field.getFigure() instanceof King) {
			Knight knight = field.getBoard().getKnight();

			List<ChessField> trueFields = new ArrayList<>();
			for(ChessField to : fields) {

				// cat be attacked after move
				if(knight.getAccessibleFields().contains(to)) {
					trueFields.add(to);
				}

				King king = new King(Color.BLACK, to, false);
				// cat attack again after move
				if(king.getAccessibleFields().contains(knight.getField())) {
					trueFields.add(to);
				}
				to.getBoard().reset(to);
			}
			fields = trueFields;
		}

		if(field.getFigure() instanceof Knight) {
			King king = field.getBoard().getKing();

			List<ChessField> trueFields = new ArrayList<>();
			for(ChessField to : fields) {

				// cat be attacked after move
				if(king.getAccessibleFields().contains(to)) {
					trueFields.add(to);
				}

				// cat attack again after move
				Knight knight = new Knight(Color.BLACK, to, false);
				if(knight.getAccessibleFields().contains(king.getField())) {
					trueFields.add(to);
				}
				to.getBoard().reset(to);
			}
			fields = trueFields;
		}

		return fields;
	}

	public int getFirstTurn() {
		return firstTurn;
	}

	public void setFirstTurn(int firstTurn) {
		this.firstTurn = firstTurn;
	}

	//returns a list of all accessible fields of this figure, including fields with opponent's figures
	//that can be beaten
	public abstract List<ChessField> getAccessibleFields();

	@Override
	public String toString() {
		return "Figure:<" + color.getName() +
				" " + name + " " +
				"x=" + x + " y=" + y +
				" Field=" + field + ">";
	}
}

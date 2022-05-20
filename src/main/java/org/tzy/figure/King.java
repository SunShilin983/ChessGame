package org.tzy.figure;

import org.tzy.ChessField;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class King extends Figure {

	public King(Color color, ChessField field) {
		super(color, "king", field);
	}

	public King(Color color, ChessField field, boolean graphic) {
		super(color, "king", field, graphic);
	}

	@Override
	public List<ChessField> getAccessibleFields() {
		List<ChessField> fields = new ArrayList<>();
		addField(x + 1, y + 1, fields);
		addField(x + 1, y - 1, fields);
		addField(x - 1, y + 1, fields);
		addField(x - 1, y - 1, fields);
		addField(x + 1, y, fields);
		addField(x - 1, y, fields);
		addField(x, y + 1, fields);
		addField(x, y - 1, fields);

		return fields;
	}

	public boolean isCheck() {
		return field.getBoard().getAllAccessibleFields(color.revert()).contains(field);
	}

	public boolean isCheckMate() {
		if (isCheck()) {
			for (Figure figure : field.getBoard().getFigures(color)) {
				if (figure.getAllAccessibleFields().size() > 0) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isStaleMate() {
		if (!isCheck()) {
			for (Figure figure : field.getBoard().getFigures(color)) {
				if (figure.getAllAccessibleFields().size() > 0) {
					return false;
				}
			}
		}
		return true;
	}

	private void addField(int x, int y, List<ChessField> fields) {
		ChessField field = this.field.getBoard().getField(x, y);
		if (field != null) {
			fields.add(field);
		}
	}
}

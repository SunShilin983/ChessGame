package org.tzy.figure;

import org.tzy.ChessField;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Figure {

	public Knight(Color color, ChessField field) {
		super(color, "knight", field);
	}

	public Knight(Color color, ChessField field, boolean graphic) {
		super(color, "knight", field, graphic);
	}

	@Override
	public List<ChessField> getAccessibleFields() {
		List<ChessField> fields = new ArrayList<>();
		addField(x + 2, y + 1, fields);
		addField(x + 2, y - 1, fields);
		addField(x + 1, y + 2, fields);
		addField(x + 1, y - 2, fields);
		addField(x - 2, y + 1, fields);
		addField(x - 2, y - 1, fields);
		addField(x - 1, y + 2, fields);
		addField(x - 1, y - 2, fields);
		return fields;
	}

	private void addField(int x, int y, List<ChessField> fields) {
		ChessField field = this.field.getBoard().getField(x, y);
		if (field != null) {
			fields.add(field);
		}
	}
}

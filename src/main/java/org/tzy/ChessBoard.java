package org.tzy;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.tzy.figure.Color;
import org.tzy.figure.Figure;
import org.tzy.figure.King;
import org.tzy.figure.Knight;

import java.io.File;
import java.util.*;
/**
 * ChessBoard class.
 *
 * <p>Note that the {@code start} method is abstract and must be overridden.
 * The {@code init} and {@code stop} methods have concrete implementations
 * that do nothing.</p>
 *
 * <p>
 * The Java launcher loads and initializes the specified Application class
 * on the JavaFX Application Thread. If there is no main method in the
 * Application class, or if the main method calls Application.launch(), then
 * an instance of the Application is then constructed on the JavaFX Application
 * Thread.
 * </p>
 *
 * <p>
 * The {@code init} method is called on the launcher thread, not on the
 * JavaFX Application Thread.
 * This means that an application must not construct a {@link Scene}
 * or a {@link Stage} in the {@code init} method.
 * An application may construct other JavaFX objects in the {@code init}
 * method.
 * </p>
 *
 * <p>
 * All the unhandled exceptions on the JavaFX application thread that occur during
 * event dispatching, running animation timelines, or any other code, are forwarded
 * to the thread's {@link java.lang.Thread.UncaughtExceptionHandler uncaught
 * exception handler}.
 * </p>
 *
 *
 */
public class ChessBoard extends GridPane {

    private ChessField[] fields = new ChessField[64];
    private Map<Color, Set<ChessField>> attackedFields = new HashMap<>();
    private int currentTurn = 1;
    private int ruleOf50 = 0;
    private String username;
    private boolean gameState = false;
    private Map<String, ChessIO> io = new LinkedHashMap<>();

    ChessBoard() {
        //resetAttackedFields();
        for (int i = 0; i < 64; i++) {
            int x = getX(i);
            int y = getY(i);
            ChessField field = new ChessField(this, x, y);
            add(field, x, y);
            fields[i] = field;
        }
        //recalculateAttackedFields();
    }

    private void resetAttackedFields() {
        attackedFields.put(Color.BLACK, new HashSet<>());
        attackedFields.put(Color.WHITE, new HashSet<>());
    }

    private int getX(int index) {
        return index % 8;
    }

    private int getY(int index) {
        return (index - getX(index)) / 8;
    }

    public ChessField getField(int x, int y) {
        return x < 0 || x > 7 || y < 0 || y > 7 ? null : fields[y * 8 + x];
    }

    public void setFigure(Figure figure) {
        getField(figure.getX(), figure.getY()).setFigure(figure, true);
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrenTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public int get50MoveRuleTurns() {
        return ruleOf50;
    }

    public void set50MoveRuleTurns(int turns) {
        this.ruleOf50 = turns;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    void nextTurn() {
        currentTurn++;
        ruleOf50++;
        //recalculateAttackedFields();
        gameStateTest();
    }

    public Color getTurn() {
        return currentTurn % 2 == 0 ? Color.BLACK : Color.WHITE;
    }

    void gameStateTest() {

        for(ChessField field : fields) {
            if((field.getFigure() != null) &&
                    (field.getX() == 6) && (field.getY() == 7)) {
                this.gameState = true;
                ChessGame.displayStatusText("Congratulations, You win.");
                return;
            }
        }
/*
        King king = getKing();
        if (king != null) {
            if (king.isCheck()) {
                if (king.isCheckMate()) {
                    ChessGame.displayStatusText("Check mate! " + king.getColor().revert().getFancyName() + " wins.");
                    return;
                } else {
                    ChessGame.displayStatusText("Check! " + king.getColor().getFancyName() + " has to defend.");
                    return;
                }
            } else if (king.isStaleMate()) {
                ChessGame.displayStatusText("Stalemate! " + king.getColor().getFancyName() + " can't move.");
                return;
            }
        }*/
        if (ruleOf50 >= 100) {
            ChessGame.displayStatusText("50-move-rule applies");
        }
        ChessGame.displayStatusText("");
    }

/*    public void recalculateAttackedFields() {
        resetAttackedFields();
        Arrays.stream(fields)
                .filter(f -> f.figure != null && f.figure.getColor() == Color.WHITE)
                .forEach(f -> attackedFields.get(Color.WHITE).addAll(f.getFigure().getAccessibleFields()));
        Arrays.stream(fields)
                .filter(f -> f.figure != null && f.figure.getColor() == Color.BLACK)
                .forEach(f -> attackedFields.get(Color.BLACK).addAll(f.getFigure().getAccessibleFields()));
    }*/

    public Set<ChessField> getAllAccessibleFields(Color color) {
        return attackedFields.get(color);
    }

    public King getKing() {
        for (ChessField field : fields) {
            if (field.figure instanceof King) {
                return (King) field.figure;
            }
        }
        return null;
    }

    public Knight getKnight() {
        for (ChessField field : fields) {
            if (field.figure instanceof Knight) {
                return (Knight) field.figure;
            }
        }
        return null;
    }

    public boolean isGameState() {
        return gameState;
    }

    public List<Figure> getFigures() {
        return getFigures(null);
    }

    public List<Figure> getFigures(Color color) {
        List<Figure> figures = new ArrayList<>();
        Arrays.stream(fields)
                .filter(f -> f.figure != null && (color == null || f.figure.getColor() == color))
                .forEach(f -> figures.add(f.figure));
        return figures;
    }

    public void reset(ChessField field) {
        for (ChessField o : fields) {
            if(field.equals(o)) {
                field.setFigure(null, true);
            }
        }
    }

    public void clear() {
        for (ChessField field : fields) {
            field.setFigure(null, true);
        }
        //recalculateAttackedFields();
        currentTurn = 1;
        ruleOf50 = 0;
        ChessGame.displayStatusText("");
    }

    public void setIO(ChessIO io) {
        this.io.put(io.getFileExtension(), io);
    }

    public Map<String, ChessIO> getIO() {
        return io;
    }

    void loadFromResource(String resource) {
        load(getFileExtension(resource), Helper.loadDataFromResource(resource));
    }

    public void load(File file) {
        load(getFileExtension(file.getName()), Helper.loadDataFromFile(file));
    }

    private String getFileExtension(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    private void load(String type, byte[] s) {
        clear();
        io.get(type).load(s, this);
        //recalculateAttackedFields();
        //gameStateTest();
    }

    public void save(File file) {
        byte[] s = io.get(
                        getFileExtension(
                                file.getName()
                        ))
                .save(this);
        Helper.saveDataToFile(s, file);
    }
}

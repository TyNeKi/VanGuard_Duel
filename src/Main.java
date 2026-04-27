import gameengine.GUIStartScreen;


public class Main {
    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(() -> new GUIStartScreen().setVisible(true));
    }
}
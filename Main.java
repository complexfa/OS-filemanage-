public class Main {
    public static void main(String[] args) {
        Function fc = new Function();
        GUI gui = new GUI(fc);
        gui.getfunction().setGui(gui);
    }
}

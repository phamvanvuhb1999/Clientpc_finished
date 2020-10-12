public class ClientRun {
    public static void main(String[] args){
        ClientView1 view = new ClientView1();
        ClientControl control = new ClientControl(view);
        view.setVisible(true);
    }
}

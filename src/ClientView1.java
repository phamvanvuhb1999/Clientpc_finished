import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


import java.awt.event.*;
import java.awt.*;

public class ClientView1 extends JFrame implements ActionListener{
    private JTextField txtHostname;
    private JTextField txtPortnumber;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnConnect;
    public JTextField message;

    public ClientView1(){
        super("CLIENT Login");
        initContent();
    }
    public void initContent(){
        this.setSize(600, 450);
        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        txtHostname = new JTextField(20);
        txtPortnumber = new JTextField(6);
        btnLogin = new JButton("Login");
        btnConnect = new JButton("Connect");
        message = new JTextField(100);

        JLabel userLabel = new JLabel("Username: ");
        JLabel passLabel = new JLabel("Password: ");
        JLabel hostLabel = new JLabel("HostIpAddress: ");
        JLabel portLabel = new JLabel("PortNumber: ");

        //design content
        hostLabel.setBounds(50, 100, 100, 25);
        txtHostname.setBounds(150, 100, 100, 25);
        portLabel.setBounds(270, 100, 80, 25);
        txtPortnumber.setBounds(350, 100, 50 , 25);
        btnConnect.setBounds(420, 100, 100, 25);
        message.setBounds(50, 250, 470, 25);
        message.setEditable(false);
        
        userLabel.setBounds(90, 180, 100, 25);
        txtUsername.setBounds(190, 180, 100, 25);
        passLabel.setBounds(300, 180, 100, 25);
        txtPassword.setBounds(370, 180, 100, 25);
        btnLogin.setBounds(260, 310, 80, 40);

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(hostLabel);
        content.add(txtHostname);
        content.add(portLabel);
        content.add(txtPortnumber);
        content.add(btnConnect);
        content.add(message);

        content.add(userLabel);
        content.add(txtUsername);
        content.add(passLabel);
        content.add(txtPassword);

        
        content.add(btnLogin);

        content.add(new JLabel());
        this.setContentPane(content);
        this.setDefaultCloseOperation(3);
        this.setResizable(false);
    }


    public void sendMessage(String message){
        this.message.setText(message);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

    }

    public User getUser(){
        User model = new User(txtUsername.getText(), txtPassword.getText());
        return model;
    }

    public Address getAddress(){
        Address address = new Address(txtHostname.getText(), txtPortnumber.getText());
        return address;
    }

    public void addLoginListener(ActionListener login){
        btnLogin.addActionListener(login);
    }


    public void addConnectListener(ActionListener connect){
        btnConnect.addActionListener(connect);
    }
}

import javax.swing.JFrame;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.event.*;
import java.io.File;
import java.util.Vector;
import java.awt.*;
public class ClientView2 extends JFrame implements ActionListener{
    private Vector<String> listFile = new Vector<>();
    private JFrame par; //clientview 1
    private JTextArea txtListFile;
    private JTextField txtFileDownLoad;
    private JFileChooser fcFileChoose;
    private JButton btnDownload;
    private JButton btnUpload;
    private JButton btnFileChoose;
    private JTextField txtfileChoose;


    public ClientView2(JFrame par){
        super("FTP Client");
        this.listFile = new Vector<>();
        this.par = par;
        initContent();
    }

    private void initContent(){
        this.setSize(600, 400);
        this.setResizable(false);

        //setting content
        txtListFile = new JTextArea(50,20);
        txtFileDownLoad = new JTextField(30);
        btnDownload = new JButton("download");
        btnUpload = new JButton("upload");
        txtfileChoose = new JTextField(30);
        fcFileChoose = new JFileChooser("/DATA");
        fcFileChoose.setFileSelectionMode(JFileChooser.FILES_ONLY);
        btnFileChoose = new JButton("filechoose");
        btnFileChoose.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fcFileChoose.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    File file = fcFileChoose.getSelectedFile();
                    txtfileChoose.setText(file.getAbsolutePath());
                }
            }

        });

        //label
        JLabel fileL = new JLabel("file: ");
        fileL.setBounds(30, 220, 80, 25);
        JLabel fileLC = new JLabel("file: ");
        fileLC.setBounds(30, 270, 80, 25);
        //design
        txtListFile.setBounds(10, 1, 567, 199);
        txtListFile.setEditable(false);
        txtFileDownLoad.setBounds(60, 220, 150, 25);
        btnDownload.setBounds(210, 220, 100, 23);
        txtfileChoose.setBounds(60, 270, 150, 25);
        btnFileChoose.setBounds(210, 270, 100, 23);
        btnUpload.setBounds(330, 270, 80, 23);

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        //content.add(fcFileChoose);
        content.add(txtListFile);
        content.add(fileL);
        content.add(txtFileDownLoad);
        content.add(btnDownload);
        content.add(fileLC);
        content.add(txtfileChoose);
        content.add(btnFileChoose);
        content.add(btnUpload);


        content.add(new JLabel());
        this.setContentPane(content);
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                par.setVisible(true);
            }
        });
    }

    public void ReLoadFileServer(String[] list){
        for(int i = 0; i < list.length; i++){
            this.listFile.add(list[i]);
        }
    }
    public void UpDateUI(){
        String temp = "";
        for(String i : this.listFile){
            temp += i + "\n";
        }
        this.txtListFile.setText(temp);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

    }
    public void addDownloadListener(ActionListener downloads){
        btnDownload.addActionListener(downloads);
    }


    public void addUploadListener(ActionListener uploads){
        btnUpload.addActionListener(uploads);
    }

    public String getFileDownload(){
        return txtFileDownLoad.getText();
    }

    public String getFileUpload(){
        return txtfileChoose.getText();
    }
    
}

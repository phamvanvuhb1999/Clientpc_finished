import java.awt.event.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.awt.*;

public class ClientControl {
    private ClientView1 view1;
    private ClientView2 view2;
    private boolean isConnect = false;
    private ClientFTP client;

    public void initView2(String[] listfile){
        view2 = new ClientView2(view1);
        view2.ReLoadFileServer(listfile);
        view2.UpDateUI();
        view2.addDownloadListener(new DownloadListener());
        view2.addUploadListener(new UploadListener());
        view1.setVisible(false);
        view2.setVisible(true);
    }
    public ClientControl(ClientView1 view1){
        this.view1 = view1;
        this.view1.addLoginListener(new LoginListener());
        this.view1.addConnectListener(new ConnectListener());
    }

    public void connect(Address address){
        try{
            client = new ClientFTP(address.Host, Integer.parseInt(address.Port));
            if(client.ControlConnect()){
                isConnect = true;
                view1.sendMessage("OK isConnet="+isConnect);
            }else {
                isConnect = false;
                view1.sendMessage("NOT CONNECTED");
            }
        }catch(Exception e){
            e.printStackTrace();
            view1.sendMessage(e.getStackTrace().toString().substring(0, 100));
        }
        

    } 

    public void download(String filename){
        this.client.DOWNLOAD(filename);
    }

    public void upload(String filename){
        this.client.STORE(filename);
    }

    // public void afterUpload(){
    //     String[] listfile = this.client.LIST("");
    //     view2.ReLoadFileServer(listfile);
    //     view2.UpDateUI();

    // }

    class LoginListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if(!isConnect){
                return;
            }
            try{
                User user = view1.getUser();
                if(!client.Login(user.getUsername().trim(), user.getPassword().trim())){
                    view1.sendMessage("Invalid username and/or password ");
                }else{

                    //gui nhan list file , excute here
                    String[] listfile = client.LIST("");
                    initView2(listfile);
                }
                
            }catch(Exception e1){
                view1.sendMessage(e1.getStackTrace().toString().substring(0,100));
            }
        }

    }

    class ConnectListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            Address address = view1.getAddress();

            connect(address);
        }

    }

    class DownloadListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
            //todo
            String filename = view2.getFileDownload();
            download(filename);   
		}

    }

    class UploadListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            // 
            String filename = view2.getFileUpload();
            upload(filename);
            //afterUpload();

        }

    }
}

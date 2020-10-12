import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Vector;


public class ClientFTP {
    //transfer type
    private enum transferType{
        ASCII, BINARY
    }
    //for control connection
    private Socket controlSockets;
    private PrintWriter controlOutWriter;
    private BufferedReader controlIn;
    private int controlPort = 8888;
    private String ServerAddress;


    //for data Connection
    private Socket dataConnection;

    private transferType transferMode = transferType.ASCII;

    private boolean quitCommandLoop = false;
    private static String directory = "DATA/"; //file transfed store in current directory//data
    private static String[] sholdASCII = {"txt", "html", "htm", "cgi", "pl", "php", "cf", "svg", "asp", "rtf", "ps"};

    public ClientFTP(String serverAddress, int controlPort){
        this.ServerAddress = serverAddress;
        this.controlPort = controlPort;
    }

    public boolean ControlConnect(){
        try{
            this.controlSockets = new Socket(this.ServerAddress, this.controlPort);
            if(!this.controlSockets.isConnected()){
                return false;
            }
            this.controlOutWriter = new PrintWriter(controlSockets.getOutputStream(), true);
            this.controlIn = new BufferedReader(new InputStreamReader(controlSockets.getInputStream()));
            return true;

        }catch(Exception e){
            e.printStackTrace();
            DebugMsg("Could connect to serverFTP.");
            return false;
        }
    }

    public boolean Login(String user, String password){
        if(!CheckControlConnection()){
            return false;
        }else{
            try{
                if(user != null){
                    boolean flag = false;
                    long prev = new Date().getTime();
                    //send username
                    controlOutWriter.println("USER "+ user);
                    while(!flag && (new Date().getTime()-prev<5000)){
                        String response = controlIn.readLine().trim();
                        DebugMsg(response);
                        String[] splitResponse = response.split(" ");
                        if(splitResponse[0].equals("531")){
                            return false;
                        }else if(splitResponse[0].equals("530")){
                            return true;
                        }else {
                            DebugMsg("User OK.");
                            flag = true;
                        }
                    }

                    if(password != null && flag){
                        controlOutWriter.println("PASS " + password);
                        while(new Date().getTime()-prev<15000){
                            String response = controlIn.readLine();
                            DebugMsg(response);
                            String[] splitResponse = response.split(" ");
                            if(splitResponse[0].equals("531")){
                                return false;
                            }else {
                                return true;
                            }
                        }

                        DebugMsg("Stoped wait for server response.");
                        return false;
                    }else{
                        DebugMsg("Can not Login.");
                        return false;
                    }
                }else{
                    DebugMsg("Can not Login server.");
                    return false;
                    
                }
            }catch(Exception e){
                e.printStackTrace();
                DebugMsg("Can not Login server.");
            }
            return false;
        }
    }  

    public String[] LIST(String childFolder){
        String[] listfile = null;
        if(!CheckControlConnection()){
            return listfile;
        }
        try{
            openPassive();
            if(!CheckDataConnection()){
                return listfile;
            }
            ChangeDataType(".txt");
            SetDataType();
            controlOutWriter.println("LIST");
            BufferedReader rin = new BufferedReader(new InputStreamReader(this.dataConnection.getInputStream()));
            String line;
            Vector <String> lis = new Vector<>();
            while((line = rin.readLine())!= null){
                DebugMsg(line);
                lis.add(line);
            }

            listfile = new String[lis.size()];
            for(int i = 0 ; i < lis.size(); i ++){
                listfile[i] = lis.get(i);
            }
            CloseDataConnection();
            return listfile;
        }catch(Exception e){
            e.printStackTrace();
            return listfile;
        }
    }
    
    private boolean CheckControlConnection(){
        if(this.controlSockets == null || this.controlOutWriter == null || this.controlIn == null){
            DebugMsg("Control connection is not OK.");
            return false;
        }else{
            return true;
        }
    }

    private boolean CheckDataConnection(){
        if(this.dataConnection == null){
            return false;
        }else {
            return true;
        }
    }

    private void CloseDataConnection(){
        try{
            if(this.dataConnection == null){
                return;
            }
            this.dataConnection.close();

            this.dataConnection = null;
            DebugMsg("Data connection is close.");
        }catch(Exception e){
            e.printStackTrace();
            DebugMsg("Can not close data connection.");
        }
    }
    
    private boolean ChangeDataType(String filename){
        if(filename == null){
            return false;
        }else {
            int index = filename.lastIndexOf(".");
            String typeFile = filename.substring(index + 1, filename.length()).toLowerCase();
            this.transferMode = transferType.BINARY;

            for(String type : ClientFTP.sholdASCII){
                if(typeFile.equals(type)){
                    this.transferMode = transferType.ASCII;
                    return true;
                }
            }
            return false;
        }
    }

    private boolean SetDataType(){
        try{
            if(this.transferMode == transferType.ASCII){
                controlOutWriter.println("TYPE A");
            }else {
                controlOutWriter.println("TYPE I");
            }
            DebugMsg("set type: " + this.transferMode);

            String resType = controlIn.readLine().split(" ")[0];
            if(resType.equals("200")){
                return true;
            }
            return false;
        }catch(Exception e){
            e.printStackTrace();
            DebugMsg("Could request datatype to server.");
            return false;
        }
    }

    private void openPassive(){
        try{
            if(!CheckControlConnection()){
                DebugMsg("Could not open PASV MODE.");
                return;
            }
            controlOutWriter.println("PASV");
            boolean flag = false;
            while(!flag) {
                String response = controlIn.readLine().trim();
                String[] resPasv = response.split(" ");
                if(resPasv[0].equals("227")){
                    String[] prPort = response.split(",");
                    int dtPort = Integer.parseInt(prPort[prPort.length-1]) + 256*Integer.parseInt(prPort[prPort.length - 2]);
                    DebugMsg(dtPort+"");
                    this.
                    openDataConnectionPasv(dtPort);
                    flag = true;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            DebugMsg("Could not open PASV MODE.");
        }
    }

    private void openDataConnectionPasv(int port){
        try{
            dataConnection = new Socket(this.ServerAddress, port);
            DebugMsg("Open data connection passive mode for client.");
        }catch(Exception e){
            e.printStackTrace();
            DebugMsg("Could not open data connection pasv mode.");
        }
    }
    public void STORE(String prevfile){
        int index = prevfile.lastIndexOf("/");
        String filename = null;
        if(index == -1){
            filename = new String(prevfile);
        }else{
            filename = prevfile.substring(index+1, prevfile.length());
        }
        //got filenam
        ChangeDataType(filename);
        SetDataType();
        openPassive();
        try{
            File file = new File(prevfile);
            if(!file.exists() || !CheckDataConnection()){
                return;
            }
            controlOutWriter.println("STOR " + filename);
            String resStor = controlIn.readLine();
            DebugMsg(resStor);
            if(resStor.split(" ")[0].equals("150") && CheckDataConnection()){
                DebugMsg("Starting transfer file " + filename); 
                if(this.transferMode == transferType.BINARY){
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                    BufferedOutputStream bos = new BufferedOutputStream(dataConnection.getOutputStream());
                    SenderI sender = new SenderI(bis,bos);
                    sender.start();
                    sender.join();

                    bis.close();
                    bos.close();
                    
                }else{
                    BufferedReader rin = new BufferedReader(new FileReader(file));
                    PrintWriter rout = new PrintWriter(dataConnection.getOutputStream());
                    SenderA sender = new SenderA(rin, rout);
                    
                    sender.start();
                    sender.join();

                    rin.close();
                    rout.close();
                }
                DebugMsg("Finished send file " + filename);
            }
        }catch(Exception e){
            e.printStackTrace();
            DebugMsg("Could not STORE file" + filename);
        }
        CloseDataConnection();
    }

    private static void DebugMsg(String msg){
        if(msg != ""){
            System.out.println(msg);
        }
    }

    public void DOWNLOAD(String filename){
        File file = new File(this.directory + filename);

        if(file.exists()){
            return;
        }
        //send PASV, receive response and open dataconnection on port
        if(!CheckControlConnection()){
            return;
        }
        openPassive();
        ChangeDataType(filename);
        if(!SetDataType()){
            DebugMsg("Could not set datatype transfer for the server.");
        }
        if(CheckDataConnection()){
            try{
                controlOutWriter.println("RETR " + filename);
                String response = controlIn.readLine();
                DebugMsg(response);
                if(response.split(" ")[0].equals("150")){
                    if(transferMode == transferType.ASCII){
                        BufferedReader rin = new BufferedReader(new InputStreamReader(dataConnection.getInputStream()));
                        PrintWriter rout = new PrintWriter(new FileOutputStream(file), true);
                        ReceiverA receiver = new ReceiverA(rin, rout);
                        receiver.start();
                        receiver.join();

                        rin.close();
                        rout.close();
                    }
                    else {
                        BufferedInputStream fin = new BufferedInputStream(dataConnection.getInputStream());
                        BufferedOutputStream fout = new BufferedOutputStream(new FileOutputStream(file));

                        ReceiverI receiver = new ReceiverI(fin, fout);
                        receiver.start();
                        receiver.join();

                        fin.close();
                        fout.close();
                    }
                    DebugMsg("Finished receive file " + filename);
                }
            }catch(Exception e){
                e.printStackTrace();
                DebugMsg("Could not download file from server.");
            }
        }else{
            return;
        }
        CloseDataConnection();
    }
    
    class SenderI extends Thread{
        private BufferedInputStream is;
        private BufferedOutputStream os;
        public SenderI(BufferedInputStream is, BufferedOutputStream os){
            this.is = is;
            this.os = os;
        }
        @Override
        public void run() {
            byte[] buf = new byte[1024];
            int i;
            try{
                while((i = is.read(buf, 0, 1024)) != -1){
                    os.write(buf, 0, i);
                }
                os.flush();
                //close data stream
                is.close();
                os.close();
            }catch(Exception  e){
                e.printStackTrace();
            }
        }
    }
    
    
    //class support send and receive file from/to SERVER
    class SenderA extends Thread{
        private BufferedReader bfr;
        private PrintWriter prw;
        public SenderA(BufferedReader bfr, PrintWriter prw){
            this.bfr = bfr;
            this.prw = prw;
        }
        @Override
        public void run() {
            String s;
            try{
                while((s = bfr.readLine()) != null){
                    prw.println(s);
                    System.out.println(s);
                }
                prw.flush();
                prw.close();
                bfr.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    class ReceiverI extends Thread{
        private BufferedInputStream is;
        private BufferedOutputStream os;
        public ReceiverI(BufferedInputStream is, BufferedOutputStream os){
            this.is = is;
            this.os = os;
        }
        @Override
        public void run() {
            byte[] buf = new byte[1024];
            int len = 0;
            try{
                while((len = is.read(buf, 0, 1024)) != -1){
                    os.write(buf, 0, len);
                }
                os.flush();

                is.close();
                os.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    class ReceiverA extends Thread {
        BufferedReader in;
        PrintWriter out;
        public ReceiverA(BufferedReader in, PrintWriter out){
            this.in = in;
            this.out = out;
        }
        @Override
        public void run() {
            String s;
            try{
                while((s = in.readLine()) != null){
                    out.println(s);
                }

                in.close();
                out.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}


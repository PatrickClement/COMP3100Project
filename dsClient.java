import java.net.*;
import java.io.*;

class dsClient{
    public static void main(String args[])throws Exception {
        Socket s = new Socket("localhost",50000);
        String server = "super-silk 0\n";
        String largestServer = "";
        int largestCoreSize;
        int count;
        int jobIt;
        String serverRsp;
        
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());

        dout.write(("HELO\n").getBytes());
        dout.flush();

        serverRsp = in.readLine();
        System.out.print("Server says: " + serverRsp);

        dout.write(("AUTH pat\n").getBytes());
        dout.flush();

        serverRsp = in.readLine();
        System.out.print("Server says: " + serverRsp);

        while(true){

            

            dout.write(("REDY\n").getBytes());
            dout.flush();

            serverRsp = in.readLine();
            System.out.print("Server says: " + serverRsp);

            
            while(serverRsp.contains("JCPL")){
                dout.write(("REDY\n").getBytes());
                dout.flush();
                serverRsp = in.readLine();
                System.out.print("Server says: " + serverRsp);
            }
            if(serverRsp.contains("NONE")){
                break;
            }

            String[] jobInfo = serverRsp.split(" ");
            dout.write(("GETS Capable " + jobInfo[4] + " " + jobInfo[5] + " " + jobInfo[6] + "\n").getBytes());
            dout.flush();

            


        }



        dout.close();
        s.close();

    }



}
import java.net.*;
import java.io.*;

class dsClient{
    public static void main(String args[])throws Exception {
        Socket s = new Socket("localhost",50000);
        String server = "super-silk 0\n";
        String largestServer = "";
        int largestCoreSize = 0;
        int count = 0;
        int jobIt = 0;
        int serverIt = 0;
        String serverRsp;
        
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());

        dout.write(("HELO\n").getBytes());
        dout.flush();

        serverRsp = in.readLine();
        System.out.println("Server says: " + serverRsp);

        dout.write(("AUTH pat\n").getBytes());
        dout.flush();

        serverRsp = in.readLine();
        System.out.println("Server says: " + serverRsp);

        while(true){

            

            dout.write(("REDY\n").getBytes());
            dout.flush();

            serverRsp = in.readLine();
            System.out.println("Server says: " + serverRsp);

            
            while(serverRsp.contains("JCPL")){
                dout.write(("REDY\n").getBytes());
                dout.flush();
                serverRsp = in.readLine();
                System.out.println("Server says: " + serverRsp);
            }
            if(serverRsp.contains("NONE")){
                break;
            }

            String[] jobInfo = serverRsp.split(" ");
            dout.write(("GETS Capable " + jobInfo[4] + " " + jobInfo[5] + " " + jobInfo[6] + "\n").getBytes());
            dout.flush();

            serverRsp = in.readLine();
            System.out.println("Server says: " + serverRsp);

            String[] dataArr = serverRsp.split(" ");
            int nRecs = Integer.parseInt(dataArr[1]);

            dout.write(("OK\n").getBytes());
            dout.flush();

            for (int i = 0; i < nRecs;i++){
                serverRsp = in.readLine();
                System.out.println("Server says: " + serverRsp);
                String[] serverDetails = serverRsp.split(" ");
                int coreSize = Integer.parseInt(serverDetails[4]);
                if (coreSize >= largestCoreSize){
                    largestCoreSize = coreSize;
                    if (largestServer.contains(serverDetails[0])){
                        count++;
                    }
                    else{
                        largestServer = serverDetails[0];
                        count = 1;
                    }
                }
                System.out.println(largestServer + count);

            }

            dout.write(("OK\n").getBytes());
            dout.flush();

            serverRsp = in.readLine();
            System.out.println("Server says: " + serverRsp);

            
            dout.write(("SCHD " + jobIt + " " + largestServer + " " + (serverIt%count) + "\n").getBytes());
            dout.flush();

            serverIt++;
            jobIt++;

            serverRsp = in.readLine();
            System.out.println("Server says: " + serverRsp);


        }

        dout.write(("QUIT\n").getBytes());
        dout.flush();
        serverRsp = in.readLine();
        System.out.println("Server says: " + serverRsp);


        dout.close();
        s.close();

    }



}
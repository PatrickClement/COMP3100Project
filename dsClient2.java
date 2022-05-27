import java.net.*;
import java.io.*;

class dsClient2{
    public static void main(String args[])throws Exception {
        Socket s = new Socket("localhost",50000);
        
        //For BF need to find smallest fitness value (Server available cores - job required cores)
        int jobCores = 0;
        int jobNum = 0;
        int availServerCore = 0;
        int fitnessValue = 0;
        int largestFitnessValue = 0;
        boolean firstFitnessValue = true;
        String serverNum = "";
        String BFServer = "";
        
        //int count = 0;
        int jobIt = 0;
        //int serverIt = 0;
        String serverRsp;
        //boolean getsNotCalled = true;
        
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());

        //start handshake
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
                //no more jobs left
                break;
            }

            //if (getsNotCalled){

                String[] jobInfo = serverRsp.split(" ");

                jobNum = Integer.parseInt(jobInfo[2]);
                jobCores = Integer.parseInt(jobInfo[4]);
                
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
                    availServerCore = Integer.parseInt(serverDetails[4]);
                    fitnessValue = availServerCore - jobCores;
                    if (firstFitnessValue){
                        largestFitnessValue = fitnessValue;
                        firstFitnessValue = false;

                    }
                    else if (fitnessValue <= largestFitnessValue){
                        largestFitnessValue = fitnessValue;                       
                        BFServer = serverDetails[0];
                        serverNum = serverDetails[1];                       
                    }
                    
                    System.out.println(BFServer + serverNum);

                }
                largestFitnessValue = 0;
                firstFitnessValue = true;
                dout.write(("OK\n").getBytes());
                dout.flush();

                serverRsp = in.readLine();
                System.out.println("Server says: " + serverRsp);
                //getsNotCalled = false;
            //}
            dout.write(("SCHD " + jobNum + " " + BFServer + " " + serverNum + "\n").getBytes());
            dout.flush();

            //serverIt++;
            jobIt++;

            serverRsp = in.readLine();
            System.out.println("Server says: " + serverRsp);

            //while (serverRsp.contains(".")){
            //    serverRsp = in.readLine();
            //}

        }

        dout.write(("QUIT\n").getBytes());
        dout.flush();
        serverRsp = in.readLine();
        System.out.println("Server says: " + serverRsp);


        dout.close();
        s.close();

    }



}
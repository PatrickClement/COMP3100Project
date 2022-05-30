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
        int smallestFitnessValue = 0;
        boolean firstFitnessValue = true;
        String serverNum = "";
        String BFServer = "";
        String serverRsp;
        
        
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());

        //start handshake
        dout.write(("HELO\n").getBytes());
        dout.flush();

        serverRsp = in.readLine();
        

        dout.write(("AUTH pat\n").getBytes());
        dout.flush();

        serverRsp = in.readLine();
        

        while(true){

            

            dout.write(("REDY\n").getBytes());
            dout.flush();

            serverRsp = in.readLine();
            

            
            while(serverRsp.contains("JCPL")){
                dout.write(("REDY\n").getBytes());
                dout.flush();
                serverRsp = in.readLine();
                
            }
            if(serverRsp.contains("NONE")){
                //no more jobs left
                break;
            }

            

                String[] jobInfo = serverRsp.split(" ");

                jobNum = Integer.parseInt(jobInfo[2]);
                jobCores = Integer.parseInt(jobInfo[4]);
                
                dout.write(("GETS Capable " + jobInfo[4] + " " + jobInfo[5] + " " + jobInfo[6] + "\n").getBytes());
                dout.flush();

                serverRsp = in.readLine();
                

                String[] dataArr = serverRsp.split(" ");
                int nRecs = Integer.parseInt(dataArr[1]);



                dout.write(("OK\n").getBytes());
                dout.flush();

                for (int i = 0; i < nRecs;i++){
                    serverRsp = in.readLine();
                    
                    String[] serverDetails = serverRsp.split(" ");
                    availServerCore = Integer.parseInt(serverDetails[4]);
                    fitnessValue = availServerCore - jobCores;
                    
                    //to get a baseline fitnessvalue
                    if (firstFitnessValue && fitnessValue>=0){
                        smallestFitnessValue = fitnessValue;
                        firstFitnessValue = false;
                        BFServer = serverDetails[0];
                        serverNum = serverDetails[1]; 
                        
                    }
                    //if a smaller fitness value is found
                    if (fitnessValue < smallestFitnessValue && fitnessValue >=0){
                        smallestFitnessValue = fitnessValue;                       
                        BFServer = serverDetails[0];
                        serverNum = serverDetails[1];                       
                    }
                    //Just in case every server has jobs
                    if (BFServer.isEmpty() && fitnessValue < smallestFitnessValue){
                        BFServer = serverDetails[0];
                        serverNum = serverDetails[1]; 
                    }
                    
                    

                }
                smallestFitnessValue = 0;
                firstFitnessValue = true;
                dout.write(("OK\n").getBytes());
                dout.flush();

                serverRsp = in.readLine();
               
            dout.write(("SCHD " + jobNum + " " + BFServer + " " + serverNum + "\n").getBytes());
            dout.flush();

            BFServer = "";
            serverNum = "";
            

            serverRsp = in.readLine();
            

            //while (serverRsp.contains(".")){
            //    serverRsp = in.readLine();
            //}

        }

        dout.write(("QUIT\n").getBytes());
        dout.flush();
        serverRsp = in.readLine();
        


        dout.close();
        s.close();

    }



}
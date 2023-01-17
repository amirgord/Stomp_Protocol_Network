
// package bgu.spl.net.impl.echoClients;

// /**
//  * SendMessage
//  */
// public class SendMessage {

//     public static void sendMessage(Socket socket, String message){
        
//         try {
//             in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
//             String line = "";

//             System.out.println(">>> sending message " + i ++  +" to server:");
//             System.out.println(msg);
//             out.write(msg + '\0');
//             out.flush();

//             System.out.println(">>> awaiting response");

//             String response = "";
//             StompEncoderDecoder encdec = new StompEncoderDecoder();
//             int read;
//             while ((read = in.read()) >= 0) {
//                 response = encdec.decodeNextByte((byte) read);
//                 if (response != null) {
//                     break;
//                 }
//             }

//             System.out.println("message got from the server: ");
//             System.out.println("----- START ------");
//             if(response != "")
//                 System.out.println(response);
//             System.out.println("-----  END  ------");

//         } catch (IOException e) {
//             e.printStackTrace();
//         }

// }
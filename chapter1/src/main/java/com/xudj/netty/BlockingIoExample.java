package com.xudj.netty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @program: netty-in-action
 * @description:
 * @author: xudj
 * @create: 2022-08-25 07:33
 **/
public class BlockingIoExample {

    /**
     * Blocking I/O example
     */
    public void serve(int portNumber) throws IOException {
        ServerSocket serverSocket = new ServerSocket(portNumber);
        Socket socket = serverSocket.accept();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );

        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

        String req;
        String res;
        while ((req = reader.readLine()) != null) {
            if ("Done".equals(req)) {
                break;
            }

            res = processResult(req);
            writer.write(res);
        }
    }

    private String processResult(String req) {
        return "Processed";
    }

}

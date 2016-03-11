/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lrnneval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Gusta
 */
public class LRNNServlet implements Runnable {

    private final Socket m_socket;
    private final int m_num;

    static int port = 9000;

    LRNNServlet(Socket socket, int num) {
        m_socket = socket;
        m_num = num;

        Thread handler = new Thread(this, "handler-" + m_num);
        handler.start();
    }

    public void run() {
        try {
            try {
                System.out.println(m_num + " Connected.");
                BufferedReader in = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
                OutputStreamWriter out = new OutputStreamWriter(m_socket.getOutputStream());
                out.write("Welcome connection #" + m_num + "; please enter vectors for evaluation,\n");
                out.flush();

                while (true) {
                    String line = in.readLine();
                    if (line == null) {
                        System.out.println(m_num + " Closed.");
                        return;
                    } else {
                        System.out.println(m_num + " -> recieved vector ");
                        double evaluate = LRNNeval.evaluate(line);
                        //out.write("echo " + line + "\n\r");
                        out.write(evaluate + ",\n");
                        out.flush();
                    }
                }
            } finally {
                m_socket.close();
            }
        } catch (IOException e) {
            System.out.println(m_num + " Error: " + e.toString());
        }
    }

    public static void main(String[] args) throws Exception {
        LRNNeval.main(args);    //backend program

        System.out.println("Accepting connections on port: " + port);
        int nextNum = 1;
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket socket = serverSocket.accept();
            LRNNServlet hw = new LRNNServlet(socket, nextNum++);
        }
    }

}

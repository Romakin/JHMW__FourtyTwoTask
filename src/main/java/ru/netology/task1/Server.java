package ru.netology.task1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final ExecutorService executor;
    private String directory;

    public Server(int maxThreadsNum, String directory) {
        this.executor = Executors.newFixedThreadPool(maxThreadsNum);
        this.directory = directory == null ? "" : directory;
    }

    public static void main(String[] args) {
        Integer port, maxThreads;
        String directory;
        if (args.length > 0) {
            port = Integer.parseInt(args[1]);
            maxThreads = Integer.parseInt(args[0]);
            directory = args[2];
        } else {
            port = 9988;
            maxThreads = 60;
            directory = "Public";
        }
        new Server(maxThreads, directory).start(port);
    }

    public void start(int port) {
        if (!checkPort(port)) return;
        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server TASK1 started on port " + port);
            while (true) {
                Handler handler = new Handler(serverSocket.accept(), directory);
                this.executor.submit(handler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkPort(int portNum) {
        try (
                ServerSocket serverSocket = new ServerSocket()
        ) {
            serverSocket.setReuseAddress(false);
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName("localhost"), portNum), 1);
            return true;
        } catch (IOException ex) {
            System.out.println("Порт занят, попробуйте другой");
            return false;
        }
    }
}

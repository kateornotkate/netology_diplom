import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Main {
    private static final int PORT = 8989;

    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));

        // server
        try (ServerSocket serverSocket = new ServerSocket(PORT);) {
            System.out.println("Соединение установлено");
            while (true) {
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(
                                socket.getOutputStream(), true);
                ) {
                    String word = in.readLine().toLowerCase();
                    List<PageEntry> result = engine.search(word); // обработка запроса;

                    if (result == null) {
                        out.println("Слово " + word + " отсуствует!");
                    } else {
                        out.println(printInJson(result)); // ответ на запрос должен быть в формате JSON;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }

    public static String printInJson(List<PageEntry> list) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(list);
    }
}
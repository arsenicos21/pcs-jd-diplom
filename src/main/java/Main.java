import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            BooleanSearchEngine engine = new BooleanSearchEngine(new File("pcs-jd-diplom//pdfs"));
            int port = 8989;
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Сервер запущен");
                while (true) {
                    try (Socket clientSocket = serverSocket.accept(); // ждем подключения
                         PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                         BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                        System.out.println("Обнаружено новое подключение");
                        out.println("Введите слово для поиска");
                        String word = in.readLine();
                        List<PageEntry> searchResult = engine.search(word);
                        out.println(listToJson(searchResult));
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            ;
        }
    }

    public static String listToJson(List<PageEntry> list) {
        Type listType = new TypeToken<List<PageEntry>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(list, listType);
    }
}
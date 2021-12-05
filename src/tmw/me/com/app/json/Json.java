package tmw.me.com.app.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public final class Json {

    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T getFromFile(File file, Class<T> tClass) throws FileNotFoundException {
        return getFromFile(file, tClass, gson);
    }
    public static <T> T getFromFile(File file, Class<T> tClass, Gson gson) throws FileNotFoundException {
        return gson.fromJson(new FileReader(file), tClass);
    }

    public static void writeJsonToFile(File writeTo, Object jsonObject) throws IOException {
        writeJsonToFile(writeTo, jsonObject, gson);
    }
    public static void writeJsonToFile(File writeTo, Object jsonObject, Gson gson) throws IOException {
        FileWriter fileWriter = new FileWriter(writeTo);
        fileWriter.write(jsonObjectToString(jsonObject, gson));
        fileWriter.close();
    }

    public static String jsonObjectToString(Object jsonObject) {
        return jsonObjectToString(jsonObject, gson);
    }
    public static String jsonObjectToString(Object jsonObject, Gson gson) {
        return gson.toJson(jsonObject);
    }

}

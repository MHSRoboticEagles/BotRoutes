package io;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.AutoDot;
import entity.AutoRoute;
import entity.BotActionObj;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileLoader {
    protected static Charset charset = Charset.forName("UTF-8");
    private static final String HOME_FOLDER_PATH = "user.home";
    private static final String ROOT_FOLDER = "botroutes";
    private static final String ROUTE_FOLDER = "routes";
    private static final String DOT_FOLDER = "dots";
    public static final String BOT_ACTIONS_FILENAME = "bot-actions.json";
    private static final String HOME_FOLDER = System.getProperty(HOME_FOLDER_PATH);
    public static void ensureAppDirectories() throws Exception {
        try {
            Path pathRoot = Paths.get(HOME_FOLDER, ROOT_FOLDER);
            ensureDirectory(pathRoot);

            ensureDirectory(getRouteFolder());

            ensureDirectory(getDotFolder());
        }
        catch (Exception ex){
            throw new Exception("Unable to initialize directories", ex);
        }
    }

    public static Path getHomeFolder(){
        Path pathRoot = Paths.get(HOME_FOLDER, ROOT_FOLDER);
        return pathRoot;
    }

    public static Path getRouteFolder(){
        Path pathRoutes = Paths.get(HOME_FOLDER, ROOT_FOLDER, ROUTE_FOLDER);
        return pathRoutes;
    }

    public static Path getDotFolder(){
        Path pathDots = Paths.get(HOME_FOLDER, ROOT_FOLDER, DOT_FOLDER);
        return pathDots;
    }

    public static void ensureDirectory(Path dirPath) throws Exception {
        try {

            boolean pathExists = Files.exists(dirPath);
            if (!pathExists) {
                Files.createDirectory(dirPath);
            }
        }
        catch (Exception ex){
            throw new Exception("Unable to initialize directories", ex);
        }
    }

    public static File getRoutesFolder(){
        Path folder = getRouteFolder();
        if (Files.exists(folder)){
            return new File(folder.toString());
        }
        return null;
    }

    public static File getDotsFolder(){
        Path folder = getDotFolder();
        if (Files.exists(folder)){
            return new File(folder.toString());
        }
        return null;
    }

    public static ArrayList<AutoRoute> listRoutes() throws IOException {
        File folder = getRoutesFolder();
        ArrayList<AutoRoute> resutls = new ArrayList<>();
        if (folder != null){
            File [] list = folder.listFiles();
            for (int i = 0; i < list.length; i++){
                File f = list[i];
                try {
                    String content = Files.readString(Path.of(f.toURI()), StandardCharsets.US_ASCII);
                    AutoRoute route = AutoRoute.deserialize(content);
                    resutls.add(route);
                }
                catch (Exception ex){
                    System.out.println(String.format("Cannot read file %s. %s", f.getName(), ex.getMessage()));
                }
            }
        }
        return resutls;
    }

    public static void saveRoute(AutoRoute route) throws IOException {
        String fileContents = route.serialize();
        ByteBuffer byteContents = charset.encode(fileContents);
        File folder = getRoutesFolder();
        String fullName = String.format("%s.json", route.getRouteName());
        FileOutputStream outputStream = new FileOutputStream(new File(folder, fullName));
        try {
            outputStream.write(byteContents.array(), 0, byteContents.limit());
            outputStream.flush();
        } finally {
            outputStream.close();
        }
    }

    public static String getRouteFilePath(String routeName){
        Path folder = getRouteFolder();
        return String.format("%s/%s.json", folder.toString(), routeName);
    }

    public static String getDotFilePath(String dotName){
        Path folder = getDotFolder();
        return String.format("%s/%s.json", folder.toString(), dotName);
    }

    public static void deleteRouteFile(String routeName) throws Exception{
        Path path = Paths.get(getRouteFilePath(routeName));
        try {
            Files.delete(path);
        }
        catch (Exception ex){
            throw new Exception(String.format("Cannot delete file %s. %s", path.toString(), ex.getMessage()));
        }
    }

    public static ArrayList<AutoDot> listDots() throws IOException {
        File folder = getDotsFolder();
        ArrayList<AutoDot> resutls = new ArrayList<>();
        if (folder != null){
            File [] list = folder.listFiles();
            for (int i = 0; i < list.length; i++){
                File f = list[i];
                try {
                    String content = Files.readString(Path.of(f.toURI()), StandardCharsets.US_ASCII);
                    AutoDot dot = AutoDot.deserialize(content);
                    resutls.add(dot);
                }
                catch (Exception ex){
                    System.out.println(String.format("Cannot read file %s. %s", f.getName(), ex.getMessage()));
                }
            }
        }
        return resutls;
    }

    public static void saveDot(AutoDot dot) throws IOException {
        String fileContents = dot.serialize();
        ByteBuffer byteContents = charset.encode(fileContents);
        File folder = getDotsFolder();
        String fullName = String.format("%s.json", dot.getDotName());
        FileOutputStream outputStream = new FileOutputStream(new File(folder, fullName));
        try {
            outputStream.write(byteContents.array(), 0, byteContents.limit());
            outputStream.flush();
        } finally {
            outputStream.close();
        }
    }

    public static void deleteDotFile(String dotName) throws Exception{
        Path path = Paths.get(getDotFilePath(dotName));
        try {
            Files.delete(path);
        }
        catch (Exception ex){
            throw new Exception(String.format("Cannot delete file %s. %s", path.toString(), ex.getMessage()));
        }
    }

    public static ArrayList<BotActionObj> listBotActions() throws Exception{
        Path homePath = getHomeFolder();
        ArrayList<BotActionObj> actions = new ArrayList<>();
        File actionsFile = new File(homePath.toString(), BOT_ACTIONS_FILENAME);
        if (actionsFile.exists()) {
            String content = Files.readString(Path.of(actionsFile.toURI()), StandardCharsets.US_ASCII);
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<BotActionObj>>() {}.getType();
            actions = gson.fromJson(content, listType);
        }

        return actions;
    }

}

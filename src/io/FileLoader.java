package io;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.AutoDot;
import entity.AutoRoute;
import entity.BotActionObj;
import entity.BotCalibConfig;

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
    private static final String LOG_FOLDER = "logs";
    private static final String CONFIG_FOLDER = "configs";
    private static final String DOT_FOLDER = "dots";
    public static final String BOT_ACTIONS_FILENAME = "bot-actions.json";
    public static final String BOT_CONFIG_FILENAME = "bot-config.json";
    private static final String HOME_FOLDER = System.getProperty(HOME_FOLDER_PATH);

    private static Path resolvedHomeFolder;

    static {
        resolvedHomeFolder = Paths.get(HOME_FOLDER, ROOT_FOLDER);
    }

    public static void ensureAppDirectories() throws Exception {
        try {
            ensureDirectory(resolvedHomeFolder);
            ensureDirectory(getRouteFolder());
            ensureDirectory(getDotFolder());
            ensureDirectory(getLogFolder());
            ensureDirectory(getConfigFolder());
        }
        catch (Exception ex){
            throw new Exception("Unable to initialize directories", ex);
        }
    }

    public static void setHomeFolder(String path) throws Exception {
        resolvedHomeFolder = Path.of(path);
        ensureAppDirectories();
    }

    public static Path getHomeFolder(){
        return resolvedHomeFolder;
    }

    public static Path getRouteFolder(){
        Path pathRoutes = Paths.get(String.valueOf(resolvedHomeFolder), ROUTE_FOLDER);
        return pathRoutes;
    }

    public static Path getDotFolder(){
        Path pathDots = Paths.get(String.valueOf(resolvedHomeFolder), DOT_FOLDER);
        return pathDots;
    }

    public static Path getLogFolder(){
        Path pathLogs = Paths.get(String.valueOf(resolvedHomeFolder), LOG_FOLDER);
        return pathLogs;
    }

    public static Path getConfigFolder(){
        Path pathConfig = Paths.get(String.valueOf(resolvedHomeFolder), CONFIG_FOLDER);
        return pathConfig;
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

    public static String getBotConfigFilePath(){
        Path folder = getHomeFolder();
        return String.format("%s/%s", folder.toString(), BOT_CONFIG_FILENAME);
    }

    public static String getDotFilePath(String dotName, String fieldSide){
        Path folder = getDotFolder();
        return String.format("%s/%s_%s.json", folder.toString(), dotName, fieldSide);
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
        String fullName = String.format("%s_%s.json", dot.getDotName(), dot.getFieldSide());
        FileOutputStream outputStream = new FileOutputStream(new File(folder, fullName));
        try {
            outputStream.write(byteContents.array(), 0, byteContents.limit());
            outputStream.flush();
        } finally {
            outputStream.close();
        }
    }

    public static void deleteDotFile(String dotName, String fieldSide) throws Exception{
        Path path = Paths.get(getDotFilePath(dotName, fieldSide));
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
        BotActionObj dummy = new BotActionObj();
        dummy.setDescription("None");
        dummy.setMethodName("");
        dummy.setReturnRef("");
        dummy.setGeo(false);
        File actionsFile = new File(homePath.toString(), BOT_ACTIONS_FILENAME);
        if (actionsFile.exists()) {
            String content = Files.readString(Path.of(actionsFile.toURI()), StandardCharsets.US_ASCII);
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<BotActionObj>>() {}.getType();
            actions = gson.fromJson(content, listType);
        }
        actions.add(dummy);

        return actions;
    }

    public static BotCalibConfig loadBotConfig() throws Exception{
        Path homePath = getHomeFolder();
        BotCalibConfig config = new BotCalibConfig();
        File configFile = new File(homePath.toString(), BOT_CONFIG_FILENAME);
        if (configFile.exists()) {
            String content = Files.readString(Path.of(configFile.toURI()), StandardCharsets.US_ASCII);
            config = BotCalibConfig.deserialize(content);
        }
        return config;
    }

    public static void saveBotConfig(BotCalibConfig config) throws IOException {
        Path homePath = getHomeFolder();
        File configFile = new File(homePath.toString(), BOT_CONFIG_FILENAME);
        FileOutputStream outputStream = new FileOutputStream(configFile);
        try {
            ByteBuffer byteContents = charset.encode(config.serialize());
            outputStream.write(byteContents.array(), 0, byteContents.limit());
            outputStream.flush();
        } finally {
            outputStream.close();
        }
    }

}

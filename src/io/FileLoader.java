package io;

import entity.AutoRoute;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

}

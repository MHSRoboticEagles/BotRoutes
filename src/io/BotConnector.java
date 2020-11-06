package io;

import entity.AutoRoute;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static io.FileLoader.BOT_ACTIONS_FILENAME;

public class BotConnector {
    private static final long CONNECT_TIMEOUT = 5000;
    private static final String OS = System.getProperty("os.name").toLowerCase();

    public static void runConnect() throws Exception{
        String command = String.format("%s connect 192.168.43.1:5555", getAdbCommand());
        executeCommand(command, CONNECT_TIMEOUT);
    }

    public static void runDisconnect() throws Exception{
        String command = String.format("%s disconnect 192.168.43.1:5555", getAdbCommand());
        executeCommand(command, CONNECT_TIMEOUT);
    }

    public static void pullBotActions() throws Exception{
        String homeFolder = FileLoader.getHomeFolder().toString();
        String remotePath = String.format("/sdcard/FIRST/settings/%s", BOT_ACTIONS_FILENAME);
        String command = String.format("%s pull %s %s", getAdbCommand(), remotePath, homeFolder);
        executeCommand(command, CONNECT_TIMEOUT);
    }

    public static void pullBotConfig() throws Exception{
        String homeFolder = FileLoader.getHomeFolder().toString();
        String remotePath = "/sdcard/FIRST/settings/bot-config.json";
        String command = String.format("%s pull %s %s", getAdbCommand(), remotePath, homeFolder);
        executeCommand(command, CONNECT_TIMEOUT);
    }


    public static void pullRoutes() throws Exception{
        Path local = FileLoader.getRouteFolder();
        String command = String.format("%s pull /sdcard/FIRST/routes/. %s", getAdbCommand(), local.toString());
        executeCommand(command, CONNECT_TIMEOUT);
    }

    public static void pullDots() throws Exception{
        Path local = FileLoader.getDotFolder();
        String command = String.format("%s pull /sdcard/FIRST/dots/. %s", getAdbCommand(), local.toString());
        executeCommand(command, CONNECT_TIMEOUT);
    }

    public static void publishRoute(AutoRoute route) throws Exception{
        String local = FileLoader.getRouteFilePath(route.getRouteName());
        String command = String.format("%s push %s /sdcard/FIRST/routes/%s.json", getAdbCommand(), local.toString(), route.getRouteName());
        executeCommand(command, CONNECT_TIMEOUT);
    }

    private static String executeCommand(String command, long timeoutMS) throws Exception {

        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            if (!p.waitFor(timeoutMS, TimeUnit.MILLISECONDS)){
                p.destroy();
                throw new Exception(String.format("Command %s timed out", command));
            }
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            throw new Exception(String.format("Command %s failed. %s", command, e.getMessage()));
        }
        System.out.println(output.toString());

        return output.toString();

    }

    public static String getAdbCommand(){
        if (isWindows()){
            return "adb.exe";
        }
        else if (isMac()){
            return "adb";
        }
        return "adb";
    }

    public static boolean isWindows() {

        return (OS.indexOf("win") >= 0);

    }

    public static boolean isMac() {

        return (OS.indexOf("mac") >= 0);

    }

}

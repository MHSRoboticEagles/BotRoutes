package io;

import entity.AutoRoute;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class BotConnector {
    private static long CONNECT_TIMEOUT = 5000;
    public static void runConnect() throws Exception{
        String command = "adb connect 192.168.43.1:5555";
        executeCommand(command, CONNECT_TIMEOUT);
    }

    public static void runDisconnect() throws Exception{
        String command = "adb disconnect 192.168.43.1:5555";
        executeCommand(command, CONNECT_TIMEOUT);
    }

    public static void pullBotActions() throws Exception{
        String homeFolder = FileLoader.getHomeFolder().toString();
        String remotePath = "/sdcard/FIRST/settings/bot-actions";
        String command = String.format("adb pull %s %s", remotePath, homeFolder);
        executeCommand(command, CONNECT_TIMEOUT);
    }

    public static void pullBotConfig() throws Exception{
        String homeFolder = FileLoader.getHomeFolder().toString();
        String remotePath = "/sdcard/FIRST/settings/bot-config.json";
        String command = String.format("adb pull %s %s", remotePath, homeFolder);
        executeCommand(command, CONNECT_TIMEOUT);
    }


    public static void pullRoutes() throws Exception{
        Path local = FileLoader.getRouteFolder();
        String command = String.format("adb pull /sdcard/FIRST/routes/. %s", local.toString());
        executeCommand(command, CONNECT_TIMEOUT);
    }

    public static void pullDots() throws Exception{
        Path local = FileLoader.getDotFolder();
        String command = String.format("adb pull /sdcard/FIRST/dots/. %s", local.toString());
        executeCommand(command, CONNECT_TIMEOUT);
    }

    public static void publishRoute(AutoRoute route) throws Exception{
        String local = FileLoader.getRouteFilePath(route.getRouteName());
        String command = String.format("adb push %s /sdcard/FIRST/routes/%s", local.toString(), route.getRouteName());
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
}

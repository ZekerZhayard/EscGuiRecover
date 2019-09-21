package io.github.zekerzhayard.vanillarecover.hooks;

import java.util.ArrayList;
import java.util.Arrays;

public class MainHook {
    public static String[] main(String[] args) {
        ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));
        for (int i = argsList.size() - 1; i >= 0; i--) {
            String arg = argsList.get(i);
            if (arg.equals("--server") || arg.equals("--port")) {
                System.out.println(argsList.remove(i) + " " + argsList.remove(i));
            }
        }
        return argsList.toArray(new String[0]);
    }
}

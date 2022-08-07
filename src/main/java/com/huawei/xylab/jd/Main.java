package com.huawei.xylab.jd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        if (Arrays.asList(args).contains("--dir")) {
            String dirPath = args[1];
            String outPath = args[3];;
            String dirPaht2 = new File(dirPath).getAbsolutePath();
            String outPath2 = new File(outPath).getAbsolutePath();

            //处理jar包
            Stream<Path> paths = Files.walk(Paths.get(dirPath));
            paths.parallel().filter(filePath-> {
                        return filePath.toString().endsWith(".jar") && !filePath.toString().contains("classes_xy.jar");
                    }).
                    forEach(jarPath->{
                        System.out.println(jarPath.toString());
                        String filePath = new File(jarPath.toString()).getAbsolutePath();
                        String targetPath = filePath.replace(dirPaht2, outPath2);
                        ArrayList<String> newArgs = new ArrayList<String>();
                        newArgs.add(jarPath.toString());
                        newArgs.addAll(Arrays.asList(args));
                        newArgs.add("-n");
                        newArgs.add("-od");
                        newArgs.add(targetPath);
                        System.out.println(newArgs.toString());
                        com.github.kwart.jd.cli.Main.main(newArgs.toArray(new String[newArgs.size()]));
                    });

            //处理classes目录
            Stream<Path> paths2 = Files.walk(Paths.get(dirPath));
            paths2.parallel().filter(Files::isDirectory).filter(filePath-> {
                        return filePath.toString().endsWith(File.separator + "classes");
                    }).
                    forEach(jarPath->{
                        System.out.println(jarPath.toString());
                        String filePath = new File(jarPath.toString()).getAbsolutePath();
                        String classJarPath = filePath + "_xy.jar";
                        try {
                            Runtime.getRuntime().exec("jar -cf " + classJarPath + " -C " +filePath + " .");
                        } catch (IOException e) {
                            System.err.println(e);
                            return;
                        }
                        String targetPath = filePath.replace(dirPaht2, outPath2);
                        ArrayList<String> newArgs = new ArrayList<String>();
                        newArgs.add(classJarPath);
                        newArgs.addAll(Arrays.asList(args));
                        newArgs.add("-n");
                        newArgs.add("-od");
                        newArgs.add(targetPath);
                        System.out.println(newArgs.toString());
                        com.github.kwart.jd.cli.Main.main(newArgs.toArray(new String[newArgs.size()]));
                    });

        } else {
            com.github.kwart.jd.cli.Main.main(args);
        }
    }
}

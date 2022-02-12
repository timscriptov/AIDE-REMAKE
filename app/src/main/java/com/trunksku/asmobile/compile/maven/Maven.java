package com.trunksku.asmobile.compile.maven;

import android.os.Environment;

import com.trunksku.asmobile.dependency.Dependency;
import com.trunksku.asmobile.dependency.DependencyDownloader;
import com.trunksku.asmobile.dependency.DependencyResolver;
import com.trunksku.asmobile.utils.LoggerUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Maven {
    static List<Dependency> dependencies = new ArrayList<>();

    public static void parseDependencies(String content) {
        dependencies.clear();
        for (String apis : findFromLines(content, "api").split("\n")) {
            String api = apis.replace("'", "");
            //System.out.println(api + " " + api.split(":")[0]);
            try {
                Dependency dependency = Dependency.from(api);
                dependency.setUserDefined(true);
                dependencies.add(dependency);
            } catch (Exception e) {

            }
            //String[] api = apis.split(":");
            //System.out.println(api[0]+"."+api[1]+"."+api[2]);
            LoggerUtils.writeLog("parseDependencies(..)" + api);
        }
        for (Dependency dep : dependencies) {
            System.out.println("implementation" + dep.getFileName());
            LoggerUtils.writeLog("implementation" + dep.getFileName());
        }

        DependencyResolver resolver = new DependencyResolver(dependencies, new DependencyResolver.ResolveTask() {

            @Override
            public void onResolving(Dependency dependency) {
            }

            @Override
            public void onResult(List<Dependency> result) {
                //result.addAll(dependencies);
                dependencies = result;
                List<String> deps = new ArrayList();/* result.stream().map(new Function<Dependency, String>() {
                     @Override
                     public String apply(Dependency dep) {
                     return dep.toString();
                     }
                     }).collect(Collectors.toList());*/
                for (Dependency res : result) {
                    deps.add(res.getPomDownloadLink());
                }


            }
        });
        try {
            DependencyDownloader dd = new DependencyDownloader();
            dd.setListener(new DependencyDownloader.Listener() {

                @Override
                public void onFinish(List<Dependency> downloaded) {
                    System.out.println("Downloaded " + downloaded.size());
                }

                @Override
                public void onDownload(Dependency dependency) {
                    System.out.println("Downloading " + dependency.getFileName());
                }
            });

            dd.download(dependencies);
            resolver.resolve();
        } catch (Exception e) {
        }

    }

    public static List<Dependency> propertiesDependencies(String content) {
        final List<Dependency> depend = new ArrayList<>();
        final List<Dependency> dependencies = new ArrayList<>();
        for (String apis : findFromLines(content, "implementation").split("\n")) {
            String api = apis.replace("'", "");
            //System.out.println(api + " " + api.split(":")[0]);
            try {
                Dependency dependency = Dependency.from(api);
                dependency.setUserDefined(true);
                dependencies.add(dependency);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        for (Dependency dep : dependencies) {
            System.out.println("implementation " + dep.getPath() + " " + dep.getType());
            LoggerUtils.writeLog("implementation " + dep.getPath() + " " + dep.getType());
        }

        DependencyResolver resolver = new DependencyResolver(dependencies, new DependencyResolver.ResolveTask() {

            @Override
            public void onResolving(Dependency dependency) {
            }

            @Override
            public void onResult(List<Dependency> result) {
                //depend.addAll(dependencies);
                depend.addAll(result);
            }
        });
        try {
            resolver.resolve();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        return depend;
    }

    public static String addDependency(String content, String data) {
        String dep = "dependencies " + findFromLines(content, "dependencies").replace(" ", "");

        LoggerUtils.writeLog("implementation " + dep + "\n" + data);

        return content.replace(dep, dep + "\n" + data);
    }


    private static String findFromLines(String lines, String text) {
        int lineNumber = 0;
        String m_str = "";
        try {
            for (String line : lines.split("\\r?\\n")) {
                lineNumber++;
                if (line.contains(text)) {
                    String[] items = line.replace(" ", "").replace(text, text + " ").split(" ");
                    m_str += items[1] + "\n";
                }
            }
            return m_str;
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isExist(String file) {
        boolean exist = false;
        File maven = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.aide/Local Libraries/maven");
        for (File f : maven.listFiles()) {
            if (f.getName().equals(file)) {
                exist = true;
                break;
            }
        }
        return exist;
    }

}

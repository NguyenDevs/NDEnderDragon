package com.github.nguyendevs.ND_EnderDragon.generals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.annotation.Nullable;

public class EndWorldsData {
    public static ArrayList<String> endWorldsList = new ArrayList();

    public static void saveObject(String path, Object object) {
        try {
            File file = new File(path);
            if (!file.isFile()) {
                file.createNewFile();
            }

            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
            output.writeObject(object);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    @Nullable
    private static Object loadObject(String path) {
        try {
            File file = new File(path);
            if (!file.isFile()) {
                System.out.println("Đã có lỗi khi cố tải config: file " + path + " không tồn tại.");
                return null;
            } else {
                ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
                return input.readObject();
            }
        } catch (ClassNotFoundException | IOException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static void saveEndWorlds() {
        if (endWorldsList == null || endWorldsList.isEmpty()) {
            endWorldsList = new ArrayList();
            endWorldsList.add("world_the_end");
        }

        saveObject("plugins/NDEnderDragon/endWorlds.ser", endWorldsList);
    }

    public static void restoreEndWorlds() {
        endWorldsList = new ArrayList();
        ArrayList<String> loaded = (ArrayList)loadObject("plugins/NDEnderDragon/endWorlds.ser");
        if (loaded == null) {
            endWorldsList = new ArrayList();
            endWorldsList.add("world_the_end");
        } else {
            endWorldsList = loaded;
        }

    }
}
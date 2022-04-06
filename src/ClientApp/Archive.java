package ClientApp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Archive implements Serializable {
    private static String path;
    private static File file;
    private static ArrayList<String> list;

    public Archive (File file, String path, List<String> list) throws IOException {
        this.path = path;
        this.file = new File(path);
        this.list = (ArrayList<String>) list;
    }

    public void addMessage(String message){
        if(list.size() <= 100){
            list.add(message);
        } else {
            list.remove(0);
            list.add(message);
        }
    }

    public ArrayList<String> getList() {
        return list;
    }

    public void serialize () throws IOException {
        FileOutputStream fos = new FileOutputStream(path,false);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this.list);
        oos.close();
        fos.close();
    }

    public String deserialize() throws IOException, NoClassDefFoundError, ClassNotFoundException, IOException {
        if (file.length() != 0) {
            try (FileInputStream fis = new FileInputStream(path);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                this.list = (ArrayList) ois.readObject();
                fis.close();
                ois.close();
            }
            for (String message : this.list) {
                System.out.println(message);
            }
        }
        return null;
    }

}

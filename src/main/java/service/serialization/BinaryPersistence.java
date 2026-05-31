package service.serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BinaryPersistence {
    public static final File DEFAULT_FILE = new File("data/system_state.bin");

    public void save(SystemState state) throws IOException {
        save(state, DEFAULT_FILE);
    }

    public void save(SystemState state, File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(state);
        }
    }

    public SystemState load() throws IOException, ClassNotFoundException {
        return load(DEFAULT_FILE);
    }

    public SystemState load(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (SystemState) in.readObject();
        }
    }
}

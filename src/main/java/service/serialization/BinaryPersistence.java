package service.serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Serviço responsável por gerir a persistência binária de dados da aplicação.
 * Fornece a infraestrutura necessária para serializar e desserializar o estado global
 * do sistema ({@link SystemState}), bem como instantâneos ou coleções de objetos genéricos,
 * utilizando os fluxos nativos de manipulação de objetos do Java.
 */
public class BinaryPersistence {

    /**
     * O ficheiro padrão de destino utilizado para as operações de salvamento e
     * carregamento quando nenhum ficheiro alternativo é explicitamente fornecido.
     */
    public static final File DEFAULT_FILE = new File("data/system_state.bin");

    /**
     * Serializa o estado global do sistema no ficheiro binário definido por defeito.
     *
     * @param state O objeto {@link SystemState} contendo o estado atual da aplicação.
     * @throws IOException Se ocorrer um erro de entrada/saída durante a gravação física.
     */
    public void save(SystemState state) throws IOException {
        save(state, DEFAULT_FILE);
    }

    /**
     * Serializa o estado global do sistema num ficheiro customizado específico.
     * Cria de forma preemptiva a árvore de diretórios pai caso esta ainda não exista em disco.
     *
     * @param state O objeto {@link SystemState} contendo o estado atual da aplicação.
     * @param file  O ficheiro {@link File} de destino onde os dados serão gravados.
     * @throws IOException Se ocorrer um erro de entrada/saída durante a escrita.
     */
    public void save(SystemState state, File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(state);
        }
    }

    /**
     * Recupera e desserializa o estado global do sistema a partir do ficheiro padrão.
     *
     * @return O objeto {@link SystemState} devidamente reconstruído.
     * @throws IOException            Se houver um erro na leitura do ficheiro.
     * @throws ClassNotFoundException Se a classe do objeto serializado não for encontrada em tempo de execução.
     */
    public SystemState load() throws IOException, ClassNotFoundException {
        return load(DEFAULT_FILE);
    }

    /**
     * Recupera e desserializa o estado global do sistema a partir de um ficheiro específico.
     *
     * @param file O ficheiro {@link File} de origem contendo os dados binários.
     * @return O objeto {@link SystemState} reconstruído a partir do ficheiro.
     * @throws IOException            Se ocorrer uma falha física de entrada/saída.
     * @throws ClassNotFoundException Se a definição da classe correspondente não for localizada.
     */
    public SystemState load(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (SystemState) in.readObject();
        }
    }

    /**
     * Guarda um objeto ou coleção genérica qualquer num ficheiro de destino.
     * Útil para exportações modulares baseadas em snapshots parciais de abas ou tabelas de símbolos.
     * Garante a criação automática das pastas intermédias do caminho do ficheiro.
     *
     * @param object O objeto a ser serializado (deve implementar {@link java.io.Serializable}).
     * @param file   O ficheiro {@link File} onde o fluxo binário será persistido.
     * @throws IOException Se ocorrer um erro no fluxo de escrita.
     */
    public void saveObject(Object object, File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(object);
        }
    }

    /**
     * Lê e reconstrói um objeto genérico a partir de um ficheiro binário indicado.
     * O objeto retornado deve sofrer um cast seguro para a sua estrutura original por quem o invoca.
     *
     * @param file O ficheiro {@link File} de origem.
     * @return O {@link Object} genérico desserializado.
     * @throws IOException            Se houver um problema no fluxo de leitura física.
     * @throws ClassNotFoundException Se o tipo do objeto gravado não puder ser instanciado por falta da classe.
     */
    public Object loadObject(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return in.readObject();
        }
    }
}
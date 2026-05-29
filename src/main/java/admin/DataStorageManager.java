package admin;

import model.graph.EdgeMetadata;
import model.graph.GraphEdge;
import model.graph.RelationType;
import model.graph.StreamingGraph;
import model.utilities.Region;
import model.users.User;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataStorageManager {

    private static final String TAG_USER = "USER";
    private static final String TAG_EDGE = "EDGE";
    private static final String TAG_SEARCH = "SEARCH";
    private static final String DELIMITER = ";";

    /**
     * EXPORTAR: Guarda utilizadores, grafo e histórico de pesquisas num ficheiro TXT.
     */
    public static void exportData(File file, List<User> users, StreamingGraph graph, List<String> searchHistory) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            // exporta os Utilizadores
            writer.write("# --- UTILIZADORES ---\n");
            for (User u : users) {
                // garante que extraímos o código e o nome do objeto Region
                String regCode = (u.getRegion() != null) ? u.getRegion().getCode() : "";
                String regName = (u.getRegion() != null) ? u.getRegion().getName() : "";

                // formato ATUALIZADO: USER;id;nome;genero;email;senha;dataRegisto;codigoRegiao;nomeRegiao;dataNascimento
                writer.write(TAG_USER + DELIMITER +
                        u.getId() + DELIMITER +
                        u.getName() + DELIMITER +
                        (u.getGender() != null ? u.getGender() : "") + DELIMITER +
                        u.getEmail() + DELIMITER +
                        u.getPassword() + DELIMITER +
                        u.getRegistrationDate() + DELIMITER +
                        regCode + DELIMITER +
                        regName + DELIMITER +
                        u.getBirthDate() + "\n");
            }

            // exporta as Arestas do grafo
            writer.write("\n# --- RELAÇÕES DO GRAFO ---\n");
            for (GraphEdge edge : graph.edges()) {
                EdgeMetadata meta = edge.getMetadata();
                writer.write(TAG_EDGE + DELIMITER +
                        edge.getFrom() + DELIMITER +
                        edge.getTo() + DELIMITER +
                        meta.getType().name() + DELIMITER +
                        meta.getWeight() + DELIMITER +
                        meta.getTimestamp() + "\n");
            }

            // exporta o Histórico de Pesquisas
            writer.write("\n# --- HISTÓRICO DE PESQUISAS ---\n");
            for (String search : searchHistory) {
                writer.write(TAG_SEARCH + DELIMITER + search + "\n");
            }
        }
    }

    /**
     * IMPORTAR: Lê o ficheiro TXT e popula as estruturas de dados.
     */
    public static List<String> importData(File file, StreamingGraph graph, List<User> userListOutput) throws IOException {
        List<String> importedSearches = new ArrayList<>();

        userListOutput.clear();
        new ArrayList<>(graph.vertices()).forEach(graph::removeVertex);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(DELIMITER);
                String tag = parts[0];

                switch (tag) {
                    case TAG_USER:
                        // parsing dos campos do Utilizador mapeados pelos índices
                        String id = parts[1];
                        String name = parts[2];
                        String gender = parts[3].isEmpty() ? null : parts[3];
                        String email = parts[4];
                        String password = parts[5];
                        LocalDate regDate = LocalDate.parse(parts[6]);

                        // captura as strings do Region
                        String regionCode = parts[7];
                        String regionName = parts[8];

                        LocalDate birthDate = LocalDate.parse(parts[9]);

                        Region region = new Region(regionCode, regionName);

                        // cria o utilizador passando o objeto 'region' acabado de reconstruir
                        User user = new User(name, gender, email, password, regDate, region, birthDate);

                        // manter o ID original do ficheiro para não quebrar o grafo
                        user.setId(id);

                        userListOutput.add(user);
                        graph.addUser(user);
                        break;

                    case TAG_EDGE:
                        String from = parts[1];
                        String to = parts[2];
                        RelationType type = RelationType.valueOf(parts[3]);
                        double weight = Double.parseDouble(parts[4]);
                        LocalDateTime timestamp = LocalDateTime.parse(parts[5]);

                        EdgeMetadata metadata = new EdgeMetadata(type, weight, timestamp, new HashMap<>());
                        graph.addEdge(from, to, metadata);
                        break;

                    case TAG_SEARCH:
                        if (parts.length > 1) {
                            importedSearches.add(parts[1]);
                        }
                        break;
                }
            }
        }
        return importedSearches;
    }
}
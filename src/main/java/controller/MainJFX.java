package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principal do ecossistema JavaFX responsável por inicializar
 * e carregar a interface gráfica da aplicação.
 * Herda as funcionalidades base da classe {@link Application}.
 */
public class MainJFX extends Application {

    /**
     * O ponto de entrada padrão da aplicação. Invoca o método interno de lançamento
     * da infraestrutura do JavaFX para gerir o ciclo de vida da interface.
     *
     * @param args Argumentos de linha de comando passados ao iniciar o programa.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Inicializa o palco (Stage) principal da aplicação. Carrega o ficheiro
     * descritivo FXML correspondente à vista principal, instancia a cena (Scene)
     * e expõe a janela ao utilizador.
     *
     * @param primaryStage O palco principal fornecido pelo runtime do JavaFX no qual
     * a cena da aplicação será definida.
     * @throws IOException Caso ocorra algum erro na localização ou leitura do ficheiro FXML.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setTitle("Projeto de AED2 e LPR2");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
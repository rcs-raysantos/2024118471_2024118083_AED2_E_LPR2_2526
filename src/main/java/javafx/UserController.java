package javafx;

import edu.ufp.inf.lp2.p01_intro.Date;
import edu.ufp.inf.lp2.p10_examples.transitbrigade.Driver;
import edu.ufp.inf.lp2.p10_examples.transitbrigade.PenaltyFee;
import edu.ufp.inf.lp2.p10_examples.transitbrigade.TransitPolice;
import edu.ufp.inf.lp2.p10_examples.transitbrigade.Vehicle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.ResourceBundle;


public class UserController implements Initializable {

    //Constants declaration for PATHS
    // NB: when running inside IDE, paths are relative to Project's root directory
    private static final String PATH_VEHICLES="./data/vehicles.txt";
    private static final String FILE_DELIMITER=";";
    private static final String PATH_BIN="./data/data_bt.bin";

    //Attributes linking to users_view.fxml UI components
    //Table for Vehicles
    @FXML
    public TableView<Vehicle> vehiclesTable;
    public TableColumn<Vehicle, String> registrationCol;
    public TableColumn<Vehicle, String> brandCol;
    public TableColumn<Vehicle, String> modelCol;
    public TableColumn<Vehicle, Integer> cylindersCol;
    public TextField registrationField;
    public TextField brandField;
    public TextField modelField;
    public TextField cylindersField;
    @FXML
    public ComboBox<String> vehicleComboBox;
    public ComboBox<String> driversComboBox;

    //ToDo: Table for Drivers

    //Table for Penalties
    @FXML
    public TableView<PenaltyFee> penaltiesTable;
    public TableColumn<PenaltyFee, String> driverCol;
    public TableColumn<PenaltyFee, String> vehicleCol;
    public TableColumn<PenaltyFee, Date> dateCol;
    public TableColumn<PenaltyFee, String> localCol;
    public TableColumn<PenaltyFee, String> motiveCol;

    //Create instance of TransitPolice manager class
    private final TransitPolice transitePolice = new TransitPolice();

    /**
     * Inicialização GUI
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //=================== =================== ===================
        //=================== Vehicles Table ===================
        //=================== =================== ===================
        //Associate Columns with CellValueFactory and CellFactory:
        // * setCellValueFactory():
        //  - Cell value factory is a Callback that expects an ObservableValue to be returned,
        //    for visualizing values into cells.
        // * setCellFactory():
        //  - Default cell factory may be replaced with custom implementation to support editing TableCells.
        registrationCol.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("registration"));
        registrationCol.setCellFactory(TextFieldTableCell.forTableColumn());

        brandCol.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("brand"));
        brandCol.setCellFactory(TextFieldTableCell.forTableColumn());

        modelCol.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("model"));
        modelCol.setCellFactory(TextFieldTableCell.forTableColumn());

        cylindersCol.setCellValueFactory(new PropertyValueFactory<Vehicle, Integer>("cylinders"));
        cylindersCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        /*
        cylindersCol.setCellFactory(new Callback<TableColumn<Vehicle, Integer>, TableCell<Vehicle, Integer>>() {
            @Override
            public TableCell<Vehicle, Integer> call(TableColumn<Vehicle, Integer> col) {
                return new TableCell<Vehicle, Integer>() {
                    @Override
                    protected void updateItem(Integer cylinders, boolean empty) {
                        super.updateItem(cylinders, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(Integer.toString(cylinders));
                        }
                    }
                };
            }
        });
        */

        /*
        vehicleCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PenaltyFee, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PenaltyFee, String> cellDataFeatures) {
                if (cellDataFeatures.getValue() != null) {
                    return new SimpleStringProperty(cellDataFeatures.getValue().getDriver().getName() + " - " + cellDataFeatures.getValue().getDriver().getDriversLicense());
                } else {
                    return new SimpleStringProperty("<No Info>");
                }
            }
        });
        */
        vehicleCol.setCellValueFactory((cellData) -> {
            if (cellData.getValue() != null) {
                return new SimpleStringProperty(cellData.getValue().getDriver().getName() + " - " + cellData.getValue().getDriver().getDriversLicense());
            } else {
                return new SimpleStringProperty("<No Info>");
            }
        });

        //=================== =================== ===================
        //Set listener to allow editing/changing cell values in vehiclesTable
        vehiclesTable.getItems().addListener( (ListChangeListener<? super Vehicle>)vehiclesChanges -> {
            System.out.println("ListChangeListener - vehiclesChanges = " + vehiclesChanges);
            ObservableList<? extends Vehicle> list = vehiclesChanges.getList();
            list.forEach( vehicle  -> {
                System.out.println("added "+vehicle);
            });
        });

        //ToDo: ...
        //=================== =================== ===================
        //=================== PenaltyFee Table ===================
        //=================== =================== ===================


        //=================== =================== ===================
        //================= Create Vehicles + Drivers & Penalties for Testing Purposes =================
        //=================== =================== ===================
        ArrayList<Vehicle> testVehicles = transitePolice.createVehciclesForTestingPurposes(4);
        ArrayList<Driver> testDrivers = transitePolice.createDriversAndPenaltyFeesForEachVehicleTestingPurposesOnly(testVehicles);
        //Clear and add all vehicles to vehiclesTable
        vehiclesTable.getItems().clear();
        vehiclesTable.getItems().addAll(testVehicles);
        //Update vehicles ComboBox
        this.addVehiclesToComboBox(testVehicles);
        /*
        //Clear and add all drivers to driversTable
        driversTable.getItems().clear();
        driversTable.getItems().addAll(driverArrayList);
        */
        //Update drivers ComboBox
        this.addDriversToComboBox(testDrivers);
    }

    /**
     * Handler para acção do botão de abertura do ficheiro de texto, referente aos dados dos veículos.
     *
     * @param actionEvent
     */
    public void handleReadTxtFileAction(ActionEvent actionEvent) {
        ArrayList<Vehicle> vehicleArrayList = readVehiclesFromTxtFile(PATH_VEHICLES);
        // Clear and add all vehicles to vehiclesTable
        vehiclesTable.getItems().clear();
        vehiclesTable.getItems().addAll(vehicleArrayList);

        // Update ComboBox
        addVehiclesToComboBox(vehicleArrayList);
    }

    /**
     * Handler para leitura de dados dos veículos a partir de um ficheiro de texto.
     *
     * @return ArrayList<Vehicle>
     * @throws IOException
     */
    private static ArrayList<Vehicle> readVehiclesFromTxtFile(String path) {
        ArrayList<Vehicle> vehicleArrayList = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
            // Lê o cabeçalho (se existir) e ignora
            String line = br.readLine();

            // Lê os dados
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(FILE_DELIMITER);
                if (tokens.length == 4) {
                    String reg = tokens[0];
                    String brand = tokens[1];
                    String model = tokens[2];
                    int cyl = Integer.parseInt(tokens[3]);
                    // Instancia e adiciona à lista
                    vehicleArrayList.add(new Vehicle(reg, brand, model, cyl));
                }
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        } finally {
            if (br!=null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return vehicleArrayList;
    }

    /**
     * Método para inserção de novos veículos na vehicleComboBox (tab Penalties).
     *
     * @param vehicles
     */
    private void addVehiclesToComboBox(ArrayList<Vehicle> vehicles) {
        vehicleComboBox.getItems().clear();
        for (Vehicle v : vehicles) {
            // Adiciona apenas a matrícula (registration) ou outro identificador único
            vehicleComboBox.getItems().add(v.getRegistration());
        }
    }

    private void addDriversToComboBox(ArrayList<Driver> drivers) {
        driversComboBox.getItems().clear();
        for (Driver d : drivers) {
            // Adiciona o nome do condutor (pode adaptar para incluir carta de condução, ex: nome - carta)
            driversComboBox.getItems().add(d.getName());
        }
    }

    /**
     * Handler para acção do botão de armazenamento de dados dos veículos num ficheiro de texto.
     *
     * @param actionEvent
     */
    public void handleSaveTxtFileAction(ActionEvent actionEvent) {
        saveVehiclesToTxtFile(PATH_VEHICLES, vehiclesTable.getItems().listIterator());
    }

    /**
     * Método para efectuar o armazenamento dos dados dos veículos num ficheiro de texto.
     */
    private static void saveVehiclesToTxtFile(String path, ListIterator<Vehicle> listIt) {
        System.out.println("saveVehiclesToTxtFile(): save vehicles to txt...");
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            // Save header of file
            pw.println("Registration" + FILE_DELIMITER + "Brand" + FILE_DELIMITER + "Model" + FILE_DELIMITER + "Cylinders");

            // Iterate over listIt and save vehicles into file
            while (listIt.hasNext()) {
                Vehicle v = listIt.next();
                pw.println(v.getRegistration() + FILE_DELIMITER +
                        v.getBrand() + FILE_DELIMITER +
                        v.getModel() + FILE_DELIMITER +
                        v.getCylinders());
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    /**
     * Handler para acção do botão de encerramento da aplicação.
     *
     * @param actionEvent
     */
    public void handleExitAction(ActionEvent actionEvent) {
        System.exit(0);
    }

    /**
     * Handler para acção do botão Add, responsável pela inserção de um veiculo na vehiclesTable.
     *
     * @param actionEvent
     */
    public void handleAddVehicleAction(ActionEvent actionEvent) {
        try {
            // Get vehicle info from GUI TextFields
            String reg = registrationField.getText();
            String brand = brandField.getText();
            String model = modelField.getText();
            int cyl = Integer.parseInt(cylindersField.getText());

            // Instantiate new vehicle
            Vehicle newVehicle = new Vehicle(reg, brand, model, cyl);

            // Add vehicle to table row
            vehiclesTable.getItems().add(newVehicle);

            // Create ArrayList from all vehicles inside vehiclesTable
            ArrayList<Vehicle> allVehicles = new ArrayList<>(vehiclesTable.getItems());

            // Add all vehicles to GUI ComboBox
            addVehiclesToComboBox(allVehicles);

            // Clear GUI TextFields
            registrationField.setText("");
            brandField.setText("");
            modelField.setText("");
            cylindersField.setText("");

        } catch (NumberFormatException e) {
            System.out.println("Erro: Os cilindros devem ser um número inteiro válido.");
            // Opcional: mostrar um Alert do JavaFX aqui avisando o utilizador
        }
    }

    /**
     * Handler para acção do botão de abertura do ficheiro binário, referente aos dados dos veículos.
     *
     * @param actionEvent
     */
    public void handleReadBinFileAction(ActionEvent actionEvent) {
        ArrayList<Vehicle> vehicleArrayList = readFromBinFile(PATH_BIN);
        if (vehicleArrayList != null) {
            // Clear vehiclesTable and add vehicles read from file
            vehiclesTable.getItems().clear();
            vehiclesTable.getItems().addAll(vehicleArrayList);

            // Update ComboBox
            addVehiclesToComboBox(vehicleArrayList);
        }
    }

    /**
     * Metodo para leitura do ficheiro binário, no path indicado.
     */
    private static ArrayList<Vehicle> readFromBinFile(String path) {
        ArrayList<Vehicle> vehicleArrayList = null;
        ObjectInputStream ois=null;
        try {
            //Open input stream
            ois = new ObjectInputStream(new FileInputStream(new File(path)));
            vehicleArrayList = (ArrayList<Vehicle>) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return vehicleArrayList;
    }

    /**
     * Handler para acção do botão de armazenamento de dados dos veículos num ficheiro binário.
     *
     * @param actionEvent
     */
    public void handleSaveBinFileAction(ActionEvent actionEvent) {
        saveToBinFile(PATH_BIN, this.vehiclesTable.getItems().listIterator());
    }

    /**
     * Metodo para efectuar o armazenamento dos dados dos veículos num ficheiro binário.
     */
    private static void saveToBinFile(String path, ListIterator<Vehicle> listIt) {
        ObjectOutputStream oos=null;
        try {
            //Open output stream
            oos=new ObjectOutputStream(new FileOutputStream(new File(path)));
            // Create ArrayList with all vehicles
            ArrayList<Vehicle> vehicleList = new ArrayList<>();
            while (listIt.hasNext()) {
                vehicleList.add(listIt.next());
            }

            // Save to file
            oos.writeObject(vehicleList);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Handler para acção de edição dos dados dos veículos na vehiclesTable.
     *
     * @param vehicleStringCellEditEvent
     */
    public void handleEditVehicleAction(TableColumn.CellEditEvent<Vehicle, Object> vehicleStringCellEditEvent) {
        int col=vehicleStringCellEditEvent.getTablePosition().getColumn();
        switch (col) {
            case 0:
                vehicleStringCellEditEvent.getRowValue().setRegistration((String) vehicleStringCellEditEvent.getNewValue());
                break;
            case 1:
                vehicleStringCellEditEvent.getRowValue().setBrand((String) vehicleStringCellEditEvent.getNewValue());
                break;
            case 2:
                vehicleStringCellEditEvent.getRowValue().setModel((String) vehicleStringCellEditEvent.getNewValue());
                break;
            case 3:
                //vehicleStringCellEditEvent.getRowValue().setCylinders(Integer.parseInt(vehicleStringCellEditEvent.getNewValue()));
                vehicleStringCellEditEvent.getRowValue().setCylinders((Integer) vehicleStringCellEditEvent.getNewValue());
                break;
        }
    }

    /**
     * Handler para acção de selecção do veículo na vehicleComboBox (tab Penalties).
     * Pesquisa se veículo seleccionado tem alguma multa inserida pela brigada de trânsito
     *
     * @param actionEvent
     */
    public void handleSelectVehicleAction(ActionEvent actionEvent) {
        penaltiesTable.getItems().clear();
        String vRegistration=vehicleComboBox.getValue();
        try {
            Vehicle v = transitePolice.searchVehicle(vRegistration);
            if (v != null) {
                penaltiesTable.getItems().addAll(transitePolice.allPenaltyFeesByVehicle(v));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handler para acção de selecção dos condutores na driversComboBox (tab Penalties).
     *
     * @param actionEvent
     */
    public void handleSelectDriverAction(ActionEvent actionEvent) {
        System.out.println("Not implemented yet! :(");
    }
}

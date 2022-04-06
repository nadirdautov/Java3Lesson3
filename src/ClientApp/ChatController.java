package ClientApp;

import Server.Authorization.JdbcConnection;
import Server.ServerCommandConstants;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ChatController implements Initializable {
    @FXML
    private TextArea textArea;
    @FXML
    private TextField messageField, loginField, changeNickField;
    @FXML
    private HBox messagePanel, authPanel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ListView<String> clientList;
    @FXML
    public HBox changeNickPanel;


    private final Network network;
    private String pathname = "archiveMessages.txt";
    private File file = new File(pathname);
    private ArrayList<String> archiveMsg = new ArrayList<String>();
    private Archive archive;

    public ChatController() {
        this.network = new Network(this);
    }

    public void setAuthenticated(boolean authenticated) {
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        messagePanel.setVisible(authenticated);
        messagePanel.setManaged(authenticated);
        clientList.setVisible(authenticated);
        clientList.setManaged(authenticated);
        changeNickPanel.setVisible(false);
        changeNickPanel.setManaged(false);
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setAuthenticated(false);
    }

    public void displayMessage(String text){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
             int oldText = textArea.getLength() + 1;
             String[] newText = text.split(" ");
             int selectText = oldText + newText[0].length()+ newText[1].length()+1;
             if(textArea.getText().isEmpty()){
                 textArea.setText(text);
             } else {
                 textArea.setText(textArea.getText()+ "\n" + text);
                 try {
                     archive.addMessage(text);
                     archive.serialize();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
             if(text.contains(" private ")){
                 textArea.selectRange(oldText,selectText);
             }
            }
        });
    }
    public void displayClient(String nickName) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clientList.getItems().add(nickName);
            }
        });
    }


    public void removeClient(String nickName) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clientList.getItems().remove(nickName);
            }
        });
    }


    public void sendAuth(ActionEvent event) {
        boolean authenticated = network.sendAuth(loginField.getText(), passwordField.getText());
        if (authenticated) {
            loginField.clear();
            passwordField.clear();
            setAuthenticated(true);
            //3.2 если файла не существует, то мы создаём новый.
            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    archive = new Archive(file,pathname,archiveMsg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    archive.deserialize();
                    archiveMsg = archive.getList();
                    for(String msg : archiveMsg){
                        if(!(textArea.getText().isEmpty())){
                            textArea.setText(textArea.getText() + '\n' + msg);
                        } else {
                            textArea.setText(msg);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void sendMessage(ActionEvent event) {
        network.sendMessage(messageField.getText());
        messageField.clear();
    }
    // закрыли коннект
    public void close() {
        network.closeConnection();
    }

    public void handleMouseClick(MouseEvent mouseEvent) {
        String nickName = clientList.getSelectionModel().getSelectedItem();
        messageField.setText(ServerCommandConstants.PRIVATE + " " + nickName + " ");
    }
    //Спасибо за туториал от Туськи
    // ПОЯВЛЯЕТСЯ ПАНЕЛЬ ДЛЯ ВВОДА НОВОГО НИКА
    public void changeNick(ActionEvent event) {
        changeNickPanel.setVisible(true);
        changeNickPanel.setManaged(true);
    }
    //МЕТОД ВЫЗЫВАЕТ МЕТОД ИЗ КЛАССА JDBC ДЛЯ СМЕНЫ НИКА В БАЗЕ ДАННЫХ
    public void chN(String lastNick, String newNick) {
        try {
            JdbcConnection.updateEx(lastNick, newNick);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("ошибка обновления ника");
        }
    }

    //ДЛЯ ЗАМЕНЫ НИКА В ЛИСТВИВЕ, У ВСЕХ КРОМЕ СЕБЯ ПОЯВЛЯЕТСЯ НОВЫЙ НИК
    //********
    public void setClientList(String lastNick, String newNick) {
        Platform.runLater(new Runnable() {
                              @Override
                              public void run() {
                                  for (String client : clientList.getItems()) {
                                      if (client.equals(lastNick)) {
                                          displayClient(newNick);
                                          removeClient(lastNick);
                                      }
                                  }
                              }
                          }
        );
    }

    //МЕТОД ОТПРАВЛЯЕТ СООБЩЕНИЕ НА СЕРВЕР И СКРЫВАЕТ / ОЧИЩАЕТ ПАНЕЛЬ ДЛЯ ВВОДА НОВОГО НИКНЕЙМА
    public void changeNickOk(ActionEvent event) {
        network.sendMessage(ServerCommandConstants.CHANGENICK + " " + changeNickField.getText());
        changeNickPanel.setVisible(false);
        changeNickPanel.setManaged(false);
        changeNickField.clear();

    }

}

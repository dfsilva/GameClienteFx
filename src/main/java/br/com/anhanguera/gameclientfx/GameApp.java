package br.com.anhanguera.gameclientfx;

import br.com.anhanguera.gameclientfx.controller.RootController;
import br.com.diegosilva.grpc.hello.AutenticacaoGrpc;
import br.com.diegosilva.grpc.hello.AutenticacaoRequest;
import br.com.diegosilva.grpc.hello.SairRequest;
import br.com.diegosilva.grpc.hello.UsuariosGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.reactivex.Observable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class GameApp extends Application {

    private static GameApp instance;
    private Channel canal;
    private String usuarioAutenticado;

    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println(Thread.currentThread().getName());
        instance = this;
        this.stage = stage;
        abrirTelaLogin(stage);
    }

    private void abrirTelaLogin(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/LoginLayout.fxml"));
            VBox loginLayout = (VBox) loader.load();
            Scene scene = new Scene(loginLayout);
            stage.setTitle("Entrar");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirTelaPrincipal(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/RootLayout.fxml"));
            StackPane loginLayout = (StackPane) loader.load();
            RootController controller = loader.<RootController>getController();
            Scene scene = new Scene(loginLayout);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {

        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static GameApp getInstance() {
        return instance;
    }

    public void setUsuarioAutenticado(String usuarioAutenticado) {
        this.usuarioAutenticado = usuarioAutenticado;
    }

    public String getUsuarioAutenticado() {
        return usuarioAutenticado;
    }

    public Channel getCanal() {
        if(canal == null){
            canal = ManagedChannelBuilder.forAddress("191.252.92.246", 9280)
                    .usePlaintext(true).build();
        }
        return canal;
    }

    public Stage getStage() {
        return stage;
    }

    public void showErrorMessage(Optional<String> title, Optional<String> header, Optional<String> content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title.orElse(""));
        alert.setHeaderText(header.orElse(""));
        alert.setContentText(content.orElse(""));
        alert.showAndWait();
    }

    @Override
    public void stop() throws Exception {
        UsuariosGrpc.UsuariosBlockingStub stub
                = UsuariosGrpc.newBlockingStub(GameApp.getInstance().getCanal());
        SairRequest request = SairRequest.newBuilder().setNome(usuarioAutenticado).build();
        stub.sair(request);

        super.stop();

    }
}

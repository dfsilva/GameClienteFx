package br.com.anhanguera.gameclientfx.controller;


import br.com.anhanguera.gameclientfx.GameApp;
import br.com.anhanguera.gameclientfx.ui.ProgressView;
import br.com.diegosilva.grpc.hello.AutenticacaoGrpc;
import br.com.diegosilva.grpc.hello.AutenticacaoRequest;
import br.com.diegosilva.grpc.hello.AutenticacaoResponse;
import io.reactivex.Observable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by diego on 15/06/17.
 */
public class LoginController implements Initializable {

    private static final String TAG = LoginController.class.getName();

    @FXML
    private Button btnEntrar;

    @FXML
    private Button btnCancelar;

    @FXML
    private TextField txUsuario;

    ProgressView progress = new ProgressView();

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        btnEntrar.setOnAction(ev -> {
            progress.show("Entrando...");

            //codigo asincrono
             Observable.fromCallable(()->{
              AutenticacaoGrpc.AutenticacaoBlockingStub stub
                        = AutenticacaoGrpc.newBlockingStub(GameApp.getInstance().getCanal());
                AutenticacaoRequest request = AutenticacaoRequest.newBuilder()
                        .setUsuario(txUsuario.getText()).build();
                return stub.autenticar(request);
            }).observeOn(JavaFxScheduler.platform())
            .subscribeOn(Schedulers.newThread()).subscribe((response)->{
                progress.hide();
                if(response.getCodigo() < 0){
                    GameApp.getInstance().showErrorMessage(Optional.of("Erro ao efetuar login"),
                            Optional.empty(), Optional.ofNullable(response.getMessage()));
                }else{
                    GameApp.getInstance().setUsuarioAutenticado(txUsuario.getText());
                    exibirTelaPrincipal();
                }
            });

             /*
             código sincrono
            progress.show("Entrando...");

            AutenticacaoGrpc.AutenticacaoBlockingStub stub
                    = AutenticacaoGrpc.newBlockingStub(GameApp.getInstance().getCanal());
            AutenticacaoRequest request = AutenticacaoRequest.newBuilder()
                    .setUsuario(txUsuario.getText()).build();
            AutenticacaoResponse response = stub.autenticar(request);

            progress.hide();

            if(response.getCodigo() < 0){
                GameApp.getInstance().showErrorMessage(Optional.of("Erro ao efetuar login"),
                        Optional.empty(), Optional.ofNullable(response.getMessage()));
            }else{
                GameApp.getInstance().setUsuarioAutenticado(txUsuario.getText());
                try {
                    exibirTelaPrincipal();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            */

        });

        btnCancelar.setOnAction(ev -> {
            System.exit(0);
        });
    }


    public void exibirTelaPrincipal() throws IOException {
        Stage stage = (Stage) GameApp.getInstance().getStage();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/RootLayout.fxml"));
        StackPane loginLayout = (StackPane) loader.load();
        RootController controller = loader.<RootController>getController();

        Scene scene = new Scene(loginLayout);
        stage.setScene(scene);
        stage.show();
    }
}

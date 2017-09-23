package br.com.anhanguera.gameclientfx.controller;

import br.com.anhanguera.gameclientfx.GameApp;
import br.com.diegosilva.grpc.hello.Usuario;
import br.com.diegosilva.grpc.hello.UsuariosGrpc;
import io.grpc.stub.StreamObserver;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

/**
 * Created by diego on 15/06/17.
 */
public class RootController implements Initializable {

    private static class OperacoesUsuario{
        public static final int INCLUSAO = 0;
        public static final int EXCLUCAO = 1;
    }


    private static final String TAG = RootController.class.getName();

    @FXML
    private TableView tbUsuarios;
    private ObservableList<UsuarioVm> usuarios = FXCollections.observableArrayList();
    private StreamObserver<Usuario> usuariosStream;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GameApp.getInstance().getStage().setTitle(GameApp.getInstance().getUsuarioAutenticado());

        TableColumn colunaNomeUsuario = new TableColumn("Nome do Usuário");
        colunaNomeUsuario.setCellValueFactory(new PropertyValueFactory<UsuarioVm, String>("nome"));

        tbUsuarios.setItems(usuarios);

        tbUsuarios.getColumns().add(colunaNomeUsuario);

        usuariosStream = new StreamObserver<Usuario>() {

            @Override
            public void onNext(final Usuario value) {
                //adicionar usuario na tabela
                if(value.getOp() == OperacoesUsuario.INCLUSAO) {
                    usuarios.add(new UsuarioVm(new SimpleStringProperty(value.getNome())));
                }else{
                    Iterator<UsuarioVm> it = usuarios.iterator();
                    while (it.hasNext()){
                        UsuarioVm u = it.next();
                        if(u.nome.getValue().equals(value.getNome())){
                            it.remove();
                        }
                    }
                }
                tbUsuarios.refresh();
            }
            @Override
            public void onError(Throwable t) {
                System.err.println("Erro no observer");
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                registrarStreamUsuarios();
                            }
                        },
                        5000
                );
            }
            @Override
            public void onCompleted() {
                System.out.println("Servico completado");
            }
        };

        registrarStreamUsuarios();
    }


    private void registrarStreamUsuarios(){
        Usuario request = Usuario.newBuilder().setNome(GameApp.getInstance().getUsuarioAutenticado()).build();
        UsuariosGrpc.UsuariosStub stub = UsuariosGrpc.newStub(GameApp.getInstance().getCanal());
        stub.listarUsuarios(request, usuariosStream);
    }

    public static class UsuarioVm {
        private final SimpleStringProperty nome;

        public UsuarioVm(SimpleStringProperty nome) {
            this.nome = nome;
        }

        public String getNome() {
            return nome.get();
        }

        public SimpleStringProperty nomeProperty() {
            return nome;
        }
    }



}

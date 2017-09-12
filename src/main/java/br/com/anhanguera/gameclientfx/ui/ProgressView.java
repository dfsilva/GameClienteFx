package br.com.anhanguera.gameclientfx.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProgressView {
    private final Stage dialogStage;
    private final ProgressBar pb = new ProgressBar();
    private final ProgressIndicator pin = new ProgressIndicator();
    private final Label label = new Label();

    public ProgressView() {

        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.setResizable(false);

        dialogStage.initModality(Modality.APPLICATION_MODAL);

        pb.setProgress(-1F);
        pin.setProgress(-1F);


        final HBox hb = new HBox();
        hb.setSpacing(5);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(pin, label);

        Scene scene = new Scene(hb);
        dialogStage.setScene(scene);
    }

    public void show(String text)  {
       // dialogStage.setTitle(text);
        label.setText(text);
        dialogStage.show();
    }

    public void hide(){
        dialogStage.hide();
    }
}

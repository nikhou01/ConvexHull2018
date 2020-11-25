/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enveloppeconvexe;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Nicol
 */
public class FXMLDocumentController implements Initializable {

	private ArrayList<Circle> points;
	private ArrayList<Circle> enveloppe;

	@FXML
	private AnchorPane mainPointPanel;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		points = new ArrayList<>();
		/* On effecture une rotation de la Layout où sera dessiné les points dans le but d'avoir une origine Y en bas à gauche de l'écran
		 * par défaut, l'origine Y se trouve en haut à gauche de l'écran */
		Scale scale = new Scale();
		scale.setX(1);
		scale.setY(-1);
		/* On pivote par rapport au centre de la Layout */
		scale.pivotYProperty().bind(Bindings.createDoubleBinding(()
				-> mainPointPanel.getBoundsInLocal().getMinY() + mainPointPanel.getBoundsInLocal().getHeight() / 2,
				mainPointPanel.boundsInLocalProperty()));
		mainPointPanel.getTransforms().add(scale);
	}

	@FXML
	private void close(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
	}

	@FXML
	private void clickEvent(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
			if (points.size() < 100 && event.getX() >= 10 && event.getY() >= 10 && event.getX() <= 670 && event.getY() <= 670) {
				points.add(new Circle(event.getX(), event.getY(), 10, Color.color(Math.random(), Math.random(), Math.random())));
				drawEnveloppe();
			}
		} else {
			clearPane();
		}
	}

	@FXML
	private void clearAll(ActionEvent event) {
		clearPane();
	}

	@FXML
	private void randomPoint(ActionEvent event) {
		points = ConvexHull.pointsInitiaux(12);
		drawEnveloppe();

	}

	private void drawEnveloppe() {
		mainPointPanel.getChildren().clear();
		mainPointPanel.getChildren().addAll(points);
		if (points.size() > 1) {
			enveloppe = ConvexHull.enveloppe(points);
			for (int i = 1; i < enveloppe.size(); i++) {
				mainPointPanel.getChildren().add(new Line(enveloppe.get(i - 1).getCenterX(), enveloppe.get(i - 1).getCenterY(), enveloppe.get(i).getCenterX(), enveloppe.get(i).getCenterY()));
			}
			mainPointPanel.getChildren().add(new Line(enveloppe.get(0).getCenterX(), enveloppe.get(0).getCenterY(), enveloppe.get(enveloppe.size() - 1).getCenterX(), enveloppe.get(enveloppe.size() - 1).getCenterY()));
		}
	}

	private void clearPane() {
		if (!mainPointPanel.getChildren().isEmpty()) {
			mainPointPanel.getChildren().clear();
			points.clear();
			enveloppe.clear();
		}
	}

}

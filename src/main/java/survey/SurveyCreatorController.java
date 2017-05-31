package survey;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import exceptions.EmptyRequiredAttributeException;
import exceptions.NotInRangeException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;
import question.*;

import java.io.IOException;
import java.util.*;

public class SurveyCreatorController {

    private static final String STYLE = "/views/Style.css";
    private static final String FONTS = "/views/fonts.css";
    private static final String INTEGER = "^$|^([-]?[1-9]\\d*|0)$";
    private static final String NATURAL = "^$|^([1-9]\\d*|0)$";

    @FXML
    public JFXTextField surveyTitle;
    @FXML
    public JFXTextField surveyDescription;

    @FXML
    public VBox vPrincipalBox;

    @FXML
    public Label missingLabel;

    private Stage stage;

    private Set<QuestionBuilder> questionBuilders = new HashSet<>();

    private List<String> questionTypes = Arrays.asList("Free question",
            "Numeric question",
            "Sorted qualitative question",
            "Unsorted qualitative question",
            "Multivalued qualitative question");


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void newQuestionButtonPressed(ActionEvent actionEvent) {

        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("New question");
        dialog.setHeaderText("Add a new question");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Okei", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Question name");
        JFXComboBox<Label> comboBox = new JFXComboBox<>();
        for (String type : questionTypes) {
            comboBox.getItems().add(new Label(type));
        }
        comboBox.getSelectionModel().select(0);
        grid.add(new Label("Question:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(comboBox, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> username.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), comboBox.getSelectionModel().getSelectedItem().toString());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(questionStatement -> {
            System.out.println("Username=" + questionStatement.getKey() + ", Password=" + questionStatement.getValue());
            addQuestion(questionStatement.getKey(), questionStatement.getValue());
        });
    }

    private void addQuestion(String statement, String type) {
        if (type.indexOf(questionTypes.get(0)) != -1) {
            addFreeQuestion(statement);
        } else if (type.indexOf(questionTypes.get(1)) != -1) {
            addNumericQuestion(statement);
        } else if (type.indexOf(questionTypes.get(2)) != -1) {
            addSortedQualitativeQuestion(statement);
        } else if (type.indexOf(questionTypes.get(3)) != -1) {
            addUnsortedQualitativeQuestion(statement);
        } else {
            addMultivaluedQualitativeQuestion(statement);
        }
    }

    private void addMultivaluedQualitativeQuestion(String statement) {
        VBox mainBox = new VBox();
        HBox questionBox = new HBox();
        HBox optionsBox = new HBox();

        MultivaluedUnsortedQualitativeQuestionBuilder builder = new MultivaluedUnsortedQualitativeQuestionBuilder();

        optionsBox.getChildren().add(new JFXButton("Add option"));

        questionBox.getChildren().add(optionsBox);
        questionBox.getChildren().add(new JFXButton("X"));

        JFXTextField statementField = new JFXTextField();
        statementField.setText(statement);
        builder.setStatement(statementField);
        mainBox.getChildren().add(statementField);
        TextField maxAnswers = new TextField();
        builder.setMaxAnswers(maxAnswers);
        mainBox.getChildren().addAll(maxAnswers, questionBox);
        questionBuilders.add(builder);
        vPrincipalBox.getChildren().add(mainBox);
    }

    private void addUnsortedQualitativeQuestion(String statement) {
        HBox individualOptionBox = new HBox();
        VBox allOptionsBox = new VBox();
        HBox optionsBox = new HBox();

        UnsortedQualitativeQuestionBuilder unsortedQualitativeQuestionBuilder = new UnsortedQualitativeQuestionBuilder();

        optionsBox.getChildren().add(allOptionsBox);
        optionsBox.getChildren().add(new JFXButton("Add option"));
        HBox questionBox = new HBox();
        questionBox.getChildren().add(optionsBox);
        questionBox.getChildren().add(new JFXButton("X"));
        VBox mainBox = new VBox();
        JFXTextField statementField = new JFXTextField();
        statementField.setText(statement);
        unsortedQualitativeQuestionBuilder.setStatement(statementField);

        questionBuilders.add(unsortedQualitativeQuestionBuilder);

        mainBox.getChildren().add(statementField);
        mainBox.getChildren().add(questionBox);
        vPrincipalBox.getChildren().add(mainBox);
    }

    private void addSortedQualitativeQuestion(String statement) {
        HBox individualOptionBox = new HBox();
        VBox allOptionsBox = new VBox();
        HBox optionsBox = new HBox();

        SortedQualitativeQuestionBuilder sortedQualitativeQuestionBuilder = new SortedQualitativeQuestionBuilder();

        optionsBox.getChildren().add(allOptionsBox);
        optionsBox.getChildren().add(new JFXButton("Add option"));
        HBox questionBox = new HBox();
        questionBox.getChildren().add(optionsBox);
        questionBox.getChildren().add(new JFXButton("X"));
        VBox mainBox = new VBox();
        JFXTextField statementField = new JFXTextField();
        statementField.setText(statement);
        sortedQualitativeQuestionBuilder.setStatement(statementField);

        questionBuilders.add(sortedQualitativeQuestionBuilder);

        mainBox.getChildren().add(statementField);
        mainBox.getChildren().add(questionBox);
        vPrincipalBox.getChildren().add(mainBox);
    }

    private void addNumericQuestion(String statement) {
        VBox vBox = new VBox();

        NumericQuestionBuilder numericQuestionBuilder = new NumericQuestionBuilder();

        JFXTextField statementField = new JFXTextField();
        statementField.setText(statement);
        numericQuestionBuilder.setStatement(statementField);

        vBox.getChildren().add(statementField);
        HBox hBox = new HBox();

        hBox.getChildren().add(new Label("Min:"));
        JFXTextField minField = new JFXTextField();
        numericQuestionBuilder.setMinValue(minField);
        hBox.getChildren().add(checkRegExField(minField, INTEGER));

        hBox.getChildren().add(new Label("Max:"));
        JFXTextField maxField = new JFXTextField();
        numericQuestionBuilder.setMaxValue(maxField);
        hBox.getChildren().add(checkRegExField(maxField, INTEGER));

        questionBuilders.add(numericQuestionBuilder);

        vBox.getChildren().add(hBox);
        HBox mainHBox = new HBox();
        mainHBox.getChildren().add(vBox);
        mainHBox.getChildren().add(new JFXButton("X"));
        vPrincipalBox.getChildren().add(mainHBox);
    }

    private void addFreeQuestion(String statement) {
        VBox vBox = new VBox();

        FreeQuestionBuilder freeQuestionBuilder = new FreeQuestionBuilder();

        JFXTextField statementField = new JFXTextField();
        statementField.setText(statement);
        freeQuestionBuilder.setStatement(statementField);

        vBox.getChildren().add(statementField);
        HBox hBox = new HBox();
        hBox.getChildren().add(new Label("Max length:"));
        JFXTextField maxLenghtField = new JFXTextField();
        freeQuestionBuilder.setMaxLength(maxLenghtField);

        questionBuilders.add(freeQuestionBuilder);

        hBox.getChildren().add(checkRegExField(maxLenghtField, NATURAL));
        vBox.getChildren().add(hBox);
        maxLenghtField.getParent();
        HBox mainHBox = new HBox();
        mainHBox.getChildren().add(vBox);
        mainHBox.getChildren().add(new JFXButton("X"));

        vPrincipalBox.getChildren().add(mainHBox);

    }

    public void cancelButtonPressed() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane root = loader.load(getClass().getResource("/views/SurveyListView.fxml").openStream());
        SurveyListController controller = loader.getController();
        controller.setStage(stage);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(STYLE);
        scene.getStylesheets().add(FONTS);
        stage.setScene(scene);
        stage.show();
    }

    private JFXTextField checkRegExField(JFXTextField textField, String regex) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> textField.setFocusColor(newValue.matches(regex) ? Color.BLUE : Color.RED));
        return textField;
    }

    public void saveButtonPressed() throws IOException {
        System.out.print("Save!");
        Survey survey = new Survey();
        survey.setTitle("Miquel");
        for (QuestionBuilder questionBuilder : this.questionBuilders) {
            try {
                survey.addQuestion(questionBuilder.build());
            } catch (NotInRangeException e) {
                missingLabel.setText("Incorrect values");
            } catch (EmptyRequiredAttributeException e) {
                missingLabel.setText("Empty fields");
            }
        }
        try {
            survey.save();
            //cancelButtonPressed();
        } catch (EmptyRequiredAttributeException e) {
            e.printStackTrace();
        }
    }
}

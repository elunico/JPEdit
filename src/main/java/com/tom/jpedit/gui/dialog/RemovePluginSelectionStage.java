package com.tom.jpedit.gui.dialog;

import com.tom.jpedit.ApplicationContext;
import com.tom.jpedit.gui.DependantStage;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.gui.i18n.Strings;
import com.tom.jpedit.util.LoadedJPPlugin;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RemovePluginSelectionStage extends DependantStage {
  public RemovePluginSelectionStage(JPEditWindow owner) {
    super(owner);

    TableView<LoadedJPPlugin> view = new TableView<>(((ObservableList<LoadedJPPlugin>) ApplicationContext.getContext()
                                                                                                         .getLoadedPlugins()));
    TableColumn<LoadedJPPlugin, String> nameColumn = new TableColumn<>("Plugin Name");
    nameColumn.setCellValueFactory(dataFeatures -> new SimpleStringProperty(dataFeatures.getValue()
                                                                                        .getMainClass()
                                                                                        .getClass()
                                                                                        .getName()));
    nameColumn.prefWidthProperty().bind(view.widthProperty().subtract(1));
    view.getColumns().add(nameColumn);

    VBox root = new VBox();
    root.setPadding(new Insets(5));
    root.setSpacing(5);

    HBox buttonBox = new HBox();
    buttonBox.setPadding(new Insets(5));
    buttonBox.setSpacing(5);

    Button removeButton = new Button(Strings.Content.BUTTON_REMOVE.text);
    Button closeButton = new Button(Strings.Content.FILE_MENU_ITEM_CLOSE.text);

    buttonBox.getChildren().addAll(closeButton, removeButton);

    removeButton.setOnMousePressed(event -> {
      LoadedJPPlugin plugin = view.getSelectionModel().getSelectedItem();
      if (plugin == null) {
        return;
      }
      ApplicationContext.getContext().unregisterPlugin(plugin);
    });

    closeButton.setOnMousePressed(event -> close());

    root.getChildren().addAll(view, buttonBox);
    Scene scene = new Scene(root);
    setScene(scene);
  }


}

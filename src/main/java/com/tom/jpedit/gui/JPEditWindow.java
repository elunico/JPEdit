package com.tom.jpedit.gui;

import com.tom.jpedit.Action;
import com.tom.jpedit.ApplicationContext;
import com.tom.jpedit.gui.i18n.Strings;
import com.tom.jpedit.gui.menu.*;
import com.tom.jpedit.handlers.ActionHandler;
import com.tom.jpedit.handlers.file.*;
import com.tom.jpedit.handlers.misc.JPEditWindowKeyHandler;
import com.tom.jpedit.listeners.TextAreaTextChangeListener;
import com.tom.jpedit.logging.JPLogger;
import com.tom.jpedit.plugins.PluginProperties;
import com.tom.jpedit.plugins.components.PluginKeyboardShortcut;
import com.tom.jpedit.plugins.components.PluginMenuItem;
import com.tom.jpedit.plugins.components.PluginToolbarButton;
import com.tom.jpedit.util.LoadedJPPlugin;
import com.tom.jpedit.workers.AutoSaveWorker;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tom.utils.file.FileUtilsKt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.tom.jpedit.ApplicationContext.getContext;
import static tom.javafx.JavaFXUtilsKt.popupAlert;
import static tom.javafx.JavaFXUtilsKt.stackTraceToString;


/**
 * This class represents a text editor window of JPEdit
 * <p>
 * These windows contain all the JavaFX controls that the window contains
 * including those for plugins that are loaded.
 * <p>
 * It also contains state regarding save files, autosave workers, autosave state
 * and whether the text area is dirty (unsaved) or not.
 * <p>
 * The window can be duplicated with all its properties.
 * <p>
 * Because it expends {@link DependableStage} stages can depend on
 * JPEditWindows. Stages that depend on a {@code DependableStage}
 * must close if the {@code DependableStage} closes. See the documentation
 * for {@link DependableStage} for more
 *
 * @author Thomas Povinelli
 * Created 4/5/19
 * In JPEdit
 */
public class JPEditWindow extends DependableStage {
    private final static Map<Action, EventTarget> actionControlMap = new HashMap<>();

    public static @NotNull Map<Action, EventTarget> actionControlMap() {
        return actionControlMap;
    }
    // Controls
    private final VBox root = new VBox();
    private final TextArea textArea = new TextArea();
    private final HBox buttonBox = new HBox();
    private final Button newButton = new Button(Strings.Content.FILE_MENU_ITEM_NEW.text);
    private final Button newWindowButton = new Button(Strings.Content.FILE_MENU_ITEM_NEW_WINDOW.text);
    private final Button openButton = new Button(Strings.Content.FILE_MENU_ITEM_OPEN.text);
    private final Button saveButton = new Button(Strings.Content.FILE_MENU_ITEM_SAVE.text);
    private final Button saveAsButton = new Button(Strings.Content.FILE_MENU_ITEM_SAVE_AS.text);
    private final Label lastSavedLabeler = new Label(Strings.Content.UILABEL_LAST_SAVE_LABEL.text);
    private final Label lastSaveLabel = new Label(Strings.Content.UILABEL_LAST_SAVE_LABEL.text);
    private final MenuBar menuBar = new MenuBar();
    private final Menu pluginMenu = new JPEditPluginMenu(this, "Plugins");
    private final String tempSavesDir = getContext().getProperty("tempsaves_dir");
    // STATE
    private final AtomicBoolean autoSaveEnabled = new AtomicBoolean(true);
    private final int id;
    private Menu openRecentMenu;
    // WORKERS
    private AutoSaveWorker autoSaveWorker;
    // Properties
    private SimpleObjectProperty<File> saveFile; // = null;
    private SimpleBooleanProperty dirty; // = false;
    private SimpleBooleanProperty fullPathInTitle;// = true;

    public JPEditWindow() {
        this(Strings.Content.WINDOW_TITLE_DEFAULT.text);
    }

    public JPEditWindow(String title) {
        id = getContext().newWindowId();
        JPLogger.getAppLog().info("Bootstrap of Window with ID=" + id);
        setTitle(title);
        populateMenus();
        textArea.textProperty().addListener(new TextAreaTextChangeListener(this));
        textArea.setOnContextMenuRequested(e -> wasDirtied());
        addEventHandler(KeyEvent.KEY_PRESSED, new JPEditWindowKeyHandler(this));
        addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            JPLogger.getAppLog().info("Closing Window with ID=" + id);
            new CloseWindowActionHandler(this).handle(ActionHandler.asAction(event));
            event.consume();
        });
        registerButtonHandlers();
        styleWindow();
        startAutoSaveWorker();
        setScene(new Scene(root));
        textArea.requestFocus();
        putActions();
        enableDebugShortcuts();
        JPLogger.getAppLog().info("Bootstrap of Window with ID=" + id + " complete");
    }

    private void enableDebugShortcuts() {
        this.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.isShiftDown() && event.isAltDown() && event.isMetaDown() && event.getCode() == KeyCode.G) {
                throw new AssertionError("Intentional Debug Assertion Failure");
            }
        });
    }

    private void populateMenus() {
        Menu fileMenu = new JPEditFileMenu(this, Strings.Content.MENU_TITLE_FILE.text);
        Menu editMenu = new JPEditEditMenu(this, Strings.Content.MENU_TITLE_EDIT.text);
        Menu formatMenu = new JPEditFormatMenu(this, Strings.Content.MENU_TITLE_FORMAT.text);
        Menu advancedMenu = new JPEditAdvancedMenu(this, Strings.Content.MENU_TITLE_ADVANCED.text);
        menuBar.getMenus().addAll(fileMenu, editMenu, formatMenu, advancedMenu, pluginMenu);
    }

    private void putActions() {
        actionControlMap.put(Action.NEW_ACTION, newButton);
        actionControlMap.put(Action.NEW_WINDOW_ACTION, newWindowButton);
        actionControlMap.put(Action.OPEN_ACTION, openButton);
        actionControlMap.put(Action.SAVE_ACTION, saveButton);
        actionControlMap.put(Action.SAVE_AS_ACTION, saveAsButton);
    }

    /**
     * Marks the window as dirty. Setter does not exist because
     * unmarking should be up to the JPEditWindow
     */
    public void wasDirtied() {
        dirtyPropertyInternal().set(true);
    }

    private SimpleBooleanProperty dirtyPropertyInternal() {
        return (SimpleBooleanProperty) dirtyProperty();
    }

    /**
     * This property indicates if the {@link TextArea} is dirtied
     * <p>
     * This is a ReadOnlyProperty but it can be watched. It is true
     * if the TextArea has been changed since the last save.
     *
     * @return the Dirty Property of the JPEditWindow
     */
    public ReadOnlyBooleanProperty dirtyProperty() {
        if (dirty == null) {
            dirty = new SimpleBooleanProperty(this, "dirty", false);
        }
        return dirty;
    }

    private void styleWindow() {
        buttonBox.setSpacing(5);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(5));
        buttonBox.getChildren()
                 .addAll(newButton, newWindowButton, openButton, saveButton, saveAsButton, lastSavedLabeler, lastSaveLabel);
        setWidth(800);
        setHeight(600);
        setFullPathInTitle(ApplicationContext.getContext().getUserPreferences().isFullPathShowing());
        textArea.setFont(ApplicationContext.getContext().getUserPreferences().getPreferredFont());
        textArea.prefHeightProperty().bind(heightProperty());
        root.getChildren().addAll(menuBar, buttonBox, textArea);
    }

    private void registerButtonHandlers() {
        newButton.setOnAction(new NewActionHandler(this));
        newWindowButton.setOnAction(new NewWindowActionHandler(this));
        openButton.setOnAction(new OpenActionHandler(this));
        saveButton.setOnAction(new SaveActionHandler(this));
        saveAsButton.setOnAction(new SaveAsActionHandler(this));
    }

    private void startAutoSaveWorker() {
        final File f = new File(tempSavesDir);
        boolean ok = true;
        if (!f.exists()) {
            ok = f.mkdir();
        }
        if (!ok || !f.isDirectory() || !f.canWrite()) {
            popupAlert(Strings.Content.AUTOSAVE_WORKER_FAILED_DIR_INACC_STRING.text, Strings.Content.AUTOSAVE_WORKER_FAILED_DIR_INACC_TITLE.text);
            autoSaveEnabled.getAndSet(false);
        }
        if (autoSaveEnabled.get()) {
            this.autoSaveWorker = new AutoSaveWorker(this);
            autoSaveWorker.start();
        }
    }

    /**
     * Sets the autoSaveEnabled to true
     */
    public void turnAutoSaveOn() {
        autoSaveEnabled.getAndSet(true);
        JPLogger.getAppLog()
                .info(() -> "Auto save turned on beginning fixed interval task with delay=" + ApplicationContext.getContext()
                                                                                                                .getUserPreferences()
                                                                                                                .getAutosavePeriodMillis() + "ms");
    }

    /**
     * Sets autoSaveEnabled to false
     */
    public void turnAutoSaveOff() {
        autoSaveEnabled.getAndSet(false);
        JPLogger.getAppLog().info(() -> "Auto save turned off.");
    }

    /**
     * Sets the autoSaveEnabled to false, cancels the Future and shuts down the executor, <b><i>and deletes
     * the temporary file created by autosave worker</i></b>
     * Auto-save <b>cannot</b> be turned back on once this method is called.
     */
    public void terminateAutoSaveWorker() throws ExecutionException, InterruptedException {
        autoSaveEnabled.getAndSet(false);
        if (autoSaveWorker != null) {
            autoSaveWorker.terminate();
        }
        final File autoSaveFile = FileUtilsKt.toFile(getTempSaveFileName());
        try {
            JPLogger.debug(
                    JPLogger.getAppLog(),
                    "About to delete " + autoSaveFile.toPath() + " when tearing down auto save worker thread"
            );
            Files.deleteIfExists(autoSaveFile.toPath());
        } catch (IOException | SecurityException e) {
            popupAlert("Autosave disabled but temporary files not deleted!\nSee logs for more details.");
            JPLogger.getErrLog()
                    .severe("Failed to delete autosave temporary files -> " + stackTraceToString(e.getStackTrace()));
        }
        JPLogger.getAppLog().info(() -> "Auto save worker terminated.");
    }

    /**
     * Get the name of the temporary file that is used by the
     * {@link AutoSaveWorker} to autosave the file
     *
     * @return the name of the temporary autosave file
     */
    public String getTempSaveFileName() {
        return tempSavesDir + File.separator + "tempSave_" + getId() + ".tmp";
    }

    public int getId() {
        return id;
    }

    /**
     * Updates the list of recent files in the file menu according to the
     * record of recent files that the application maintains
     */
    public void populateRecentItems() {

    }

    /**
     * Display the saved file name in the title bar if it exists
     */
    public void updateTitleForSave() {
        if (hasSaveFile()) {
            setTitle(isFullPathInTitle() ? getSaveFile().getAbsolutePath() : getSaveFile().getName());
        }
    }

    /**
     * Duplicates the JPEditWindow making all settings in the new window
     * equal to the settings of the originating window.
     * <p>
     * If the originating
     * window has been saved, the duplicated window must also be saved
     * before preceding. Otherwise the window will duplicate without
     * asking for a save
     *
     * @return a new JPEditWindow with all the same settings and text
     */
    @Nullable
    public JPEditWindow duplicate(JPEditWindow intoWindow) {
        intoWindow.getTextArea().setText(textArea.getText());
        if (hasSaveFile()) {
            intoWindow.fire(Action.SAVE_AS_ACTION);
            // if they choose not to resave the file, the window
            // should not be opened and the object should be
            // destroyed
            if (!intoWindow.hasSaveFile()) {
                return null;
            }
        } else {
            intoWindow.setTitle(getTitle() + " duplicate");
            intoWindow.dirtyPropertyInternal().set(dirtyPropertyInternal().get());
        }
        if (!autoSaveEnabled.get()) {
            intoWindow.turnAutoSaveOff();
        }
        intoWindow.setFullPathInTitle(isFullPathInTitle());
        intoWindow.getTextArea().setFont(textArea.getFont());
        return intoWindow;
    }

    /**
     * Fires an action from {@link Action} on the window.
     * This triggers an {@link ActionEvent} that is fired on
     * this Window
     *
     * @param action the action to fire on this window
     */
    public void fire(Action action) {
        final EventTarget target = actionControlMap.get(action);
        Event.fireEvent(target, new ActionEvent(this, target));
    }

    /**
     * Returns true if the Window has a save file meaning the window has been saved at least once.
     * <p>
     * A save file is just the file to which the text has been saved.
     * This allows the "Save" action to save to the existing file and not
     * prompt with a {@link javafx.stage.FileChooser}.
     *
     * @return true if the window has a save file
     */
    public boolean hasSaveFile() {
        return getSaveFile() != null;
    }

    /**
     * Create a new file <b>without checking if the file has been saved</b>
     * <p>
     * This method creates a new file by 1) clearing the {@link TextArea}, 2) setting
     * the save file to null and setting the window as dirty.
     * <p>
     * This method will <b>NOT</b> prompt to save ever!
     */
    public void newFile() {
        textArea.clear();
        ((SimpleObjectProperty<File>) saveFileProperty()).set(null);
        lastSaveLabel.setText(Strings.Content.UILABEL_LAST_SAVE_TIME.text);
        wasCleaned();
    }

    public void wasCleaned() {
        dirtyPropertyInternal().set(false);
    }

    /**
     * This method sets dirty to false and updates the save label to the current time
     * <p>
     * This method turns sets dirty to false meaning no save prompts will be issued
     * until the window is dirtied again and updates the save label
     */
    public void saveUpdated() {
        saveUpdated(Date.from(Instant.now()));
    }

    private void saveUpdated(@NotNull Date when) {
        lastSaveLabel.setText(DateFormat.getDateTimeInstance().format(when));
        wasCleaned();
        if (getSaveFile() != null) {
            if (isFullPathInTitle()) {
                setTitle(getSaveFile().getAbsolutePath());
            } else {
                setTitle(getSaveFile().getName());
            }
        }
    }

    /**
     * Returns true if the full path to the open Save File is displayed in the title bar and false otherwise
     *
     * @return true if the full path to the open Save File is displayed in the title bar and false otherwise
     */
    public boolean isFullPathInTitle() {
        return fullPathInTitleProperty().get();
    }

    public void setFullPathInTitle(boolean fullPathInTitle) {
        final SimpleBooleanProperty p = (SimpleBooleanProperty) fullPathInTitleProperty();
        if (getSaveFile() != null) {
            if (fullPathInTitle) {
                setTitle(getSaveFile().getAbsolutePath());
            } else {
                setTitle(getSaveFile().getName());
            }
        }
        p.set(fullPathInTitle);
    }

    public ReadOnlyBooleanProperty fullPathInTitleProperty() {
        if (fullPathInTitle == null) {
            fullPathInTitle = new SimpleBooleanProperty(true);
        }
        return fullPathInTitle;
    }

    public File getSaveFile() {
        return saveFileProperty().get();
    }

    public void setSaveFile(File f) {
        final SimpleObjectProperty<File> p = (SimpleObjectProperty<File>) saveFileProperty();
        p.set(f);
    }

    public ReadOnlyObjectProperty<File> saveFileProperty() {
        if (saveFile == null) {
            saveFile = new SimpleObjectProperty<>(this, "saveFile", null);
        }
        return saveFile;
    }

    public void clearRecentFiles() {
        openRecentMenu.getItems().clear();
        openRecentMenu.getItems().addAll(ApplicationContext.emptyRecents());
    }

    public AutoSaveWorker getAutoSaveWorker() {
        return autoSaveWorker;
    }

    public boolean isAutoSaveEnabled() {
        return autoSaveEnabled.get();
    }

    public void setAutoSaveEnabled(boolean autoSaveEnabled) {
        this.autoSaveEnabled.getAndSet(autoSaveEnabled);
    }

    public boolean isDirty() {
        return dirtyProperty().get();
    }

    /**
     * Called by the {@link ApplicationContext} when a plugin is removed from the Window
     * Should <b>not</b> be called manually by a plugin or related class.
     *
     * @param plugin the plugin being removed
     */
    public void removePlugin(@NotNull LoadedJPPlugin plugin) {
        PluginToolbarButton button = plugin.getButton().get(this);
        if (button != null) {
            getToolbar().getChildren().remove(button);
        }
        PluginMenuItem item = plugin.getItem().get(this);
        if (item != null) {
            getPluginMenu().getItems().remove(item);
        }
        PluginKeyboardShortcut shortcut = plugin.getShortcut().get(this);
        if (shortcut != null) {
            // todo: this might not work
            getTextArea().removeEventHandler(KeyEvent.KEY_PRESSED, shortcut.asHandler());
        }
    }

    public Menu getPluginMenu() {
        return pluginMenu;
    }

    public TextArea getTextArea() {
        return textArea;
    }

    public HBox getToolbar() {
        return buttonBox;
    }

    /**
     * Called by the {@link ApplicationContext} when a plugin is being loaded.
     * Should <b>not</b> be called by the plugin or related classes
     *
     * @param properties     the PluginProperties being loaded
     * @param loadedJPPlugin the Plugin whose properties are being loaded
     * @see PluginProperties
     */
    public void addPluginProperties(
            @NotNull PluginProperties properties, @NotNull LoadedJPPlugin loadedJPPlugin
    ) {
        PluginMenuItem item = properties.getMenuItem();
        PluginKeyboardShortcut shortcut = properties.getKeyboardShortcut();
        PluginToolbarButton button = properties.getToolbarButton();
        if (item != null) {
            item.setOwner(this);
            getPluginMenu().getItems().add(item);
            loadedJPPlugin.getItem().put(this, item);
        }
        if (shortcut != null) {
            shortcut.setOwner(this);
            addEventHandler(KeyEvent.KEY_PRESSED, shortcut.asHandler());
            loadedJPPlugin.getShortcut().put(this, shortcut);
        }
        if (button != null) {
            button.setOwner(this);
            getToolbar().getChildren().add(button);
            loadedJPPlugin.getButton().put(this, button);
        }
    }
}

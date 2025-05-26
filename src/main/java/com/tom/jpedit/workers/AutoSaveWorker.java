package com.tom.jpedit.workers;

import com.tom.jpedit.ApplicationContext;
import com.tom.jpedit.gui.JPEditWindow;
import com.tom.jpedit.logging.JPLogger;
import org.jetbrains.annotations.NotNull;
import tom.javafx.JavaFXUtilsKt;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AutoSaveWorker {

  private final JPEditWindow owner;
  private ScheduledFuture<?> scheduledFuture;

  public AutoSaveWorker(JPEditWindow owner) {
    this.owner = owner;
  }

  public void start() {
    scheduledFuture = scheduleTask(
        ApplicationContext.getContext().getUserPreferences().getAutosavePeriodMillis(),
        TimeUnit.MILLISECONDS
    );
  }

  @NotNull
  public ScheduledFuture<?> scheduleTask(long delay, TimeUnit unit) {
    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
      try {
        scheduledFuture.get();
        throw new IllegalStateException("Failed to cancel scheduled future during rescheduling");
      } catch (CancellationException | ExecutionException | InterruptedException e) {
        // ignore this is what we want from #get();
      }
    }
    return ApplicationContext.getContext().getExecutor().scheduleAtFixedRate(this::autosaveAction, 0, delay, unit);
  }

  private void autosaveAction() {
    if (owner.isAutoSaveEnabled()) {
      try (PrintWriter writer = new PrintWriter(new FileWriter(owner.getTempSaveFileName()))) {
        // TODO: watch out for NPE, thread starts in constructor
        JPLogger.getAppLog()
                .info("About to autosave " + owner.getTempSaveFileName() + " with " + owner.getTextArea().getText());
        writer.print(owner.getTextArea().getText());
        writer.flush();
      } catch (IOException e) {
        JPLogger.getErrLog().severe("Temporary save file cannot be accessed. Turning autosave off!");
        JPLogger.getErrLog().severe(e.getMessage());
        JPLogger.getErrLog().severe(JavaFXUtilsKt.stackTraceToString(e.getStackTrace()));
        owner.setAutoSaveEnabled(false);
        this.stop();
      }
    }
  }

  protected void stop() {
    JPLogger.getAppLog().info("Performing abnormal stop. Scheduled future will not be awaited");
    scheduledFuture.cancel(true);
    JPLogger.getAppLog().info("Future cancellation initiated");
  }

  public void terminate() throws ExecutionException, InterruptedException {
    JPLogger.getAppLog().info("Terminating scheduled task for " + owner.getTitle());
    scheduledFuture.cancel(true);
    try {
      JPLogger.getAppLog().info("Awaiting cancellation of future");
      scheduledFuture.get();
    } catch (CancellationException e) {
      //ignore--we expect this
    }
    JPLogger.getAppLog().info("Future cancelled");
  }
}


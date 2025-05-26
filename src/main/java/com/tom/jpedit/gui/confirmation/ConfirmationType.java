package com.tom.jpedit.gui.confirmation;

public enum ConfirmationType {

  /**
   * Indicates the user clicked the "Yes" button
   * <p>
   * Indicates the users wishes to perform the prompted action and
   * to proceed with the subsequent actions
   */
  YES,

  /**
   * Indicates the user clicked the "No" button
   * <p>
   * Indicates the user wishes to avoid the prompted action but proceed with
   * the subsequent actions anyway
   */
  NO,

  /**
   * Indicates the user clicked the "Cancel" button
   * <p>
   * Indicates the user wishes to avoid the prompted action and NOT proceed
   * with the subsequent actions
   */
  CANCEL
}

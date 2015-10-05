package com.indusborn.common;

public class ErrorObject {
   private MessageObject _message;
   private Severity _severity = Severity.ERROR; // default is error

   public static enum Severity {
      INFO, WARN, ERROR
   }

   public ErrorObject(MessageObject message) {
      _message = message;
   }

   public ErrorObject(String messageKey) {
      _message = new MessageObject(messageKey, null);
   }

   public Severity getSeverity() {
      return _severity;
   }

   public void setSeverity(Severity _severity) {
      this._severity = _severity;
   }

   public MessageObject getMessage() {
      return _message;
   }

   public void setMessage(MessageObject message) {
      _message = message;
   }

   @Override
   public String toString() {
      if (_message != null) {
         return _message.toString();
      }
      return "";
   }
}

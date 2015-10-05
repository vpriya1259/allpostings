package com.indusborn.common;

public class AppServerException extends Exception {
   private MessageObject _message;

   public AppServerException() {
      super();
   }

   public AppServerException(Throwable cause) {
      super(cause);
      _message = createMessageObject();
   }

   public AppServerException(MessageObject message) {
      super((message != null) ? message.getMessageKey() : "");
      _message = message;
   }

   public AppServerException(String messageKey) {
      super("");
      _message = createMessageObject(messageKey);
   }

   public AppServerException(MessageObject message, Throwable cause) {
      super((message != null) ? message.getMessageKey() : "", cause);
      _message = message;
   }

   public AppServerException(String messageKey, Throwable cause) {
      super(cause);
      _message = createMessageObject(messageKey);
   }


   public MessageObject getMessageObject() {
      return _message;
   }

   public ErrorObject getErrorObject() {
      return new ErrorObject(_message);
   }

   @Override
   public String getMessage() {
      if (_message != null) {
         return _message.toString();
      }

      return "";
   }

   private MessageObject createMessageObject() {
      MessageObject mo = new MessageObject(MessageKeys.APP_SERVER_ERROR, null);
      return mo;
   }

   private MessageObject createMessageObject(String messageKey) {
      MessageObject mo = new MessageObject(messageKey, null);
      return mo;
   }
}

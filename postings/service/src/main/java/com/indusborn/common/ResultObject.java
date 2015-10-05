
package com.indusborn.common;

import java.util.ArrayList;
import java.util.List;

public class ResultObject
{
   private List<ErrorObject> _errors;
   private Object           _data;

   public ResultObject() {}

   public ResultObject(Object data, ErrorObject error) {
      addError(error);
      _data = data;
    }

   public ResultObject(ErrorObject error) {
      addError(error);
   }

   public ResultObject(Object data) {
      _data = data;
   }

   /**
    * A convenient function.  It will return the first error.  Useful for
    * cases where it has only one error.
    *
    * @return The first ErrorObject from the error list, if not null.
    */
   public ErrorObject getError() {
      return (_errors != null && !_errors.isEmpty()) ? _errors.get(0) : null;
   }
   
   public void addError(ErrorObject error) {
      if (error != null) {
         if (_errors == null) {
            _errors = new ArrayList<ErrorObject>();
         }

         this._errors.add(error);
      }
   }
   
   public boolean hasError() {
      boolean ret = false;
      if (_errors != null && !_errors.isEmpty()) {
         for (ErrorObject err : _errors) {
            if (err.getSeverity() == ErrorObject.Severity.ERROR) {
               ret = true;
               break;
            }
         }
      }
      return ret;
   }

   // Javabean getters/setters
   
   public List<ErrorObject> getErrors() {
      return _errors;
   }

   public void setErrors(List<ErrorObject> errors) {
      if (_errors == null) {
         _errors = new ArrayList<ErrorObject>(2);
      }

      _errors.addAll(errors);
   }

   public Object getData() {
      return _data;
   }
   public void setData(Object _data) {
      this._data = _data;
   }
   
   public boolean
   containsError(String messageKey) {
      boolean ret = false;
      if (_errors != null && !_errors.isEmpty()) {
         for (ErrorObject err : _errors) {
            if (err.getMessage().getMessageKey().equals(messageKey)) {
               ret = true;
               break;
            }
         }
      }
      return ret;
   }
}

package com.indusborn.ui.mobile;

import javax.servlet.http.HttpServletRequest;

import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceResolver;

public class MDeviceResolver implements DeviceResolver {

   private MDevice mDevice;
   
   public MDevice getmDevice() {
      return mDevice;
   }


   public void setmDevice(MDevice mDevice) {
      this.mDevice = mDevice;
   }


   public Device resolveDevice(HttpServletRequest request) {
      // TODO Auto-generated method stub
      return mDevice;
   }

}

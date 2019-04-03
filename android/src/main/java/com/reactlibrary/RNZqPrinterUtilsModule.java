
package com.reactlibrary;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;

import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.discovery.BluetoothDiscoverer;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;

import java.util.ArrayList;

public class RNZqPrinterUtilsModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNZqPrinterUtilsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNZqPrinterUtils";
  }

  @ReactMethod
  public void listNearbyDevicesAddress(Promise promise) {
    final ArrayList<String> devicesAddress = new ArrayList<String>();
    final Promise promise = promise;

    try {
      BluetoothDiscoverer.findPrinters(this.reactContext, new DiscoveryHandler() {
        @Override
        public void foundPrinter(DiscoveredPrinter discoveredPrinter) {
          devicesAddress.add(discoveredPrinter.address);
          Log.i(getName(), "Discovered a printer");
        }

        @Override
        public void discoveryFinished() {
          Log.i(getName(), "Discovery finished");
          promise.resolve(devicesAddress);
        }

        @Override
        public void discoveryError(String s) {
          Log.i(getName(), "Discovery error");
        }
      });
    } catch (ConnectionException e) {
      Log.i(getName(), "Printer connection error");
      promise.reject(getName(), e);
    }
  }
}
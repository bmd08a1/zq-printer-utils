
package com.reactlibrary;

import android.util.Log;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.ParcelUuid

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.Arguments;

import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.discovery.BluetoothDiscoverer;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLinkOs;
import com.zebra.sdk.printer.PrinterStatus;

public class RNZqPrinterUtilsModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  private BluetoothAdapter bluetoothAdapter;
  private BluetoothManager bluetoothManager;
  private Context context;

  public RNZqPrinterUtilsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  private BluetoothAdapter getBluetoothAdapter() {
    if (bluetoothAdapter == null) {
      BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
      bluetoothAdapter = manager.getAdapter();
    }
    return bluetoothAdapter;
  }

  private BluetoothManager getBluetoothManager() {
    if (bluetoothManager == null) {
      bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }
    return bluetoothManager;
  }

  @Override
  public String getName() {
    return "RNZqPrinterUtils";
  }

  @ReactMethod
  public void listNearbyDevicesAddress(final Promise promise) {
    final WritableArray devicesAddress = Arguments.createArray();

    try {
      BluetoothDiscoverer.findPrinters(this.reactContext, new DiscoveryHandler() {
        @Override
        public void foundPrinter(DiscoveredPrinter discoveredPrinter) {
          Log.i(getName(), "Discovered a printer");
          devicesAddress.pushString(discoveredPrinter.address);
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

  @ReactMethod
  public void printWithCommands(String printerAddress, String commands, Promise promise) {
    Connection connection = new BluetoothConnection(printerAddress);

    try {
      connection.open();
      Log.i(getName(), "connect successful");

      ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
      ZebraPrinterLinkOs linkOsPrinter = ZebraPrinterFactory.createLinkOsPrinter(printer);
      Log.i(getName(), "init printer successful");

      PrinterStatus printerStatus = (linkOsPrinter != null) ? linkOsPrinter.getCurrentStatus() : printer.getCurrentStatus();
      Log.i(getName(), "get printerStatus successful");

      if (printerStatus.isReadyToPrint) {
        Log.i(getName(), "printer is ready to print");
        printer.sendCommand(commands);
        Log.i(getName(), "print successful");
        promise.resolve("success");
      }
    } catch (ConnectionException e) {
      Log.i(getName(), "cannot connect");
      promise.reject(e.getMessage());
    } catch (ZebraPrinterLanguageUnknownException e) {
      Log.i(getName(), "unknow printer languague");
      promise.reject(e.getMessage());
    }
  }

  @ReactMethod
  public void getBondedPeripherals(Promise promise) {
    Log.d(LOG_TAG, "Get bonded peripherals");
    Set<BluetoothDevice> deviceSet = getBluetoothAdapter().getBondedDevices();
    for (BluetoothDevice device : deviceSet) {
      ParcelUuid[] deviceUuids = device.getUuids();
      for (ParcelUuid uuid : deviceUuids) {
        if (uuid.toString() == "0001101-0000-1000-8000-00805F9B34F") {
          promise.resolve(device.getAddress());
        }
      }
    }
  }
}
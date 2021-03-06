
package com.reactlibrary;

import android.util.Log;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.ParcelUuid;

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

import java.util.Set;

public class RNZqPrinterUtilsModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  private BluetoothAdapter bluetoothAdapter;
  private BluetoothManager bluetoothManager;
  private Context context;

  public RNZqPrinterUtilsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    context = reactContext;
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
  public void getBondedPrinter(Promise promise) {
    Log.d(getName(), "Get bonded printer");
    Set<BluetoothDevice> deviceSet = getBluetoothAdapter().getBondedDevices();
    for (BluetoothDevice device : deviceSet) {
      Log.d(getName(), device.getAddress());
      ParcelUuid[] deviceUuids = device.getUuids();
      for (ParcelUuid uuid : deviceUuids) {
        Log.d(getName(), uuid.toString());
        if (uuid.toString().equals("00001101-0000-1000-8000-00805f9b34fb")) {
          promise.resolve(device.getAddress());
          return;
        }
      }
    }
    promise.resolve("");
  }

  @ReactMethod
  public void isBluetoothEnabled(Promise promise) {
    Log.d(getName(), "check if Bluetooth is enabled");
    if (getBluetoothAdapter() == null) {
      Log.d(getName(), "Bluetooth is not supported");
      promise.resolve(false);
    }
    promise.resolve(getBluetoothAdapter().isEnabled());
  }
}

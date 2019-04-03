
# react-native-zq-printer-utils

## Getting started

`$ npm install git+https://github.com/bmd08a1/zq-printer-utils.git`

### Mostly automatic installation

`$ react-native link react-native-zq-printer-utils`

### Manual installation

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNZqPrinterPackage;` to the imports at the top of the file
  - Add `new RNZqPrinterPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-zq-printer'
  	project(':react-native-zq-printer').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-zq-printer/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-zq-printer')
  	```

## Usage
```javascript
import RNZqPrinterUtils from 'react-native-zq-printer-utils';

// TODO: What to do with the module?
RNZqPrinterUtils;
```

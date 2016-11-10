## multi-image-selector
multi-image-selector android for react-native

# 1.npm Install (required 必须)
	npm i react-native-multi-image-selector --save 
> 自动安装和手动安装必须要选择一种
# Android:
## 2.自动安装 Automatic Installation
	react-native link 
## 2.手动安装
 
	// 编辑  edit: android/settings.gradle
	include ':react-native-multi-image-selector'
	project(':react-native-multi-image-selector').projectDir = new File(settingsDir, '../node_modules/react-native-multi-image-selector/android')

---
	// 编辑 edit : android/app/build.gradle
	...
	
	dependencies {
	    ...
	    compile project(':react-native-multi-image-selector')
	}
---
	// 编辑 edit : android/app/src/main/java/com/<...>/MainApplication.java
	...
	
	import me.nereo.multi_image_selector.MultiImagePackage; // <-- add this import
	
	public class MainApplication extends Application implements ReactApplication {
	    @Override
	    protected List<ReactPackage> getPackages() {
	        return Arrays.<ReactPackage>asList(
	            new MainReactPackage(),
	            new MultiImagePackage()// <-- add this line 添加这行
	        );
	    }
	...
	}	
## Usage:
	
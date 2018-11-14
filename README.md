# RongCloud [![](https://jitpack.io/v/mliyuanbiao/RongCloud.svg)](https://jitpack.io/#mliyuanbiao/RongCloud)

可以使用gradle的方式集成融云即时云通讯SDK

# 集成方式

## Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

## Step 2. Add the dependency [![](https://jitpack.io/v/mliyuanbiao/RongCloud.svg)](https://jitpack.io/#mliyuanbiao/RongCloud) 
使用时请把版本号替换成最新


```groovy
dependencies {
	implementation 'com.github.mliyuanbiao.RongCloud:CallKit:2.9.7_rc2'
	implementation 'com.github.mliyuanbiao.RongCloud:CallLib:2.9.7_rc2'
	implementation 'com.github.mliyuanbiao.RongCloud:IMKit:2.9.7_rc2'
	implementation 'com.github.mliyuanbiao.RongCloud:IMLib:2.9.7_rc2'
	//需要定位
	implementation 'com.github.mliyuanbiao.RongCloud:LocationLib:2.9.7_rc2'
	implementation 'com.github.mliyuanbiao.RongCloud:LocationLib:2.9.7_rc2'
	implementation 'com.github.mliyuanbiao.RongCloud:PushLib:2.9.7_rc2'
	implementation 'com.github.mliyuanbiao.RongCloud:RCSticker:2.9.7_rc2'
	implementation 'com.github.mliyuanbiao.RongCloud:RedPacket:2.9.7_rc2'
	implementation 'com.github.mliyuanbiao.RongCloud:Sight:2.9.7_rc2'
	
	//工具集
	implementation 'com.github.mliyuanbiao.RongCloud:IMUtil:2.9.7_rc2'
	implementation 'com.github.mliyuanbiao.RongCloud:RxIMLib:2.9.7_rc2'
}
```


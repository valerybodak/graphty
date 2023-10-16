# Graphty
Android Library to display graphs

![graphty_bg](https://github.com/valerybodak/graphty/assets/26433088/0cba9c28-8f4b-4a66-beaf-dec1044d93da)


## Installation
### Step 1. Add the JitPack repository to your build file 
Add it in your root build.gradle at the end of repositories:
```Kotlin
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2. Add the dependency
Add the following dependency in your app build.gradle:
```Kotlin
implementation 'com.github.valerybodak:Graphty:1.0.0'
```

## Usage

### 1. WeekLineGraph

<img src="https://github.com/valerybodak/graphty/assets/26433088/fb4f93aa-3322-432e-a4c1-83d2ecb918ad" width="30%"></img> <img src="https://user-images.githubusercontent.com/26433088/201077885-0114a3e2-e45f-4127-815f-3bb079c4482c.jpg" width="30%"></img> <img src="https://user-images.githubusercontent.com/26433088/201077887-377125e1-06d8-452d-8009-8e8d5a401251.jpg" width="30%"></img> <img src="https://user-images.githubusercontent.com/26433088/201077889-a6af344a-2ef7-4779-a220-f5efdb88da80.jpg" width="30%"></img> <img src="https://user-images.githubusercontent.com/26433088/201077890-6cea6868-70a7-4329-819e-c89b04fcb9c4.jpg" width="30%"></img> <img src="https://user-images.githubusercontent.com/26433088/201077891-e61b0906-38cc-4f5d-adb5-4a7f1de80b5f.jpg" width="30%"></img> <img src="https://user-images.githubusercontent.com/26433088/211999513-a15b2c97-4048-43aa-b265-74a74af67472.png" width="30%"></img> <img src="https://user-images.githubusercontent.com/26433088/211999581-3e4453f9-1a2b-4008-93b2-0eb39e805096.png" width="30%"></img>

```Kotlin
<com.vbodak.graphtylib.graph.week.WeekLineGraph
        android:id="@+id/weekLineGraph"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```
Property | Type | Description 
--- | --- | --- 
minValue | Int | Min value to display on scale. In case is not specified the min value from values list will be applied
maxValue | Int | Max value to display on scale. In case is not specified the max value from values list will be applied
enableGuidelines | Boolean | Enable / Disable vertical guidelines
lineWidth | Float | The width of graph's line
lineColor | Int | The color (@ColorRes) of graph's line
guidelineWidth | Float | The width of guideline
guidelineColor | Int | The color (@ColorRes) of guideline
nodesMode | NodesMode | 1. NodesMode.NONE - to disable nodes. 2. NodesMode.ALL - to display the node for each value, 3. NodesMode.MAX - to display the node only for the max value
nodeRadiusPx | Float | The node's radius
nodeFillColor | Int | The color (@ColorRes) of node
valueScaleWidthPx | Float | The width of the left side panel of values
valueTextSize | Float | The text size of values on the left side panel
valueTextColor | Int | The color (@ColorRes) of values on the left side panel
weekdayStart | Int | The first day of the week. Use the Calendar's contsants: Calendar.MONDAY, Calendar.SUNDAY etc.
weekdayTextColor | Int | The color (@ColorRes) of the weekday title
weekdayNameMap | Map<Int, String> | The mapping to display the weekday titles. The key is Calendar's contsant (Calendar.MONDAY, Calendar.TUESDAY etc.). The value is the weekday's text representation, for example, "M", "T", "W" etc.
weekdayScaleHeightPx | Float | The height of the bottom panel of weekdays
weekdayTextSize | Float | The text size of weekdays on the bottom panel    

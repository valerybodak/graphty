# Graphty
Android Library to display graphs

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

<img src="[https://user-images.githubusercontent.com/26433088/201077882-bccf4433-b934-4f79-8f11-697916c54e2f.jpg](https://github.com/valerybodak/graphty/assets/26433088/3f542867-da11-47b8-84d6-aef28068ddf4)" width="30%"></img>

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

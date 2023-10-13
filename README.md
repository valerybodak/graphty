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
guidelineWidth | Float | The width of guideline
valueScaleWidthPx | Float | The width of the left side panel of values
valueTextSize | Float | The text size of values on the left side panel


    val weekdayStart: Int = Calendar.MONDAY,
    val weekdayNameMap: Map<Int, String> = emptyMap(),
    val weekdayScaleHeightPx: Float = 70F,
    val weekdayTextSize: Float = 36F,
    val nodesMode: NodesMode = NodesMode.ALL,
    val nodeRadiusPx: Float = 14F,
    @ColorRes
    val lineColor: Int = android.R.color.black,
    @ColorRes
    val guideColor: Int = android.R.color.darker_gray,
    @ColorRes
    val nodeFillColor: Int = android.R.color.white,
    @ColorRes
    val weekdayTextColor: Int = android.R.color.black,
    @ColorRes
    val valueTextColor: Int = android.R.color.black,

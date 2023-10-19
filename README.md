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

<img src="https://github.com/valerybodak/graphty/assets/26433088/1b4f0d2b-f4d8-4367-b36a-f910f941d96f" width="30%"></img> <img src="https://github.com/valerybodak/graphty/assets/26433088/f94abd1a-09da-4d3d-90b7-f56c759c1dd2" width="30%"></img> <img src="https://github.com/valerybodak/graphty/assets/26433088/54cad739-db10-4300-85f4-647f2d2e1430" width="30%"></img>

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

### 2. BarGraph

<img src="https://github.com/valerybodak/graphty/assets/26433088/76c4385b-2f11-460a-87e3-1a61694a85fe" width="30%"></img> <img src="https://github.com/valerybodak/graphty/assets/26433088/4a20d02f-d48f-4bbb-809c-053d7a26b64b" width="30%"></img>
<img src="https://github.com/valerybodak/graphty/assets/26433088/6ea85add-61cb-46a8-85d7-e3107d8cba90" width="30%"></img>

#### 2.1 Put BarGraph to your xml layout

```Kotlin
<com.vbodak.graphtylib.graph.bar.BarGraph
    android:id="@+id/barGraph"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

#### 2.2 Setup BarGraph and display it

```Kotlin
val params = BarGraphParams()
    params.minValue = 5
    params.maxValue = 100
    params.valueScaleWidthPx = 82F
    params.titleTextSize = 30F
    params.barColors = listOf(R.color.cyan, R.color.pink, R.color.yellow_2)
    params.barCornerRadiusPx = 6F

binding.barGraph.setup(
    params = params
)

...

binding.barGraph.draw(
    bars = listOf(
        Bar(title = "12/10", values = listOf(80, 10, 98)),
        Bar(title = "13/10", values = listOf(65, 87, 76)),
        Bar(title = "14/10", values = listOf(69, 32, 15)),
        Bar(title = "15/10", values = listOf(46, 15, 23)),
        Bar(title = "16/10", values = listOf(96, 87, 78)),
        Bar(title = "17/10", values = listOf(78, 76, 54)),
        Bar(title = "18/10", values = listOf(70, 60, 43))
    )
)
```

#### 2.3 Params
Property | Type | Description 
--- | --- | --- 
minValue | Int | Min value to display on scale. In case is not specified the min value from values list will be applied
maxValue | Int | Max value to display on scale. In case is not specified the max value from values list will be applied
valueScaleWidthPx | Float | The width of the left side panel of values
valueTextSize | Float | The text size of values on the left side panel
valueTextColor | Int | The color (@ColorRes) of values on the left side panel
titleScaleHeightPx | Float | The height of the bottom panel of titles
titleTextSize | Float | The text size of titles on the bottom panel
titleTextColor | Int | The color (@ColorRes) of titles on the bottom panel




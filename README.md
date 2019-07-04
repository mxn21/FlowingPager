<p align="center"><img width="150"src="http://baobaoloveyou.com/flowingpager_icon.png"></p>
<h1 align="center">FlowingPager</h1>


### showcase
<img width="500"src="http://baobaoloveyou.com/flowingpager.gif"/>

### Summary
A Flexible Side Sliding View Controlled by a Button

### Download
Include the following dependency in your build.gradle file.

Gradle:

```Gradle
    repositories {
        jcenter()
    }

    dependencies {
        implementation 'com.mxn.soul:flowingpager_core:0.1.0'
    }
```

## Sample Usage  

*For a working implementation of this project see the `app/` folder and check out the sample app*


```
   <com.mxn.soul.flowingpager_core.FlowingPager xmlns:android="http://schemas.android.com/apk/res/android"
       xmlns:app="http://schemas.android.com/apk/res-auto"
       android:id="@+id/pagerlayout"
       android:layout_width="match_parent"
       android:layout_height="match_parent"
       android:clipChildren="false"
       android:clipToPadding="false"
       app:edContentBackground="#FEFEFE"
       app:edCrackWidth="5dp"
       app:edIconSize="50dp"
       app:edMarginBottom="150dp"
       app:edMenuBackground="#000000"
       app:edPosition="1"
       app:edMaxAnimationDuration="300"
       app:edSlideRange="0.3">
   
       <!--content-->
       <com.mxn.soul.flowingpager_core.FlowingContentLayout
           android:layout_width="match_parent"
           android:layout_height="match_parent">
   
           <android.support.design.widget.CoordinatorLayout
               android:id="@+id/content"
               android:layout_width="match_parent"
               android:layout_height="match_parent"
               android:orientation="vertical">
           </android.support.design.widget.CoordinatorLayout>
   
       </com.mxn.soul.flowingpager_core.FlowingContentLayout>
   
       <!--menu-->
       <com.mxn.soul.flowingpager_core.FlowingMenuLayout
           android:layout_width="match_parent"
           android:layout_height="match_parent">
   
           <FrameLayout
               android:id="@+id/id_container_menu"
               android:layout_width="match_parent"
               android:layout_height="match_parent" />
   
       </com.mxn.soul.flowingpager_core.FlowingMenuLayout>
   
       <!--button -->
       <com.mxn.soul.flowingpager.PlayPauseView
           android:id="@+id/flowingbutton"
           android:layout_width="50dp"
           android:layout_height="50dp"
           android:padding="5dp"
           app:anim_direction="positive"
           app:space_padding="8dp"
           app:anim_duration="300"
           app:bg_color="#692FFE"
           app:btn_color="#ffffff"
           />
   </com.mxn.soul.flowingpager_core.FlowingPager>

```


### Attributes
Property | Type | Description
--- | --- | ---
edContentBackground | color | Background color of home page,you need to set it up here.Set transparent colors elsewhere
edMenuBackground | color | Background color of the side page,you need to set it up here.Set transparent colors elsewhere
edCrackWidth | dimension | The width of the gap on both sides
edIconSize | dimension | icon size ,you need to set it in the root view(FlowingPager)
edMaxAnimationDuration | integer | animation duration, The default value is 300
edPosition | integer | If the value is 1, the side page is on the left, and if the value is 2, the side page is on the right.
edMarginTop | dimension | The button margin bottom, edMarginTop and edMarginBottom just need to set up one
edMarginBottom | dimension | The button margin bottom,edMarginBottom and edMarginTop just need to set up one
edSlideRange | float | The proportion of sliding range to screen width.The default value is 0.3,It is not recommended to set more than 0.5.


## Licence
```
Copyright 2019 soul.mxn

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```






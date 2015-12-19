# WTDrawingAndroid
A simple way to draw smooth line on Android.
## Screenshot
![Screenshot](Screenshot/demo.jpg)
## Features
* Change stroke color
* Change stroke width
* Undo
* Clear all drawings
* Eraser drawing
* Get final bitmap

## Usage
In Android Studio, just import module `wtdrawing`. In other IDE, you should copy `WTBezierPath.java` and `WTDrawingView.java` into your project.
### Init in layout
~~~xml
<com.water.wtdrawing.WTDrawingView
       android:id="@+id/wtDrawingView"
       android:layout_width="match_parent"
       android:layout_height="match_parent"/>
~~~
**Note:** `layout_width` and `layout_height` can not be `wrap_content`.
### Init with Java code
~~~java
int width = width you want;
int height = height you want;
WTDrawingView wtDrawingView = new WTDrawingView(context,width,height);
~~~
Then add WTDrawingView instance to your view group.
### Undo
~~~java
wtDrawingView.undo();
~~~
### Clear all drawings
~~~java
wtDrawingView.clear();
~~~
### Draw eraser
~~~java
wtDrawingView.setEraserMode(true);
~~~
### Change stroke width
~~~java
wtDrawingView.setStrokeWidth(5.0f); // 5.0f is a dp value.
~~~
Default stroke width is 2.0(dp).
### Change stroke color
~~~java
wtDrawingView.setStrokeColor(Color.RED);
~~~
Default stroke color is black.
### Change eraser stroke width
~~~java
wtDrawingView.setEraserWidth(20.0f); // 20.0f is a dp value.
~~~
Default eraser width is 20(dp).
### Get bitmap
~~~java
int backgroundColor = Color.WHITE;
Bitmap bitmap = wtDrawingView.getBitmap(backgroundColor);
~~~

### Changelog
**1.0.1**

* add getBitmap method to retrieve final bitmap
* Use `dirty rect` to improve drawing performance
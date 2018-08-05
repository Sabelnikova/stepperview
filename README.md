## Installation and usage
1. Add to Gradle file:
  ```
	dependencies {
		implementation 'com.sabelnikova.libraries:stepperview:1.0.1'
	}
  ```
2. Add to layout
  ```xml
  <FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
      xmlns:app="http://schemas.android.com/apk/res-auto"
      xmlns:tools="http://schemas.android.com/tools"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      tools:context=".MainActivity">

      <com.sabelnikova.stepperview.StepperView
          android:id="@+id/stepper"
          android:layout_width="match_parent"
          android:layout_height="match_parent"
          app:defaultStepButtonText="@string/next"/>

  </FrameLayout>
  ```
3. In activity or fragment initialize stepper:
```kotlin
  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stepper.apply {
            addStep(TestFragment.newInstance("Hello"), "Step 1")
            addStep(TestFragment.newInstance("World!"), "Step 2", "Finish")

            beforeStepOpening = { _, newStep ->
                Toast.makeText(this@MainActivity, "going to step " + (newStep + 1), Toast.LENGTH_SHORT).show()
                true
            }

            onLastStepButtonClick = {
                Toast.makeText(this@MainActivity, "all steps completed", Toast.LENGTH_SHORT).show()
            }
        }

    }
```

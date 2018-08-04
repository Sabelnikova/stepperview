package com.sabelnikova.stepper

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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
}

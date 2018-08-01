package com.sabelnikova.stepper

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stepper.addStep(TestFragment.newInstance(), "Step 1")
        stepper.addStep(TestFragment.newInstance(), "Step 2", "Finish")
    }
}

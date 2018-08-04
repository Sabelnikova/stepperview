package com.example.stepperview

import android.content.Context
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class StepperView(context: Context, attrs: AttributeSet? = null) : NestedScrollView(context, attrs) {

    var activity: AppCompatActivity? = context as AppCompatActivity?

    /**
     * invokes on step header or step button click
     * currentStepPosition is the position of currently opened step
     * openingStepPosition is the position of step intended to be opened
     * Step opens if invocation returns true
     */
    var beforeStepOpening: ((currentStepPosition: Int, openingStepPosition: Int) -> Boolean)? = null

    /**
     * invokes on last step button click
     */
    var onLastStepButtonClick: (() -> Unit)? = null

    var currentStepPosition = 0
    private lateinit var container: LinearLayout
    private val steps = mutableListOf<Step>()
    private var currentStepView: StepView? = null
    private var defaultButtonText: String = context.getString(R.string.next)

    init {
        attrs?.let {
            val typedArray = context.theme?.obtainStyledAttributes(attrs, R.styleable.StepperView, 0, 0)
            try {
                typedArray?.getString(R.styleable.StepperView_defaultStepButtonText)?.let { defaultButtonText = it }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                typedArray?.recycle()
            }
        }
        context.let { createView(it) }
    }

    private fun createView(context: Context) {
        val v = LayoutInflater.from(context).inflate(R.layout.view_stepper_navigation, this, true)
        container = v.findViewById(R.id.container)
    }

    /**
     * @return count of steps
     */
    fun getStepCount() = steps.size

    /**
     * Disables steps at needed position
     */
    fun disableStep(position: Int) = getStepView(position)?.disable()

    /**
     * Enables steps at needed position
     */
    fun enableStep(position: Int) = getStepView(position)?.enable()

    /**
     * Disables steps from needed position
     */
    fun disableStepsFrom(position: Int) {
        for (i in position until getStepCount()) {
            disableStep(i)
        }
    }

    /**
     * Enables steps to needed position
     */
    fun enableStepsTo(position: Int) {
        for (i in 0..position) {
            enableStep(i)
        }
    }

    /**
     * Enables steps to needed position and disables other steps
     */
    fun setEnabledSteps(toPosition: Int) {
        enableStepsTo(toPosition)
        disableStepsFrom(toPosition + 1)
    }


    /**
     * Opens step at needed position
     */
    fun goToStep(position: Int) {
        getStepView(position)?.openStep()
    }


    /**
     * Adds a step to stepper
     * @param fragment is the step contenr
     * @param title is the title of a step
     * @param nextButtonText is the text of step button, if null the text will be the value of defaultButtonText attribute
     */
    fun addStep(fragment: Fragment, title: String, nextButtonText: String? = null) {
        val step = Step(fragment, title, nextButtonText)
        steps.add(step)

        val stepView = StepView(context, step)
        container.addView(stepView)
    }

    /**
     * sets button text of needed step
     */
    fun setStepButtonText(stepNum: Int, @StringRes id: Int) {
        getStepView(stepNum)?.setStepButtonText(id)
    }

    /**
     * @return fragment that is the content of needed step
     */
    fun getFragment(position: Int): Fragment? {
        return getStepView(position)?.getFragment()
    }

    private fun getStepView(position: Int): StepView? {
        return container.getChildAt(position) as StepView?
    }

    private inner class StepView(context: Context?, private val step: Step) : LinearLayout(context) {

        private lateinit var titleTv: TextView
        private lateinit var numberTv: TextView
        private lateinit var container: ViewGroup
        private lateinit var stepLayout: ViewGroup
        private lateinit var headerLayout: ViewGroup
        private lateinit var nextBtn: Button
        private lateinit var checkedIv: View

        private var expanded = true
        private var stepEnabled = true

        init {
            context?.let { createView(context) }
        }

        private fun createView(context: Context) {
            val stepView = LayoutInflater.from(context).inflate(R.layout.view_step, this, true)
            titleTv = stepView.findViewById(R.id.stepNameTv)
            numberTv = stepView.findViewById(R.id.stepNumberTv)
            container = stepView.findViewById(R.id.stepContainer)
            stepLayout = stepView.findViewById(R.id.stepLayout)
            headerLayout = stepView.findViewById(R.id.headerLayout)
            nextBtn = stepView.findViewById(R.id.nextBtn)
            checkedIv = stepView.findViewById(R.id.checkedIv)

            container.id = steps.indexOf(step) + 1

            nextBtn.text = step.stepButtonText ?: defaultButtonText

            addFragment(step.fragment, step.stepTitle, container.id)
            titleTv.text = step.stepTitle
            numberTv.text = (steps.indexOf(step) + 1).toString()
            if (steps.indexOf(step) != currentStepPosition) {
                stepLayout.visibility = View.GONE
                expanded = false
                disable()
            } else {
                currentStepView = this
                enable()
            }

            headerLayout.setOnClickListener {
                if (getStepView(currentStepPosition) != this && stepEnabled) {
                    if (beforeStepOpening?.invoke(currentStepPosition, steps.indexOf(step)) != false) {
                        if (!expanded && stepEnabled) {
                            openStep()
                        }
                    }
                }
            }

            if (steps.indexOf(step) == 2) disable()

            nextBtn.setOnClickListener {
                if (steps.last() == step){
                    onLastStepButtonClick?.invoke()
                } else {
                    if (beforeStepOpening?.invoke(steps.indexOf(step), steps.indexOf(step) + 1) != false) {
                        val step = getStepView(steps.indexOf(step) + 1)
                        step?.openStep()
                        step?.enable()
                    }
                }
            }
        }

        fun getFragment() = activity?.supportFragmentManager?.findFragmentById(container.id)

        fun openStep() {
            expand()
            currentStepView?.collapse()
            currentStepPosition = steps.indexOf(step)
            currentStepView = this
        }

        private fun expand() {
            checkedIv.visibility = View.GONE
            Animations.expand(stepLayout)
            expanded = true
        }

        private fun collapse() {
            checkedIv.visibility = if (stepEnabled) View.VISIBLE else View.GONE
            Animations.collapse(stepLayout)
            expanded = false
        }

        fun disable() {
            headerLayout.alpha = 0.5f
            stepEnabled = false
        }


        fun enable() {
            headerLayout.alpha = 1f
            stepEnabled = true
            val currentStep = getStepView(currentStepPosition)
            if (currentStep == this || currentStep == null) {
                checkedIv.visibility = View.GONE
            } else {
                checkedIv.visibility = View.VISIBLE
            }
        }

        fun setStepButtonText(@StringRes id: Int) {
            nextBtn.setText(id)
        }


        private fun addFragment(fragment: Fragment, tag: String?, containerId: Int) {
            activity?.let {
                val fragmentTransaction = it.supportFragmentManager.beginTransaction()
                fragmentTransaction.add(containerId, fragment, tag)
                fragmentTransaction.commit()
            }

        }
    }

    private class Step(var fragment: Fragment,
                       var stepTitle: String,
                       var stepButtonText: String?)
}

object Animations {
    fun expand(v: View) {
        if (v.visibility != View.VISIBLE) {

            v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            val targetHeight = v.measuredHeight

            setHeight(v, 1)
            v.visibility = View.VISIBLE
            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    val newHeight = if (interpolatedTime == 1f)
                        WindowManager.LayoutParams.WRAP_CONTENT
                    else
                        (targetHeight * interpolatedTime).toInt()
                    setHeight(v, newHeight)
                    v.requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

            a.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    v.visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            a.duration = (targetHeight / v.context.resources.displayMetrics.density).toLong()
            v.startAnimation(a)
        }
    }

    fun collapse(v: View) {
        if (v.visibility == View.VISIBLE) {

            val initialHeight = v.measuredHeight

            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    if (interpolatedTime != 1f) {
                        val newHeight = initialHeight - (initialHeight * interpolatedTime).toInt()
                        setHeight(v, newHeight)
                        v.requestLayout()
                    }
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }

            }
            a.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    v.visibility = View.GONE
                }

                override fun onAnimationStart(animation: Animation?) {}

            })

            a.duration = (initialHeight / v.context.resources.displayMetrics.density).toLong()
            v.startAnimation(a)
        }
    }

    private fun setHeight(v: View, newHeight: Int) {
        v.layoutParams = LinearLayout.LayoutParams(v.layoutParams.width, newHeight)
    }
}
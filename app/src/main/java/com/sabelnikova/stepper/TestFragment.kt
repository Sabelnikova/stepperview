package com.sabelnikova.stepper

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_test.*

class TestFragment : Fragment() {

    companion object {
        private const val TEXT_ARG = "text"

        fun newInstance(text: String) =
                TestFragment().apply {
                    arguments = Bundle().apply {
                        putString(TEXT_ARG, text)
                    }
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text.text = arguments?.getString(TEXT_ARG)
    }
}
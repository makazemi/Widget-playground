package com.makazemi.bitcoinwidget.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.makazemi.bitcoinwidget.R
import com.makazemi.bitcoinwidget.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel.response.observe(this) {
            it?.data?.peekContent()?.let {
                binding.txtValueInDollar.text = getString(R.string.value_dollar, it.value)
            }
            displayProgressBar(it.loading.isLoading)
            it.error?.getContentIfNotHandled()?.message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun displayProgressBar(inProgress: Boolean) {
        binding.progressBar.isVisible = inProgress
    }
}
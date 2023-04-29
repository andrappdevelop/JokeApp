package com.example.jokeapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.jokeapp.JokeApp
import com.example.jokeapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = (application as JokeApp).viewModel

        binding.showFavoriteCheckBox.setOnCheckedChangeListener { _, isCheked ->
            viewModel.chooseFavorite(isCheked)
        }

        binding.favoriteButton.setOnClickListener {
            viewModel.changeJokeStatus()
        }

        binding.actionButton.setOnClickListener {
            binding.actionButton.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            viewModel.getJoke()
        }

        val jokeUiCallback = object : JokeUiCallback {
            override fun provideText(text: String) {
                binding.actionButton.isEnabled = true
                binding.progressBar.visibility = View.INVISIBLE
                binding.textView.text = text
            }

            override fun provideIconResId(iconResId: Int) {
                binding.favoriteButton.setImageResource(iconResId)
            }
        }

        viewModel.init(jokeUiCallback)
    }
}
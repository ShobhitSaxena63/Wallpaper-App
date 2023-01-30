package com.shobhit63.wallpaperworld.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.shobhit63.wallpaperworld.R
import com.shobhit63.wallpaperworld.data.FetchType
import com.shobhit63.wallpaperworld.data.SetOptions
import com.shobhit63.wallpaperworld.data.network.ErrorCode
import com.shobhit63.wallpaperworld.data.network.Status
import com.shobhit63.wallpaperworld.databinding.FragmentHomeBinding
import timber.log.Timber

class Home : Fragment() {
    private lateinit var _binding:FragmentHomeBinding
    private val binding get() = _binding
    private lateinit var viewModel: HomeViewModel

    //make this variable in viewModel
    private var firstTimeOpen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
       _binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      with(binding.recyclerView) {
        adapter = WallpapersAdapter{
            findNavController().navigate(HomeDirections.actionHome2ToDetail(it,SetOptions.Nothing.name))
        }
      }
        //show curated whenever user open app for first time like home screen

        viewModel.wallpapers.observe(viewLifecycleOwner) {
            (binding.recyclerView.adapter as WallpapersAdapter).submitList(it)
            Timber.d("First time open app: $firstTimeOpen")
            if (firstTimeOpen) {
                viewModel.fetchFromNetwork(FetchType.Curated)
                firstTimeOpen = false
            }
        }
        viewModel.loadingStatus.observe(viewLifecycleOwner){ loadingStatus->
            when (loadingStatus?.status) {
                Status.LOADING -> {
                    binding.loadingStatus.visibility = View.VISIBLE
                    binding.statusError.visibility = View.INVISIBLE
                }
                Status.SUCCESS -> {
                    binding.loadingStatus.visibility = View.INVISIBLE
                    binding.statusError.visibility = View.INVISIBLE
                }
                Status.ERROR -> {
                    binding.loadingStatus.visibility = View.INVISIBLE
                    showErrorMessage(loadingStatus.errorCode,loadingStatus.message)
                    binding.statusError.visibility = View.VISIBLE
                }
                else -> {
                    binding.statusError.visibility = View.VISIBLE
                    binding.statusError.text = getString(R.string.loading_error_msg)
                }
            }
        }

        binding.searchBtn.setOnClickListener {
            validateInput()
        }
    }

    private fun validateInput() {
        try {
            val search = binding.searchEditText.text.trim()
            if(search.isNotEmpty()){
                viewModel.fetchFromNetwork(FetchType.UserSearch,search.toString())
            }
        }catch (ex:Exception){
            Timber.e("ValidateInput() Exception is $ex")
        }

    }

    private fun showErrorMessage(errorCode: ErrorCode?, message: String?) {
        when(errorCode) {
            ErrorCode.NO_DATA -> binding.statusError.text = getString(R.string.no_data_msg)
            ErrorCode.NETWORK_ERROR -> binding.statusError.text = getString(R.string.network_error_msg)
            ErrorCode.UNKNOWN_ERROR -> binding.statusError.text = getString(R.string.unknown_error_msg,message)

            else -> {binding.statusError.text = getString(R.string.unknown_error_msg,message)}
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        viewModel.refreshData()
//    }

}
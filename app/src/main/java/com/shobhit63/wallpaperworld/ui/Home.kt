package com.shobhit63.wallpaperworld.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shobhit63.wallpaperworld.R
import com.shobhit63.wallpaperworld.data.network.ErrorCode
import com.shobhit63.wallpaperworld.data.network.Status
import com.shobhit63.wallpaperworld.databinding.FragmentHomeBinding
import timber.log.Timber

class Home : Fragment() {
    private lateinit var _binding:FragmentHomeBinding
    private val binding get() = _binding
    private lateinit var viewModel: HomeViewModel

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

        }
    }
        viewModel.wallpapers.observe(viewLifecycleOwner, Observer {
            (binding.recyclerView.adapter as WallpapersAdapter).submitList(it)
            if(it.isEmpty()){
                viewModel.fetchFromNetwork()
            }
        })
        viewModel.loadingStatus.observe(viewLifecycleOwner){ loadingStatus->
            when{
                loadingStatus?.status == Status.LOADING->{
                    binding.loadingStatus.visibility = View.VISIBLE
                    binding.statusError.visibility = View.INVISIBLE
                }
                loadingStatus?.status == Status.SUCCESS -> {
                    binding.loadingStatus.visibility = View.INVISIBLE
                    binding.statusError.visibility = View.INVISIBLE
                }
                loadingStatus?.status == Status.ERROR -> {
                    binding.loadingStatus.visibility = View.INVISIBLE
                    showErrorMessage(loadingStatus.errorCode,loadingStatus.message)
                    binding.statusError.visibility = View.VISIBLE
                }
            }

        }
    }
    private fun showErrorMessage(errorCode: ErrorCode?, message: String?) {
        when(errorCode) {
            ErrorCode.NO_DATA -> binding.statusError.text = getString(R.string.no_data_msg)
            ErrorCode.NETWORK_ERROR -> binding.statusError.text = getString(R.string.network_error_msg)
            ErrorCode.UNKNOWN_ERROR -> binding.statusError.text = getString(R.string.unknown_error_msg,message)

            else -> {}
        }

    }

}
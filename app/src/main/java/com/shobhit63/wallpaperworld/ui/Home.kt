package com.shobhit63.wallpaperworld.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.facebook.shimmer.ShimmerFrameLayout
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        Timber.d("Back Press tracking | onCreate() fun = ${viewModel.backAction.value}")
        //reach to curated wallpapers when user press back button
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
//                Timber.d("Back Press tracking | onCreate = ${viewModel.backAction.value}")
                if(viewModel.backAction.value == true){
                    // in here you can do logic when backPress is clicked
                    viewModel.fetchFromNetwork(FetchType.Curated)
                    viewModel.setBackAction(false)
                    Timber.d("Back Press tracking | handleOnBackPressed() fun = ${viewModel.backAction.value}")
                    binding.searchEditText.setText("")
                    binding.searchEditText.clearFocus()

                }else{
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        })
        //On start of app show curated wallpapers
        if (viewModel.onStartOfApp.value==true) {
            viewModel.refreshData()
            viewModel.setOnStartOfApp(false)
        }

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
        Timber.d("Back Press tracking | onViewCreated() fun = ${viewModel.backAction.value}")
      with(binding.recyclerView) {
        adapter = WallpapersAdapter{
            findNavController().navigate(HomeDirections.actionHome2ToDetail(it))
        }
      }

        //show curated whenever user open app for first time like home screen
        viewModel.wallpapers.observe(viewLifecycleOwner) {
            it?.let {(binding.recyclerView.adapter as WallpapersAdapter).submitList(it) }

        }

        // start and end shimmer effect according to fetching and show error message if any
        viewModel.loadingStatus.observe(viewLifecycleOwner){ loadingStatus->
            when (loadingStatus?.status) {
                Status.LOADING -> {
                    binding.shimmerViewContainer.startShimmerAnimation()
                    binding.shimmerViewContainer.visibility = View.VISIBLE
                    binding.statusError.visibility = View.INVISIBLE
                }
                Status.SUCCESS -> {
                    binding.statusError.visibility = View.INVISIBLE
                    binding.shimmerViewContainer.stopShimmerAnimation()
                    binding.shimmerViewContainer.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.shimmerViewContainer.stopShimmerAnimation()
                    binding.shimmerViewContainer.visibility = View.GONE
                    showErrorMessage(loadingStatus.errorCode,loadingStatus.message)
                    binding.statusError.visibility = View.VISIBLE
                }
                else -> {
                    binding.statusError.visibility = View.VISIBLE
                    binding.statusError.text = getString(R.string.loading_error_msg)
                }
            }
            binding.swipeRefresh.isRefreshing = false
        }

        //On refresh show curated wallpapers
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData()
            binding.searchEditText.setText("")
            binding.searchEditText.clearFocus()
        }

        binding.searchBtn.setOnClickListener {
            validateInput()
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                validateInput()
            }
            true
        }

    }

    private fun validateInput() {
        //clear focus and close the keyboard when user search
        binding.searchEditText.clearFocus()
        val inputMethod: InputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethod.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)

        //check user input is not blank then search
        try {
            val search = binding.searchEditText.text.trim()
            if(search.isNotEmpty()){
                //fetch according to user search and set per page to 78
                viewModel.fetchFromNetwork(FetchType.UserSearch,search.toString(),"78")
                viewModel.setBackAction(true)
                Timber.d("Back Press tracking | validateInput = ${viewModel.backAction.value}")
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

    override fun onResume() {
        super.onResume()
        binding.shimmerViewContainer.startShimmerAnimation()
    }

    override fun onPause() {
        binding.shimmerViewContainer.stopShimmerAnimation()
        super.onPause()
    }

}
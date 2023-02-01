package com.shobhit63.wallpaperworld.ui


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.shobhit63.wallpaperworld.R
import com.shobhit63.wallpaperworld.data.Wallpapers
import com.shobhit63.wallpaperworld.databinding.FragmentDetailBinding
import timber.log.Timber



class Detail : Fragment(){
    private lateinit var _binding:FragmentDetailBinding
    private val binding get() = _binding
    private  lateinit var viewModel:DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //viewpager
        binding.viewPager.adapter = ViewPagerAdapter()
        viewModel.allWallpapers.observe(viewLifecycleOwner) {
            it?.let {
                (binding.viewPager.adapter as ViewPagerAdapter).submitList(it)
                Timber.d("View Pager Current Position: ${binding.viewPager.currentItem}")
                val currentIndex:Int = DetailArgs.fromBundle(requireArguments()).currentItem
                 binding.viewPager.currentItem = currentIndex
            }

        }
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.allWallpapers.observe(viewLifecycleOwner) {
                    it?.let {
                        //set background color and photographer name
                        setData(it[position])
                    }
                }
                super.onPageSelected(position)
            }

        })


        binding.downloadBtn.setOnClickListener {
            // Pass current view id to bottom dialog fragment
            viewModel.allWallpapers.observe(viewLifecycleOwner) {
                it?.let {
                    //pass id of current element so that we can perform - set and download operations
                    findNavController().navigate(DetailDirections.actionDetailToBottomOptionsFragment(it[binding.viewPager.currentItem].id))
                }
            }

        }

    }

    private fun setData(wallpaper: Wallpapers) {
        binding.view.setBackgroundColor(Color.parseColor(wallpaper.avg_color))
        binding.photographerName.text = getString(R.string.photographer_name,wallpaper.photographer)
    }


}
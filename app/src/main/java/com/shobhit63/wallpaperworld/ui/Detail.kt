package com.shobhit63.wallpaperworld.ui


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
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
        binding.viewPager.adapter = ViewPagerAdapter{
            Toast.makeText(requireActivity(),"View Pager Tapped",Toast.LENGTH_SHORT).show()
        }
        viewModel.allWallpapers.observe(viewLifecycleOwner) {
            it?.let {
                (binding.viewPager.adapter as ViewPagerAdapter).submitList(it)
                Timber.d("View Pager Testing Current Position: ${binding.viewPager.currentItem}")
                val currentIndex:Int = DetailArgs.fromBundle(requireArguments()).currentItem
                 binding.viewPager.currentItem = currentIndex
            }

        }
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
//                Timber.d("View Pager Testing Current Position onPageSelected: $position")
                //
                viewModel.allWallpapers.observe(viewLifecycleOwner) {
                    it?.let {
                        Timber.d("View Pager Testing Complete list: ${it[position]}")
                        Timber.d("View Pager Testing Current Position onPageSelected: $position")
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
                    findNavController().navigate(DetailDirections.actionDetailToBottomOptionsFragment(it[binding.viewPager.currentItem].id))
                }
            }

        }

    }

    private fun setData(wallpaper: Wallpapers) {
        binding.view.setBackgroundColor(Color.parseColor(wallpaper.avg_color))
        binding.photographerName.text = getString(R.string.photographer_name,wallpaper.photographer)
        Glide.with(requireActivity())
            .load(wallpaper.src.portrait)
            .error(R.drawable.error_image)
            .into(binding.wallpaperImageView)
    }


}
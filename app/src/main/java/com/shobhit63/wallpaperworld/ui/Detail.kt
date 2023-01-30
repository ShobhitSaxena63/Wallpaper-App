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
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shobhit63.wallpaperworld.R
import com.shobhit63.wallpaperworld.data.SetOptions
import com.shobhit63.wallpaperworld.data.Wallpapers
import com.shobhit63.wallpaperworld.databinding.FragmentDetailBinding


class Detail : Fragment(){
    private lateinit var _binding:FragmentDetailBinding
    private val binding get() = _binding
    private  lateinit var viewModel:DetailViewModel

    //    private var mListener:ItemClickListener?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id:Long = DetailArgs.fromBundle(requireArguments()).id
        val viewModelFactory = DetailViewModelFactory(id,requireActivity().application)
        viewModel = ViewModelProvider(this,viewModelFactory)[DetailViewModel::class.java]

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
        viewModel.wallpapers.observe(viewLifecycleOwner) {
            setData(it)
        }
        binding.downloadBtn.setOnClickListener {
            val id:Long = DetailArgs.fromBundle(requireArguments()).id

//            val bottomSheetFragment: BottomSheetDialogFragment = ActionBottomDialogFragment()
//            bottomSheetFragment.show(requireActivity().supportFragmentManager, bottomSheetFragment.tag)
            findNavController().navigate(DetailDirections.actionDetailToBottomOptionsFragment(id))
        }

        //buttons clicks from bottomSheet
        when(DetailArgs.fromBundle(requireArguments()).clickOn){
            SetOptions.HomeScreen.name -> Toast.makeText(requireActivity(),"SET WALLPAPER IS SELECTED",Toast.LENGTH_SHORT).show()
            SetOptions.LockScreen.name -> Toast.makeText(requireActivity(),"SET LOCK SCREEN IS SELECTED",Toast.LENGTH_SHORT).show()
            SetOptions.Both.name -> Toast.makeText(requireActivity(),"SET BOTH IS SELECTED",Toast.LENGTH_SHORT).show()
            SetOptions.Save.name -> Toast.makeText(requireActivity(),"SAVE TO GALLERY IS SELECTED",Toast.LENGTH_SHORT).show()
        }

    }

    private fun setData(wallpaper: Wallpapers) {
//        binding.view.setBackgroundColor(798468)
        binding.view.setBackgroundColor(Color.parseColor(wallpaper.avg_color))
        Glide.with(requireActivity())
            .load(wallpaper.src.portrait)
            .error(R.drawable.error_image)
            .into(binding.wallpaperImageView)
    }



//    override fun onItemClick(item: String?) {
//        Toast.makeText(requireActivity(), "Yes $item", Toast.LENGTH_SHORT).show()
//    }

}
package com.shobhit63.wallpaperworld.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shobhit63.wallpaperworld.R
import com.shobhit63.wallpaperworld.data.SetOptions
import com.shobhit63.wallpaperworld.databinding.FragmentBottomOptionsBinding


class BottomOptionsFragment : BottomSheetDialogFragment(),View.OnClickListener{
    private lateinit var _binding:FragmentBottomOptionsBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBottomOptionsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setHomeWallpaper.setOnClickListener(this)
        binding.setLockScreen.setOnClickListener(this)
        binding.setBoth.setOnClickListener(this)
        binding.saveToGallery.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        val id:Long = BottomOptionsFragmentArgs.fromBundle(requireArguments()).id
        when(v!!.id){
            R.id.set_home_wallpaper -> findNavController().navigate(BottomOptionsFragmentDirections.actionBottomOptionsFragmentToDetail(id,SetOptions.HomeScreen.name))
            R.id.set_lock_screen -> findNavController().navigate(BottomOptionsFragmentDirections.actionBottomOptionsFragmentToDetail(id,SetOptions.LockScreen.name))
            R.id.set_both -> findNavController().navigate(BottomOptionsFragmentDirections.actionBottomOptionsFragmentToDetail(id,SetOptions.Both.name))
            R.id.save_to_gallery -> findNavController().navigate(BottomOptionsFragmentDirections.actionBottomOptionsFragmentToDetail(id,SetOptions.Save.name))
        }
    }


}
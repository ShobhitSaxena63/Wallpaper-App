package com.shobhit63.wallpaperworld.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.shobhit63.wallpaperworld.R
import com.shobhit63.wallpaperworld.data.SetOptions
import com.shobhit63.wallpaperworld.databinding.FragmentBottomOptionsBinding
import timber.log.Timber
import java.io.IOException


class BottomOptionsFragment : BottomSheetDialogFragment(),View.OnClickListener,View.OnTouchListener{
    private lateinit var _binding:FragmentBottomOptionsBinding
    private val binding get() = _binding
    private lateinit var viewModel:BottomDialogViewModel
    private lateinit var downloadManager: DownloadManager
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    companion object {
        const val SNACKBAR_DURATION = 400
        const val DELAY_DURATION = 1100L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        val id:Long = BottomOptionsFragmentArgs.fromBundle(requireArguments()).id
        val viewModelFactory = BottomDialogViewModelFactory(id,requireActivity().application)
        viewModel = ViewModelProvider(this,viewModelFactory)[BottomDialogViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBottomOptionsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setHomeWallpaper.setOnTouchListener(this)
        binding.setLockScreen.setOnTouchListener(this)
        binding.setBoth.setOnTouchListener(this)
        binding.saveToGallery.setOnTouchListener(this)


        //save to gallery
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action
                viewModel.wallpapers.observe(viewLifecycleOwner) {
                    it?.let {
                        startDownload(it.src.portrait)
                    }
                }
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Toast.makeText(requireActivity(), "Permission Denied",
                    Toast.LENGTH_LONG).show()
            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        //change background of all options when user touches
        when(event.action){
            //On press
            MotionEvent.ACTION_DOWN -> v.background = ContextCompat.getDrawable(requireActivity(),R.drawable.bottom_dialog_options_background)
            //on release
            MotionEvent.ACTION_UP -> {
                v.background = ContextCompat.getDrawable(requireActivity(),R.drawable.bd_not_select_bg)
              // on release perform action
                onClick(v)
                            }
        }
        return true
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.set_home_wallpaper -> {
                getWallpaper(SetOptions.HomeScreen)
            }
            R.id.set_lock_screen -> {
                getWallpaper(SetOptions.LockScreen)
            }
            R.id.set_both -> {
                getWallpaper(SetOptions.Both)
            }
            R.id.save_to_gallery -> {
                getWallpaper(SetOptions.Save)

            }
        }
    }

    private fun getWallpaper(setOptions: SetOptions){
        viewModel.wallpapers.observe(viewLifecycleOwner){
            if(setOptions == SetOptions.Save){
                downloadFile(binding.root,it.src.portrait)
            }else{
                setWallpaper(it.src.portrait,setOptions)
            }

        }
    }

    private fun setWallpaper(photoPath:String,setOptions: SetOptions){
        val wallpaperManager = WallpaperManager.getInstance(requireActivity())
        Glide.with(requireContext())
            .asBitmap()
            .load(photoPath)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    try {
                        when(setOptions){
                            SetOptions.HomeScreen -> {
                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_SYSTEM)
                                Snackbar.make(binding.root.rootView, "Wallpaper Set to Home Screen", SNACKBAR_DURATION).show()
                                callDismiss()
                            }
                            SetOptions.LockScreen -> {
                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK)
                                Snackbar.make(binding.root.rootView, "Wallpaper Set to Lock Screen ", SNACKBAR_DURATION).show()
                                callDismiss()
                            }
                            SetOptions.Both -> {
                                wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK or  WallpaperManager.FLAG_SYSTEM)
                                Snackbar.make(binding.root.rootView, "Wallpaper Set to Both Screens", SNACKBAR_DURATION).show()
                                callDismiss()
                            }

                            else -> { wallpaperManager.setBitmap(resource) }
                        }
//
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun downloadFile(view: View,photoPath: String) {
        if(ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            startDownload(photoPath)
        } else {
            requestWritePermission(view)
        }
    }
    private fun requestWritePermission(view: View) {
        if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            val snack = Snackbar.make(view,"Please accept this to download wallpapers",Snackbar.LENGTH_INDEFINITE)
            snack.setAction("OK") {
//                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                PERMISSION_REQUEST_WRITE)
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            snack.show()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun startDownload(photoPath: String) {
        try{
            downloadManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(photoPath))
            request.apply {
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                setTitle("image")
                setDescription("Image is downloading")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "image.jpg")
                downloadManager.enqueue(this)
            }
            Snackbar.make(binding.root.rootView, "Wallpaper Save to Gallery", SNACKBAR_DURATION).show()
            callDismiss()
        }catch (exception:Exception){
            Timber.e("Download Exception $exception ")
        }

    }

    private fun callDismiss(){
        //call some delay to show snack bar to user related to their action
        Handler(Looper.getMainLooper()).postDelayed({
            dismiss()
        }, DELAY_DURATION)
    }



}
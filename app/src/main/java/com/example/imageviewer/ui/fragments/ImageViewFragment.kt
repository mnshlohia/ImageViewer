package com.example.imageviewer.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.imageloader.ImageLoader
import com.example.imageviewer.R
import com.example.imageviewer.databinding.ImageViewLayoutBinding
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.*

class ImageViewFragment :DaggerFragment() {

    private var imageUrl: String? = null
    lateinit var binding: ImageViewLayoutBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=ImageViewLayoutBinding.inflate(inflater)
        hideToolbar()
        imageUrl=arguments?.getString("imageUrl")
        return binding.root
    }

    private fun hideToolbar() {
        activity?.findViewById<Toolbar>(R.id.white_toolbar)?.visibility=View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setImageUrlEncoding()
        ImageLoader.with(context).load(imageUrl,binding.view){
            if(it=="success"){
                binding.progressCircular.visibility=View.GONE
            }else{
                binding.progressCircular.visibility=View.GONE
                Navigation.findNavController(context as Activity,R.id.nav_host_fragment_container).navigateUp()
            }
        }
    }

    private fun setImageUrlEncoding() {
        if(imageUrl!!.contains("&amp;auto")){
            imageUrl=imageUrl?.replace("&amp;auto","&auto")
        }
        if(imageUrl!!.contains("&amp;s")){
            imageUrl=imageUrl?.replace("&amp;s","&s")
        }
        if(imageUrl!!.contains("&amp;crop")){
            imageUrl=imageUrl?.replace("&amp;crop","&crop")
        }
        if(imageUrl!!.contains("&amp;format")){
            imageUrl=imageUrl?.replace("&amp;format","&format")
        }
    }
}
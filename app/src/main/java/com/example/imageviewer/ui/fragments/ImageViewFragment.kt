package com.example.imageviewer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.example.imageloader.ImageLoader
import com.example.imageviewer.R
import com.example.imageviewer.databinding.ImageViewLayoutBinding
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.*

class ImageViewFragment :DaggerFragment() {

    private var image_url: String? = null
    lateinit var binding: ImageViewLayoutBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=ImageViewLayoutBinding.inflate(inflater)
        hideToolbar()
        image_url=arguments?.getString("imageUrl")
        return binding.root
    }

    private fun hideToolbar() {
        activity?.findViewById<Toolbar>(R.id.white_toolbar)?.visibility=View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setImageUrlEncoding(image_url)
        binding.progressCircular.visibility=View.VISIBLE
      CoroutineScope(Dispatchers.Default).launch {
            val result=async {ImageLoader.with(context).load(image_url,binding.view)  }
          result.await()
          withContext(Dispatchers.Main){
              binding.progressCircular.visibility=View.GONE
          }
      }






    }

    private fun setImageUrlEncoding(imageUrl: String?) {
        if(image_url!!.contains("&amp;auto")){
            image_url=image_url?.replace("&amp;auto","&auto")
        }
        if(image_url!!.contains("&amp;s")){
            image_url=image_url?.replace("&amp;s","&s")
        }
        if(image_url!!.contains("&amp;crop")){
            image_url=image_url?.replace("&amp;crop","&crop")
        }
        if(image_url!!.contains("&amp;format")){
            image_url=image_url?.replace("&amp;format","&format")
        }
    }
}
package com.example.imageviewer.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imageviewer.ui.decorators.CustomItemDecorationImageList
import com.example.imageviewer.R
import com.example.imageviewer.ui.pojos.RedditItem
import com.example.imageviewer.databinding.ImageListingLayoutBinding
import com.example.imageviewer.di.ViewModelProviderFactory
import com.example.imageviewer.ui.adapters.ImageListingAdapter
import com.example.imageviewer.ui.viewmodels.ImageListingViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class ImageListingFragment : DaggerFragment(), ImageListingAdapter.ItemCallback {
    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    private lateinit var imageListingViewModel: ImageListingViewModel

    private lateinit var binding: ImageListingLayoutBinding

    @Inject
    lateinit var imageListAdapter: ImageListingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ImageListingLayoutBinding.inflate(inflater)
        activity?.findViewById<Toolbar>(R.id.white_toolbar)?.visibility=View.VISIBLE
        setImageListRecyclerViewLayoutManagerToAdapter()
        return binding.root

    }

    private fun setImageListRecyclerViewLayoutManagerToAdapter() {
        val mLayoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(context, 2,RecyclerView.VERTICAL, false)
        context?.let {
            CustomItemDecorationImageList(
                it
            )
        }?.let {
            binding.usersRV.addItemDecoration(it)
        }
        binding.usersRV.layoutManager = mLayoutManager
        binding.usersRV.adapter = imageListAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        imageListingViewModel = ViewModelProvider(viewModelStore, viewModelProviderFactory)
            .get(ImageListingViewModel::class.java)

        observeUserListLiveData()
        getData()
    }

    private fun getData() {
        imageListingViewModel.fetchImages()
    }

    private fun observeUserListLiveData() {
        imageListingViewModel.getImageLiveData().observe(viewLifecycleOwner, Observer {
            it?.let {
                setUserListingAdapter(it)
            } ?: run {
            }
        })

    }

    private fun setUserListingAdapter(it: RedditItem) {
        imageListAdapter.setItemCallBack(this)
        imageListAdapter.submitList(it.data.children)
    }

    override fun clickItemCallback(imageUrl: String?) {
        val bundle = bundleOf("imageUrl" to imageUrl)
        Navigation.findNavController(context as Activity, R.id.nav_host_fragment_container)
            .navigate(R.id.action_imageListingFragment_to_imageViewFragment, bundle)
    }
}
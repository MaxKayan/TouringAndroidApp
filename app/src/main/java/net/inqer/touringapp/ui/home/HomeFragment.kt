package net.inqer.touringapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import net.inqer.touringapp.R
import net.inqer.touringapp.data.models.TourRoute
import net.inqer.touringapp.databinding.FragmentHomeBinding
import net.inqer.touringapp.util.Resource
import java.text.DateFormat
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ToursAdapter

    @Inject
    lateinit var dateFormat: DateFormat

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.root.setColorSchemeResources(R.color.purple_200)

        setupRecyclerView()

        binding.swipeLayout.setOnRefreshListener {
            viewModel.refreshRoutes()
        }

        viewModel.routes.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.routesEvents.collect { event ->
                when (event) {
                    is Resource.Error -> {
                        Log.e(TAG, "onViewCreated: $event")
                        Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()

                        binding.swipeLayout.isRefreshing = false
                    }

                    is Resource.Loading -> {
                        Log.d(TAG, "onViewCreated: Loading...")
                        binding.swipeLayout.isRefreshing = true
                    }

                    is Resource.Empty -> {
                        Log.d(TAG, "onViewCreated: empty routes")
                    }
                    else -> {
                        binding.swipeLayout.isRefreshing = false
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ToursAdapter(object : ToursAdapter.Companion.TourViewHolder.OnTourViewInteraction {
            override fun rootClick(item: TourRoute) {
            }

            override fun cardOpened(item: TourRoute) {
                viewModel.refreshFullRouteData(item.id)
            }

            override fun fabClick(item: TourRoute) {
            }

            override fun launchClick(item: TourRoute) {
                viewModel.refreshFullRouteData(item.id)
                viewModel.activateRoute(item.id)
                navigateToRouteMap()
            }

            override fun cancelClick(item: TourRoute) {
                viewModel.deactivateRoutes()
            }
        },
                dateFormat)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    private fun navigateToRouteMap() {
        findNavController().navigate(R.id.navigation_map)
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}